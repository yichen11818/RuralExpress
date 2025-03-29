<template>
  <view class="activity-detail-container">
    <!-- 活动标题 -->
    <view class="activity-header">
      <view class="activity-title">{{ activity.title || '活动详情' }}</view>
      <view class="activity-date">{{ formatDate(activity.startTime) }} - {{ formatDate(activity.endTime) }}</view>
    </view>
    
    <!-- 活动图片 -->
    <view class="activity-banner">
      <image 
        :src="activity.bannerUrl || '/static/images/banner_placeholder.jpg'" 
        mode="widthFix" 
        class="banner-image"
      ></image>
    </view>
    
    <!-- 活动内容 -->
    <view class="activity-content">
      <view class="section-title">活动详情</view>
      <rich-text :nodes="activity.content || '加载中...'"></rich-text>
    </view>
    
    <!-- 活动规则 -->
    <view class="activity-rules" v-if="activity.rules">
      <view class="section-title">活动规则</view>
      <rich-text :nodes="activity.rules"></rich-text>
    </view>
    
    <!-- 参与按钮 -->
    <view class="activity-action">
      <button class="action-button" @click="joinActivity" v-if="!activity.isExpired">立即参与</button>
      <button class="action-button expired" v-else disabled>活动已结束</button>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      id: null,
      activity: {
        id: null,
        title: '快递送货优惠活动',
        bannerUrl: '/static/images/banner2.jpg',
        content: '<p>为了感谢广大用户的支持，乡递通特推出快递送货优惠活动！</p><p>活动期间，所有用户下单享受以下优惠：</p><ul><li>单笔订单满15元减5元</li><li>新用户首单免配送费</li><li>老用户邀请新用户，双方各得10元优惠券</li></ul>',
        rules: '<p>活动规则：</p><ol><li>每位用户活动期间最多可享受3次优惠</li><li>优惠不与其他活动同时使用</li><li>最终解释权归乡递通所有</li></ol>',
        startTime: '2025-03-20 00:00:00',
        endTime: '2025-04-20 23:59:59',
        isExpired: false
      },
      isLoading: true
    };
  },
  
  onLoad(options) {
    // 获取活动ID
    if (options.id) {
      this.id = options.id;
      this.fetchActivityDetail();
    } else {
      // 没有ID参数时显示错误提示
      uni.showToast({
        title: '活动ID不存在',
        icon: 'none'
      });
    }
  },
  
  methods: {
    // 获取活动详情
    fetchActivityDetail() {
      // 显示加载中
      uni.showLoading({
        title: '加载中...'
      });
      
      this.isLoading = true;
      
      // 实际开发中，这里应该调用API获取活动详情
      // 演示项目中，我们使用模拟数据
      setTimeout(() => {
        // 模拟API调用
        // 在实际开发中，这里应该是API请求代码
        // 例如：
        // uni.request({
        //   url: `${this.$config.baseUrl}/api/activities/${this.id}`,
        //   method: 'GET',
        //   success: (res) => {
        //     if (res.data.code === 200) {
        //       this.activity = res.data.data;
        //     }
        //   },
        //   complete: () => {
        //     uni.hideLoading();
        //     this.isLoading = false;
        //   }
        // });
        
        // 这里我们直接使用默认数据，并增加一些随机变化
        if (this.id === '1') {
          this.activity = {
            ...this.activity,
            id: this.id,
            title: '快递送货优惠活动',
            isExpired: false
          };
        } else if (this.id === '2') {
          this.activity = {
            ...this.activity,
            id: this.id,
            title: '春季特惠活动',
            bannerUrl: '/static/images/banner1.jpg',
            content: '<p>春暖花开，快递优惠送不停！</p><p>春季特惠活动强势来袭：</p><ul><li>指定区域免配送费</li><li>下单即送精美礼品</li></ul>',
            startTime: '2025-03-15 00:00:00',
            endTime: '2025-04-15 23:59:59',
            isExpired: false
          };
        } else {
          // 默认活动详情
          this.activity = {
            ...this.activity,
            id: this.id,
            title: '活动 ' + this.id,
            isExpired: Math.random() > 0.5 // 随机是否过期
          };
        }
        
        uni.hideLoading();
        this.isLoading = false;
      }, 500);
    },
    
    // 格式化日期
    formatDate(dateStr) {
      if (!dateStr) return '';
      
      try {
        const date = new Date(dateStr);
        return date.getFullYear() + '-' + 
               this.padZero(date.getMonth() + 1) + '-' + 
               this.padZero(date.getDate());
      } catch (e) {
        return dateStr;
      }
    },
    
    // 数字补零
    padZero(num) {
      return num < 10 ? '0' + num : num;
    },
    
    // 参与活动
    joinActivity() {
      if (this.activity.isExpired) {
        uni.showToast({
          title: '活动已结束',
          icon: 'none'
        });
        return;
      }
      
      uni.showToast({
        title: '成功参与活动',
        icon: 'success'
      });
      
      // 实际开发中，这里应该调用API进行活动参与逻辑
      // 例如：
      // uni.request({
      //   url: `${this.$config.baseUrl}/api/activities/${this.id}/join`,
      //   method: 'POST',
      //   success: (res) => {
      //     if (res.data.code === 200) {
      //       uni.showToast({
      //         title: '成功参与活动',
      //         icon: 'success'
      //       });
      //     }
      //   }
      // });
    }
  }
};
</script>

<style>
.activity-detail-container {
  padding: 30rpx;
  background-color: #f8f8f8;
  min-height: 100vh;
}

.activity-header {
  margin-bottom: 30rpx;
}

.activity-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
}

.activity-date {
  font-size: 24rpx;
  color: #999;
}

.activity-banner {
  width: 100%;
  border-radius: 12rpx;
  overflow: hidden;
  margin-bottom: 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.banner-image {
  width: 100%;
}

.activity-content,
.activity-rules {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.section-title {
  font-size: 30rpx;
  font-weight: bold;
  margin-bottom: 20rpx;
  color: #333;
  position: relative;
  padding-left: 20rpx;
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 6rpx;
  height: 24rpx;
  background-color: #FF6B35;
  border-radius: 3rpx;
}

.activity-action {
  margin-top: 50rpx;
  padding: 0 30rpx;
}

.action-button {
  width: 100%;
  height: 90rpx;
  line-height: 90rpx;
  background: linear-gradient(to right, #FF6B35, #FF9A5A);
  color: #fff;
  font-size: 32rpx;
  font-weight: bold;
  border-radius: 45rpx;
  box-shadow: 0 6rpx 16rpx rgba(255, 107, 53, 0.3);
}

.action-button.expired {
  background: linear-gradient(to right, #999, #bbb);
  box-shadow: none;
}
</style> 