package com.eksjk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eksjk.common.exception.BusinessException;
import com.eksjk.mapper.FileNoteMapper;
import com.eksjk.model.entity.FileNote;
import com.eksjk.service.FileService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 本地文件系统文件管理服务实现
 * 仅在存储类型为local时生效
 *
 * @author eksjk
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "eksjk.upload.storage-type", havingValue = "local", matchIfMissing = false)
public class FileServiceImpl implements FileService {

    @Value("${eksjk.upload.path:./uploads}")
    private String uploadBasePath;

    private final FileNoteMapper fileNoteMapper;

    public FileServiceImpl(FileNoteMapper fileNoteMapper) {
        this.fileNoteMapper = fileNoteMapper;
    }

    @PostConstruct
    public void init() {
        Path path = Paths.get(uploadBasePath);
        if (!path.isAbsolute()) {
            uploadBasePath = path.toAbsolutePath().normalize().toString();
        }
        log.info("文件上传基础路径: {}", uploadBasePath);
        try {
            Files.createDirectories(Paths.get(uploadBasePath));
        } catch (IOException e) {
            log.error("无法创建上传目录: {}", uploadBasePath, e);
        }
    }

    @Value("${eksjk.upload.max-size:52428800}")
    private long maxFileSize; // 默认50MB

    /** 允许的文件扩展名 */
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "bmp", "webp",
            "dcm", "dicom",
            "pdf", "doc", "docx", "xls", "xlsx",
            "zip", "rar"
    );

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
        String relativePath = category + "/" + datePath + "/" + patientId;
        Path dirPath = Paths.get(uploadBasePath, relativePath);

        try {
            Files.createDirectories(dirPath);

            // 生成唯一文件名
            String newFilename = UUID.randomUUID().toString().replace("-", "") + "." + extension;
            Path filePath = dirPath.resolve(newFilename);

            file.transferTo(filePath.toFile());

            String resultPath = relativePath + "/" + newFilename;
            log.info("文件上传成功: path={}, patientId={}", resultPath, patientId);
            return resultPath;

        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public byte[] download(String filePath) {
        Path path = Paths.get(uploadBasePath, filePath);
        if (!Files.exists(path)) {
            throw new BusinessException("文件不存在");
        }

        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            log.error("文件下载失败", e);
            throw new BusinessException("文件下载失败: " + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> listByPatientId(Long patientId, String category) {
        List<Map<String, Object>> fileList = new ArrayList<>();

        // 未指定分类时扫描所有目录，指定分类时只扫描该分类目录
        Path searchRoot = category != null
                ? Paths.get(uploadBasePath, category)
                : Paths.get(uploadBasePath);

        if (!Files.exists(searchRoot)) {
            return fileList;
        }

        try {
            String patientIdStr = "/" + patientId + "/";
            Files.walk(searchRoot)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().replace('\\', '/').contains(patientIdStr))
                    .forEach(p -> {
                        Map<String, Object> fileInfo = new HashMap<>();
                        try {
                            fileInfo.put("name", p.getFileName().toString());
                            String relativePath = Paths.get(uploadBasePath).relativize(p).toString().replace('\\', '/');
                            fileInfo.put("path", relativePath);
                            fileInfo.put("size", Files.size(p));
                            fileInfo.put("lastModified", Files.getLastModifiedTime(p).toMillis());
                            String ext = getFileExtension(p.getFileName().toString());
                            fileInfo.put("type", ext);
                            fileInfo.put("isDicom", "dcm".equalsIgnoreCase(ext) || "dicom".equalsIgnoreCase(ext));
                            // 从路径中提取分类（第一级目录名）
                            String[] parts = relativePath.split("/");
                            if (parts.length > 0) {
                                fileInfo.put("category", parts[0]);
                            }
                            // 查询文件备注
                            LambdaQueryWrapper<FileNote> noteQuery = new LambdaQueryWrapper<>();
                            noteQuery.eq(FileNote::getFilePath, relativePath);
                            FileNote fileNote = fileNoteMapper.selectOne(noteQuery);
                            fileInfo.put("note", fileNote != null ? fileNote.getNote() : null);
                            fileList.add(fileInfo);
                        } catch (IOException ignored) {
                        }
                    });
        } catch (IOException e) {
            log.error("获取文件列表失败", e);
        }

        return fileList;
    }

    @Override
    public void delete(String filePath) {
        Path path = Paths.get(uploadBasePath, filePath);
        if (!Files.exists(path)) {
            throw new BusinessException("文件不存在");
        }

        try {
            Files.delete(path);
            // 同步删除文件备注
            LambdaQueryWrapper<FileNote> noteQuery = new LambdaQueryWrapper<>();
            noteQuery.eq(FileNote::getFilePath, filePath);
            fileNoteMapper.delete(noteQuery);
            log.info("文件删除成功: path={}", filePath);
        } catch (IOException e) {
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
                    Path path = Paths.get(uploadBasePath, filePath);
                    if (Files.exists(path)) {
                        ZipEntry entry = new ZipEntry(patientId + "/" + fileInfo.get("name"));
                        zos.putNextEntry(entry);
                        Files.copy(path, zos);
                        zos.closeEntry();
                    }
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
}
