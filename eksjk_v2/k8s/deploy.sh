#!/bin/bash
# ============================================================
# EKSJK V2 一键部署脚本
# 功能：编译构建 + 镜像打包 + K8S 部署
# 用法：./deploy.sh [--skip-build] [--only-monitoring]
# ============================================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
BACKEND_DIR="$PROJECT_DIR/eksjk-backend"
FRONTEND_DIR="$PROJECT_DIR/eksjk-frontend"

# 参数解析
SKIP_BUILD=false
ONLY_MONITORING=false
for arg in "$@"; do
    case $arg in
        --skip-build) SKIP_BUILD=true ;;
        --only-monitoring) ONLY_MONITORING=true ;;
    esac
done

log_info()  { echo -e "${BLUE}[INFO]${NC} $1"; }
log_ok()    { echo -e "${GREEN}[OK]${NC} $1"; }
log_warn()  { echo -e "${YELLOW}[WARN]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# ==================== 步骤 1：检查前置依赖 ====================
log_info "步骤 1/6：检查前置依赖..."

check_cmd() {
    if ! command -v "$1" &> /dev/null; then
        log_error "$1 未安装，请先安装后重试"
        exit 1
    fi
    log_ok "$1 已安装: $(command -v "$1")"
}

check_cmd kubectl

# 检查容器构建工具（优先 nerdctl，其次 docker）
BUILD_CMD=""
if command -v nerdctl &> /dev/null; then
    BUILD_CMD="nerdctl"
elif command -v docker &> /dev/null; then
    BUILD_CMD="docker"
else
    log_error "未找到 nerdctl 或 docker，请安装 Rancher Desktop 或 Docker Desktop"
    exit 1
fi
log_ok "容器构建工具: $BUILD_CMD"

if [ "$SKIP_BUILD" = false ]; then
    check_cmd mvn
    check_cmd npm
fi

# ==================== 步骤 2：检查 K8S 集群 ====================
log_info "步骤 2/6：检查 K8S 集群连接..."
if ! kubectl cluster-info &> /dev/null; then
    log_error "无法连接 K8S 集群，请确认 Rancher Desktop 已启动且 Kubernetes 已启用"
    exit 1
fi
log_ok "K8S 集群连接正常"

# ==================== 步骤 3：构建镜像 ====================
if [ "$SKIP_BUILD" = false ] && [ "$ONLY_MONITORING" = false ]; then
    log_info "步骤 3/6：构建后端 Maven 项目..."
    (cd "$BACKEND_DIR" && mvn clean package -DskipTests -q)
    log_ok "后端 Maven 构建完成"

    log_info "构建后端 Docker 镜像..."
    (cd "$BACKEND_DIR" && $BUILD_CMD build -t eksjk-backend:latest .)
    log_ok "后端镜像构建完成: eksjk-backend:latest"

    log_info "构建前端项目..."
    (cd "$FRONTEND_DIR" && npm install --silent && npm run build)
    log_ok "前端构建完成"

    log_info "构建前端 Docker 镜像..."
    (cd "$FRONTEND_DIR" && $BUILD_CMD build -t eksjk-frontend:latest .)
    log_ok "前端镜像构建完成: eksjk-frontend:latest"
else
    log_warn "跳过镜像构建步骤"
fi

# ==================== 步骤 4：应用 K8S 资源清单 ====================
log_info "步骤 4/6：应用 K8S 资源清单..."

# 创建命名空间
kubectl apply -f "$SCRIPT_DIR/namespace.yaml"
log_ok "命名空间 eksjk 已创建"

if [ "$ONLY_MONITORING" = false ]; then
    # 部署 MySQL
    log_info "部署 MySQL..."
    kubectl apply -f "$SCRIPT_DIR/mysql/"
    log_ok "MySQL 资源已应用"

    # 部署 MinIO（对象存储，后端依赖）
    log_info "部署 MinIO..."
    kubectl apply -f "$SCRIPT_DIR/minio/"
    log_ok "MinIO 资源已应用"

    # 部署后端
    log_info "部署后端..."
    kubectl apply -f "$SCRIPT_DIR/backend/"
    log_ok "后端资源已应用"

    # 部署前端
    log_info "部署前端..."
    kubectl apply -f "$SCRIPT_DIR/frontend/"
    log_ok "前端资源已应用"
fi

# 部署监控
log_info "部署监控组件..."
kubectl apply -f "$SCRIPT_DIR/monitoring/"
log_ok "监控资源已应用"

# ==================== 步骤 5：等待 Pod 就绪 ====================
log_info "步骤 5/6：等待所有 Pod 就绪（超时 180 秒）..."

if [ "$ONLY_MONITORING" = false ]; then
    # 等待 MySQL
    log_info "等待 MySQL 就绪..."
    kubectl wait --for=condition=ready pod -l app=eksjk-mysql -n eksjk --timeout=120s 2>/dev/null || log_warn "MySQL Pod 尚未就绪，请稍后检查"

    # 等待 MinIO（后端启动会连接 MinIO，必须先就绪）
    log_info "等待 MinIO 就绪..."
    kubectl wait --for=condition=ready pod -l app=eksjk-minio -n eksjk --timeout=120s 2>/dev/null || log_warn "MinIO Pod 尚未就绪，请稍后检查"

    # 确保默认桶 eksjk-files 存在（幂等，已存在则忽略）
    # 官方 minio/minio 镜像不支持 MINIO_DEFAULT_BUCKETS 环境变量自动建桶，需要用 mc 手动创建
    log_info "确保 MinIO 桶 eksjk-files 存在..."
    if kubectl exec -n eksjk deploy/eksjk-minio -- sh -c '
        mc alias set local http://127.0.0.1:9000 "$MINIO_ROOT_USER" "$MINIO_ROOT_PASSWORD" >/dev/null 2>&1
        mc mb --ignore-existing local/eksjk-files
    ' >/dev/null 2>&1; then
        log_ok "MinIO 桶 eksjk-files 已就绪"
    else
        log_warn "MinIO 桶创建失败，文件上传可能不可用，请手动执行: kubectl exec -n eksjk deploy/eksjk-minio -- mc mb --ignore-existing local/eksjk-files"
    fi

    # ---------- Mock 数据自动初始化（方案 B） ----------
    # MySQL 就绪后，把 scripts/mock/sql/ 打成 ConfigMap，触发 Job 把测试账号/数据写入 MySQL
    # 所有 SQL 均使用 INSERT IGNORE，幂等可重复执行
    MOCK_SQL_DIR="$PROJECT_DIR/scripts/mock/sql"
    MOCK_K8S_DIR="$SCRIPT_DIR/mock-data"
    if [ -d "$MOCK_SQL_DIR" ] && [ -d "$MOCK_K8S_DIR" ]; then
        log_info "导入 Mock 测试数据（自动）..."

        # 1) 生成/更新 ConfigMap eksjk-mock-sql（包含 scripts/mock/sql/ 下所有 .sql 文件）
        kubectl create configmap eksjk-mock-sql -n eksjk \
            --from-file="$MOCK_SQL_DIR" \
            --dry-run=client -o yaml | kubectl apply -f - >/dev/null
        log_ok "ConfigMap eksjk-mock-sql 已更新（来源: $MOCK_SQL_DIR）"

        # 2) 删除旧 Job（如果存在），确保本次 apply 触发重新执行
        kubectl delete job -n eksjk eksjk-mock-data-init --ignore-not-found=true >/dev/null 2>&1 || true

        # 3) apply Job
        kubectl apply -f "$MOCK_K8S_DIR/job.yaml" >/dev/null
        log_ok "Mock 数据初始化 Job 已提交"

        # 4) 等待 Job 完成（最多 120 秒）
        if kubectl wait --for=condition=complete job/eksjk-mock-data-init -n eksjk --timeout=120s 2>/dev/null; then
            log_ok "Mock 数据导入完成（7 个测试账号已写入）"
        else
            log_warn "Mock 数据 Job 未在 120s 内完成，请查看日志: kubectl logs -n eksjk -l app=eksjk-mock-data"
        fi
    else
        log_warn "未找到 $MOCK_SQL_DIR 或 $MOCK_K8S_DIR，跳过 Mock 数据初始化"
    fi
    # ---------- Mock 数据自动初始化 END ----------

    # 等待后端
    log_info "等待后端就绪..."
    kubectl wait --for=condition=ready pod -l app=eksjk-backend -n eksjk --timeout=180s 2>/dev/null || log_warn "后端 Pod 尚未就绪，请稍后检查"

    # 等待前端
    log_info "等待前端就绪..."
    kubectl wait --for=condition=ready pod -l app=eksjk-frontend -n eksjk --timeout=60s 2>/dev/null || log_warn "前端 Pod 尚未就绪，请稍后检查"
fi

# 等待监控
log_info "等待监控组件就绪..."
kubectl wait --for=condition=ready pod -l app=eksjk-prometheus -n eksjk --timeout=60s 2>/dev/null || log_warn "Prometheus Pod 尚未就绪"
kubectl wait --for=condition=ready pod -l app=eksjk-grafana -n eksjk --timeout=60s 2>/dev/null || log_warn "Grafana Pod 尚未就绪"

# ==================== 步骤 6：输出访问信息 ====================
log_info "步骤 6/6：部署完成！"
echo ""
echo "============================================================"
echo -e "${GREEN}  EKSJK V2 部署成功！${NC}"
echo "============================================================"
echo ""
echo "  📋 Pod 状态："
kubectl get pods -n eksjk -o wide
echo ""
echo "  🌐 访问地址："
echo "  ├── 前端页面:    http://localhost:30080"
echo "  ├── 后端 API:    http://localhost:30080/api/"
echo "  ├── MinIO API:   http://localhost:30900"
echo "  ├── MinIO 控制台: http://localhost:30901  (minioadmin / minioadmin)"
echo "  ├── Grafana:     通过 Ingress: http://localhost/grafana/"
echo "  └── Prometheus:  kubectl port-forward -n eksjk svc/eksjk-prometheus 9090:9090"
echo ""
echo "  🔑 默认账号："
echo "  ├── 应用登录 (Test@1234):"
echo "  │   ├── super_admin           (超级管理员)"
echo "  │   ├── hospital_admin_1/2    (医院管理员)"
echo "  │   ├── doctor_1/2/3          (普通医生)"
echo "  │   └── parent_1              (家长 / 小程序)"
echo "  ├── Grafana:   admin / admin"
echo "  └── MySQL:     root / root  |  eksjk / eksjk123"
echo ""
echo "  💡 常用命令："
echo "  ├── 查看日志:  kubectl logs -f -n eksjk deployment/eksjk-backend"
echo "  ├── 进入容器:  kubectl exec -it -n eksjk deployment/eksjk-backend -- bash"
echo "  └── 清理环境:  ./teardown.sh"
echo "============================================================"
