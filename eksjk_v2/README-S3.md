# EKSJK V2 - S3对象存储集成说明

## 概述

V2版本采用标准的Kubernetes部署方案，主要改进包括：


### 架构升级
- **Kubernetes原生部署**：所有组件采用标准Kubernetes资源（Deployment、Service、PVC等）
- **S3对象存储集成**：文件上传功能从本地文件系统迁移到S3兼容的对象存储
- **Redis缓存层**：引入Redis作为分布式缓存和会话存储，提升系统性能

### 部署环境支持
- **本地开发环境**：使用Kubernetes部署的MinIO模拟S3接口
- **生产环境**：使用阿里云OSS（兼容S3协议）

### 关键特性
- **高可用性**：通过Kubernetes副本和Redis集群实现高可用
- **可扩展性**：支持水平扩展和资源动态调整
- **运维友好**：标准Kubernetes工具链支持监控、日志和故障排除

## 配置说明

### 1. 本地开发环境（MinIO）

#### 启动MinIO服务（Kubernetes部署）
```bash
# 部署MinIO到Kubernetes集群
cd eksjk_v2/k8s
kubectl apply -f minio/

# 等待MinIO Pod就绪
kubectl wait --for=condition=ready pod -l app=eksjk-minio -n eksjk --timeout=120s
```

访问MinIO控制台：http://localhost:30901
- 用户名：minioadmin
- 密码：minioadmin

#### 创建存储桶
1. 登录MinIO控制台
2. 点击"Create Bucket"
3. 输入桶名：`eksjk-files`
4. 点击创建

#### 配置文件
使用 `application-k8s.yml` 配置：
```yaml
eksjk:
  upload:
    storage-type: s3
    s3:
      endpoint: http://eksjk-minio.eksjk.svc.cluster.local:9000
      region: us-east-1
      bucket: eksjk-files
      access-key: minioadmin
      secret-key: minioadmin
```

### 2. 生产环境（阿里云OSS）

#### 创建OSS存储桶
1. 登录阿里云控制台
2. 进入OSS服务
3. 创建存储桶，例如：`eksjk-prod-files`
4. 设置地域，例如：`cn-hangzhou`

#### 获取访问密钥
1. 进入RAM访问控制
2. 创建AccessKey（AccessKey ID和AccessKey Secret）

#### 配置文件
使用 `application-prod.yml` 配置：
```yaml
eksjk:
  upload:
    storage-type: s3
    s3:
      endpoint: https://oss-cn-hangzhou.aliyuncs.com
      region: cn-hangzhou
      bucket: eksjk-prod-files
      access-key: ${ALIYUN_ACCESS_KEY}
      secret-key: ${ALIYUN_SECRET_KEY}
```

#### 环境变量设置
```bash
export ALIYUN_ACCESS_KEY=your-access-key-id
export ALIYUN_SECRET_KEY=your-secret-access-key
```

## 功能特性

### 文件组织结构
文件在对象存储中按以下结构组织：
```
{category}/{yyyy}/{MM}/{dd}/{patientId}/{uuid}.{ext}
```

示例：
```
image/2024/01/15/12345/abc123def456.jpg
```

### 支持的文件格式
- 图片：jpg, jpeg, png, gif, bmp
- DICOM：dcm, dicom
- 文档：pdf, doc, docx, xls, xlsx
- 压缩包：zip, rar

### 文件大小限制
- 单文件最大：50MB
- 请求最大：100MB

## 切换存储模式

### 切换到本地文件系统（测试用）
```yaml
eksjk:
  upload:
    storage-type: local
    path: ./uploads  # 本地存储路径
```

### 切换到S3对象存储
```yaml
eksjk:
  upload:
    storage-type: s3
    # S3配置...
```

## 故障排除

### MinIO连接问题
1. 确保MinIO服务已启动：`kubectl get pods -n eksjk | grep minio`
2. 检查MinIO Pod状态：`kubectl describe pod -n eksjk -l app=eksjk-minio`
3. 验证MinIO控制台可访问：http://localhost:30901
4. 检查PVC绑定状态：`kubectl get pvc -n eksjk eksjk-minio-pvc`

### 权限问题
1. 检查AccessKey和SecretKey是否正确
2. 验证存储桶是否存在且有读写权限

### 网络问题
1. 生产环境确保网络可访问阿里云OSS
2. 检查防火墙设置

## 性能优化

1. **启用CDN**：生产环境可配置CDN加速文件访问
2. **分片上传**：大文件支持分片上传（后续版本支持）
3. **缓存策略**：配置合适的缓存头提高性能

## 监控和日志

- S3 SDK调试日志：`software.amazon.awssdk: DEBUG`（开发环境）
- 生产环境建议设置为：`software.amazon.awssdk: WARN`
- 监控文件上传/下载成功率、延迟等指标

## Redis作用分析

### Redis在EKSJK系统中的关键作用

#### 1. 会话管理（Session Storage）
- **分布式会话**：在Kubernetes多副本部署中，Redis确保用户会话在所有Pod间共享
- **无状态应用**：后端服务可无状态扩展，会话数据集中存储在Redis中
- **会话持久化**：配置持久化策略，防止重启丢失用户登录状态

#### 2. 缓存优化
- **热点数据缓存**：缓存频繁访问的患者信息、配置数据等
- **查询结果缓存**：缓存复杂查询结果，减少数据库压力
- **文件元数据缓存**：缓存文件上传状态、处理进度等信息

#### 3. 分布式锁机制
- **文件上传防冲突**：确保同一文件不会被多个用户同时上传
- **数据一致性**：在并发操作时保证数据处理的原子性
- **任务调度**：协调多个Pod间的定时任务执行

#### 4. 消息队列和事件处理
- **异步任务处理**：文件处理、数据导出等耗时任务异步执行
- **事件驱动架构**：系统各组件通过Redis Pub/Sub进行通信
- **实时通知**：向客户端推送处理进度、系统通知等

#### 5. 性能提升
- **降低数据库负载**：通过缓存减少对MySQL的直接查询
- **快速响应**：Redis内存访问速度远高于磁盘数据库
- **水平扩展**：Redis集群支持系统的高并发访问

#### 6. 高可用性保障
- **数据备份**：Redis支持主从复制和数据持久化
- **故障转移**：自动故障检测和切换
- **集群模式**：支持Redis Cluster实现高可用分布式缓存

### Redis配置说明

#### Kubernetes部署
```yaml
# Redis Deployment配置
apiVersion: apps/v1
kind: Deployment
metadata:
  name: eksjk-redis
  namespace: eksjk
spec:
  replicas: 1
  selector:
    matchLabels:
      app: eksjk-redis
  template:
    metadata:
      labels:
        app: eksjk-redis
    spec:
      containers:
      - name: redis
        image: redis:7-alpine
        ports:
        - containerPort: 6379
        volumeMounts:
        - name: redis-data
          mountPath: /data
        command: ["redis-server", "--appendonly", "yes"]
```

#### 后端连接配置
```yaml
spring:
  redis:
    host: eksjk-redis.eksjk.svc.cluster.local
    port: 6379
    database: 0
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
        max-wait: -1ms
```

### 监控和运维
- **Redis指标监控**：通过Prometheus监控Redis性能指标
- **内存使用监控**：设置内存使用阈值和告警
- **连接数监控**：监控客户端连接数和连接池状态
- **备份策略**：定期备份Redis数据到持久化存储