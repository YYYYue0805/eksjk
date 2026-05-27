<template>
  <view class="baby-edit-page">
    <view class="card">
      <view class="form-item">
        <text class="form-label required">姓名</text>
        <input class="form-input" v-model="form.name" placeholder="请输入宝宝姓名" />
      </view>

      <view class="form-item">
        <text class="form-label required">性别</text>
        <view class="sex-picker flex-row">
          <view class="sex-option" :class="{ active: form.sex === '1' }" @click="form.sex = '1'">
            <text>👦 男</text>
          </view>
          <view class="sex-option" :class="{ active: form.sex === '2' }" @click="form.sex = '2'">
            <text>👧 女</text>
          </view>
        </view>
      </view>

      <view class="form-item">
        <text class="form-label required">出生日期</text>
        <picker mode="date" :value="form.birthTime" @change="e => form.birthTime = e.detail.value">
          <view class="form-input">{{ form.birthTime || '请选择出生日期' }}</view>
        </picker>
      </view>

      <view class="form-item">
        <text class="form-label">当前身高 (cm)</text>
        <input class="form-input" type="digit" v-model="form.height" placeholder="请输入身高" />
      </view>

      <view class="form-item">
        <text class="form-label">当前体重 (kg)</text>
        <input class="form-input" type="digit" v-model="form.weight" placeholder="请输入体重" />
      </view>

      <view class="form-item">
        <text class="form-label">与孩子关系</text>
        <picker :range="relationOptions" :value="relationIndex" @change="onRelationChange">
          <view class="form-input">{{ form.relation || '请选择' }}</view>
        </picker>
      </view>

      <view class="form-item">
        <text class="form-label">联系电话</text>
        <input class="form-input" type="number" v-model="form.selfTel" placeholder="请输入联系电话" />
      </view>

      <view class="form-item">
        <text class="form-label">父亲身高 (cm)</text>
        <input class="form-input" type="digit" v-model="form.fht" placeholder="请输入父亲身高" />
      </view>

      <view class="form-item">
        <text class="form-label">母亲身高 (cm)</text>
        <input class="form-input" type="digit" v-model="form.mht" placeholder="请输入母亲身高" />
      </view>

      <view class="form-item">
        <text class="form-label">期望身高 (cm)</text>
        <input class="form-input" type="digit" v-model="form.expectedHeight" placeholder="请输入期望身高" />
      </view>

      <view class="form-item">
        <text class="form-label">当前城市</text>
        <input class="form-input" v-model="form.currentCity" placeholder="请输入当前城市" />
      </view>
    </view>

    <view class="btn-area">
      <view class="btn-primary" @click="handleSave" :class="{ disabled: saving }">
        {{ saving ? '保存中...' : '保存' }}
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { get, post, put } from '@/utils/request'
import { onLoad } from '@dcloudio/uni-app'

const relationOptions = ['父亲', '母亲', '爷爷', '奶奶', '外公', '外婆', '其他']
const relationIndex = ref(-1)

const mode = ref('create') // create | edit | view
const babyId = ref('')
const saving = ref(false)

const form = reactive({
  name: '',
  sex: '',
  birthTime: '',
  height: '',
  weight: '',
  relation: '',
  selfTel: '',
  fht: '',
  mht: '',
  expectedHeight: '',
  currentCity: ''
})

onLoad((options) => {
  mode.value = options.mode || 'create'
  babyId.value = options.id || ''

  if (options.mode === 'create') {
    uni.setNavigationBarTitle({ title: '添加宝宝' })
  } else if (options.mode === 'edit') {
    uni.setNavigationBarTitle({ title: '编辑宝宝' })
    loadBabyDetail()
  } else {
    uni.setNavigationBarTitle({ title: '宝宝详情' })
    loadBabyDetail()
  }
})

async function loadBabyDetail() {
  try {
    const res = await get(`/api/miniapp/babies/${babyId.value}`)
    const data = res.data
    Object.keys(form).forEach(key => {
      if (data[key] !== undefined && data[key] !== null) {
        form[key] = data[key]
      }
    })
    const idx = relationOptions.indexOf(form.relation)
    if (idx >= 0) relationIndex.value = idx
  } catch (e) {
    console.error('加载宝宝详情失败', e)
  }
}

function onRelationChange(e) {
  relationIndex.value = e.detail.value
  form.relation = relationOptions[e.detail.value]
}

async function handleSave() {
  if (!form.name) {
    uni.showToast({ title: '请输入宝宝姓名', icon: 'none' })
    return
  }
  if (!form.sex) {
    uni.showToast({ title: '请选择性别', icon: 'none' })
    return
  }
  if (!form.birthTime) {
    uni.showToast({ title: '请选择出生日期', icon: 'none' })
    return
  }

  saving.value = true
  try {
    if (mode.value === 'create') {
      await post('/api/miniapp/babies', form)
      uni.showToast({ title: '添加成功', icon: 'success' })
    } else {
      await put(`/api/miniapp/babies/${babyId.value}`, form)
      uni.showToast({ title: '保存成功', icon: 'success' })
    }
    uni.$emit('babyListChanged')
    setTimeout(() => uni.navigateBack(), 500)
  } catch (e) {
    console.error('保存失败', e)
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.baby-edit-page { padding-bottom: 120rpx; }

.form-item { margin-bottom: 24rpx; }
.form-label { font-size: 28rpx; color: #666; margin-bottom: 10rpx; display: block; }
.form-label.required::before { content: '* '; color: #F56C6C; }
.form-input {
  height: 80rpx; line-height: 80rpx; padding: 0 24rpx;
  background: #f5f5f5; border-radius: 12rpx; font-size: 30rpx;
}

.sex-picker { gap: 20rpx; }
.sex-option {
  flex: 1; text-align: center; padding: 20rpx;
  background: #f5f5f5; border-radius: 12rpx; border: 2rpx solid transparent;
  font-size: 30rpx;
}
.sex-option.active { border-color: #409EFF; background: #ECF5FF; color: #409EFF; }

.btn-area { padding: 30rpx; position: fixed; bottom: 0; left: 0; right: 0; background: #fff; }
.disabled { opacity: 0.6; pointer-events: none; }
</style>
