# ============================================================
# 网络输出
# ============================================================

output "vpc_id" {
  description = "VPC ID"
  value       = alicloud_vpc.main.id
}

output "vswitch_id" {
  description = "交换机 ID"
  value       = alicloud_vswitch.main.id
}

# ============================================================
# ACK 集群输出
# ============================================================

output "ack_cluster_id" {
  description = "ACK 集群 ID"
  value       = alicloud_cs_managed_kubernetes.main.id
}

output "ack_cluster_name" {
  description = "ACK 集群名称"
  value       = alicloud_cs_managed_kubernetes.main.name
}

# ============================================================
# RDS 输出（敏感信息不输出到终端）
# ============================================================

output "rds_instance_id" {
  description = "RDS 实例 ID"
  value       = alicloud_db_instance.main.id
}

output "rds_connection_string" {
  description = "RDS 内网连接地址（host）"
  value       = alicloud_db_instance.main.connection_string
}

output "rds_port" {
  description = "RDS 端口"
  value       = alicloud_db_instance.main.port
}

output "rds_staging_user" {
  description = "RDS staging 数据库账号"
  value       = alicloud_rds_account.staging.account_name
}

output "rds_prod_user" {
  description = "RDS prod 数据库账号"
  value       = alicloud_rds_account.prod.account_name
}

# ============================================================
# OSS 输出
# ============================================================

output "oss_staging_bucket" {
  description = "staging OSS Bucket 名称"
  value       = alicloud_oss_bucket.staging.bucket
}

output "oss_prod_bucket" {
  description = "prod OSS Bucket 名称"
  value       = alicloud_oss_bucket.prod.bucket
}

output "oss_endpoint" {
  description = "OSS 内网 Endpoint（供后端 Pod 使用）"
  value       = "https://oss-${var.region}-internal.aliyuncs.com"
}

# ============================================================
# CLB 公网 IP 输出
# ============================================================

output "clb_staging_ip" {
  description = "staging CLB 公网 IP（用于配置 DNS 解析）"
  value       = alicloud_slb_load_balancer.staging.address
}

output "clb_staging_id" {
  description = "staging CLB 实例 ID（用于 K8s Service Annotation 绑定）"
  value       = alicloud_slb_load_balancer.staging.id
}

output "clb_prod_ip" {
  description = "prod CLB 公网 IP（用于配置 DNS 解析）"
  value       = alicloud_slb_load_balancer.prod.address
}

output "clb_prod_id" {
  description = "prod CLB 实例 ID（用于 K8s Service Annotation 绑定）"
  value       = alicloud_slb_load_balancer.prod.id
}

# ============================================================
# RAM CI/CD 账号输出（敏感信息标记 sensitive）
# ============================================================

output "cicd_access_key_id" {
  description = "CI/CD RAM 账号 AccessKey ID（配置到 GitHub Repository Secrets: ALIYUN_ACCESS_KEY_ID）"
  value       = alicloud_ram_access_key.cicd.id
  sensitive   = true
}

output "cicd_access_key_secret" {
  description = "CI/CD RAM 账号 AccessKey Secret（配置到 GitHub Repository Secrets: ALIYUN_ACCESS_KEY_SECRET）"
  value       = alicloud_ram_access_key.cicd.secret
  sensitive   = true
}

# ============================================================
# 部署后操作提示
# ============================================================

output "next_steps" {
  description = "Terraform apply 完成后的操作指引"
  value       = <<-EOT
    ✅ 阿里云资源创建完成！请按以下步骤完成后续配置：

    1. 获取 ACK KubeConfig：
       aliyun cs GET /k8s/${alicloud_cs_managed_kubernetes.main.id}/user_config > kubeconfig.yaml
       # 将 kubeconfig.yaml 内容配置到 GitHub Secret: ACK_KUBECONFIG

    2. 配置 GitHub Repository Secrets：
       terraform output -raw cicd_access_key_id    → ALIYUN_ACCESS_KEY_ID
       terraform output -raw cicd_access_key_secret → ALIYUN_ACCESS_KEY_SECRET
       terraform output rds_connection_string       → 用于创建 K8s Secret

    3. 创建 K8s Namespace 和 Secret：
       kubectl apply -f k8s/staging/namespace.yaml
       kubectl apply -f k8s/prod/namespace.yaml
       bash scripts/k8s-secrets/create-staging-secret.sh
       bash scripts/k8s-secrets/create-prod-secret.sh

    4. 配置 DNS 解析：
       staging CLB IP: ${alicloud_slb_load_balancer.staging.address}
       prod    CLB IP: ${alicloud_slb_load_balancer.prod.address}
  EOT
}
