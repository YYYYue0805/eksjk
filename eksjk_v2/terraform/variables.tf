# ============================================================
# 基础配置
# ============================================================

variable "region" {
  description = "阿里云地域，默认华东2（上海）"
  type        = string
  default     = "cn-shanghai"
}

variable "access_key" {
  description = "阿里云 AccessKey ID（通过 terraform.tfvars 或环境变量 TF_VAR_access_key 传入）"
  type        = string
  sensitive   = true
}

variable "secret_key" {
  description = "阿里云 AccessKey Secret（通过 terraform.tfvars 或环境变量 TF_VAR_secret_key 传入）"
  type        = string
  sensitive   = true
}

variable "availability_zone" {
  description = "可用区，默认华东2可用区B"
  type        = string
  default     = "cn-shanghai-b"
}

# ============================================================
# 网络配置
# ============================================================

variable "vpc_cidr" {
  description = "VPC CIDR 地址段"
  type        = string
  default     = "172.16.0.0/12"
}

variable "vswitch_cidr" {
  description = "交换机 CIDR 地址段"
  type        = string
  default     = "172.16.0.0/24"
}

# ============================================================
# ACK 集群配置
# ============================================================

variable "k8s_version" {
  description = "Kubernetes 版本"
  type        = string
  default     = "1.34.3-aliyun.1"
}

variable "worker_instance_type" {
  description = "ACK Worker 节点规格"
  type        = string
  default     = "ecs.c6.xlarge"
}

variable "worker_disk_size" {
  description = "Worker 节点系统盘大小（GB）"
  type        = number
  default     = 40
}

# ============================================================
# RDS 配置
# ============================================================

variable "rds_instance_type" {
  description = "RDS 实例规格（Serverless 使用 mysql.n2.serverless.1c）"
  type        = string
  default     = "mysql.n2.serverless.1c"
}

variable "rds_storage_size" {
  description = "RDS 存储大小（GB），Serverless 最小 20GB"
  type        = number
  default     = 20
}

variable "rds_zone_id" {
  description = "RDS 所在可用区（上海 Serverless 仅 cn-shanghai-m 支持）"
  type        = string
  default     = "cn-shanghai-m"
}

variable "rds_vswitch_cidr" {
  description = "RDS 专用 vswitch CIDR（位于 rds_zone_id 可用区）"
  type        = string
  default     = "172.16.1.0/24"
}

variable "rds_serverless_min_capacity" {
  description = "Serverless 最小 RCU 数，MySQL 范围 0.5~8"
  type        = number
  default     = 0.5
}

variable "rds_serverless_max_capacity" {
  description = "Serverless 最大 RCU 数，MySQL 范围 1~8"
  type        = number
  default     = 4
}

variable "rds_staging_password" {
  description = "RDS staging 数据库账号密码（通过 terraform.tfvars 传入）"
  type        = string
  sensitive   = true
}

variable "rds_prod_password" {
  description = "RDS prod 数据库账号密码（通过 terraform.tfvars 传入）"
  type        = string
  sensitive   = true
}

# ============================================================
# 项目标识
# ============================================================

variable "project_name" {
  description = "项目名称，用于资源命名前缀"
  type        = string
  default     = "eksjk"
}
