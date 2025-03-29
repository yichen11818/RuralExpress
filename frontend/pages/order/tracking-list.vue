<?xml version="1.0" encoding="UTF-8"?>
<template>
  <view class="tracking-list-container">
    <!-- 头部标题 -->
    <view class="header">
      <view class="title">物流追踪</view>
    </view>
    
    <!-- 列表内容 -->
    <view class="tracking-list">
      <!-- 加载状态提示 -->
      <view class="loading-state" v-if="loading">
        <view class="loading-icon"></view>
        <text class="loading-text">正在加载物流数据...</text>
      </view>
      
      <!-- 物流列表内容 -->
      <view class="tracking-item" v-for="(item, index) in trackingList" :key="index" @click="navigateToDetail(item.trackingNo)">
        <view class="tracking-header">
          <view class="tracking-company">
            <image :src="item.logo || '/static/images/package.png'" mode="aspectFit" class="company-logo"></image>
            <view class="company-info">
              <text class="company-name">{{ item.company }}</text>
              <text class="tracking-number">{{ item.trackingNo }}</text>
            </view>
          </view>
          <view class="tracking-status" :class="'status-' + item.status">
            {{ getStatusText(item.status) }}
          </view>
        </view>
        
        <view class="tracking-content">
          <view class="package-info">
            <text class="package-label">包裹信息：</text>
            <text class="package-value">{{ item.packageInfo || '暂无信息' }}</text>
          </view>
          <view class="address-info" v-if="item.address">
            <text class="address-label">收货地址：</text>
            <text class="address-value">{{ item.address }}</text>
          </view>
          <view class="time-info">
            <text class="time-label">更新时间：</text>
            <text class="time-value">{{ item.updateTime || '暂无信息' }}</text>
          </view>
        </view>
        
        <view class="tracking-footer">
          <view class="tracking-operations">
            <view class="operation-btn" @click.stop="copyTrackingNo(item.trackingNo)">
              <uni-icons type="paperclip" size="16" color="#666"></uni-icons>
              <text>复制单号</text>
            </view>
            <view class="operation-btn" @click.stop="shareTracking(item)">
              <uni-icons type="redo" size="16" color="#666"></uni-icons>
              <text>分享</text>
            </view>
            <view class="operation-btn" @click.stop="refreshTracking(item.trackingNo, index)">
              <uni-icons type="refresh" size="16" color="#666"></uni-icons>
              <text>刷新</text>
            </view>
          </view>
          <view class="view-detail">
            <text>查看详情</text>
            <uni-icons type="right" size="14" color="#999"></uni-icons>
          </view>
        </view>
      </view>
      
      <!-- 空数据提示 -->
      <view class="empty-state" v-if="trackingList.length === 0 && !loading">
        <image src="/static/images/empty-box.png" mode="aspectFit" class="empty-image"></image>
        <text class="empty-text">暂无物流信息</text>
        <text class="empty-tips">您可以通过订单页面查看物流详情</text>
        <button class="nav-btn" @click="navigateToOrderPage">去查看订单</button>
      </view>
    </view>
    
    <!-- 底部加载更多 -->
    <view class="load-more" v-if="trackingList.length > 0">
      <text v-if="hasMore && !loadingMore" @click="loadMore">加载更多</text>
      <text v-if="loadingMore">加载中...</text>
      <text v-if="!hasMore && !loadingMore">没有更多数据了</text>
    </view>
  </view>
</template>

<script>
import uniIcons from '@/uni_modules/uni-icons/components/uni-icons/uni-icons.vue'
import { getTrackingList, getLogisticsInfo } from '@/api/order'

export default {
  components: {
    uniIcons
  },
  data() {
    return {
      loading: true,
      loadingMore: false,
      hasMore: true,
      page: 1,
      pageSize: 10,
      trackingList: []
    }
  },
  onLoad() {
    this.loadTrackingList()
  },
  // 下拉刷新
  onPullDownRefresh() {
    this.refreshList()
  },
  // 上拉加载
  onReachBottom() {
    if (this.hasMore && !this.loadingMore) {
      this.loadMore()
    }
  },
  methods: {
    // 加载物流列表
    loadTrackingList(append = false) {
      if (this.loadingMore && append) return
      
      if (append) {
        this.loadingMore = true
      } else {
        this.loading = true
        this.page = 1
      }
      
      const params = {
        page: this.page,
        pageSize: this.pageSize
      }
      
      getTrackingList(params)
        .then(res => {
          if (res.code === 200 && res.data) {
            const list = res.data.list || []
            const total = res.data.total || 0
            
            if (append) {
              this.trackingList = [...this.trackingList, ...list]
            } else {
              this.trackingList = list
            }
            
            this.hasMore = this.trackingList.length < total
          } else {
            uni.showToast({
              title: res.message || '获取物流列表失败',
              icon: 'none'
            })
          }
        })
        .catch(err => {
          console.error('获取物流列表失败', err)
          uni.showToast({
            title: '获取物流列表失败',
            icon: 'none'
          })
        })
        .finally(() => {
          if (append) {
            this.loadingMore = false
          } else {
            this.loading = false
          }
          uni.stopPullDownRefresh()
        })
    },
    
    // 加载更多
    loadMore() {
      if (this.loadingMore || !this.hasMore) return
      this.page++
      this.loadTrackingList(true)
    },
    
    // 刷新列表
    refreshList() {
      this.page = 1
      this.loadTrackingList()
    },
    
    // 刷新单个物流
    refreshTracking(trackingNo, index) {
      uni.showLoading({
        title: '刷新中...'
      })
      
      getLogisticsInfo({ trackingNo })
        .then(res => {
          if (res.code === 200 && res.data) {
            // 更新物流信息
            this.trackingList[index] = {
              ...this.trackingList[index],
              status: res.data.status || 0,
              updateTime: res.data.updateTime || new Date().toLocaleString(),
              packageInfo: res.data.packageInfo || this.trackingList[index].packageInfo
            }
            
            uni.showToast({
              title: '刷新成功',
              icon: 'success'
            })
          } else {
            uni.showToast({
              title: res.message || '刷新失败',
              icon: 'none'
            })
          }
        })
        .catch(err => {
          console.error('刷新物流失败', err)
          uni.showToast({
            title: '刷新失败',
            icon: 'none'
          })
        })
        .finally(() => {
          uni.hideLoading()
        })
    },
    
    // 复制运单号
    copyTrackingNo(trackingNo) {
      uni.setClipboardData({
        data: trackingNo,
        success: () => {
          uni.showToast({
            title: '复制成功',
            icon: 'success'
          })
        }
      })
    },
    
    // 分享物流
    shareTracking(item) {
      uni.share({
        provider: 'weixin',
        scene: 'WXSceneSession',
        type: 0,
        title: `${item.company}物流追踪`,
        summary: `运单号：${item.trackingNo}，当前状态：${this.getStatusText(item.status)}`,
        imageUrl: '/static/images/package.png',
        success: (res) => {
          console.log('分享成功', res)
        },
        fail: (err) => {
          console.log('分享失败', err)
        }
      })
    },
    
    // 获取状态文本
    getStatusText(status) {
      const statusMap = {
        0: '等待揽收',
        1: '已揽收',
        2: '运输中',
        3: '已到达',
        4: '派送中',
        5: '已签收'
      }
      return statusMap[status] || '未知状态'
    },
    
    // 跳转到物流详情
    navigateToDetail(trackingNo) {
      uni.navigateTo({
        url: `/pages/order/track?trackingNo=${trackingNo}`
      })
    },
    
    // 跳转到订单页面
    navigateToOrderPage() {
      uni.navigateTo({
        url: '/pages/order/list'
      })
    }
  }
}
</script>

<style>
.tracking-list-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 40rpx;
}

.header {
  background-color: #fff;
  padding: 30rpx;
  border-bottom: 2rpx solid #f0f0f0;
}

.title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.tracking-list {
  padding: 30rpx;
}

.tracking-item {
  background-color: #fff;
  border-radius: 12rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.tracking-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 2rpx solid #f8f8f8;
}

.tracking-company {
  display: flex;
  align-items: center;
}

.company-logo {
  width: 80rpx;
  height: 80rpx;
  margin-right: 20rpx;
  border-radius: 10rpx;
}

.company-info {
  display: flex;
  flex-direction: column;
}

.company-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 8rpx;
}

.tracking-number {
  font-size: 26rpx;
  color: #999;
}

.tracking-status {
  font-size: 28rpx;
  font-weight: bold;
}

.status-0 {
  color: #999;
}

.status-1, .status-2 {
  color: #3cc51f;
}

.status-3 {
  color: #ff9900;
}

.status-4 {
  color: #ff5500;
}

.status-5 {
  color: #999;
}

.tracking-content {
  padding: 20rpx 30rpx;
  border-bottom: 2rpx solid #f8f8f8;
}

.package-info, .address-info, .time-info {
  display: flex;
  margin-bottom: 12rpx;
}

.package-info:last-child {
  margin-bottom: 0;
}

.package-label, .address-label, .time-label {
  color: #999;
  font-size: 26rpx;
  margin-right: 10rpx;
  width: 150rpx;
  flex-shrink: 0;
}

.package-value, .address-value, .time-value {
  color: #333;
  font-size: 26rpx;
  flex: 1;
}

.tracking-footer {
  padding: 20rpx 30rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tracking-operations {
  display: flex;
}

.operation-btn {
  display: flex;
  align-items: center;
  margin-right: 30rpx;
  color: #666;
  font-size: 24rpx;
}

.operation-btn text {
  margin-left: 6rpx;
}

.view-detail {
  display: flex;
  align-items: center;
  color: #3cc51f;
  font-size: 26rpx;
}

.view-detail text {
  margin-right: 6rpx;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
}

.loading-icon {
  width: 40rpx;
  height: 40rpx;
  border-radius: 50%;
  border: 4rpx solid #3cc51f;
  border-top-color: transparent;
  animation: spin 0.8s linear infinite;
  margin-bottom: 20rpx;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.loading-text {
  font-size: 26rpx;
  color: #666;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
}

.empty-image {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 30rpx;
}

.empty-text {
  font-size: 30rpx;
  color: #333;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.empty-tips {
  font-size: 26rpx;
  color: #999;
  margin-bottom: 30rpx;
}

.nav-btn {
  background-color: #3cc51f;
  color: #fff;
  font-size: 28rpx;
  padding: 10rpx 40rpx;
  border-radius: 40rpx;
}

.load-more {
  text-align: center;
  padding: 30rpx 0;
  color: #999;
  font-size: 26rpx;
}
</style> 