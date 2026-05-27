terraform {
  required_version = ">= 1.5.0"

  required_providers {
    alicloud = {
      source  = "aliyun/alicloud"
      version = "~> 1.220"
    }
  }

  # 使用阿里云 OSS 作为 Remote Backend 存储 terraform.tfstate
  # 首次使用前需先手动创建 eksjk-terraform-state Bucket
  backend "oss" {
    bucket   = "eksjk-terraform-state"
    prefix   = "eksjk/terraform.tfstate"
    region   = "cn-shanghai"
    # access_key 和 secret_key 通过环境变量 ALICLOUD_ACCESS_KEY / ALICLOUD_SECRET_KEY 传入
  }
}

provider "alicloud" {
  region     = var.region
  access_key = var.access_key
  secret_key = var.secret_key
}
