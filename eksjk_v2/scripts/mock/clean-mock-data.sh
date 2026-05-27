#!/bin/bash
# ============================================================
# EKSJK V2 Mock 数据清理脚本
# 功能：清除所有 Mock 数据，恢复空库状态
# 用法：./clean-mock-data.sh
# ============================================================

set -e

MYSQL_HOST="${MYSQL_HOST:-127.0.0.1}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
MYSQL_USER="${MYSQL_USER:-root}"
MYSQL_PASSWORD="${MYSQL_PASSWORD:-root}"
MYSQL_DATABASE="${MYSQL_DATABASE:-eksjk}"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}========================================${NC}"
echo -e "${YELLOW}  EKSJK V2 Mock 数据清理${NC}"
echo -e "${YELLOW}========================================${NC}"
echo ""
echo -e "${RED}⚠️  此操作将删除所有 Mock 数据！${NC}"
read -p "确认清理？(yes/no): " confirm
if [ "$confirm" != "yes" ]; then
    echo "已取消"
    exit 0
fi

# 按外键依赖逆序删除
CLEAN_SQL="
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM datamain_masfoll WHERE id > 0;
DELETE FROM datamain_patfoll WHERE id > 0;
DELETE FROM datamain_case WHERE id > 0;
DELETE FROM datamain_short WHERE id > 0;
DELETE FROM datamain_sexprecocity WHERE id > 0;
DELETE FROM datamain_mas WHERE id > 0;
DELETE FROM datamain_sga WHERE id > 0;
DELETE FROM datamain_jzxshort WHERE id > 0;
DELETE FROM datamain_szfyeltm WHERE id > 0;
DELETE FROM datamain_patient WHERE id > 0;
DELETE FROM login_user WHERE id > 0;
DELETE FROM login_unit WHERE id > 0;
SET FOREIGN_KEY_CHECKS = 1;
"

if ! command -v mysql &> /dev/null; then
    MYSQL_POD=$(kubectl get pods -n eksjk -l app=eksjk-mysql -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)
    if [ -z "$MYSQL_POD" ]; then
        echo -e "${RED}未找到 MySQL Pod${NC}"
        exit 1
    fi
    echo "$CLEAN_SQL" | kubectl exec -i -n eksjk "$MYSQL_POD" -- mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE"
else
    echo "$CLEAN_SQL" | mysql -h"$MYSQL_HOST" -P"$MYSQL_PORT" -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE"
fi

echo ""
echo -e "${GREEN}✅ Mock 数据已清理完成${NC}"
