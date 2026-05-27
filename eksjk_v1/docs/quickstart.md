# 快速开始

## 环境要求

- Docker Desktop 或 Rancher Desktop（已启用 Kubernetes）
- 或：Node.js 12+、Python 3.7+、MySQL 5.7+（手动部署）

## 一键部署

```bash
chmod +x deploy.sh
./deploy.sh          # 构建镜像 + 部署到 K8s
```

## 访问系统

| 服务 | 地址 |
|------|------|
| 前端页面 | http://localhost:30180 |
| 后端 API | 通过 Nginx 反向代理自动转发 |

## 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 超级管理员 | `admin` | `admin123` |
| 医生 | `doctor01` ~ `doctor10` | `doctor123` |
| 普通用户 | `user0001` ~ `user1000` | `user123` |