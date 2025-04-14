<template>
  <view class="user-container">
    <!-- 用户信息头部 -->
    <view class="user-header">
      <view class="user-info">
        <image class="avatar" :src="userInfo.avatar || '/static/images/default-avatar.png'" mode="aspectFill"></image>
        <view class="user-detail">
          <text class="nickname">{{ userInfo.nickname || '游客' }}</text>
          <text class="phone">{{ formatPhone(userInfo.phone) }}</text>
          <view class="tag-container">
            <text class="tag" v-if="userInfo.verified">已实名认证</text>
            <text class="tag tag-warn" v-else>未实名认证</text>
            <text class="tag tag-courier" v-if="userInfo.userType === 1">快递员</text>
            <text class="tag tag-admin" v-if="userInfo.userType === 2">管理员</text>
          </view>
        </view>
      </view>
      <view class="setting-icon" @click="navigateTo('/pages/user/settings')">
        <uni-icons type="gear" size="24" color="#333"></uni-icons>
      </view>
    </view>
    
    <!-- 管理员功能菜单 - 仅对管理员显示 -->
    <view class="menu-section" v-if="userInfo.userType === 2">
      <view class="menu-title">管理员功能</view>
      <view class="menu-list">
        <view class="menu-item" @click="navigateTo('/pages/admin/users/index')">
          <uni-icons type="person-filled" size="20" color="#ff7043"></uni-icons>
          <text class="menu-name">用户管理</text>
          <uni-icons type="right" size="16" color="#ccc"></uni-icons>
        </view>
        <view class="menu-item" @click="navigateTo('/pages/admin/couriers/index')">
          <uni-icons type="staff" size="20" color="#ff7043"></uni-icons>
          <text class="menu-name">快递员管理</text>
          <uni-icons type="right" size="16" color="#ccc"></uni-icons>
        </view>
        <view class="menu-item" @click="navigateTo('/pages/admin/stations/index')">
          <uni-icons type="shop" size="20" color="#ff7043"></uni-icons>
          <text class="menu-name">服务点管理</text>
          <uni-icons type="right" size="16" color="#ccc"></uni-icons>
        </view>
        <view class="menu-item" @click="navigateTo('/pages/admin/orders/index')">
          <uni-icons type="list" size="20" color="#ff7043"></uni-icons>
          <text class="menu-name">订单管理</text>
          <uni-icons type="right" size="16" color="#ccc"></uni-icons>
        </view>
        <view class="menu-item" @click="navigateTo('/pages/admin/companies/index')">
          <view class="icon-wrapper">
            <image src="/static/images/kdgs.png" mode="aspectFill" class="custom-icon"></image>
          </view>
          <text class="menu-name">快递公司管理</text>
          <uni-icons type="right" size="16" color="#ccc"></uni-icons>
        </view>
        <view class="menu-item" @click="navigateTo('/pages/admin/system/index')">
          <uni-icons type="gear-filled" size="20" color="#ff7043"></uni-icons>
          <text class="menu-name">系统设置</text>
          <uni-icons type="right" size="16" color="#ccc"></uni-icons>
        </view>
      </view>
    </view>
    
    <!-- 功能菜单 -->
    <view class="menu-section">
      <view class="menu-title">我的服务</view>
      <view class="menu-list">
        <view class="menu-item" @click="navigateTo('/pages/user/profile')">
          <uni-icons type="person" size="20" color="#3cc51f"></uni-icons>
          <text class="menu-name">个人资料</text>
          <uni-icons type="right" size="16" color="#ccc"></uni-icons>
        </view>
        <view class="menu-item" @click="navigateTo('/pages/user/address')">
          <uni-icons type="location" size="20" color="#3cc51f"></uni-icons>
          <text class="menu-name">收货地址</text>
          <uni-icons type="right" size="16" color="#ccc"></uni-icons>
        </view>
        <view class="menu-item" @click="navigateTo('/pages/user/verify')" v-if="!userInfo.verified">
          <uni-icons type="paperclip" size="20" color="#3cc51f"></uni-icons>
          <text class="menu-name">实名认证</text>
          <uni-icons type="right" size="16" color="#ccc"></uni-icons>
        </view>
        <view class="menu-item" v-if="userInfo.userType !== 1 && userInfo.userType !== 2" @click="navigateTo('/pages/courier/apply')">
          <uni-icons type="staff" size="20" color="#3cc51f"></uni-icons>
          <text class="menu-name">申请成为快递员</text>
          <uni-icons type="right" size="16" color="#ccc"></uni-icons>
        </view>
      </view>
    </view>
    
    <view class="menu-section">
      <view class="menu-title">订单管理</view>
      <view class="menu-list">
        <view class="menu-item" @click="navigateTo('/pages/order/list?status=all')">
          <uni-icons type="list" size="20" color="#3cc51f"></uni-icons>
          <text class="menu-name">全部订单</text>
          <uni-icons type="right" size="16" color="#ccc"></uni-icons>
        </view>
        <view class="menu-item" @click="navigateTo('/pages/order/list?status=pending')">
          <uni-icons type="help" size="20" color="#3cc51f"></uni-icons>
          <text class="menu-name">待处理订单</text>
          <uni-icons type="right" size="16" color="#ccc"></uni-icons>
        </view>
        <view class="menu-item" @click="navigateTo('/pages/order/list?status=completed')">
          <uni-icons type="checkmarkempty" size="20" color="#3cc51f"></uni-icons>
          <text class="menu-name">已完成订单</text>
          <uni-icons type="right" size="16" color="#ccc"></uni-icons>
        </view>
      </view>
    </view>
    
    <view class="menu-section">
      <view class="menu-title">其他服务</view>
      <view class="menu-list">
        <view class="menu-item" @click="navigateTo('/pages/help/help')">
          <uni-icons type="help" size="20" color="#3cc51f"></uni-icons>
          <text class="menu-name">帮助中心</text>
          <uni-icons type="right" size="16" color="#ccc"></uni-icons>
        </view>
        <view class="menu-item" @click="navigateTo('/pages/feedback/feedback')">
          <uni-icons type="chat" size="20" color="#3cc51f"></uni-icons>
          <text class="menu-name">意见反馈</text>
          <uni-icons type="right" size="16" color="#ccc"></uni-icons>
        </view>
        <view class="menu-item" @click="navigateTo('/pages/about/about')">
          <uni-icons type="info" size="20" color="#3cc51f"></uni-icons>
          <text class="menu-name">关于我们</text>
          <uni-icons type="right" size="16" color="#ccc"></uni-icons>
        </view>
      </view>
    </view>
    
    <!-- 退出登录按钮 -->
    <view class="logout-btn" @click="handleLogout">退出登录</view>
  </view>
</template>

<script>
import { getUserInfo, logout, isLoggedIn } from '@/api/auth';

export default {
  data() {
    return {
      userInfo: {}
    };
  },
  
  onShow() {
    // 检查是否登录
    if (!isLoggedIn()) {
      uni.navigateTo({
        url: '/pages/login/login'
      });
      return;
    }
    
    // 获取用户信息
    this.userInfo = getUserInfo() || {};
    
    // 添加调试日志
    console.log('当前用户信息:', this.userInfo);
    console.log('用户类型 userType:', this.userInfo.userType);
    console.log('管理员入口条件检查:', this.userInfo.userType === 2);
    
    // 临时将当前用户设置为管理员(仅开发测试用)
    // this.userInfo.userType = 2;
    // console.log('已临时设置为管理员，userType=', this.userInfo.userType);
  },
  
  methods: {
    // 格式化手机号，中间4位显示*
    formatPhone(phone) {
      if (!phone) return '';
      return phone.substring(0, 3) + '****' + phone.substring(7);
    },
    
    // 页面导航
    navigateTo(url) {
      console.log('导航到:', url);
      uni.navigateTo({
        url
      });
    },
    
    // 退出登录
    handleLogout() {
      uni.showModal({
        title: '提示',
        content: '确定要退出登录吗？',
        success: (res) => {
          if (res.confirm) {
            logout();
          }
        }
      });
    }
  }
};
</script>

<style>
.user-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 40rpx;
}

.user-header {
  padding: 40rpx 30rpx;
  background-color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.user-info {
  display: flex;
  align-items: center;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
  margin-right: 30rpx;
  background-color: #f0f0f0;
}

.user-detail {
  display: flex;
  flex-direction: column;
}

.nickname {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
}

.phone {
  font-size: 28rpx;
  color: #666;
  margin-bottom: 10rpx;
}

.tag-container {
  display: flex;
}

.tag {
  font-size: 24rpx;
  color: #3cc51f;
  background-color: rgba(60, 197, 31, 0.1);
  padding: 4rpx 12rpx;
  border-radius: 20rpx;
  margin-right: 10rpx;
}

.tag-warn {
  color: #ff9900;
  background-color: rgba(255, 153, 0, 0.1);
}

.tag-courier {
  color: #007aff;
  background-color: rgba(0, 122, 255, 0.1);
}

.tag-admin {
  color: #ff5722;
  background-color: rgba(255, 87, 34, 0.1);
}

.menu-section {
  background-color: #fff;
  margin-bottom: 20rpx;
}

.menu-title {
  font-size: 30rpx;
  color: #333;
  font-weight: bold;
  padding: 30rpx;
  border-bottom: 1rpx solid #f5f5f5;
}

.menu-list {
  padding: 0 30rpx;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 30rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-name {
  flex: 1;
  font-size: 30rpx;
  color: #333;
  margin-left: 20rpx;
}

.logout-btn {
  width: 90%;
  height: 90rpx;
  line-height: 90rpx;
  text-align: center;
  background-color: #fff;
  color: #ff5a5f;
  font-size: 32rpx;
  border-radius: 45rpx;
  margin: 60rpx auto;
}

.icon-wrapper {
  width: 20px;
  height: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.custom-icon {
  width: 25rpx;
  height: 25rpx;
  filter: brightness(0) saturate(100%) invert(55%) sepia(87%) saturate(1206%) hue-rotate(334deg) brightness(101%) contrast(103%);
}
</style> 