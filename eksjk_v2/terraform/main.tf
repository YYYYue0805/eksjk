# ============================================================
# 网络基础设施：VPC / 交换机 / 安全组
# ============================================================

# VPC
resource "alicloud_vpc" "main" {
  vpc_name   = "${var.project_name}-vpc"
  cidr_block = var.vpc_cidr

  tags = {
    Project     = var.project_name
    ManagedBy   = "terraform"
  }
}

# 交换机（vSwitch）- ACK/ECS 使用
resource "alicloud_vswitch" "main" {
  vswitch_name = "${var.project_name}-vswitch"
  vpc_id       = alicloud_vpc.main.id
  cidr_block   = var.vswitch_cidr
  zone_id      = var.availability_zone

  tags = {
    Project   = var.project_name
    ManagedBy = "terraform"
  }
}

# RDS 专用交换机（Serverless MySQL 仅 cn-shanghai-m 可用）
resource "alicloud_vswitch" "rds" {
  vswitch_name = "${var.project_name}-rds-vswitch"
  vpc_id       = alicloud_vpc.main.id
  cidr_block   = var.rds_vswitch_cidr
  zone_id      = var.rds_zone_id

  tags = {
    Project   = var.project_name
    ManagedBy = "terraform"
    Purpose   = "rds-serverless"
  }
}

# ACK 节点安全组：仅开放 80/443 及集群内部通信端口
resource "alicloud_security_group" "ack_nodes" {
  security_group_name = "${var.project_name}-ack-sg"
  description         = "ACK 节点安全组，仅开放必要端口"
  vpc_id              = alicloud_vpc.main.id

  tags = {
    Project   = var.project_name
    ManagedBy = "terraform"
  }
}

resource "alicloud_security_group_rule" "ack_http" {
  type              = "ingress"
  ip_protocol       = "tcp"
  nic_type          = "intranet"
  policy            = "accept"
  port_range        = "80/80"
  priority          = 1
  security_group_id = alicloud_security_group.ack_nodes.id
  cidr_ip           = "0.0.0.0/0"
  description       = "HTTP 入站（CLB 健康检查 + 用户访问）"
}

resource "alicloud_security_group_rule" "ack_https" {
  type              = "ingress"
  ip_protocol       = "tcp"
  nic_type          = "intranet"
  policy            = "accept"
  port_range        = "443/443"
  priority          = 1
  security_group_id = alicloud_security_group.ack_nodes.id
  cidr_ip           = "0.0.0.0/0"
  description       = "HTTPS 入站"
}

resource "alicloud_security_group_rule" "ack_internal" {
  type              = "ingress"
  ip_protocol       = "all"
  nic_type          = "intranet"
  policy            = "accept"
  port_range        = "-1/-1"
  priority          = 2
  security_group_id = alicloud_security_group.ack_nodes.id
  cidr_ip           = var.vpc_cidr
  description       = "VPC 内部通信（集群节点间通信）"
}

# ACK 托管版 API Server 位于阿里云托管网络，其 IP 可能不在用户 VPC CIDR 内
# 需要放通所有来源的 6443 入站，确保 Worker 节点能连接 API Server 完成注册
resource "alicloud_security_group_rule" "ack_apiserver" {
  type              = "ingress"
  ip_protocol       = "tcp"
  nic_type          = "intranet"
  policy            = "accept"
  port_range        = "6443/6443"
  priority          = 1
  security_group_id = alicloud_security_group.ack_nodes.id
  cidr_ip           = "0.0.0.0/0"
  description       = "API Server 6443 入站（ACK 托管版 API Server 注册节点）"
}

# 出方向：允许节点访问外部服务（APIServer、ACR、OSS、SLS、NTP、DNS 等）
# 说明：Terraform 创建的安全组默认无出方向规则，会阻断所有出向流量，必须显式放通
resource "alicloud_security_group_rule" "ack_egress_all" {
  type              = "egress"
  ip_protocol       = "all"
  nic_type          = "intranet"
  policy            = "accept"
  port_range        = "-1/-1"
  priority          = 1
  security_group_id = alicloud_security_group.ack_nodes.id
  cidr_ip           = "0.0.0.0/0"
  description       = "允许节点访问外部（APIServer/ACR/OSS/SLS 等）"
}

# RDS 安全组：仅允许 ACK 节点访问 3306
resource "alicloud_security_group" "rds" {
  security_group_name = "${var.project_name}-rds-sg"
  description         = "RDS 安全组，仅允许 ACK 节点访问 3306"
  vpc_id              = alicloud_vpc.main.id

  tags = {
    Project   = var.project_name
    ManagedBy = "terraform"
  }
}

resource "alicloud_security_group_rule" "rds_from_ack" {
  type                     = "ingress"
  ip_protocol              = "tcp"
  nic_type                 = "intranet"
  policy                   = "accept"
  port_range               = "3306/3306"
  priority                 = 1
  security_group_id        = alicloud_security_group.rds.id
  source_security_group_id = alicloud_security_group.ack_nodes.id
  description              = "仅允许 ACK 节点访问 MySQL 3306"
}

# RDS 安全组出方向：放通，避免后续联动问题（RDS 服务端本身不主动外连，此规则仅为保险）
resource "alicloud_security_group_rule" "rds_egress_all" {
  type              = "egress"
  ip_protocol       = "all"
  nic_type          = "intranet"
  policy            = "accept"
  port_range        = "-1/-1"
  priority          = 1
  security_group_id = alicloud_security_group.rds.id
  cidr_ip           = "0.0.0.0/0"
  description       = "出方向放通"
}

# ============================================================
# ACK 托管版集群
# ============================================================

resource "alicloud_cs_managed_kubernetes" "main" {
  name                         = "${var.project_name}-cluster"
  cluster_spec                 = "ack.standard"
  version                      = var.k8s_version
  new_nat_gateway              = true
  vswitch_ids                  = [alicloud_vswitch.main.id]
  pod_cidr                     = "10.0.0.0/16"
  service_cidr                 = "10.96.0.0/16"
  slb_internet_enabled         = true
  deletion_protection          = false

  # 开启日志服务（SLS）集成
  addons {
    name   = "logtail-ds"
    config = ""
  }

  # Ingress Controller
  addons {
    name   = "nginx-ingress-controller"
    config = ""
  }

  tags = {
    Project   = var.project_name
    ManagedBy = "terraform"
  }
}

# ACK 节点池（单节点，不配置自动伸缩）
resource "alicloud_cs_kubernetes_node_pool" "main" {
  cluster_id    = alicloud_cs_managed_kubernetes.main.id
  node_pool_name = "${var.project_name}-nodepool"
  vswitch_ids   = [alicloud_vswitch.main.id]

  # 节点规格：ecs.c6.xlarge 4C8G
  instance_types = [var.worker_instance_type]
  desired_size   = 1

  system_disk_category = "cloud_essd"
  system_disk_size     = var.worker_disk_size

  # 注意：不手动指定 security_group_ids，让 ACK 自动将节点加入集群安全组
  # ACK 托管版集群会自动创建安全组并配置 API Server ↔ Worker 之间的互通规则
  # 手动指定自定义安全组会导致节点无法连接 API Server 6443 端口

  # 不配置自动伸缩
  scaling_policy = "release"

  tags = {
    Project   = var.project_name
    ManagedBy = "terraform"
  }
}

# ============================================================
# RDS MySQL 8.0 实例
# ============================================================

resource "alicloud_db_instance" "main" {
  engine               = "MySQL"
  engine_version       = "8.0"
  instance_type        = var.rds_instance_type
  instance_storage     = var.rds_storage_size
  instance_name        = "${var.project_name}-rds"
  zone_id              = var.rds_zone_id
  vswitch_id           = alicloud_vswitch.rds.id
  security_group_ids   = [alicloud_security_group.rds.id]
  db_instance_storage_type = "cloud_essd"
  category             = "serverless_basic"

  # Serverless：按实际使用量计费，空闲时自动缩到 min_capacity RCU
  instance_charge_type = "Serverless"

  serverless_config {
    min_capacity = var.rds_serverless_min_capacity
    max_capacity = var.rds_serverless_max_capacity
    # auto_pause: Terraform 不支持设为 true（实例停机后资源检查会失败）
    auto_pause   = false
    # switch_force: false 即可满足普通弹性伸缩
    switch_force = false
  }

  tags = {
    Project   = var.project_name
    ManagedBy = "terraform"
  }
}

# Serverless 实例内置 7 天全量备份策略，无需额外配置
# alicloud_db_backup_policy 在 Serverless 下不支持自定义，已移除

# 创建 staging 数据库
resource "alicloud_db_database" "staging" {
  instance_id    = alicloud_db_instance.main.id
  data_base_name = "eksjk_staging"
  character_set  = "utf8mb4"
  description    = "EKSJK 测试环境数据库"
}

# 创建 prod 数据库
resource "alicloud_db_database" "prod" {
  instance_id    = alicloud_db_instance.main.id
  data_base_name = "eksjk_prod"
  character_set  = "utf8mb4"
  description    = "EKSJK 生产环境数据库"
}

# staging 数据库账号（仅有 eksjk_staging 权限）
resource "alicloud_rds_account" "staging" {
  db_instance_id   = alicloud_db_instance.main.id
  account_name     = "eksjk_staging_user"
  account_password = var.rds_staging_password
  account_type     = "Normal"
  account_description = "EKSJK staging 环境专用账号"
}

resource "alicloud_db_account_privilege" "staging" {
  instance_id  = alicloud_db_instance.main.id
  account_name = alicloud_rds_account.staging.account_name
  privilege    = "ReadWrite"
  db_names     = [alicloud_db_database.staging.data_base_name]
}

# prod 数据库账号（仅有 eksjk_prod 权限）
resource "alicloud_rds_account" "prod" {
  db_instance_id   = alicloud_db_instance.main.id
  account_name     = "eksjk_prod_user"
  account_password = var.rds_prod_password
  account_type     = "Normal"
  account_description = "EKSJK prod 环境专用账号"
}

resource "alicloud_db_account_privilege" "prod" {
  instance_id  = alicloud_db_instance.main.id
  account_name = alicloud_rds_account.prod.account_name
  privilege    = "ReadWrite"
  db_names     = [alicloud_db_database.prod.data_base_name]
}

# ============================================================
# OSS 对象存储 Bucket
# ============================================================

# Terraform 状态存储 Bucket（已手动创建，此处通过 data source 引用，不由 terraform 管理生命周期）
data "alicloud_oss_buckets" "terraform_state" {
  name_regex = "^eksjk-terraform-state$"
}

# staging Bucket（数据永久保存）
resource "alicloud_oss_bucket" "staging" {
  bucket = "${var.project_name}-staging"

  tags = {
    Project     = var.project_name
    ManagedBy   = "terraform"
    Environment = "staging"
  }
}

resource "alicloud_oss_bucket_acl" "staging" {
  bucket = alicloud_oss_bucket.staging.bucket
  acl    = "private"
}

# prod Bucket（开启版本控制，防止文件被意外覆盖）
resource "alicloud_oss_bucket" "prod" {
  bucket = "${var.project_name}-prod"

  versioning {
    status = "Enabled"
  }

  tags = {
    Project     = var.project_name
    ManagedBy   = "terraform"
    Environment = "prod"
  }
}

resource "alicloud_oss_bucket_acl" "prod" {
  bucket = alicloud_oss_bucket.prod.bucket
  acl    = "private"
}

# ============================================================
# CLB 公网负载均衡（staging / prod 各自独立）
# ============================================================

# staging CLB
resource "alicloud_slb_load_balancer" "staging" {
  load_balancer_name = "${var.project_name}-clb-staging"
  address_type       = "internet"
  load_balancer_spec = "slb.s1.small"
  internet_charge_type = "PayByTraffic"

  tags = {
    Project     = var.project_name
    ManagedBy   = "terraform"
    Environment = "staging"
  }
}

# prod CLB
resource "alicloud_slb_load_balancer" "prod" {
  load_balancer_name = "${var.project_name}-clb-prod"
  address_type       = "internet"
  load_balancer_spec = "slb.s1.small"
  internet_charge_type = "PayByTraffic"

  tags = {
    Project     = var.project_name
    ManagedBy   = "terraform"
    Environment = "prod"
  }
}

# ============================================================
# RAM 子账号（CI/CD 专用）
# ============================================================

# CI/CD 专用 RAM 用户
resource "alicloud_ram_user" "cicd" {
  name         = "${var.project_name}-cicd"
  display_name = "EKSJK CI/CD 专用账号"
  comments     = "供 GitHub Actions 流水线使用，不用于人工登录控制台"
  force        = true
}

# 授予 ACR 完整权限
resource "alicloud_ram_user_policy_attachment" "acr_full" {
  policy_name = "AliyunContainerRegistryFullAccess"
  policy_type = "System"
  user_name   = alicloud_ram_user.cicd.name
}

# 自定义 ACK 部署权限策略（限定到 eksjk-staging 和 eksjk-prod Namespace）
resource "alicloud_ram_policy" "ack_deploy" {
  policy_name     = "${var.project_name}-ack-deploy-policy"
  policy_document = jsonencode({
    Version = "1"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "cs:GetKubernetesTrigger",
          "cs:DescribeClusterDetail",
          "cs:DescribeClusterResources",
          "cs:DescribeClusterNodes"
        ]
        Resource = "*"
      }
    ]
  })
  description = "EKSJK CI/CD 专用 ACK 部署权限（通过 kubeconfig 操作，此策略用于 API 访问）"
  force       = true
}

resource "alicloud_ram_user_policy_attachment" "ack_deploy" {
  policy_name = alicloud_ram_policy.ack_deploy.policy_name
  policy_type = "Custom"
  user_name   = alicloud_ram_user.cicd.name
}

# 为 CI/CD 账号创建 AccessKey
resource "alicloud_ram_access_key" "cicd" {
  user_name   = alicloud_ram_user.cicd.name
  secret_file = "/dev/null"  # 不写入文件，通过 output 输出
  status      = "Active"
}
