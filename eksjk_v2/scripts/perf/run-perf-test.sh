#!/bin/bash
# ============================================================
# EKSJK V2 基础性能验证脚本
# ============================================================
# 使用 k6 对核心 API 接口进行基础性能验证。
# 
# 前置条件：
#   1. 安装 k6: brew install k6
#   2. EKSJK V2 后端已启动（默认 http://localhost:8080）
#   3. 已导入 Mock 数据（至少有测试账号和患者数据）
#
# 使用方式：
#   chmod +x run-perf-test.sh
#   ./run-perf-test.sh
#
# 输出：
#   - 控制台输出各接口的响应时间统计（avg/p95/p99）
#   - 生成 JSON 格式测试报告：perf-report.json
# ============================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
BASE_URL="${EKSJK_BASE_URL:-http://localhost:8080}"
REPORT_FILE="${SCRIPT_DIR}/perf-report.json"

echo "============================================"
echo "  EKSJK V2 基础性能验证"
echo "============================================"
echo "目标地址: ${BASE_URL}"
echo "报告文件: ${REPORT_FILE}"
echo ""

# 检查 k6 是否安装
if ! command -v k6 &> /dev/null; then
    echo "❌ 错误: k6 未安装"
    echo "   安装方式: brew install k6"
    echo "   或访问: https://k6.io/docs/get-started/installation/"
    exit 1
fi

# 检查后端是否可达
echo "🔍 检查后端服务是否可达..."
if ! curl -s -o /dev/null -w "%{http_code}" "${BASE_URL}/actuator/health" | grep -q "200"; then
    echo "❌ 错误: 后端服务不可达 (${BASE_URL})"
    echo "   请确保 EKSJK V2 后端已启动"
    exit 1
fi
echo "✅ 后端服务可达"
echo ""

# 运行 k6 性能测试
echo "🚀 开始性能测试..."
echo ""

k6 run \
    --env BASE_URL="${BASE_URL}" \
    --out json="${REPORT_FILE}" \
    "${SCRIPT_DIR}/k6-perf-test.js"

echo ""
echo "============================================"
echo "  性能测试完成"
echo "============================================"
echo "📊 详细报告已保存至: ${REPORT_FILE}"
echo ""
echo "💡 提示: 测试期间请同时观察 Grafana 大盘："
echo "   - HTTP 错误率是否 < 1%"
echo "   - HikariCP 连接池是否有等待（pending = 0）"
echo "   - JVM 堆内存是否有持续增长趋势"
