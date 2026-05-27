<template>
  <div class="dashboard">
    <PageHeader title="工作台" />

    <!-- 数据概览卡片 -->
    <el-row :gutter="16" class="dashboard__cards">
      <el-col :span="6" v-for="card in statCards" :key="card.title">
        <el-card shadow="hover" class="dashboard__stat-card" @click="card.onClick?.()">
          <div class="dashboard__stat-card-content">
            <div class="dashboard__stat-info">
              <span class="dashboard__stat-label">{{ card.title }}</span>
              <span class="dashboard__stat-value" :style="{ color: card.color }">{{ card.value }}</span>
            </div>
            <el-icon class="dashboard__stat-icon" :style="{ color: card.color, backgroundColor: card.bgColor }">
              <component :is="card.icon" />
            </el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 疾病分布 + 用户角色分布 -->
    <el-row :gutter="16">
      <el-col :span="14">
        <el-card class="dashboard__section">
          <template #header>
            <span class="font-semibold">疾病类型分布</span>
          </template>
          <div class="dashboard__disease-grid">
            <div v-for="item in diseaseDistribution" :key="item.name" class="dashboard__disease-item">
              <div class="dashboard__disease-bar" :style="{ width: item.percent + '%', backgroundColor: item.color }" />
              <span class="dashboard__disease-name">{{ item.name }}</span>
              <span class="dashboard__disease-count">{{ item.count }}</span>
            </div>
            <el-empty v-if="!diseaseDistribution.length" description="暂无数据" :image-size="48" />
          </div>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card class="dashboard__section">
          <template #header>
            <span class="font-semibold">用户角色分布</span>
          </template>
          <div class="dashboard__role-list">
            <div v-for="item in userRoleDistribution" :key="item.name" class="dashboard__role-item">
              <span class="dashboard__role-name">{{ item.name }}</span>
              <el-tag :type="item.tagType" size="small" effect="plain">{{ item.count }}</el-tag>
            </div>
            <el-empty v-if="!userRoleDistribution.length" description="暂无数据" :image-size="48" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 + 系统公告 -->
    <el-row :gutter="16">
      <el-col :span="12">
        <el-card class="dashboard__section">
          <template #header>
            <span class="font-semibold">快捷操作</span>
          </template>
          <div class="dashboard__quick-actions">
            <el-button type="primary" plain @click="$router.push('/case/dsd')">
              <el-icon><Plus /></el-icon>
              新建病例
            </el-button>
            <el-button plain @click="$router.push('/school')">
              <el-icon><School /></el-icon>
              健康筛查
            </el-button>
            <el-button plain @click="$router.push('/statistics')">
              <el-icon><TrendCharts /></el-icon>
              统计分析
            </el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="dashboard__section">
          <template #header>
            <div class="flex-between">
              <span class="font-semibold">系统公告</span>
              <el-link type="primary" :underline="false" @click="$router.push('/notice')">查看全部</el-link>
            </div>
          </template>
          <div class="dashboard__notices">
            <el-empty v-if="!notices.length" description="暂无公告" :image-size="60" />
            <div v-else v-for="notice in notices" :key="notice.id" class="dashboard__notice-item">
              <span class="dashboard__notice-title text-ellipsis">{{ notice.title }}</span>
              <span class="dashboard__notice-time text-secondary">{{ notice.time }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import PageHeader from '@/components/PageHeader.vue'
import { getDashboardSummary } from '@/api/dashboard'

const router = useRouter()

// 统计数据（后续对接 API）
const statCards = ref([
  {
    title: '病例总数',
    value: '--',
    icon: 'Document',
    color: '#409eff',
    bgColor: '#ecf5ff',
    onClick: () => router.push('/case/dsd')
  },
  {
    title: '本月新增',
    value: '--',
    icon: 'Plus',
    color: '#67c23a',
    bgColor: '#f0f9eb',
    onClick: null
  },
  {
    title: '待随访',
    value: '--',
    icon: 'Clock',
    color: '#e6a23c',
    bgColor: '#fdf6ec',
    onClick: null
  },
  {
    title: '注册用户',
    value: '--',
    icon: 'UserFilled',
    color: '#909399',
    bgColor: '#f4f4f5',
    onClick: () => router.push('/user')
  }
])

// 疾病分布
const DISEASE_COLORS = [
  '#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#b37feb', '#36cfc9'
]
const diseaseDistribution = ref([])

// 用户角色分布
const ROLE_TAG_TYPES = {
  '超级管理员': 'danger',
  '医院管理员': 'warning',
  '普通医生': 'primary',
  '家长': 'success'
}
const userRoleDistribution = ref([])

// 公告列表（后续对接 API）
const notices = ref([])

onMounted(async () => {
  try {
    const response = await getDashboardSummary()
    const data = response.data || {}

    statCards.value[0].value = data.totalCases ?? '--'
    statCards.value[1].value = data.monthlyNewCases ?? '--'
    statCards.value[2].value = data.pendingFollowups ?? '--'
    statCards.value[3].value = data.totalUsers ?? '--'

    // 处理疾病分布
    if (data.diseaseDistribution) {
      const entries = Object.entries(data.diseaseDistribution)
      const maxCount = Math.max(...entries.map(([, c]) => c), 1)
      diseaseDistribution.value = entries.map(([name, count], idx) => ({
        name,
        count,
        percent: Math.round((count / maxCount) * 100),
        color: DISEASE_COLORS[idx % DISEASE_COLORS.length]
      }))
    }

    // 处理用户角色分布
    if (data.userRoleDistribution) {
      userRoleDistribution.value = Object.entries(data.userRoleDistribution).map(([name, count]) => ({
        name,
        count,
        tagType: ROLE_TAG_TYPES[name] || 'info'
      }))
    }

    if (data.notices) {
      notices.value = data.notices
    }
  } catch (error) {
    console.error('获取仪表板数据失败:', error)
  }
})
</script>

<style scoped>
.dashboard__cards {
  margin-bottom: var(--ek-spacing-base);
}

.dashboard__stat-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.dashboard__stat-card:hover {
  transform: translateY(-2px);
}

.dashboard__stat-card-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.dashboard__stat-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.dashboard__stat-label {
  font-size: 14px;
  color: var(--ek-text-secondary);
}

.dashboard__stat-value {
  font-size: 28px;
  font-weight: 700;
  line-height: 1;
}

.dashboard__stat-icon {
  font-size: 28px;
  width: 56px;
  height: 56px;
  border-radius: var(--ek-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
}

.dashboard__section {
  margin-bottom: var(--ek-spacing-base);
}

.dashboard__quick-actions {
  display: flex;
  gap: var(--ek-spacing-sm);
  flex-wrap: wrap;
}

.dashboard__notices {
  max-height: 200px;
  overflow-y: auto;
}

.dashboard__notice-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid var(--ek-border-color-lighter);
}

.dashboard__notice-item:last-child {
  border-bottom: none;
}

.dashboard__notice-title {
  flex: 1;
  font-size: 14px;
  color: var(--ek-text-primary);
  margin-right: 16px;
}

.dashboard__notice-time {
  font-size: 12px;
  flex-shrink: 0;
}

.dashboard__disease-grid {
  min-height: 160px;
}

.dashboard__disease-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  gap: 8px;
}

.dashboard__disease-item:last-child {
  margin-bottom: 0;
}

.dashboard__disease-bar {
  height: 8px;
  border-radius: 4px;
  min-width: 4px;
  transition: width 0.6s ease;
}

.dashboard__disease-name {
  flex: 1;
  font-size: 13px;
  color: var(--ek-text-primary);
  white-space: nowrap;
}

.dashboard__disease-count {
  font-size: 13px;
  font-weight: 600;
  color: var(--ek-text-secondary);
  min-width: 32px;
  text-align: right;
}

.dashboard__role-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-height: 160px;
}

.dashboard__role-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dashboard__role-name {
  font-size: 14px;
  color: var(--ek-text-primary);
}
</style>