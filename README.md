# EKSJK v2 - 儿童健康管理系统

## 项目概述
EKSJK v2 是一个综合性的儿童健康管理系统，包含Web管理后台、医生端小程序和家长端小程序，为儿童健康评估、病例管理和随访提供完整的解决方案。

## 重构思路与过程

### 主要重构内容
1. **技术栈升级与优化**
   - 优化项目结构和依赖管理

2. **功能模块完善**
   - 实现仪表板数据API调用和可视化展示
   - 新增完整的患者管理模块（CRUD操作）
   - 构建随访管理系统，支持多类型随访记录
   - 开发学校管理功能，支持问卷评估

3. **架构优化**
   - 后端采用分层架构：controller → service → mapper → model
   - 前端采用Vue3 + Element Plus现代化技术栈
   - 前后端分离，RESTful API设计

4. **部署与运维**
   - 添加Docker容器化部署配置
   - 配置Nginx反向代理和静态资源服务
   - 支持多环境部署（开发、测试、生产）

### 技术特色
- **多端协同**：Web管理后台 + 医生小程序 + 家长小程序
- **模块化设计**：功能模块清晰分离，便于维护和扩展
- **现代化技术栈**：Spring Boot + Vue3 + Docker
- **标准化接口**：RESTful API设计，前后端完全分离

## 项目结构
```
eksjk_v2/
├── eksjk-backend/     # Spring Boot后端服务
├── eksjk-frontend/    # Vue3管理后台
├── eksjk-miniapp/     # 家长端小程序
├── eksjk-miniapp-doctor/ # 医生端小程序
└── docker-compose.yml # Docker部署配置
```

## 快速开始
1. 后端启动：`cd eksjk-backend && mvn spring-boot:run`
2. 前端启动：`cd eksjk-frontend && npm run dev`
3. Docker部署：`docker-compose up -d`

## 主要功能
- ✅ 患者信息管理
- ✅ 病例记录与随访
- ✅ 健康评估问卷
- ✅ 学校管理
- ✅ 数据统计与分析
- ✅ 多端用户系统