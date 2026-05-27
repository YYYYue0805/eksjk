#!/bin/bash
# ============================================================
# EKSJK V2 Mock 数据初始化脚本
# 功能：按顺序执行 SQL 文件，初始化测试数据
# 用法：./init-mock-data.sh
# ============================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SQL_DIR="$SCRIPT_DIR/sql"

# MySQL 连接参数（可通过环境变量覆盖）
MYSQL_HOST="${MYSQL_HOST:-127.0.0.1}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
MYSQL_USER="${MYSQL_USER:-root}"
MYSQL_PASSWORD="${MYSQL_PASSWORD:-root}"
MYSQL_DATABASE="${MYSQL_DATABASE:-eksjk}"

GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m'

log_info() { echo -e "${BLUE}[INFO]${NC} $1"; }
log_ok()   { echo -e "${GREEN}[OK]${NC} $1"; }
log_error(){ echo -e "${RED}[ERROR]${NC} $1"; }

echo "============================================================"
echo "  EKSJK V2 Mock 数据初始化"
echo "============================================================"
echo ""
log_info "MySQL 连接: ${MYSQL_USER}@${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DATABASE}"
echo ""

# 检查 mysql 客户端
if ! command -v mysql &> /dev/null; then
    # 尝试通过 K8S Pod 执行
    log_info "本地未安装 mysql 客户端，尝试通过 K8S Pod 执行..."
    MYSQL_POD=$(kubectl get pods -n eksjk -l app=eksjk-mysql -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)
    if [ -z "$MYSQL_POD" ]; then
        log_error "未找到 MySQL Pod，请确认 K8S 环境已部署"
        exit 1
    fi
    log_info "使用 MySQL Pod: $MYSQL_POD"
    
    for sql_file in "$SQL_DIR"/*.sql; do
        filename=$(basename "$sql_file")
        log_info "执行: $filename"
        kubectl exec -n eksjk "$MYSQL_POD" -- mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE" < "$sql_file"
        log_ok "$filename 执行完成"
    done
else
    # 使用本地 mysql 客户端
    for sql_file in "$SQL_DIR"/*.sql; do
        filename=$(basename "$sql_file")
        log_info "执行: $filename"
        mysql -h"$MYSQL_HOST" -P"$MYSQL_PORT" -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE" < "$sql_file"
        log_ok "$filename 执行完成"
    done
fi

echo ""
echo "============================================================"
echo -e "${GREEN}  ✅ Mock 数据初始化完成！${NC}"
echo "============================================================"
echo ""
echo "  测试账号："
echo "  ├── super_admin    / Test@1234  (超级管理员)"
echo "  ├── hospital_admin_1 / Test@1234  (医院A管理员)"
echo "  ├── hospital_admin_2 / Test@1234  (医院B管理员)"
echo "  ├── doctor_1       / Test@1234  (张医生-医院A)"
echo "  ├── doctor_2       / Test@1234  (李医生-医院A)"
echo "  ├── doctor_3       / Test@1234  (王医生-医院B)"
echo "  └── parent_1       / Test@1234  (测试家长)"
echo ""
echo "  数据统计："
echo "  ├── 医院: 2 家"
echo "  ├── 患者: 21 名 (7种疾病×3名)"
echo "  ├── 病例: 21 条"
echo "  └── 随访: 21+ 条"
echo "============================================================"
