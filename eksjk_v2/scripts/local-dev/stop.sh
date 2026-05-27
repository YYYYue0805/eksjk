#!/usr/bin/env bash
# =============================================================
# EKSJK V2 本地开发环境停止脚本
# 停止组件：MySQL 8.0 + MinIO（S3 兼容）
# 使用方式：bash scripts/local-dev/stop.sh
# =============================================================
set -e

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

info() { echo -e "${GREEN}[INFO]${NC}  $*"; }
warn() { echo -e "${YELLOW}[WARN]${NC}  $*"; }

MYSQL_CONTAINER="eksjk-mysql-local"
MINIO_CONTAINER="eksjk-minio-local"

info "停止本地开发环境容器..."

# 停止 MySQL
if docker ps -q -f name="^${MYSQL_CONTAINER}$" | grep -q .; then
  docker stop "${MYSQL_CONTAINER}"
  info "MySQL 容器已停止 ✓"
else
  warn "MySQL 容器未在运行，跳过。"
fi

# 停止 MinIO
if docker ps -q -f name="^${MINIO_CONTAINER}$" | grep -q .; then
  docker stop "${MINIO_CONTAINER}"
  info "MinIO 容器已停止 ✓"
else
  warn "MinIO 容器未在运行，跳过。"
fi

info "本地开发环境已停止。"
info "数据已保留在 Docker Volume 中，下次执行 start.sh 可直接恢复。"
echo ""
info "如需彻底清除数据（删除 Volume）："
echo "  docker volume rm eksjk-mysql-local-data eksjk-minio-local-data"
