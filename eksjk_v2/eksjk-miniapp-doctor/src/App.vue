<script>
export default {
  onLaunch() {
    console.log('Doctor App Launch')
    // 检查更新
    if (uni.canIUse('getUpdateManager')) {
      const updateManager = uni.getUpdateManager()
      updateManager.onCheckForUpdate((res) => {
        if (res.hasUpdate) {
          updateManager.onUpdateReady(() => {
            uni.showModal({
              title: '更新提示',
              content: '新版本已经准备好，是否重启应用？',
              success(res) {
                if (res.confirm) {
                  updateManager.applyUpdate()
                }
              }
            })
          })
          updateManager.onUpdateFailed(() => {
            uni.showModal({
              title: '更新提示',
              content: '新版本下载失败，请检查网络后重试',
              showCancel: false
            })
          })
        }
      })
    }
  },
  onShow() {
    console.log('Doctor App Show')
  },
  onHide() {
    console.log('Doctor App Hide')
  }
}
</script>

<style>
/* 全局样式 */
page {
  background-color: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  font-size: 28rpx;
  color: #333333;
}

/* 通用工具类 */
.flex-row { display: flex; flex-direction: row; align-items: center; }
.flex-col { display: flex; flex-direction: column; }
.flex-1 { flex: 1; }
.flex-center { display: flex; justify-content: center; align-items: center; }
.flex-between { display: flex; justify-content: space-between; align-items: center; }
.flex-wrap { flex-wrap: wrap; }

.text-center { text-align: center; }
.text-right { text-align: right; }
.text-bold { font-weight: 600; }
.text-gray { color: #999999; }
.text-primary { color: #409EFF; }
.text-danger { color: #F56C6C; }
.text-success { color: #67C23A; }
.text-warning { color: #E6A23C; }

.mt-10 { margin-top: 10rpx; }
.mt-20 { margin-top: 20rpx; }
.mt-30 { margin-top: 30rpx; }
.mb-10 { margin-bottom: 10rpx; }
.mb-20 { margin-bottom: 20rpx; }
.ml-10 { margin-left: 10rpx; }
.ml-20 { margin-left: 20rpx; }
.p-20 { padding: 20rpx; }
.p-30 { padding: 30rpx; }

/* 卡片样式 */
.card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

/* 按钮样式 */
.btn-primary {
  background: #409EFF;
  color: #ffffff;
  border-radius: 44rpx;
  height: 88rpx;
  line-height: 88rpx;
  text-align: center;
  font-size: 32rpx;
  font-weight: 500;
}
.btn-primary:active { opacity: 0.8; }

.btn-success {
  background: #67C23A;
  color: #ffffff;
  border-radius: 44rpx;
  height: 88rpx;
  line-height: 88rpx;
  text-align: center;
  font-size: 32rpx;
  font-weight: 500;
}
.btn-success:active { opacity: 0.8; }

.btn-danger {
  background: #F56C6C;
  color: #ffffff;
  border-radius: 44rpx;
  height: 88rpx;
  line-height: 88rpx;
  text-align: center;
  font-size: 32rpx;
  font-weight: 500;
}
.btn-danger:active { opacity: 0.8; }

/* 标签样式 */
.tag {
  display: inline-block;
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
}
.tag-primary { background: #ECF5FF; color: #409EFF; }
.tag-success { background: #F0F9EB; color: #67C23A; }
.tag-warning { background: #FDF6EC; color: #E6A23C; }
.tag-danger { background: #FEF0F0; color: #F56C6C; }
.tag-info { background: #F4F4F5; color: #909399; }

/* 分割线 */
.divider {
  height: 1rpx;
  background: #EBEEF5;
  margin: 20rpx 0;
}
</style>
