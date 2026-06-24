package com.eksjk.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.eksjk.common.result.R;
import com.eksjk.common.util.HashidsUtil;
import com.eksjk.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 文件管理 Controller
 *
 * @author eksjk
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@SaCheckLogin
public class FileController {

    private final FileService fileService;

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public R<Map<String, String>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("patientId") String patientId,
            @RequestParam(value = "category", defaultValue = "image") String category) {
        long id = HashidsUtil.decode(patientId);
        String filePath = fileService.upload(file, id, category);
        return R.ok(Map.of("path", filePath));
    }

    /**
     * 下载文件
     */
    @GetMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam("path") String filePath) {
        byte[] data = fileService.download(filePath);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + getFileName(filePath) + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    /**
     * 获取文件流（用于影像预览）
     */
    @GetMapping("/stream")
    public ResponseEntity<byte[]> stream(@RequestParam("path") String filePath) {
        byte[] data = fileService.download(filePath);
        String contentType = getContentType(filePath);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(data);
    }

    /**
     * 获取某患者的文件列表
     */
    @GetMapping("/patient/{patientId}")
    public R<List<Map<String, Object>>> listByPatient(
            @PathVariable String patientId,
            @RequestParam(value = "category", required = false) String category) {
        long id = HashidsUtil.decode(patientId);
        List<Map<String, Object>> files = fileService.listByPatientId(id, category);
        return R.ok(files);
    }

    /**
     * 删除文件
     */
    @DeleteMapping
    public R<Void> delete(@RequestParam("path") String filePath) {
        fileService.delete(filePath);
        return R.ok();
    }

    /**
     * 更新文件备注
     */
    @PutMapping("/note")
    public R<Void> updateNote(
            @RequestParam("path") String filePath,
            @RequestParam("note") String note) {
        fileService.updateNote(filePath, note);
        return R.ok();
    }

    /**
     * 批量打包下载
     */
    @PostMapping("/batch-download")
    public ResponseEntity<byte[]> batchDownload(@RequestBody List<String> patientIds) {
        List<Long> ids = patientIds.stream().map(HashidsUtil::decode).toList();
        byte[] data = fileService.batchDownload(ids);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"cases.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    private String getFileName(String filePath) {
        int lastSlash = filePath.lastIndexOf("/");
        return lastSlash >= 0 ? filePath.substring(lastSlash + 1) : filePath;
    }

    private String getContentType(String filePath) {
        String ext = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
        return switch (ext) {
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
