package com.eksjk.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 文件管理服务接口
 *
 * @author eksjk
 */
public interface FileService {

    /**
     * 上传文件
     *
     * @param file      文件
     * @param patientId 关联患者ID
     * @param category  文件分类（image/dicom/document）
     * @return 文件访问路径
     */
    String upload(MultipartFile file, Long patientId, String category);

    /**
     * 按指定对象键保存字节内容（用于外部附件迁移等内部场景）
     *
     * @param objectKey        存储对象键/相对路径
     * @param content          文件字节
     * @param originalFilename 原始文件名（用于扩展名/元数据）
     * @return 实际保存的对象键
     */
    String store(String objectKey, byte[] content, String originalFilename);

    /**
     * 判断对象是否已存在
     *
     * @param filePath 文件路径/对象键
     * @return 是否存在
     */
    boolean exists(String filePath);

    /**
     * 获取文件字节内容
     *
     * @param filePath 文件路径
     * @return 文件字节数组
     */
    byte[] download(String filePath);

    /**
     * 获取某患者的文件列表
     *
     * @param patientId 患者ID
     * @param category  文件分类（可选）
     * @return 文件信息列表
     */
    List<Map<String, Object>> listByPatientId(Long patientId, String category);

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     */
    void delete(String filePath);

    /**
     * 批量打包下载
     *
     * @param patientIds 患者ID列表
     * @return ZIP文件字节数组
     */
    byte[] batchDownload(List<Long> patientIds);

    /**
     * 更新文件备注
     *
     * @param filePath 文件路径
     * @param note     备注内容（null或空字符串时删除备注）
     */
    void updateNote(String filePath, String note);

    /**
     * 获取文件备注
     *
     * @param filePath 文件路径
     * @return 备注内容，无备注时返回null
     */
    String getNote(String filePath);
}
