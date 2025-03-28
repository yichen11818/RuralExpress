<template>
  <view class="help-container">
    <view class="help-header">
      <image class="help-banner" src="/static/images/help-banner.png" mode="aspectFill"></image>
      <text class="header-title">乡递通帮助中心</text>
    </view>
    
    <view class="help-content">
      <view class="faq-section">
        <text class="section-title">常见问题</text>
        
        <view class="faq-item" v-for="(item, index) in faqList" :key="index" @click="toggleFaq(index)">
          <view class="faq-question">
            <text>{{ item.question }}</text>
            <text class="toggle-icon">{{ item.isOpen ? '∧' : '∨' }}</text>
          </view>
          <view class="faq-answer" v-if="item.isOpen">
            <text>{{ item.answer }}</text>
          </view>
        </view>
      </view>
      
      <view class="contact-section">
        <text class="section-title">联系我们</text>
        
        <view class="contact-item">
          <text class="contact-label">客服电话：</text>
          <text class="contact-value" @click="callService">400-123-4567</text>
        </view>
        
        <view class="contact-item">
          <text class="contact-label">客服邮箱：</text>
          <text class="contact-value">support@ruralexpress.com</text>
        </view>
        
        <view class="contact-item">
          <text class="contact-label">工作时间：</text>
          <text class="contact-value">周一至周日 9:00-21:00</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      faqList: [
        {
          question: '如何下单？',
          answer: '您可以在首页点击"寄件"按钮，填写寄件人和收件人信息，选择包裹类型后提交订单。',
          isOpen: false
        },
        {
          question: '如何查看订单状态？',
          answer: '您可以在"我的订单"页面查看所有订单状态，也可以通过订单号在物流追踪页面查询具体物流信息。',
          isOpen: false
        },
        {
          question: '如何成为骑手？',
          answer: '您可以在个人中心点击"骑手招募"，按要求填写相关信息并提交申请，我们会在3个工作日内审核并通知您结果。',
          isOpen: false
        },
        {
          question: '支持哪些支付方式？',
          answer: '我们目前支持微信支付、支付宝、银行卡等多种支付方式，您可以在下单时选择合适的支付方式。',
          isOpen: false
        },
        {
          question: '订单取消规则是什么？',
          answer: '订单提交后，在骑手接单前可以随时取消；骑手接单后，需要联系客服进行取消操作，可能会产生一定的取消费用。',
          isOpen: false
        }
      ]
    };
  },
  
  methods: {
    // 切换FAQ展开状态
    toggleFaq(index) {
      this.faqList[index].isOpen = !this.faqList[index].isOpen;
    },
    
    // 拨打客服电话
    callService() {
      uni.makePhoneCall({
        phoneNumber: '4001234567',
        success: () => {
          console.log('拨打电话成功');
        },
        fail: (err) => {
          console.error('拨打电话失败', err);
        }
      });
    }
  }
};
</script>

<style>
.help-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 30rpx;
}
  
.help-header {
  position: relative;
  height: 200rpx;
  overflow: hidden;
}
    
.help-banner {
  width: 100%;
  height: 200rpx;
}
    
.header-title {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 36rpx;
  font-weight: bold;
  color: #fff;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}
  
.help-content {
  padding: 30rpx;
}
    
.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 30rpx;
  position: relative;
  padding-left: 20rpx;
}
      
.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 8rpx;
  height: 32rpx;
  background-color: #FF6B00;
  border-radius: 4rpx;
}
    
.faq-section {
  margin-bottom: 40rpx;
}
      
.faq-item {
  background-color: #fff;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
  overflow: hidden;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}
        
.faq-question {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx;
  font-size: 28rpx;
  color: #333;
}
          
.toggle-icon {
  color: #999;
  transition: transform 0.3s;
}
        
.faq-answer {
  padding: 0 24rpx 24rpx;
  font-size: 26rpx;
  color: #666;
  line-height: 1.5;
  border-top: 1px solid #f2f2f2;
}
      
.contact-item {
  display: flex;
  margin-bottom: 20rpx;
  background-color: #fff;
  padding: 24rpx;
  border-radius: 12rpx;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}
        
.contact-label {
  font-size: 28rpx;
  color: #333;
  width: 200rpx;
}
        
.contact-value {
  flex: 1;
  font-size: 28rpx;
  color: #666;
}

.contact-value:active {
  color: #FF6B00;
}
</style> 