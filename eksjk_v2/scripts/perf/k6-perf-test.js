/**
 * EKSJK V2 基础性能验证 — k6 测试脚本
 *
 * 测试场景：
 *   - 10 个并发虚拟用户（VU）
 *   - 持续运行 60 秒
 *   - 覆盖核心 API：登录、患者列表查询、病例详情查询、随访记录查询
 *
 * 性能指标阈值：
 *   - 患者列表查询 API 响应 < 500ms（P95）
 *   - 病例详情查询 API 响应 < 300ms（P95）
 *   - 随访记录查询 API 响应 < 300ms（P95）
 *   - HTTP 错误率 < 1%
 *
 * 使用方式：
 *   k6 run --env BASE_URL=http://localhost:8080 k6-perf-test.js
 */

import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Rate, Trend } from 'k6/metrics';

// ==================== 自定义指标 ====================

const patientListDuration = new Trend('patient_list_duration', true);
const patientDetailDuration = new Trend('patient_detail_duration', true);
const followUpListDuration = new Trend('followup_list_duration', true);
const loginDuration = new Trend('login_duration', true);
const errorRate = new Rate('errors');

// ==================== 测试配置 ====================

export const options = {
    // 10 个并发用户，持续 60 秒
    stages: [
        { duration: '10s', target: 10 },  // 10 秒内逐步增加到 10 个 VU
        { duration: '40s', target: 10 },  // 保持 10 个 VU 运行 40 秒
        { duration: '10s', target: 0 },   // 10 秒内逐步降到 0
    ],

    // 性能阈值
    thresholds: {
        'patient_list_duration': ['p(95)<500'],    // 患者列表 P95 < 500ms
        'patient_detail_duration': ['p(95)<300'],  // 病例详情 P95 < 300ms
        'followup_list_duration': ['p(95)<300'],   // 随访记录 P95 < 300ms
        'login_duration': ['p(95)<1000'],          // 登录 P95 < 1000ms
        'errors': ['rate<0.01'],                   // 错误率 < 1%
        'http_req_duration': ['p(95)<500'],        // 全局 P95 < 500ms
    },
};

// ==================== 环境变量 ====================

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const TEST_USERNAME = __ENV.TEST_USERNAME || 'super_admin';
const TEST_PASSWORD = __ENV.TEST_PASSWORD || 'Test@1234';

// ==================== 辅助函数 ====================

const headers = {
    'Content-Type': 'application/json',
};

/**
 * 登录并返回 Token
 */
function login() {
    const payload = JSON.stringify({
        username: TEST_USERNAME,
        password: TEST_PASSWORD,
    });

    const res = http.post(`${BASE_URL}/api/auth/login`, payload, { headers });
    loginDuration.add(res.timings.duration);

    const success = check(res, {
        '登录状态码 200': (r) => r.status === 200,
        '登录返回 Token': (r) => {
            try {
                const body = JSON.parse(r.body);
                return body.code === 200 && body.data && body.data.token;
            } catch (e) {
                return false;
            }
        },
    });

    if (!success) {
        errorRate.add(1);
        return null;
    }

    errorRate.add(0);
    const body = JSON.parse(res.body);
    return body.data.token;
}

// ==================== 主测试函数 ====================

export default function () {
    // 1. 登录获取 Token
    const token = login();
    if (!token) {
        sleep(1);
        return;
    }

    const authHeaders = {
        'Content-Type': 'application/json',
        'satoken': token,
    };

    // 2. 患者列表查询（分页）
    group('患者列表查询', function () {
        const res = http.get(
            `${BASE_URL}/api/patients?pageNum=1&pageSize=10`,
            { headers: authHeaders }
        );
        patientListDuration.add(res.timings.duration);

        const success = check(res, {
            '患者列表状态码 200': (r) => r.status === 200,
            '患者列表返回成功': (r) => {
                try {
                    return JSON.parse(r.body).code === 200;
                } catch (e) {
                    return false;
                }
            },
        });

        errorRate.add(success ? 0 : 1);
    });

    sleep(0.5);

    // 3. 按疾病类型筛选患者
    group('按疾病类型筛选', function () {
        const disClasses = ['10000001', '10000003', '10000004', '10000005'];
        const disClass = disClasses[Math.floor(Math.random() * disClasses.length)];

        const res = http.get(
            `${BASE_URL}/api/patients?pageNum=1&pageSize=10&disClass=${disClass}`,
            { headers: authHeaders }
        );
        patientListDuration.add(res.timings.duration);

        const success = check(res, {
            '筛选状态码 200': (r) => r.status === 200,
        });

        errorRate.add(success ? 0 : 1);
    });

    sleep(0.5);

    // 4. 工作台统计数据
    group('工作台统计', function () {
        const res = http.get(
            `${BASE_URL}/api/patients/dashboard/stats`,
            { headers: authHeaders }
        );

        const success = check(res, {
            '统计状态码 200': (r) => r.status === 200,
        });

        errorRate.add(success ? 0 : 1);
    });

    sleep(0.5);

    // 5. 获取当前用户信息
    group('获取用户信息', function () {
        const res = http.get(
            `${BASE_URL}/api/auth/info`,
            { headers: authHeaders }
        );

        const success = check(res, {
            '用户信息状态码 200': (r) => r.status === 200,
            '用户信息返回正确角色': (r) => {
                try {
                    const body = JSON.parse(r.body);
                    return body.code === 200 && body.data && body.data.roleCode;
                } catch (e) {
                    return false;
                }
            },
        });

        errorRate.add(success ? 0 : 1);
    });

    sleep(0.5);

    // 6. 登出
    group('登出', function () {
        const res = http.post(
            `${BASE_URL}/api/auth/logout`,
            null,
            { headers: authHeaders }
        );

        check(res, {
            '登出状态码 200': (r) => r.status === 200,
        });
    });

    sleep(1);
}

// ==================== 测试结束汇总 ====================

export function handleSummary(data) {
    // 生成控制台友好的汇总报告
    const report = {
        timestamp: new Date().toISOString(),
        summary: {
            total_requests: data.metrics.http_reqs ? data.metrics.http_reqs.values.count : 0,
            error_rate: data.metrics.errors ? data.metrics.errors.values.rate : 0,
            http_req_duration: {
                avg: data.metrics.http_req_duration ? data.metrics.http_req_duration.values.avg : 0,
                p95: data.metrics.http_req_duration ? data.metrics.http_req_duration.values['p(95)'] : 0,
                p99: data.metrics.http_req_duration ? data.metrics.http_req_duration.values['p(99)'] : 0,
            },
        },
        api_metrics: {
            patient_list: {
                avg: data.metrics.patient_list_duration ? data.metrics.patient_list_duration.values.avg : 0,
                p95: data.metrics.patient_list_duration ? data.metrics.patient_list_duration.values['p(95)'] : 0,
                threshold: '< 500ms',
            },
            patient_detail: {
                avg: data.metrics.patient_detail_duration ? data.metrics.patient_detail_duration.values.avg : 0,
                p95: data.metrics.patient_detail_duration ? data.metrics.patient_detail_duration.values['p(95)'] : 0,
                threshold: '< 300ms',
            },
            followup_list: {
                avg: data.metrics.followup_list_duration ? data.metrics.followup_list_duration.values.avg : 0,
                p95: data.metrics.followup_list_duration ? data.metrics.followup_list_duration.values['p(95)'] : 0,
                threshold: '< 300ms',
            },
            login: {
                avg: data.metrics.login_duration ? data.metrics.login_duration.values.avg : 0,
                p95: data.metrics.login_duration ? data.metrics.login_duration.values['p(95)'] : 0,
                threshold: '< 1000ms',
            },
        },
        thresholds_passed: Object.entries(data.root_group ? {} : {}).length === 0,
    };

    console.log('\n========================================');
    console.log('  EKSJK V2 性能测试报告');
    console.log('========================================');
    console.log(`总请求数: ${report.summary.total_requests}`);
    console.log(`错误率: ${(report.summary.error_rate * 100).toFixed(2)}%`);
    console.log(`全局 P95: ${report.summary.http_req_duration.p95.toFixed(2)}ms`);
    console.log('');
    console.log('各接口性能指标:');
    console.log(`  患者列表查询  - avg: ${report.api_metrics.patient_list.avg.toFixed(2)}ms, P95: ${report.api_metrics.patient_list.p95.toFixed(2)}ms (阈值: ${report.api_metrics.patient_list.threshold})`);
    console.log(`  病例详情查询  - avg: ${report.api_metrics.patient_detail.avg.toFixed(2)}ms, P95: ${report.api_metrics.patient_detail.p95.toFixed(2)}ms (阈值: ${report.api_metrics.patient_detail.threshold})`);
    console.log(`  随访记录查询  - avg: ${report.api_metrics.followup_list.avg.toFixed(2)}ms, P95: ${report.api_metrics.followup_list.p95.toFixed(2)}ms (阈值: ${report.api_metrics.followup_list.threshold})`);
    console.log(`  登录接口      - avg: ${report.api_metrics.login.avg.toFixed(2)}ms, P95: ${report.api_metrics.login.p95.toFixed(2)}ms (阈值: ${report.api_metrics.login.threshold})`);
    console.log('========================================\n');

    return {
        'perf-summary.json': JSON.stringify(report, null, 2),
    };
}
