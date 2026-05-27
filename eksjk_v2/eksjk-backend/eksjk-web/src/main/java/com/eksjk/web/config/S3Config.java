package com.eksjk.web.config;

import com.eksjk.service.impl.S3FileServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

/**
 * S3客户端配置
 * 本地开发环境使用MinIO模拟S3接口
 * 生产环境使用阿里云OSS（兼容S3协议）
 *
 * @author eksjk
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "eksjk.upload.storage-type", havingValue = "s3", matchIfMissing = false)
public class S3Config {

    @Value("${eksjk.upload.s3.endpoint:http://localhost:9000}")
    private String endpoint;

    @Value("${eksjk.upload.s3.region:us-east-1}")
    private String region;

    @Value("${eksjk.upload.s3.access-key:minioadmin}")
    private String accessKey;

    @Value("${eksjk.upload.s3.secret-key:minioadmin}")
    private String secretKey;

    @Value("${eksjk.upload.s3.bucket:eksjk-files}")
    private String bucketName;

    @Value("${eksjk.upload.s3.path-style:}")
    private String pathStyleOverride;

    @Bean
    public S3Client s3Client() {
        log.info("初始化S3客户端，端点: {}, 区域: {}, 桶: {}", endpoint, region, bucketName);

        // 判断是否启用路径样式访问：MinIO 需要启用，阿里云 OSS 不支持
        boolean pathStyleEnabled;
        if (pathStyleOverride != null && !pathStyleOverride.isEmpty()) {
            pathStyleEnabled = Boolean.parseBoolean(pathStyleOverride);
        } else {
            // 自动判断：包含 aliyuncs 或 amazonaws 的使用虚拟主机样式
            pathStyleEnabled = !endpoint.contains("aliyuncs.com") && !endpoint.contains("amazonaws.com");
        }
        log.info("S3路径样式访问: {}", pathStyleEnabled);

        // 构建S3配置
        S3Configuration s3Config = S3Configuration.builder()
                .pathStyleAccessEnabled(pathStyleEnabled)
                .build();

        // 构建S3客户端
        S3Client s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .serviceConfiguration(s3Config)
                .build();

        log.info("S3客户端初始化成功");
        return s3Client;
    }

    @Bean
    public S3FileServiceImpl s3FileService(S3Client s3Client) {
        log.info("创建S3文件服务实现");
        return new S3FileServiceImpl(s3Client);
    }
}