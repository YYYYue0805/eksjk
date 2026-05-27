<template>
  <view class="patient-page">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <input class="search-input" v-model="keyword" placeholder="搜索姓名/病例编号/病历号"
             confirm-type="search" @confirm="onSearch" />
    </view>

    <!-- 疾病类型筛选标签 -->
    <scroll-view scroll-x class="filter-scroll">
      <view class="filter-tab" :class="{ active: currentDisClass === '' }" @click="onFilterChange('')">全部</view>
      <view class="filter-tab" :class="{ active: currentDisClass === item.value }"
            v-for="item in disTypes" :key="item.value" @click="onFilterChange(item.value)">
        {{ item.label }}
      </view>
    </scroll-view>

    <!-- 患者列表 -->
    <scroll-view scroll-y class="patient-list" @scrolltolower="loadMore">
      <view v-if="patientList.length > 0">
        <view class="patient-card card" v-for="p in patientList" :key="p.id" @click="goDetail(p.id)">
          <view class="flex-row">
            <view class="patient-avatar" :class="p.sex === '1' ? 'boy' : 'girl'">
              {{ p.sex === '1' ? '👦' : '👧' }}
            </view>
            <view class="flex-1">
              <view class="flex-between">
                <text class="patient-name">{{ p.name }}</text>
                <view class="tag" :class="getDisTagClass(p.disClass)">{{ p.disClassName }}</view>
              </view>
              <view class="patient-meta mt-10">
                <text class="text-gray">{{ p.sexName }} · {{ p.ageDesc || '未知年龄' }}</text>
              </view>
              <view class="patient-meta mt-10" v-if="p.caseNum || p.medrecNum">
                <text class="text-gray" v-if="p.caseNum">编号: {{ p.caseNum }}</text>
                <text class="text-gray ml-20" v-if="p.medrecNum">病历号: {{ p.medrecNum }}</text>
              </view>
              <view class="patient-data mt-10" v-if="p.height || p.weight">
                <text v-if="p.height">身高 {{ p.height }}cm</text>
                <text v-if="p.weight" class="ml-20">体重 {{ p.weight }}kg</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view v-else-if="!loading" class="empty-state">
        <text class="empty-icon">📋</text>
        <text class="empty-text">暂无患者数据</text>
      </view>

      <!-- 加载状态 -->
      <view v-if="loading" class="loading-tip">
        <text>加载中...</text>
      </view>
      <view v-else-if="noMore && patientList.length > 0" class="loading-tip">
        <text class="text-gray">没有更多了</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { get } from '@/utils/request'

const disTypes = [
  { label: 'DSD', value: '10000001' },
  { label: 'FSS', value: '10000002' },
  { label: 'CPP', value: '10000003' },
  { label: 'MAS', value: '10000004' },
  { label: 'SGA', value: '10000005' },
  { label: 'SSS', value: '10000006' },
  { label: 'ELTM', value: '10000007' }
]

const keyword = ref('')
const currentDisClass = ref('')
const patientList = ref([])
const loading = ref(false)
const noMore = ref(false)
const pageNum = ref(1)
const pageSize = 10

onMounted(() => {
  loadPatients()
})

onShow(() => {
  // 页面显示时刷新
})

function onSearch() {
  pageNum.value = 1
  patientList.value = []
  noMore.value = false
  loadPatients()
}

function onFilterChange(disClass) {
  currentDisClass.value = disClass
  pageNum.value = 1
  patientList.value = []
  noMore.value = false
  loadPatients()
}

async function loadPatients() {
  if (loading.value || noMore.value) return
  loading.value = true
  try {
    const params = { pageNum: pageNum.value, pageSize }
    if (keyword.value) params.keyword = keyword.value
    if (currentDisClass.value) params.disClass = currentDisClass.value

    const res = await get('/api/doctor-app/patients', params)
    const data = res.data || {}
    const records = data.records || []

    if (pageNum.value === 1) {
      patientList.value = records
    } else {
      patientList.value = [...patientList.value, ...records]
    }

    if (records.length < pageSize) {
      noMore.value = true
    }
  } catch (e) {
    console.error('加载患者列表失败', e)
  } finally {
    loading.value = false
  }
}

function loadMore() {
  if (!noMore.value && !loading.value) {
    pageNum.value++
    loadPatients()
  }
}

function goDetail(id) {
  uni.navigateTo({ url: `/pages-sub/patient/detail?id=${id}` })
}

function getDisTagClass(disClass) {
  const map = {
    '10000001': 'tag-primary', '10000002': 'tag-success', '10000003': 'tag-warning',
    '10000004': 'tag-danger', '10000005': 'tag-info', '10000006': 'tag-primary', '10000007': 'tag-success'
  }
  return map[disClass] || 'tag-info'
}
</script>

<style scoped>
.patient-page { display: flex; flex-direction: column; height: 100vh; }

.search-bar { padding: 20rpx; background: #fff; }
.search-input {
  height: 72rpx; line-height: 72rpx; padding: 0 24rpx;
  background: #f5f5f5; border-radius: 36rpx; font-size: 28rpx;
}

.filter-scroll { white-space: nowrap; padding: 10rpx 20rpx; background: #fff; border-bottom: 1rpx solid #eee; }
.filter-tab {
  display: inline-block; padding: 12rpx 28rpx; margin-right: 16rpx;
  background: #f5f5f5; border-radius: 30rpx; font-size: 26rpx; color: #666;
}
.filter-tab.active { background: #409EFF; color: #fff; }

.patient-list { flex: 1; overflow-y: auto; }
.patient-card { margin: 10rpx 20rpx; padding: 24rpx; }
.patient-avatar {
  width: 80rpx; height: 80rpx; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 36rpx; margin-right: 20rpx; flex-shrink: 0;
}
.patient-avatar.boy { background: #E3F2FD; }
.patient-avatar.girl { background: #FCE4EC; }
.patient-name { font-size: 32rpx; font-weight: 600; }
.patient-meta { font-size: 24rpx; }
.patient-data { font-size: 24rpx; color: #666; }

.empty-state { text-align: center; padding: 100rpx 30rpx; }
.empty-icon { font-size: 80rpx; display: block; }
.empty-text { font-size: 28rpx; color: #999; display: block; margin-top: 20rpx; }

.loading-tip { text-align: center; padding: 30rpx; font-size: 26rpx; color: #999; }
</style>
