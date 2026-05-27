<template>
  <view class="detail-page">
    <!-- 患者基本信息头部 -->
    <view class="patient-header card">
      <view class="flex-row">
        <view class="patient-avatar" :class="detail.sex === '1' ? 'boy' : 'girl'">
          {{ detail.sex === '1' ? '👦' : '👧' }}
        </view>
        <view class="flex-1">
          <view class="flex-between">
            <text class="patient-name">{{ detail.name }}</text>
            <view class="tag" :class="getDisTagClass(detail.disClass)">{{ detail.disClassName }}</view>
          </view>
          <text class="text-gray mt-10" style="display:block;">{{ detail.sexName }} · {{ detail.ageDesc || '' }}</text>
          <text class="text-gray mt-10" style="display:block;font-size:24rpx;" v-if="detail.caseNum">
            编号: {{ detail.caseNum }}
          </text>
        </view>
      </view>
      <!-- 拨打电话 -->
      <view v-if="detail.selfTel" class="call-btn mt-20" @click="callPhone">
        <text class="text-primary">📞 拨打联系电话</text>
      </view>
    </view>

    <!-- Tabs 切换 -->
    <view class="tabs flex-row">
      <view class="tab-item" :class="{ active: currentTab === 'info' }" @click="currentTab = 'info'">基本信息</view>
      <view class="tab-item" :class="{ active: currentTab === 'followup' }" @click="currentTab = 'followup'">
        随访记录 ({{ detail.followUps ? detail.followUps.length : 0 }})
      </view>
      <view class="tab-item" :class="{ active: currentTab === 'image' }" @click="currentTab = 'image'">影像资料</view>
    </view>

    <!-- 基本信息 Tab -->
    <view v-show="currentTab === 'info'" class="tab-content">
      <view class="card">
        <view class="info-section-title">基本信息</view>
        <view class="info-row">
          <text class="info-label">姓名</text>
          <text class="info-value">{{ detail.name || '-' }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">性别</text>
          <text class="info-value">{{ detail.sexName || '-' }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">出生日期</text>
          <text class="info-value">{{ detail.birthTime || '-' }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">年龄</text>
          <text class="info-value">{{ detail.ageDesc || '-' }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">病例编号</text>
          <text class="info-value">{{ detail.caseNum || '-' }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">病历号</text>
          <text class="info-value">{{ detail.medrecNum || '-' }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">联系电话</text>
          <text class="info-value">{{ detail.selfTel || '-' }}</text>
        </view>
      </view>

      <view class="card">
        <view class="info-section-title">体格数据</view>
        <view class="info-row">
          <text class="info-label">身高</text>
          <text class="info-value">{{ detail.height ? detail.height + ' cm' : '-' }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">体重</text>
          <text class="info-value">{{ detail.weight ? detail.weight + ' kg' : '-' }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">父亲身高</text>
          <text class="info-value">{{ detail.fht ? detail.fht + ' cm' : '-' }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">母亲身高</text>
          <text class="info-value">{{ detail.mht ? detail.mht + ' cm' : '-' }}</text>
        </view>
      </view>
    </view>

    <!-- 随访记录 Tab -->
    <view v-show="currentTab === 'followup'" class="tab-content">
      <view v-if="detail.followUps && detail.followUps.length > 0">
        <view class="followup-item card" v-for="(f, idx) in detail.followUps" :key="f.id">
          <view class="flex-between">
            <text class="followup-date">📅 {{ f.follTime }}</text>
            <text class="followup-idx text-gray">#{{ detail.followUps.length - idx }}</text>
          </view>
          <view class="followup-data mt-20 flex-row flex-wrap">
            <view class="data-chip" v-if="f.ht">
              <text class="chip-label">身高</text>
              <text class="chip-value">{{ f.ht }} cm</text>
            </view>
            <view class="data-chip" v-if="f.wt">
              <text class="chip-label">体重</text>
              <text class="chip-value">{{ f.wt }} kg</text>
            </view>
            <view class="data-chip" v-if="f.bmi">
              <text class="chip-label">BMI</text>
              <text class="chip-value">{{ f.bmi }}</text>
            </view>
          </view>
        </view>
      </view>
      <view v-else class="empty-state card">
        <text class="text-gray">暂无随访记录</text>
      </view>

      <!-- 新增随访按钮 -->
      <view class="add-followup-btn" @click="goAddFollowUp">
        <text class="btn-primary">+ 新增随访</text>
      </view>
    </view>

    <!-- 影像资料 Tab -->
    <view v-show="currentTab === 'image'" class="tab-content">
      <view class="empty-state card">
        <text class="text-gray">影像资料请在PC端查看</text>
        <text class="text-gray mt-10" style="display:block;font-size:24rpx;">完整影像管理功能请使用电脑端系统</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { get } from '@/utils/request'

const patientId = ref('')
const detail = ref({})
const currentTab = ref('info')
const loading = ref(false)

onLoad((options) => {
  patientId.value = options.id || ''
  if (patientId.value) {
    loadDetail()
  }
})

async function loadDetail() {
  loading.value = true
  try {
    const res = await get(`/api/doctor-app/patients/${patientId.value}`)
    detail.value = res.data || {}
  } catch (e) {
    console.error('加载患者详情失败', e)
  } finally {
    loading.value = false
  }
}

function callPhone() {
  if (detail.value.selfTel) {
    uni.makePhoneCall({ phoneNumber: detail.value.selfTel })
  }
}

function goAddFollowUp() {
  uni.navigateTo({ url: `/pages-sub/followup/create?patientId=${patientId.value}&patientName=${detail.value.name || ''}` })
}

function getDisTagClass(disClass) {
  const map = {
    '10000001': 'tag-primary', '10000002': 'tag-success', '10000003': 'tag-warning',
    '10000004': 'tag-danger', '10000005': 'tag-info', '10000006': 'tag-primary', '10000007': 'tag-success'
  }
  return map[disClass] || 'tag-info'
}

// 监听随访新增事件，刷新详情
onMounted(() => {
  uni.$on('followUpCreated', loadDetail)
})
</script>

<style scoped>
.detail-page { padding-bottom: 30rpx; }

.patient-header { margin-top: 0; border-radius: 0 0 16rpx 16rpx; }
.patient-avatar {
  width: 90rpx; height: 90rpx; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 40rpx; margin-right: 24rpx; flex-shrink: 0;
}
.patient-avatar.boy { background: #E3F2FD; }
.patient-avatar.girl { background: #FCE4EC; }
.patient-name { font-size: 34rpx; font-weight: 600; }
.call-btn {
  text-align: center; padding: 16rpx;
  border: 1rpx solid #409EFF; border-radius: 12rpx;
}

.tabs {
  background: #fff; border-bottom: 1rpx solid #eee;
  position: sticky; top: 0; z-index: 10;
}
.tab-item {
  flex: 1; text-align: center; padding: 24rpx 0;
  font-size: 28rpx; color: #666; position: relative;
}
.tab-item.active { color: #409EFF; font-weight: 600; }
.tab-item.active::after {
  content: ''; position: absolute; bottom: 0; left: 30%; right: 30%;
  height: 4rpx; background: #409EFF; border-radius: 2rpx;
}

.tab-content { min-height: 400rpx; }

.info-section-title { font-size: 30rpx; font-weight: 600; margin-bottom: 20rpx; }
.info-row {
  display: flex; justify-content: space-between; padding: 16rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}
.info-row:last-child { border-bottom: none; }
.info-label { font-size: 28rpx; color: #999; }
.info-value { font-size: 28rpx; color: #333; }

.followup-item { margin: 10rpx 20rpx; }
.followup-date { font-size: 28rpx; font-weight: 500; }
.followup-idx { font-size: 24rpx; }
.followup-data { gap: 16rpx; }
.data-chip {
  background: #f5f5f5; border-radius: 12rpx; padding: 12rpx 20rpx;
  display: flex; flex-direction: column; align-items: center; min-width: 120rpx;
}
.chip-label { font-size: 22rpx; color: #999; }
.chip-value { font-size: 28rpx; font-weight: 600; margin-top: 4rpx; }

.add-followup-btn { padding: 20rpx 30rpx; }

.empty-state { text-align: center; padding: 60rpx 30rpx; }
</style>
