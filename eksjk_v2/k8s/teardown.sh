#!/bin/bash
# ============================================================
# EKSJK V2 一键清理脚本
# 功能：清理 K8S 命名空间下的所有资源
# 用法：./teardown.sh [--with-data]
# ============================================================

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

WITH_DATA=false
for arg in "$@"; do
    case $arg in
        --with-data) WITH_DATA=true ;;
    esac
done

echo -e "${YELLOW}========================================${NC}"
echo -e "${YELLOW}  EKSJK V2 环境清理${NC}"
echo -e "${YELLOW}========================================${NC}"
echo ""

# 显示当前资源
echo -e "${YELLOW}将要删除的资源：${NC}"
kubectl get all -n eksjk 2>/dev/null || echo "  (命名空间不存在或无资源)"
echo ""

if [ "$WITH_DATA" = true ]; then
    echo -e "${RED}⚠️  --with-data 模式：将同时删除 PVC 数据卷（MySQL 数据将丢失！）${NC}"
    echo ""
fi

# 确认操作
read -p "确认删除以上资源？(yes/no): " confirm
if [ "$confirm" != "yes" ]; then
    echo "已取消操作"
    exit 0
fi

echo ""
echo -e "正在清理资源..."

# 删除 Deployment、Service、Ingress 等
kubectl delete deployments,statefulsets,services,ingress,configmaps,secrets --all -n eksjk 2>/dev/null || true

if [ "$WITH_DATA" = true ]; then
    # 同时删除 PVC
    echo -e "删除 PVC 数据卷..."
    kubectl delete pvc --all -n eksjk 2>/dev/null || true
fi

# 删除命名空间
echo -e "删除命名空间 eksjk..."
kubectl delete namespace eksjk 2>/dev/null || true

echo ""
echo -e "${GREEN}✅ 清理完成！${NC}"
if [ "$WITH_DATA" = false ]; then
    echo -e "${YELLOW}💡 PVC 数据卷已保留。如需彻底清理，请使用: ./teardown.sh --with-data${NC}"
fi
