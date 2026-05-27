#!/usr/bin/env bash
# =============================================================
# EKSJK V2 本地开发环境一键启动脚本
# 启动组件：MySQL 8.0 + MinIO（S3 兼容）
# 注意：本地环境不启动 Redis，与 staging/prod 保持一致的无 Redis 架构
# 使用方式：bash scripts/local-dev/start.sh
# =============================================================
set -e

# ---- 颜色输出 ----
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

info()  { echo -e "${GREEN}[INFO]${NC}  $*"; }
warn()  { echo -e "${YELLOW}[WARN]${NC}  $*"; }
error() { echo -e "${RED}[ERROR]${NC} $*"; exit 1; }

# ---- 配置 ----
MYSQL_CONTAINER="eksjk-mysql-local"
MINIO_CONTAINER="eksjk-minio-local"
MYSQL_PORT=3306
MINIO_PORT=9000
MINIO_CONSOLE_PORT=9001
MYSQL_ROOT_PASSWORD="root123"
MYSQL_DATABASE="eksjk_local"
MYSQL_USER="eksjk"
MYSQL_PASSWORD="eksjk123"
MINIO_ROOT_USER="minioadmin"
MINIO_ROOT_PASSWORD="minioadmin"
MINIO_BUCKET="eksjk-local"

# 脚本所在目录（兼容从任意目录执行）
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
INIT_SQL_DIR="${PROJECT_ROOT}/init-sql"

# ---- 端口冲突检测 ----
check_port() {
  local port=$1
  local name=$2
  if lsof -iTCP:"${port}" -sTCP:LISTEN -t >/dev/null 2>&1; then
    warn "端口 ${port}（${name}）已被占用！"
    warn "请先释放该端口，或修改脚本中的端口配置后重试。"
    warn "查看占用进程：lsof -iTCP:${port} -sTCP:LISTEN"
    error "端口冲突，启动中止。"
  fi
}

info "=========================================="
info "  EKSJK V2 本地开发环境启动"
info "=========================================="

# ---- 检查 Docker 是否运行 ----
if ! docker info >/dev/null 2>&1; then
  error "Docker 未运行，请先启动 Docker Desktop 后重试。"
fi

# ---- 检查端口冲突（仅在容器不存在时检查）----
if ! docker ps -q -f name="^${MYSQL_CONTAINER}$" | grep -q .; then
  check_port "${MYSQL_PORT}" "MySQL"
fi
if ! docker ps -q -f name="^${MINIO_CONTAINER}$" | grep -q .; then
  check_port "${MINIO_PORT}" "MinIO API"
  check_port "${MINIO_CONSOLE_PORT}" "MinIO Console"
fi

# ---- 启动 MySQL ----
if docker ps -q -f name="^${MYSQL_CONTAINER}$" | grep -q .; then
  info "MySQL 容器已在运行，跳过启动。"
elif docker ps -aq -f name="^${MYSQL_CONTAINER}$" | grep -q .; then
  info "启动已停止的 MySQL 容器..."
  docker start "${MYSQL_CONTAINER}"
else
  info "创建并启动 MySQL 8.0 容器..."
  docker run -d \
    --name "${MYSQL_CONTAINER}" \
    -e MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD}" \
    -e MYSQL_DATABASE="${MYSQL_DATABASE}" \
    -e MYSQL_USER="${MYSQL_USER}" \
    -e MYSQL_PASSWORD="${MYSQL_PASSWORD}" \
    -p "${MYSQL_PORT}:3306" \
    -v eksjk-mysql-local-data:/var/lib/mysql \
    --restart unless-stopped \
    mysql:8.0 \
    --character-set-server=utf8mb4 \
    --collation-server=utf8mb4_unicode_ci
fi

# ---- 启动 MinIO ----
if docker ps -q -f name="^${MINIO_CONTAINER}$" | grep -q .; then
  info "MinIO 容器已在运行，跳过启动。"
elif docker ps -aq -f name="^${MINIO_CONTAINER}$" | grep -q .; then
  info "启动已停止的 MinIO 容器..."
  docker start "${MINIO_CONTAINER}"
else
  info "创建并启动 MinIO 容器（S3 兼容存储）..."
  docker run -d \
    --name "${MINIO_CONTAINER}" \
    -e MINIO_ROOT_USER="${MINIO_ROOT_USER}" \
    -e MINIO_ROOT_PASSWORD="${MINIO_ROOT_PASSWORD}" \
    -p "${MINIO_PORT}:9000" \
    -p "${MINIO_CONSOLE_PORT}:9001" \
    -v eksjk-minio-local-data:/data \
    --restart unless-stopped \
    minio/minio server /data --console-address ":9001"
fi

# ---- 等待 MySQL 就绪 ----
info "等待 MySQL 就绪..."
MAX_RETRY=30
RETRY=0
until docker exec "${MYSQL_CONTAINER}" mysqladmin ping -u root -p"${MYSQL_ROOT_PASSWORD}" --silent 2>/dev/null; do
  RETRY=$((RETRY + 1))
  if [ "${RETRY}" -ge "${MAX_RETRY}" ]; then
    error "MySQL 启动超时（${MAX_RETRY}s），请检查容器日志：docker logs ${MYSQL_CONTAINER}"
  fi
  sleep 1
done
info "MySQL 已就绪 ✓"

# ---- 执行 init-sql 初始化 ----
if [ -d "${INIT_SQL_DIR}" ] && [ -n "$(ls -A "${INIT_SQL_DIR}"/*.sql 2>/dev/null)" ]; then
  info "执行数据库初始化 SQL..."
  for sql_file in "${INIT_SQL_DIR}"/*.sql; do
    info "  执行：$(basename "${sql_file}")"
    docker exec -i "${MYSQL_CONTAINER}" \
      mysql -u root -p"${MYSQL_ROOT_PASSWORD}" "${MYSQL_DATABASE}" \
      < "${sql_file}" 2>/dev/null || warn "  $(basename "${sql_file}") 执行时有警告（可能已初始化，忽略）"
  done
  info "数据库初始化完成 ✓"
else
  warn "未找到 init-sql/*.sql 文件，跳过数据库初始化。"
fi

# ---- 创建 MinIO Bucket ----
info "等待 MinIO 就绪并创建 Bucket..."
sleep 3
if docker exec "${MINIO_CONTAINER}" sh -c \
  "mc alias set local http://localhost:9000 ${MINIO_ROOT_USER} ${MINIO_ROOT_PASSWORD} 2>/dev/null && \
   mc mb --ignore-existing local/${MINIO_BUCKET} 2>/dev/null"; then
  info "MinIO Bucket '${MINIO_BUCKET}' 已就绪 ✓"
else
  warn "MinIO mc 命令不可用，请手动访问 http://localhost:${MINIO_CONSOLE_PORT} 创建 Bucket '${MINIO_BUCKET}'"
fi

# ---- 完成提示 ----
echo ""
info "=========================================="
info "  本地开发环境启动完成！"
info "=========================================="
echo ""
echo "  MySQL  : localhost:${MYSQL_PORT}  库名=${MYSQL_DATABASE}  用户=${MYSQL_USER}"
echo "  MinIO  : http://localhost:${MINIO_PORT}  (API)"
echo "  MinIO  : http://localhost:${MINIO_CONSOLE_PORT}  (Console 管理界面)"
echo "  Bucket : ${MINIO_BUCKET}"
echo ""
info "启动后端服务（在 eksjk-backend 目录下执行）："
echo "  mvn spring-boot:run -pl eksjk-web -Dspring-boot.run.profiles=local"
echo ""
info "停止本地环境："
echo "  bash scripts/local-dev/stop.sh"
echo ""
