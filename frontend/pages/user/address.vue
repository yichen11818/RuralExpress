<template>
  <view class="address-container">
    <!-- 增加顶部状态栏占位 -->
    <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
    
    <view class="address-header">
      <view class="back-btn" @click="goBack">
        <uni-icons type="left" size="18" color="#333"></uni-icons>
      </view>
      <text class="page-title">我的地址</text>
    </view>
    
    <!-- 地址列表 -->
    <view class="address-list" v-if="addressList.length > 0">
      <view class="address-item" v-for="(item, index) in addressList" :key="index">
        <view class="address-info" @click="selectAddress(item)">
          <view class="address-top">
            <text class="name">{{ item.name }}</text>
            <text class="phone">{{ formatPhone(item.phone) }}</text>
            <text class="tag" v-if="item.isDefault">默认</text>
          </view>
          <text class="address-text">{{ item.province + item.city + item.district + item.address }}</text>
        </view>
        <view class="address-actions">
          <view class="action-btn" @click="editAddress(item)">
            <uni-icons type="compose" size="16" color="#666"></uni-icons>
            <text class="action-text">编辑</text>
          </view>
          <view class="action-btn" @click="deleteAddress(item.id)">
            <uni-icons type="trash" size="16" color="#666"></uni-icons>
            <text class="action-text">删除</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 空状态 - 修改为使用统一的empty.png -->
    <view class="empty-state" v-else>
      <image class="empty-image" src="/static/images/empty.png" mode="aspectFit"></image>
      <text class="empty-text">暂无地址信息</text>
    </view>
    
    <!-- 底部添加按钮 -->
    <view class="add-btn" @click="addAddress">
      <text class="add-text">添加新地址</text>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      addressList: [
        {
          id: 1,
          name: '张三',
          phone: '13800138000',
          province: '广东省',
          city: '深圳市',
          district: '南山区',
          address: '科技园路1号',
          isDefault: true
        },
        {
          id: 2,
          name: '李四',
          phone: '13900139000',
          province: '广东省',
          city: '深圳市',
          district: '福田区',
          address: '中心城区1号',
          isDefault: false
        }
      ],
      statusBarHeight: 20 // 默认值
    };
  },
  
  onLoad() {
    // 获取状态栏高度
    this.getStatusBarHeight();
  },
  
  methods: {
    // 获取状态栏高度
    getStatusBarHeight() {
      // 获取系统信息
      uni.getSystemInfo({
        success: (res) => {
          this.statusBarHeight = res.statusBarHeight || 20;
        }
      });
    },
    
    goBack() {
      uni.navigateBack();
    },
    
    // 格式化手机号，中间4位显示*
    formatPhone(phone) {
      if (!phone) return '';
      return phone.substring(0, 3) + '****' + phone.substring(7);
    },
    
    // 编辑地址
    editAddress(item) {
      uni.showToast({
        title: '编辑地址功能开发中',
        icon: 'none'
      });
    },
    
    // 删除地址
    deleteAddress(id) {
      uni.showModal({
        title: '提示',
        content: '确定要删除该地址吗？',
        success: (res) => {
          if (res.confirm) {
            // 这里应该调用API删除地址
            this.addressList = this.addressList.filter(item => item.id !== id);
            uni.showToast({
              title: '删除成功',
              icon: 'success'
            });
          }
        }
      });
    },
    
    // 选择地址
    selectAddress(item) {
      // 若有页面参数type=select，表示从选择地址页面进入
      const pages = getCurrentPages();
      const currentPage = pages[pages.length - 1];
      
      if (currentPage.options && currentPage.options.type === 'select') {
        // 将地址数据传递回上一页
        const prevPage = pages[pages.length - 2];
        
        // 设置上一页的地址信息
        if (prevPage && prevPage.$vm) {
          prevPage.$vm.selectedAddress = item;
        }
        
        uni.navigateBack();
      }
    },
    
    // 添加新地址
    addAddress() {
      uni.showToast({
        title: '添加地址功能开发中',
        icon: 'none'
      });
    }
  }
};
</script>

<style>
.address-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 120rpx;
}

/* 状态栏占位 */
.status-bar {
  width: 100%;
  background-color: #fff;
}

.address-header {
  position: relative;
  padding: 30rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
  margin-bottom: 20rpx;
  /* 调整顶部和底部的padding更合理 */
  padding-top: 20rpx;
  padding-bottom: 20rpx;
}

.back-btn {
  position: absolute;
  left: 30rpx;
}

.page-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.address-list {
  margin: 0 20rpx;
}

.address-item {
  background-color: #fff;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
  overflow: hidden;
  /* 添加阴影效果 */
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.address-info {
  padding: 30rpx;
  border-bottom: 1rpx solid #f5f5f5;
}

.address-top {
  display: flex;
  align-items: center;
  margin-bottom: 10rpx;
}

.name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-right: 20rpx;
}

.phone {
  font-size: 28rpx;
  color: #666;
}

.tag {
  font-size: 24rpx;
  color: #ff5a5f;
  background-color: rgba(255, 90, 95, 0.1);
  padding: 4rpx 12rpx;
  border-radius: 20rpx;
  margin-left: auto;
}

.address-text {
  font-size: 28rpx;
  color: #333;
  line-height: 1.5;
}

.address-actions {
  display: flex;
  padding: 20rpx 0;
  background-color: #fff;
}

.action-btn {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.action-text {
  font-size: 28rpx;
  color: #666;
  margin-left: 10rpx;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
  margin-top: 60rpx;
}

.empty-image {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 30rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

.add-btn {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 100rpx;
  background-color: #3cc51f;
  display: flex;
  align-items: center;
  justify-content: center;
}

.add-text {
  font-size: 32rpx;
  color: #fff;
  font-weight: bold;
}
</style> 