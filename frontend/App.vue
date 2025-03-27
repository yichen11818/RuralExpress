<script>
export default {
  onLaunch: function() {
    console.log('App Launch');
  },
  onShow: function() {
    console.log('App Show');
  },
  onHide: function() {
    console.log('App Hide');
  }
};
</script>

<style>
/* 全局样式 */
page {
  font-family: 'PingFang SC', 'Helvetica Neue', Helvetica, 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
  min-height: 100%;
  display: flex;
  flex-direction: column;
}

.container {
  display: flex;
  flex-direction: column;
  padding: 30rpx;
}

.primary-button {
  background-color: #3cc51f;
  color: white;
  border-radius: 10rpx;
  padding: 20rpx 0;
  text-align: center;
  margin: 20rpx 0;
}

.primary-button:active {
  opacity: 0.8;
}

.secondary-button {
  background-color: #f8f8f8;
  color: #333;
  border: 1rpx solid #ddd;
  border-radius: 10rpx;
  padding: 20rpx 0;
  text-align: center;
  margin: 20rpx 0;
}

.secondary-button:active {
  background-color: #eee;
}

.card {
  background-color: white;
  border-radius: 10rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
  padding: 30rpx;
  margin-bottom: 30rpx;
}

.form-item {
  margin-bottom: 30rpx;
}

.form-label {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 15rpx;
}

.form-input {
  background-color: #f8f8f8;
  padding: 20rpx;
  border-radius: 8rpx;
  font-size: 28rpx;
}
</style> 