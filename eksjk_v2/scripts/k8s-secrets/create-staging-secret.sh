#!/usr/bin/env bash
# =============================================================================
# 脚本名称: create-staging-secret.sh
# 用途: 创建/更新 eksjk-staging 命名空间的 Kubernetes Secret
#       包含 RDS 连接信息、OSS 配置、JWT 签名密钥
# 使用方式:
#   1. 复制 .env.staging.example 为 .env.staging 并填写真实值
#   2. 执行: bash create-staging-secret.sh
# 注意: .env.staging 文件包含敏感信息，已加入 .gitignore，禁止提交到 Git
# =============================================================================
set -e

NAMESPACE="eksjk-staging"
SECRET_NAME="eksjk-staging-secret"
ENV_FILE="$(dirname "$0")/.env.staging"

# ---- 颜色输出 ----
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}[INFO] 开始创建/更新 Staging K8s Secret: ${SECRET_NAME}${NC}"

# ---- 检查 .env.staging 文件 ----
if [ ! -f "$ENV_FILE" ]; then
  echo -e "${RED}[ERROR] 未找到配置文件: ${ENV_FILE}${NC}"
  echo -e "${YELLOW}[HINT]  请复制示例文件并填写真实值:${NC}"
  echo -e "        cp $(dirname "$0")/.env.staging.example ${ENV_FILE}"
  exit 1
fi

# 加载环境变量
# shellcheck disable=SC1090
source "$ENV_FILE"

# ---- 校验必填字段 ----
REQUIRED_VARS=(
  "RDS_HOST" "RDS_PORT" "RDS_USER" "RDS_PASSWORD" "RDS_DATABASE"
  "OSS_ENDPOINT" "OSS_BUCKET" "OSS_ACCESS_KEY" "OSS_SECRET_KEY" "OSS_REGION"
  "JWT_SECRET"
)
for var in "${REQUIRED_VARS[@]}"; do
  if [ -z "${!var}" ]; then
    echo -e "${RED}[ERROR] 必填变量未设置: ${var}${NC}"
    exit 1
  fi
done

# ---- 确保 Namespace 存在 ----
kubectl get namespace "$NAMESPACE" > /dev/null 2>&1 || {
  echo -e "${YELLOW}[INFO] Namespace ${NAMESPACE} 不存在，正在创建...${NC}"
  kubectl apply -f "$(dirname "$0")/../../k8s/staging/namespace.yaml"
}

# ---- 创建/更新 Secret（幂等）----
echo -e "${YELLOW}[INFO] 正在应用 Secret...${NC}"
kubectl create secret generic "$SECRET_NAME" \
  --namespace="$NAMESPACE" \
  --from-literal=SPRING_DATASOURCE_URL="jdbc:mysql://${RDS_HOST}:${RDS_PORT}/${RDS_DATABASE}?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=Asia/Shanghai" \
  --from-literal=SPRING_DATASOURCE_USERNAME="${RDS_USER}" \
  --from-literal=SPRING_DATASOURCE_PASSWORD="${RDS_PASSWORD}" \
  --from-literal=EKSJK_UPLOAD_S3_ENDPOINT="${OSS_ENDPOINT}" \
  --from-literal=EKSJK_UPLOAD_S3_BUCKET="${OSS_BUCKET}" \
  --from-literal=EKSJK_UPLOAD_S3_ACCESS_KEY="${OSS_ACCESS_KEY}" \
  --from-literal=EKSJK_UPLOAD_S3_SECRET_KEY="${OSS_SECRET_KEY}" \
  --from-literal=EKSJK_UPLOAD_S3_REGION="${OSS_REGION}" \
  --from-literal=EKSJK_JWT_SECRET="${JWT_SECRET}" \
  --dry-run=client -o yaml | kubectl apply -f -

echo -e "${GREEN}[SUCCESS] Secret ${SECRET_NAME} 已成功创建/更新！${NC}"
echo ""
echo -e "${YELLOW}[INFO] 当前 Secret 字段列表（值已隐藏）:${NC}"
kubectl get secret "$SECRET_NAME" -n "$NAMESPACE" -o jsonpath='{.data}' | \
  python3 -c "import sys,json; [print(f'  - {k}') for k in json.load(sys.stdin).keys()]"
