package com.eksjk.service.eltm;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.eksjk.mapper.*;
import com.eksjk.model.entity.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ELTM 外部附件迁移
 *
 * <p>把 E路童萌 检查资料、体征附件、表单图片下载到本地文件存储，
 * 供「基线-辅助检查-影像资料上传」和「知情同意书」展示。
 * 外部图片需要 Referer 才能绕过防盗链。</p>
 *
 * @author eksjk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EltmAttachmentService {

    private static final String REFERER = "https://eksf.zsmm.org.cn/";
    private static final Pattern URL_EXT = Pattern.compile("\\.([a-zA-Z0-9]{2,5})(?:$|\\?)");
    private static final Set<String> ALLOWED_EXT = Set.of(
            "jpg", "jpeg", "png", "gif", "bmp", "webp", "pdf", "dcm", "dicom");

    private final PatientMapper patientMapper;
    private final EltmPatientPicMapper picMapper;
    private final EltmPhysicalRecordMapper physicalRecordMapper;
    private final EltmPhysicalItemMapper physicalItemMapper;
    private final EltmFormSubmissionMapper formSubmissionMapper;
    private final InformedConsentMapper informedConsentMapper;
    private final PatientFollowUpMapper followUpMapper;
    private final FileNoteMapper fileNoteMapper;
    private final ObjectMapper objectMapper;

    @Value("${eksjk.upload.storage-type:local}")
    private String storageType;

    @Value("${eksjk.upload.path:./uploads}")
    private String uploadBasePath;

    @Value("${eksjk.eltm.attachment-sync-enabled:true}")
    private boolean enabled;

    @Value("${eksjk.eltm.attachment-max-per-patient:150}")
    private int maxPerPatient;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * 全量附件迁移
     */
    public Map<String, Object> projectAll() {
        Map<String, Object> result = new LinkedHashMap<>();
        if (!enabled || !"local".equalsIgnoreCase(storageType)) {
            result.put("enabled", false);
            result.put("message", "附件迁移未启用或非本地存储");
            return result;
        }
        List<Patient> patients = patientMapper.selectList(new LambdaQueryWrapper<Patient>()
                .select(Patient::getId)
                .isNotNull(Patient::getExternalId)
                .eq(Patient::getDelFlg, "1"));
        java.util.concurrent.atomic.AtomicInteger downloadedCounter = new java.util.concurrent.atomic.AtomicInteger();
        java.util.concurrent.atomic.AtomicInteger skippedCounter = new java.util.concurrent.atomic.AtomicInteger();
        java.util.concurrent.atomic.AtomicInteger failedCounter = new java.util.concurrent.atomic.AtomicInteger();
        java.util.concurrent.atomic.AtomicInteger patientsWithFilesCounter = new java.util.concurrent.atomic.AtomicInteger();
        java.util.concurrent.atomic.AtomicInteger doneCounter = new java.util.concurrent.atomic.AtomicInteger();

        int threads = Math.max(2, Math.min(8, Runtime.getRuntime().availableProcessors()));
        java.util.concurrent.ExecutorService pool = java.util.concurrent.Executors.newFixedThreadPool(threads);
        List<java.util.concurrent.Future<?>> futures = new ArrayList<>();
        for (Patient patient : patients) {
            futures.add(pool.submit(() -> {
                Map<String, Object> one = projectByPatientId(patient.getId());
                int d = (int) one.getOrDefault("downloaded", 0);
                if (d > 0) patientsWithFilesCounter.incrementAndGet();
                downloadedCounter.addAndGet(d);
                skippedCounter.addAndGet((int) one.getOrDefault("skipped", 0));
                failedCounter.addAndGet((int) one.getOrDefault("failed", 0));
                int done = doneCounter.incrementAndGet();
                if (done % 500 == 0) {
                    log.info("ELTM 附件迁移进度 {}/{}", done, patients.size());
                }
            }));
        }
        for (java.util.concurrent.Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                log.warn("ELTM 附件迁移任务失败", e);
            }
        }
        pool.shutdown();

        int downloaded = downloadedCounter.get();
        int skipped = skippedCounter.get();
        int failed = failedCounter.get();
        int patientsWithFiles = patientsWithFilesCounter.get();
        result.put("enabled", true);
        result.put("patients", patients.size());
        result.put("patientsWithFiles", patientsWithFiles);
        result.put("downloaded", downloaded);
        result.put("skipped", skipped);
        result.put("failed", failed);
        return result;
    }

    /**
     * 单患者附件迁移
     */
    public Map<String, Object> projectByPatientId(Long patientId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("downloaded", 0);
        result.put("skipped", 0);
        result.put("failed", 0);
        if (!enabled || !"local".equalsIgnoreCase(storageType) || patientId == null) {
            return result;
        }

        List<Attachment> attachments = collectAttachments(patientId);
        if (attachments.isEmpty()) {
            return result;
        }

        int downloaded = 0;
        int skipped = 0;
        int failed = 0;
        int processed = 0;
        Map<Long, List<String>> followUpImages = new LinkedHashMap<>();
        Map<String, String> consentPaths = new LinkedHashMap<>();
        Set<String> seen = new HashSet<>();

        for (Attachment attachment : attachments) {
            if (processed >= maxPerPatient) {
                log.info("ELTM 附件达到单患者上限 patientId={} limit={}", patientId, maxPerPatient);
                break;
            }
            if (attachment.url == null || attachment.url.isBlank() || !seen.add(attachment.url)) {
                continue;
            }
            processed++;
            Path relative = relativePath(patientId, attachment.url, attachment.kind);
            if (relative == null) {
                failed++;
                continue;
            }
            String relativePath = relative.toString().replace('\\', '/');
            Path target = Paths.get(uploadBasePath, relative.toString());
            if (Files.exists(target)) {
                skipped++;
                applyLink(attachment, relativePath, followUpImages, consentPaths);
                continue;
            }
            try {
                byte[] data = download(attachment.url);
                if (data == null || data.length == 0) {
                    failed++;
                    continue;
                }
                Files.createDirectories(target.getParent());
                Files.write(target, data);
                insertNote(relativePath, attachment);
                applyLink(attachment, relativePath, followUpImages, consentPaths);
                downloaded++;
            } catch (Exception e) {
                failed++;
                log.warn("ELTM 附件下载失败 patientId={} url={}", patientId, attachment.url, e);
            }
        }

        updateFollowUpImages(patientId, followUpImages);
        updateConsentPaths(consentPaths);

        result.put("downloaded", downloaded);
        result.put("skipped", skipped);
        result.put("failed", failed);
        return result;
    }

    private void applyLink(Attachment attachment, String relative,
                           Map<Long, List<String>> followUpImages, Map<String, String> consentPaths) {
        if (attachment.formDataId != null) {
            followUpImages.computeIfAbsent(attachment.formDataId, key -> new ArrayList<>()).add(relative);
        }
        if ("consent".equals(attachment.kind)) {
            consentPaths.put(attachment.url, relative);
        }
    }

    private void updateFollowUpImages(Long patientId, Map<Long, List<String>> images) {
        for (Map.Entry<Long, List<String>> entry : images.entrySet()) {
            try {
                followUpMapper.update(null, new LambdaUpdateWrapper<PatientFollowUp>()
                        .eq(PatientFollowUp::getPatientId, patientId)
                        .eq(PatientFollowUp::getSourceFormDataId, entry.getKey())
                        .set(PatientFollowUp::getImage, String.join(",", entry.getValue())));
            } catch (Exception e) {
                log.warn("更新随访附件失败 patientId={} formDataId={}", patientId, entry.getKey(), e);
            }
        }
    }

    private void updateConsentPaths(Map<String, String> consentPaths) {
        for (Map.Entry<String, String> entry : consentPaths.entrySet()) {
            try {
                informedConsentMapper.update(null, new LambdaUpdateWrapper<InformedConsent>()
                        .eq(InformedConsent::getOssUrl, entry.getKey())
                        .eq(InformedConsent::getSource, "ELTM_FORM_UPLOAD")
                        .set(InformedConsent::getPdfPath, entry.getValue())
                        .set(InformedConsent::getOssUrl, null));
            } catch (Exception e) {
                log.warn("更新知情同意书附件失败 url={}", entry.getKey(), e);
            }
        }
    }

    private List<Attachment> collectAttachments(Long patientId) {
        List<Attachment> list = new ArrayList<>();

        // 1. 检查资料
        for (EltmPatientPic pic : picMapper.selectList(new LambdaQueryWrapper<EltmPatientPic>()
                .eq(EltmPatientPic::getPatientId, patientId))) {
            if (pic.getImageUrl() != null) {
                list.add(new Attachment(pic.getImageUrl(),
                        firstNonBlank(pic.getRecordName(), pic.getTypeContent(), "检查资料"),
                        "pic:" + pic.getImageType(), null, "pic"));
            }
        }

        // 2. 体征附件
        List<EltmPhysicalRecord> records = physicalRecordMapper.selectList(new LambdaQueryWrapper<EltmPhysicalRecord>()
                .eq(EltmPhysicalRecord::getPatientId, patientId));
        if (!records.isEmpty()) {
            List<Long> recordIds = records.stream().map(EltmPhysicalRecord::getId).toList();
            List<EltmPhysicalItem> items = physicalItemMapper.selectList(new LambdaQueryWrapper<EltmPhysicalItem>()
                    .in(EltmPhysicalItem::getRecordId, recordIds));
            for (EltmPhysicalItem item : items) {
                if (item.getAttributeCode() != null && item.getAttributeCode().endsWith("Url")
                        && item.getAttributeValue() != null && item.getAttributeValue().startsWith("http")) {
                    list.add(new Attachment(item.getAttributeValue(),
                            item.getAttributeCode(), "physical", null, "physical"));
                }
            }
        }

        // 3. 表单图片（检查/随访/知情同意）
        List<EltmFormSubmission> submissions = formSubmissionMapper.selectList(
                new LambdaQueryWrapper<EltmFormSubmission>()
                        .eq(EltmFormSubmission::getPatientId, patientId)
                        .in(EltmFormSubmission::getFormName,
                                List.of("实验室检查", "影像学结果", "治疗随访-实验室检查", "随访检查记录", "知情同意书上传"))
                        .isNotNull(EltmFormSubmission::getOriginalData));
        for (EltmFormSubmission submission : submissions) {
            JsonNode data = parse(submission.getOriginalData());
            if (data == null) continue;
            for (Iterator<Map.Entry<String, JsonNode>> it = data.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> entry = it.next();
                if (!entry.getKey().startsWith("image_upload")) continue;
                JsonNode files = entry.getValue();
                if (!files.isArray()) continue;
                for (JsonNode file : files) {
                    if (!file.isObject() || !file.hasNonNull("url")) continue;
                    String url = file.get("url").asText("");
                    if (url.isBlank()) continue;
                    String name = file.hasNonNull("name") ? file.get("name").asText() : "附件";
                    String kind = "知情同意书上传".equals(submission.getFormName()) ? "consent" : "form";
                    list.add(new Attachment(url, name, submission.getFormName(),
                            submission.getFormDataId(), kind));
                }
            }
        }
        return list;
    }

    private byte[] download(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .header("Referer", REFERER)
                    .header("User-Agent", "Mozilla/5.0")
                    .GET()
                    .build();
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() != 200) {
                log.warn("ELTM 附件 HTTP {} url={}", response.statusCode(), url);
                return null;
            }
            return response.body();
        } catch (Exception e) {
            return null;
        }
    }

    private Path relativePath(Long patientId, String url, String kind) {
        String ext = extension(url);
        if (ext == null || !ALLOWED_EXT.contains(ext)) {
            ext = "jpg";
        }
        String hash = sha1(url);
        String category = "consent".equals(kind) ? "consent" : "image";
        return Paths.get(category, "eltm", String.valueOf(patientId), hash + "." + ext);
    }

    private String extension(String url) {
        try {
            String path = URI.create(url).getPath();
            Matcher matcher = URL_EXT.matcher(path);
            return matcher.find() ? matcher.group(1).toLowerCase(Locale.ROOT) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String sha1(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return Integer.toHexString(text.hashCode());
        }
    }

    private void insertNote(String filePath, Attachment attachment) {
        try {
            Long count = fileNoteMapper.selectCount(new LambdaQueryWrapper<FileNote>()
                    .eq(FileNote::getFilePath, filePath));
            if (count != null && count > 0) {
                return;
            }
            FileNote note = new FileNote();
            note.setFilePath(filePath);
            String label = firstNonBlank(attachment.label, attachment.kind, "ELTM 附件");
            note.setNote("ELTM | " + label + " | " + attachment.name + " | " + attachment.url);
            note.setCreateTime(java.time.LocalDateTime.now());
            note.setUpdateTime(java.time.LocalDateTime.now());
            fileNoteMapper.insert(note);
        } catch (Exception e) {
            log.warn("写入附件备注失败 filePath={}", filePath, e);
        }
    }

    private JsonNode parse(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            return null;
        }
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) return value;
        }
        return null;
    }

    private static class Attachment {
        private final String url;
        private final String name;
        private final String label;
        private final Long formDataId;
        private final String kind;

        Attachment(String url, String name, String label, Long formDataId, String kind) {
            this.url = url;
            this.name = name;
            this.label = label;
            this.formDataId = formDataId;
            this.kind = kind;
        }
    }
}
