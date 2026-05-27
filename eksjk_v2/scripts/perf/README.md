# EKSJK V2 基础性能验证

## 概述

使用 [k6](https://k6.io/) 对 EKSJK V2 后端核心 API 进行基础性能验证，确认重构后无明显性能退化。

## 前置条件

1. **安装 k6**：`brew install k6`
2. **启动后端服务**：确保 EKSJK V2 后端运行在 `http://localhost:8080`
3. **导入 Mock 数据**：至少有测试账号（`super_admin` / `Test@1234`）和患者数据

## 快速开始

```bash
# 赋予执行权限
chmod +x run-perf-test.sh

# 运行性能测试（默认连接 localhost:8080）
./run-perf-test.sh

# 指定目标地址
EKSJK_BASE_URL=http://192.168.1.100:8080 ./run-perf-test.sh
```

## 测试场景

| 阶段 | 时长 | 并发用户数 | 说明 |
|------|------|-----------|------|
| 预热 | 10s | 0 → 10 | 逐步增加到 10 个并发用户 |
| 稳定 | 40s | 10 | 保持 10 个并发用户持续请求 |
| 冷却 | 10s | 10 → 0 | 逐步降低并发用户数 |

## 覆盖的 API 接口

| 接口 | 方法 | 路径 | P95 阈值 |
|------|------|------|----------|
| 登录 | POST | `/api/auth/login` | < 1000ms |
| 患者列表查询 | GET | `/api/patients?pageNum=1&pageSize=10` | < 500ms |
| 按疾病类型筛选 | GET | `/api/patients?disClass=xxx` | < 500ms |
| 工作台统计 | GET | `/api/patients/dashboard/stats` | — |
| 获取用户信息 | GET | `/api/auth/info` | — |
| 登出 | POST | `/api/auth/logout` | — |

## 性能指标阈值

| 指标 | 阈值 | 说明 |
|------|------|------|
| 患者列表查询 P95 | < 500ms | 含分页查询 |
| 病例详情查询 P95 | < 300ms | 单条记录查询 |
| 随访记录查询 P95 | < 300ms | 按患者 ID 查询 |
| HTTP 错误率 | < 1% | 全局错误率 |
| 全局 P95 | < 500ms | 所有请求的 P95 |

## 输出报告

测试完成后会生成以下文件：

- **控制台输出**：各接口的 avg/P95/P99 响应时间
- **`perf-summary.json`**：结构化的测试报告（JSON 格式）
- **`perf-report.json`**：k6 原始指标数据

## 配合 Grafana 观察

测试期间建议同时观察 Grafana 大盘中的以下指标：

- **HTTP 错误率** — 应 < 1%
- **HikariCP 连接池** — pending 应为 0
- **JVM 堆内存** — 不应有持续增长趋势（排除内存泄漏）
- **HTTP P95 响应时间** — 应在阈值范围内
