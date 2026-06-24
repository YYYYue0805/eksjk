package com.eksjk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eksjk.common.exception.BusinessException;
import com.eksjk.mapper.FileNoteMapper;
import com.eksjk.model.entity.FileNote;
import com.eksjk.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * S3对象存储文件服务实现
 * 本地开发环境使用MinIO模拟，生产环境使用阿里云OSS
 *
 * @author eksjk
 */
@Slf4j
@Primary
@Service
@ConditionalOnProperty(name = "eksjk.upload.storage-type", havingValue = "s3", matchIfMissing = false)
public class S3FileServiceImpl implements FileService {

    private final S3Client s3Client;
    private final FileNoteMapper fileNoteMapper;

    @Value("${eksjk.upload.s3.bucket:eksjk-files}")
    private String bucketName;

    @Value("${eksjk.upload.max-size:52428800}")
    private long maxFileSize; // 默认50MB

    /** 允许的文件扩展名 */
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "bmp", "webp",
            "dcm", "dicom",
            "pdf", "doc", "docx", "xls", "xlsx",
            "zip", "rar"
    );

    public S3FileServiceImpl(S3Client s3Client, FileNoteMapper fileNoteMapper) {
        this.s3Client = s3Client;
        this.fileNoteMapper = fileNoteMapper;
    }

    @Override
    public String upload(MultipartFile file, Long patientId, String category) {
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        if (file.getSize() > maxFileSize) {
            throw new BusinessException("文件大小超过限制（最大50MB）");
        }

        // 校验文件扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BusinessException("不支持的文件格式: " + extension);
        }

        // 按日期和患者ID组织目录结构
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String objectKey = category + "/" + datePath + "/" + patientId + "/" + 
                          UUID.randomUUID().toString().replace("-", "") + "." + extension;

        try {
            // 上传文件到S3，将原始文件名 URL 编码后存入 metadata（metadata 只支持 ASCII）
            String encodedOriginalName = URLEncoder.encode(
                    originalFilename != null ? originalFilename : "", StandardCharsets.UTF_8);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(getContentType(extension))
                    .metadata(Map.of("original-name", encodedOriginalName))
                    .build();

            s3Client.putObject(putObjectRequest, 
                    RequestBody.fromBytes(file.getBytes()));

            log.info("文件上传成功: key={}, originalName={}, patientId={}", objectKey, originalFilename, patientId);
            return objectKey;

        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public byte[] download(String filePath) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .build();

            return s3Client.getObjectAsBytes(getObjectRequest).asByteArray();
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                throw new BusinessException("文件不存在");
            }
            log.error("文件下载失败", e);
            throw new BusinessException("文件下载失败: " + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> listByPatientId(Long patientId, String category) {
        List<Map<String, Object>> fileList = new ArrayList<>();

        try {
            // 构建搜索前缀
            String searchPrefix = (category != null ? category : "image") + "/";
            
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .prefix(searchPrefix)
                    .build();

            ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);
            
            for (S3Object s3Object : listResponse.contents()) {
                String key = s3Object.key();
                // 检查是否属于该患者
                if (key.contains("/" + patientId + "/")) {
                    // 读取对象 metadata 获取原始文件名
                    String originalName = getFileName(key); // 默认用存储文件名
                    try {
                        HeadObjectRequest headRequest = HeadObjectRequest.builder()
                                .bucket(bucketName)
                                .key(key)
                                .build();
                        HeadObjectResponse headResponse = s3Client.headObject(headRequest);
                        String metaOriginalName = headResponse.metadata().get("original-name");
                        if (metaOriginalName != null && !metaOriginalName.isEmpty()) {
                            // URL 解码还原原始文件名
                            originalName = URLDecoder.decode(metaOriginalName, StandardCharsets.UTF_8);
                        }
                    } catch (S3Exception ignored) {}

                    Map<String, Object> fileInfo = new HashMap<>();
                    fileInfo.put("name", originalName);
                    fileInfo.put("path", key);
                    fileInfo.put("size", s3Object.size());
                    fileInfo.put("lastModified", s3Object.lastModified().toEpochMilli());
                    String ext = getFileExtension(key);
                    fileInfo.put("type", ext);
                    fileInfo.put("isDicom", "dcm".equalsIgnoreCase(ext) || "dicom".equalsIgnoreCase(ext));
                    // 查询文件备注
                    LambdaQueryWrapper<FileNote> noteQuery = new LambdaQueryWrapper<>();
                    noteQuery.eq(FileNote::getFilePath, key);
                    FileNote fileNote = fileNoteMapper.selectOne(noteQuery);
                    fileInfo.put("note", fileNote != null ? fileNote.getNote() : null);
                    fileList.add(fileInfo);
                }
            }
            // 按上传时间倒序排列（最新上传的在前）
            fileList.sort((a, b) -> Long.compare(
                    (Long) b.get("lastModified"),
                    (Long) a.get("lastModified")
            ));
        } catch (Exception e) {
            log.error("获取文件列表失败（S3/MinIO 可能不可用）", e);
        }

        return fileList;
    }

    @Override
    public void delete(String filePath) {
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .build();

            s3Client.deleteObject(deleteRequest);
            // 同步删除文件备注
            LambdaQueryWrapper<FileNote> noteQuery = new LambdaQueryWrapper<>();
            noteQuery.eq(FileNote::getFilePath, filePath);
            fileNoteMapper.delete(noteQuery);
            log.info("文件删除成功: path={}", filePath);
        } catch (S3Exception e) {
            log.error("文件删除失败", e);
            throw new BusinessException("文件删除失败: " + e.getMessage());
        }
    }

    @Override
    public void updateNote(String filePath, String note) {
        LambdaQueryWrapper<FileNote> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FileNote::getFilePath, filePath);
        FileNote existingNote = fileNoteMapper.selectOne(queryWrapper);

        if (note == null || note.trim().isEmpty()) {
            if (existingNote != null) {
                fileNoteMapper.deleteById(existingNote.getId());
            }
        } else {
            if (existingNote != null) {
                existingNote.setNote(note.trim());
                fileNoteMapper.updateById(existingNote);
            } else {
                FileNote newNote = new FileNote();
                newNote.setFilePath(filePath);
                newNote.setNote(note.trim());
                fileNoteMapper.insert(newNote);
            }
        }
    }

    @Override
    public String getNote(String filePath) {
        LambdaQueryWrapper<FileNote> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FileNote::getFilePath, filePath);
        FileNote fileNote = fileNoteMapper.selectOne(queryWrapper);
        return fileNote != null ? fileNote.getNote() : null;
    }

    @Override
    public byte[] batchDownload(List<Long> patientIds) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (Long patientId : patientIds) {
                List<Map<String, Object>> files = listByPatientId(patientId, null);
                for (Map<String, Object> fileInfo : files) {
                    String filePath = (String) fileInfo.get("path");
                    byte[] fileData = download(filePath);
                    
                    ZipEntry entry = new ZipEntry(patientId + "/" + fileInfo.get("name"));
                    zos.putNextEntry(entry);
                    zos.write(fileData);
                    zos.closeEntry();
                }
            }

            zos.finish();
            return baos.toByteArray();

        } catch (IOException e) {
            log.error("批量打包下载失败", e);
            throw new BusinessException("批量打包下载失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * 从路径中获取文件名
     */
    private String getFileName(String filePath) {
        int lastSlash = filePath.lastIndexOf("/");
        return lastSlash >= 0 ? filePath.substring(lastSlash + 1) : filePath;
    }

    /**
     * 根据扩展名获取Content-Type
     */
    private String getContentType(String extension) {
        return switch (extension.toLowerCase()) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "pdf" -> "application/pdf";
            case "dcm", "dicom" -> "application/dicom";
            default -> "application/octet-stream";
        };
    }
}