# EKSJK — 儿科生长发育数据管理系统

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Vue.js](https://img.shields.io/badge/Vue.js-3.x-green.svg)](https://vuejs.org/)
[![Django](https://img.shields.io/badge/Django-3.x-092E20.svg)](https://www.djangoproject.com/)

> 专业的儿科生长发育数据管理平台，支持多种疾病类型的数据采集、随访管理和统计分析

## 📖 文档导航

完整文档已拆分为多个专题文档，请按需查阅：

- **[📋 项目概述](docs/overview.md)** - 系统角色、核心能力、技术特色
- **[🚀 快速开始](docs/quickstart.md)** - 环境要求、一键部署、访问系统
- **[👥 用户使用说明](docs/user-guide.md)** - 医生、管理员、家长使用指南（12个章节）
- **[🏗️ 系统架构](docs/architecture.md)** - 技术栈、模块设计、数据库ER图
- **[🚢 部署指南](docs/deployment.md)** - 开发/生产环境部署、Kubernetes配置
- **[📚 附录](docs/appendix.md)** - 常见问题、开发规范、更新日志

## ✨ 核心特性

- **多角色支持**: 医生、管理员、家长三端协同
- **疾病全覆盖**: 支持矮小症、性早熟、肥胖症等多种儿科疾病
- **智能随访**: 自动化随访提醒和进度跟踪
- **微信小程序**: 家长端便捷访问和数据录入
- **统计分析**: 多维度数据分析和报表生成
- **学校筛查**: 批量数据处理

## 🏗️ 技术栈

### 前端
- Vue.js 3 + Element Plus + TypeScript
- ECharts 数据可视化
- 微信小程序原生开发

### 后端  
- Django 3 + Django REST Framework
- MySQL 数据库
- Redis 缓存和会话管理

### 部署
- Docker + Docker Compose
- Kubernetes (生产环境)
- Nginx 反向代理

## 🚀 快速体验

```bash
# 一键启动开发环境
docker-compose up -d

# 访问系统
前端: http://localhost:8080
后端: http://localhost:8000
管理员: admin/admin123
医生: doctor/doctor123
```

详细部署步骤请参阅 [快速开始指南](docs/quickstart.md)

## 📊 系统概览

| 模块 | 功能描述 | 技术实现 |
|------|----------|----------|
| 用户认证 | 多角色登录、权限控制 | Django Auth + JWT |
| 病例管理 | 患者信息、诊断记录 | Vue + DRF REST API |
| 随访系统 | 自动化随访提醒 | Celery 定时任务 |
| 统计分析 | 多维度数据报表 | ECharts 可视化 |
| 微信小程序 | 家长端数据录入 | 微信小程序原生 |
| 学校筛查 | 批量数据处理 | Excel 导入导出 |

## 🤝 参与贡献

欢迎提交 Issue 和 Pull Request！请参阅：[贡献指南](docs/appendix.md#贡献指南)

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

## 🆘 获取帮助

- 📖 查看完整文档: [docs 目录](docs/)
- 🐛 报告问题: [GitHub Issues](https://github.com/junedo/ekdms/issues)
- 💬 讨论交流: 通过 GitHub Discussions

---

*最后更新: 2024年* | *版本: v1.0.0*