#!/bin/bash
# ============================================================
# EKSJK 项目 K8s 一键部署脚本
# 适用于本地 K8s 集群（Rancher Desktop / Docker Desktop / Minikube / Kind）
#
# 使用方法:
#   chmod +x deploy.sh
#   ./deploy.sh          # 完整部署
#   ./deploy.sh build    # 仅构建镜像
#   ./deploy.sh apply    # 仅应用 K8s 清单
#   ./deploy.sh delete   # 删除所有资源
#   ./deploy.sh status   # 查看部署状态
#   ./deploy.sh logs     # 查看所有 Pod 日志
# ============================================================

set -e

# ==================== 颜色定义 ====================
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# ==================== 项目配置 ====================
PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"
FRONTEND_DIR="${PROJECT_ROOT}/ek-frontend"
BACKEND_DIR="${PROJECT_ROOT}/eksjk"
K8S_DIR="${PROJECT_ROOT}/k8s"

# 镜像名称
FRONTEND_IMAGE="eksjk-frontend"
BACKEND_IMAGE="eksjk-backend"
IMAGE_TAG="latest"

# K8s 命名空间
NAMESPACE="eksjk"

# ==================== 工具函数 ====================
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_step() {
    echo -e "\n${CYAN}========================================${NC}"
    echo -e "${CYAN}  $1${NC}"
    echo -e "${CYAN}========================================${NC}\n"
}

# 检查命令是否存在
check_command() {
    if ! command -v "$1" &> /dev/null; then
        log_error "$1 未安装，请先安装 $1"
        exit 1
    fi
}

# ==================== 环境检查 ====================
check_environment() {
    log_step "步骤 1/5: 检查运行环境"

    # 检查必要工具
    check_command "kubectl"
    log_info "kubectl 已安装 ✓"

    # 检测容器构建工具（优先使用 nerdctl，其次 docker）
    if command -v nerdctl &> /dev/null; then
        CTR_CMD="nerdctl"
        # 检查 nerdctl 是否可用
        if nerdctl info &> /dev/null 2>&1; then
            log_info "检测到 nerdctl (Rancher Desktop containerd 模式) ✓"
        elif command -v docker &> /dev/null && docker info &> /dev/null 2>&1; then
            CTR_CMD="docker"
            log_info "nerdctl 不可用，回退到 docker ✓"
        else
            log_error "容器运行时未启动，请先启动 Rancher Desktop"
            exit 1
        fi
    elif command -v docker &> /dev/null; then
        CTR_CMD="docker"
        if ! docker info &> /dev/null 2>&1; then
            log_error "Docker 未运行，请先启动 Rancher Desktop 或 Docker"
            exit 1
        fi
        log_info "检测到 docker ✓"
    else
        log_error "未找到 docker 或 nerdctl，请安装 Rancher Desktop"
        exit 1
    fi
    log_info "容器构建工具: ${CTR_CMD}"

    # 检查 K8s 集群连接
    if ! kubectl cluster-info &> /dev/null; then
        log_error "无法连接到 K8s 集群，请确保 K8s 集群已启动"
        echo ""
        echo "  如果使用 Rancher Desktop: 请在 Preferences > Kubernetes 中启用"
        echo "  如果使用 Docker Desktop:  请在设置中启用 Kubernetes"
        echo "  如果使用 Minikube:        运行 'minikube start'"
        echo "  如果使用 Kind:            运行 'kind create cluster'"
        exit 1
    fi
    log_info "K8s 集群已连接 ✓"

    # 检测 K8s 环境类型
    CURRENT_CONTEXT=$(kubectl config current-context 2>/dev/null || echo "unknown")
    log_info "当前 K8s 上下文: ${CURRENT_CONTEXT}"

    # 检测 Rancher Desktop 环境
    if echo "$CURRENT_CONTEXT" | grep -qi "rancher-desktop"; then
        log_info "检测到 Rancher Desktop 环境 ✓"
        IS_RANCHER_DESKTOP=true
    else
        IS_RANCHER_DESKTOP=false
    fi

    # 检查项目目录
    if [ ! -d "$FRONTEND_DIR" ]; then
        log_error "前端目录不存在: $FRONTEND_DIR"
        exit 1
    fi
    if [ ! -d "$BACKEND_DIR" ]; then
        log_error "后端目录不存在: $BACKEND_DIR"
        exit 1
    fi
    log_info "项目目录检查通过 ✓"
}

# ==================== 构建 Docker 镜像 ====================
build_images() {
    log_step "步骤 2/5: 构建 Docker 镜像"

    # 检测是否为 Minikube 环境，如果是则使用 Minikube 的 Docker daemon
    if echo "$CURRENT_CONTEXT" | grep -qi "minikube"; then
        log_info "检测到 Minikube 环境，切换到 Minikube Docker daemon..."
        eval $(minikube docker-env)
    fi

    # Rancher Desktop 使用 dockerd(moby) 模式时，构建的镜像自动对 K8s 可用
    # 使用 containerd 模式时，需要用 nerdctl --namespace k8s.io 构建
    local build_cmd="${CTR_CMD}"
    local build_ns_flag=""
    if [ "$IS_RANCHER_DESKTOP" = true ] && [ "$CTR_CMD" = "nerdctl" ]; then
        build_ns_flag="--namespace k8s.io"
        log_info "Rancher Desktop containerd 模式：使用 nerdctl --namespace k8s.io 构建"
    fi

    # 构建后端镜像
    log_info "正在构建后端镜像: ${BACKEND_IMAGE}:${IMAGE_TAG} ..."
    ${build_cmd} ${build_ns_flag} build \
        -t "${BACKEND_IMAGE}:${IMAGE_TAG}" \
        -f "${BACKEND_DIR}/Dockerfile" \
        "${BACKEND_DIR}"
    log_info "后端镜像构建完成 ✓"

    # 构建前端镜像
    log_info "正在构建前端镜像: ${FRONTEND_IMAGE}:${IMAGE_TAG} ..."
    ${build_cmd} ${build_ns_flag} build \
        -t "${FRONTEND_IMAGE}:${IMAGE_TAG}" \
        -f "${FRONTEND_DIR}/Dockerfile" \
        "${FRONTEND_DIR}"
    log_info "前端镜像构建完成 ✓"

    # 如果是 Kind 环境，需要将镜像加载到集群中
    if echo "$CURRENT_CONTEXT" | grep -qi "kind"; then
        log_info "检测到 Kind 环境，正在加载镜像到集群..."
        kind load docker-image "${BACKEND_IMAGE}:${IMAGE_TAG}"
        kind load docker-image "${FRONTEND_IMAGE}:${IMAGE_TAG}"
        log_info "镜像已加载到 Kind 集群 ✓"
    fi

    # 显示构建结果
    echo ""
    log_info "已构建的镜像:"
    ${build_cmd} ${build_ns_flag} images | grep -E "(eksjk-frontend|eksjk-backend)" | head -5
}

# ==================== 应用 K8s 清单 ====================
apply_k8s_manifests() {
    log_step "步骤 3/5: 部署到 K8s 集群"

    # 1. 创建命名空间和配置
    log_info "创建命名空间和配置..."
    kubectl apply -f "${K8S_DIR}/configmap.yaml"
    log_info "命名空间和配置已创建 ✓"

    # 2. 部署 MySQL
    log_info "部署 MySQL 数据库..."
    kubectl apply -f "${K8S_DIR}/mysql.yaml"
    log_info "MySQL 部署已提交 ✓"

    # 3. 等待 MySQL 就绪
    log_info "等待 MySQL 就绪（最长等待 120 秒）..."
    if kubectl wait --for=condition=ready pod -l app=eksjk-mysql -n "${NAMESPACE}" --timeout=120s 2>/dev/null; then
        log_info "MySQL 已就绪 ✓"
    else
        log_warn "MySQL 可能尚未完全就绪，继续部署..."
    fi

    # 4. 部署后端
    log_info "部署 Django 后端..."
    kubectl apply -f "${K8S_DIR}/backend.yaml"
    log_info "后端部署已提交 ✓"

    # 5. 部署前端
    log_info "部署 Vue.js 前端..."
    kubectl apply -f "${K8S_DIR}/frontend.yaml"
    log_info "前端部署已提交 ✓"
}

# ==================== 等待部署完成 ====================
wait_for_deployment() {
    log_step "步骤 4/5: 等待所有服务就绪"

    local timeout=300
    local components=("eksjk-mysql" "eksjk-backend" "eksjk-frontend")

    for component in "${components[@]}"; do
        log_info "等待 ${component} 就绪..."
        if kubectl rollout status deployment/"${component}" -n "${NAMESPACE}" --timeout="${timeout}s" 2>/dev/null; then
            log_info "${component} 已就绪 ✓"
        else
            log_warn "${component} 可能尚未完全就绪，请稍后检查"
        fi
    done
}

# ==================== 显示部署结果 ====================
show_result() {
    log_step "步骤 5/5: 部署完成"

    echo -e "${GREEN}所有服务已部署！${NC}\n"

    # 显示 Pod 状态
    echo -e "${BLUE}Pod 状态:${NC}"
    kubectl get pods -n "${NAMESPACE}" -o wide
    echo ""

    # 显示 Service 状态
    echo -e "${BLUE}Service 状态:${NC}"
    kubectl get svc -n "${NAMESPACE}"
    echo ""

    # 获取访问地址
    local node_ip="localhost"

    # 尝试获取 Minikube IP
    if echo "$CURRENT_CONTEXT" | grep -qi "minikube"; then
        node_ip=$(minikube ip 2>/dev/null || echo "localhost")
    fi

    echo -e "${CYAN}============================================${NC}"
    echo -e "${CYAN}  🎉 部署成功！访问地址:${NC}"
    echo -e "${CYAN}============================================${NC}"
    echo -e ""
    echo -e "  🌐 前端页面:  ${GREEN}http://${node_ip}:30180${NC}"
    echo -e "  🔧 后端 API:  通过前端 Nginx 反向代理访问"
    echo -e "  🗄️  MySQL:    集群内部 eksjk-mysql:3306"
    echo -e ""
    echo -e "${YELLOW}提示:${NC}"
    echo -e "  - 如果使用 Rancher Desktop，直接访问 http://localhost:30180"
    echo -e "  - 如果使用 Docker Desktop，直接访问 http://localhost:30180"
    echo -e "  - 如果使用 Minikube，访问 http://$(minikube ip 2>/dev/null || echo '<minikube-ip>'):30180"
    echo -e "  - 查看日志: ./deploy.sh logs"
    echo -e "  - 查看状态: ./deploy.sh status"
    echo -e "  - 删除部署: ./deploy.sh delete"
    echo ""
}

# ==================== 查看状态 ====================
show_status() {
    log_step "EKSJK 部署状态"

    echo -e "${BLUE}命名空间:${NC}"
    kubectl get ns "${NAMESPACE}" 2>/dev/null || echo "  命名空间 ${NAMESPACE} 不存在"
    echo ""

    echo -e "${BLUE}Pod 状态:${NC}"
    kubectl get pods -n "${NAMESPACE}" -o wide 2>/dev/null || echo "  无 Pod"
    echo ""

    echo -e "${BLUE}Service 状态:${NC}"
    kubectl get svc -n "${NAMESPACE}" 2>/dev/null || echo "  无 Service"
    echo ""

    echo -e "${BLUE}Deployment 状态:${NC}"
    kubectl get deployments -n "${NAMESPACE}" 2>/dev/null || echo "  无 Deployment"
    echo ""

    echo -e "${BLUE}PVC 状态:${NC}"
    kubectl get pvc -n "${NAMESPACE}" 2>/dev/null || echo "  无 PVC"
    echo ""
}

# ==================== 查看日志 ====================
show_logs() {
    echo -e "${BLUE}选择要查看的服务日志:${NC}"
    echo "  1) MySQL"
    echo "  2) 后端 (Django)"
    echo "  3) 前端 (Nginx)"
    echo "  4) 全部"
    read -p "请输入选项 [1-4]: " choice

    case $choice in
        1)
            kubectl logs -l app=eksjk-mysql -n "${NAMESPACE}" --tail=100
            ;;
        2)
            kubectl logs -l app=eksjk-backend -n "${NAMESPACE}" --tail=100 --all-containers
            ;;
        3)
            kubectl logs -l app=eksjk-frontend -n "${NAMESPACE}" --tail=100
            ;;
        4)
            echo -e "\n${CYAN}=== MySQL 日志 ===${NC}"
            kubectl logs -l app=eksjk-mysql -n "${NAMESPACE}" --tail=30 2>/dev/null
            echo -e "\n${CYAN}=== 后端日志 ===${NC}"
            kubectl logs -l app=eksjk-backend -n "${NAMESPACE}" --tail=30 --all-containers 2>/dev/null
            echo -e "\n${CYAN}=== 前端日志 ===${NC}"
            kubectl logs -l app=eksjk-frontend -n "${NAMESPACE}" --tail=30 2>/dev/null
            ;;
        *)
            log_error "无效选项"
            ;;
    esac
}

# ==================== 删除所有资源 ====================
delete_all() {
    log_step "删除 EKSJK 所有 K8s 资源"

    read -p "确认删除所有 EKSJK K8s 资源？(y/N): " confirm
    if [[ "$confirm" != "y" && "$confirm" != "Y" ]]; then
        log_info "已取消删除操作"
        exit 0
    fi

    log_info "删除前端部署..."
    kubectl delete -f "${K8S_DIR}/frontend.yaml" --ignore-not-found 2>/dev/null || true

    log_info "删除后端部署..."
    kubectl delete -f "${K8S_DIR}/backend.yaml" --ignore-not-found 2>/dev/null || true

    log_info "删除 MySQL 部署..."
    kubectl delete -f "${K8S_DIR}/mysql.yaml" --ignore-not-found 2>/dev/null || true

    log_info "删除配置..."
    kubectl delete -f "${K8S_DIR}/configmap.yaml" --ignore-not-found 2>/dev/null || true

    log_info "所有资源已删除 ✓"
    echo ""
    log_warn "注意: PVC 数据卷可能需要手动清理"
    echo "  kubectl delete pvc --all -n ${NAMESPACE}"
    echo "  kubectl delete ns ${NAMESPACE}"
}

# ==================== 重启服务 ====================
restart_service() {
    log_info "重启所有服务..."
    kubectl rollout restart deployment/eksjk-backend -n "${NAMESPACE}" 2>/dev/null || true
    kubectl rollout restart deployment/eksjk-frontend -n "${NAMESPACE}" 2>/dev/null || true
    log_info "重启命令已发送，请稍候查看状态"
    sleep 3
    show_status
}

# ==================== 主流程 ====================
main() {
    echo -e "${CYAN}"
    echo "  ╔══════════════════════════════════════════╗"
    echo "  ║     EKSJK 项目 K8s 一键部署工具         ║"
    echo "  ║     儿科数据管理系统                     ║"
    echo "  ╚══════════════════════════════════════════╝"
    echo -e "${NC}"

    case "${1:-deploy}" in
        deploy)
            check_environment
            build_images
            apply_k8s_manifests
            wait_for_deployment
            show_result
            ;;
        build)
            check_environment
            build_images
            ;;
        apply)
            check_environment
            apply_k8s_manifests
            wait_for_deployment
            show_result
            ;;
        delete)
            delete_all
            ;;
        status)
            show_status
            ;;
        logs)
            show_logs
            ;;
        restart)
            restart_service
            ;;
        *)
            echo "用法: $0 {deploy|build|apply|delete|status|logs|restart}"
            echo ""
            echo "  deploy   - 完整部署（构建镜像 + 部署到 K8s）"
            echo "  build    - 仅构建 Docker 镜像"
            echo "  apply    - 仅应用 K8s 清单（需要已构建镜像）"
            echo "  delete   - 删除所有 K8s 资源"
            echo "  status   - 查看部署状态"
            echo "  logs     - 查看服务日志"
            echo "  restart  - 重启所有服务"
            exit 1
            ;;
    esac
}

main "$@"
