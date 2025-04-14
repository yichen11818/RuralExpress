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
            <image :src="item.logo || '/static/images/icon/package.png'" mode="aspectFit" class="company-logo"></image>
            <view class="company-info">
              <text class="company-name">{{ item.company }}</text>
              <text class="tracking-number">{{ item.trackingNo }}</text>
            </view>
          </view>
          <view class="tracking-status" :class="getStatusClass(item.status)">
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
        <image src="/static/images/empty.png" mode="aspectFit" class="empty-image"></image>
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
      
      // 打印请求参数，便于调试
      console.log('请求物流列表参数：', params)
      
      // 移除模拟数据设置，直接使用API数据
      getTrackingList(params)
        .then(res => {
          // 打印响应
          console.log('物流列表响应：', res)
          
          if (res.code === 200) {
            const data = res.data
            
            if (append) {
              // 追加数据
              this.trackingList = [...this.trackingList, ...data.list]
            } else {
              // 重置数据
              this.trackingList = data.list
            }
            
            // 打印每个项目的状态信息，用于调试
            this.trackingList.forEach((item, index) => {
              console.log(`物流项[${index}] status:`, item.status, typeof item.status)
              
              // 确保状态是数字类型
              if (item.status !== undefined && item.status !== null) {
                item.status = Number(item.status)
              }
            })
            
            // 更新分页信息
            this.hasMore = data.list.length >= this.pageSize
            
            // 处理空数据情况
            if (this.trackingList.length === 0) {
              uni.showToast({
                title: '暂无物流记录',
                icon: 'none'
              })
            }
          } else {
            uni.showToast({
              title: res.message || '获取物流列表失败',
              icon: 'none'
            })
          }
        })
        .catch(err => {
          console.error('获取物流列表异常：', err)
          uni.showToast({
            title: '网络错误，请稍后重试',
            icon: 'none'
          })
        })
        .finally(() => {
          this.loading = false
          this.loadingMore = false
          uni.stopPullDownRefresh()
        })
    },
    
    // 根据快递公司名称获取代码
    getCompanyCode(companyName) {
      const companyMap = {
        '顺丰': 'sf',
        '顺丰速运': 'sf',
        '圆通': 'yt',
        '圆通快递': 'yt',
        '中通': 'zt',
        '中通快递': 'zt',
        '申通': 'sto',
        '申通快递': 'sto',
        '韵达': 'yd',
        '韵达快递': 'yd',
        '百世': 'ht',
        '百世快递': 'ht',
        '京东': 'jd',
        '京东物流': 'jd',
        '邮政': 'ems',
        '邮政快递': 'ems',
        'EMS': 'ems'
      }
      
      // 从名称中提取关键字
      let code = 'package'
      for (const [key, value] of Object.entries(companyMap)) {
        if (companyName && companyName.includes(key)) {
          code = value
          break
        }
      }
      
      return code
    },
    
    // 加载更多
    loadMore() {
      if (!this.hasMore) return
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
      
      console.log('刷新物流信息，单号：', trackingNo)
      
      getLogisticsInfo({ trackingNo })
        .then(res => {
          console.log('刷新物流返回数据：', res)
          
          if (res.code === 200 && res.data) {
            // 提取物流信息
            const logisticsData = res.data
            
            // 更新物流信息
            this.trackingList[index] = {
              ...this.trackingList[index],
              status: logisticsData.status || logisticsData.logisticsStatus || 0,
              updateTime: logisticsData.updateTime || logisticsData.lastUpdateTime || new Date().toLocaleString(),
              packageInfo: logisticsData.packageInfo || logisticsData.goodsInfo || this.trackingList[index].packageInfo
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
            title: '刷新失败，请检查网络连接',
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
        imageUrl: '/static/images/icon/package.png',
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
      // 调试状态信息
      console.log('getStatusText - 收到的状态：', status, typeof status)
      
      // 确保状态是数字
      status = Number(status)
      
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
    
    // 获取状态样式类
    getStatusClass(status) {
      // 确保状态是数字
      status = Number(status)
      
      const statusMap = {
        0: 'status-waiting',
        1: 'status-collected',
        2: 'status-transporting',
        3: 'status-arrived',
        4: 'status-delivering',
        5: 'status-signed'
      }
      return statusMap[status] || ''
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

.status-waiting {
  color: #999;
}

.status-collected {
  color: #3cc51f;
}

.status-transporting {
  color: #ff9900;
}

.status-arrived {
  color: #ff5500;
}

.status-delivering {
  color: #ff5500;
}

.status-signed {
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