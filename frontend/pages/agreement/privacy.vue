<template>
  <view class="agreement-container">
    <view class="title">隐私政策</view>
    <view class="content">
      <view class="section">
        <view class="section-title">1. 信息收集</view>
        <view class="section-content">
          <view class="paragraph">尊敬的用户，为了向您提供更好的服务，乡递通会按照本隐私政策收集和使用您的个人信息。我们收集的信息包括但不限于：</view>
          <view class="list-item">• 个人身份信息：如姓名、电话号码、身份证信息等；</view>
          <view class="list-item">• 位置信息：如您的收货地址、当前位置等；</view>
          <view class="list-item">• 设备信息：如设备型号、操作系统、唯一设备标识符等；</view>
          <view class="list-item">• 日志信息：如IP地址、搜索查询内容、使用的服务等。</view>
        </view>
      </view>

      <view class="section">
        <view class="section-title">2. 信息使用</view>
        <view class="section-content">
          <view class="paragraph">我们使用收集的信息用于：</view>
          <view class="list-item">• 提供、维护和改进我们的服务；</view>
          <view class="list-item">• 处理您的快递订单；</view>
          <view class="list-item">• 进行身份验证，防止欺诈行为；</view>
          <view class="list-item">• 根据法律法规要求提供信息。</view>
        </view>
      </view>

      <view class="section">
        <view class="section-title">3. 信息共享</view>
        <view class="section-content">
          <view class="paragraph">我们严格保护您的个人信息安全，不会将您的个人信息出售给第三方。但在以下情况下，我们可能会共享您的个人信息：</view>
          <view class="list-item">• 在获取您的明确同意后；</view>
          <view class="list-item">• 与我们的关联公司共享；</view>
          <view class="list-item">• 与授权合作伙伴共享，如配送员、物流公司等；</view>
          <view class="list-item">• 根据法律法规要求提供。</view>
        </view>
      </view>

      <view class="section">
        <view class="section-title">4. 信息存储</view>
        <view class="section-content">
          <view class="paragraph">您的个人信息将存储在我们的服务器上，我们会采取合理措施保护您的个人信息安全。我们会在法律要求或业务需要的时间内保留您的个人信息。</view>
        </view>
      </view>

      <view class="section">
        <view class="section-title">5. 信息安全</view>
        <view class="section-content">
          <view class="paragraph">我们使用各种安全技术和程序，以防信息的丢失、不当使用、未经授权阅览或披露。例如，在某些服务中，我们将使用加密技术来保护您提供的个人信息。但请注意，互联网环境并非百分之百安全，我们无法保证您的个人信息在互联网上传输的绝对安全性。</view>
        </view>
      </view>

      <view class="section">
        <view class="section-title">6. 隐私政策更新</view>
        <view class="section-content">
          <view class="paragraph">我们可能会不时更新本隐私政策。当我们这样做时，我们会在应用内发布通知，并更新本隐私政策页面上的"最后更新"日期。我们建议您定期查看本隐私政策，以了解我们如何保护您的信息。</view>
        </view>
      </view>

      <view class="section">
        <view class="section-title">7. 联系我们</view>
        <view class="section-content">
          <view class="paragraph">如果您对本隐私政策有任何问题，请通过以下方式联系我们：</view>
          <view class="list-item">• 电子邮件：privacy@ruralexpress.com</view>
          <view class="list-item">• 客服电话：400-800-7001</view>
        </view>
      </view>
    </view>

    <view class="footer">最后更新日期：2023年6月1日</view>
    
    <button class="back-btn" @click="goBack">返回</button>
  </view>
</template>

<script>
export default {
  methods: {
    goBack() {
      uni.navigateBack();
    }
  }
};
</script>

<style>
.agreement-container {
  padding: 30rpx;
  background-color: #fff;
  min-height: 100vh;
}

.title {
  font-size: 40rpx;
  font-weight: bold;
  color: #333;
  text-align: center;
  margin-bottom: 40rpx;
  padding-top: 20rpx;
}

.content {
  margin-bottom: 30rpx;
}

.section {
  margin-bottom: 30rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}

.section-content {
  padding-left: 10rpx;
}

.paragraph {
  font-size: 28rpx;
  color: #666;
  line-height: 1.6;
  margin-bottom: 20rpx;
  text-align: justify;
}

.list-item {
  font-size: 28rpx;
  color: #666;
  line-height: 1.6;
  margin-bottom: 10rpx;
  padding-left: 20rpx;
}

.footer {
  font-size: 24rpx;
  color: #999;
  text-align: center;
  margin: 40rpx 0;
}

.back-btn {
  width: 90%;
  height: 90rpx;
  line-height: 90rpx;
  background-color: #3cc51f;
  color: #fff;
  font-size: 32rpx;
  border-radius: 45rpx;
  margin: 30rpx auto;
}
</style> 