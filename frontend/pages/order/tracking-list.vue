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
      
      getTrackingList(params)
        .then(res => {
          console.log('物流列表返回数据：', res)
          
          if (res.code === 200 && res.data) {
            // 提取或转换数据
            let list = []
            let total = 0
            
            // 处理不同的后端返回数据结构
            if (Array.isArray(res.data)) {
              // 如果是数组，直接使用
              list = res.data
              total = res.data.length
            } else if (res.data.list) {
              // 如果有list字段，按分页格式处理
              list = res.data.list || []
              total = res.data.total || list.length
            } else if (res.data.records) {
              // 适配另一种后端分页格式
              list = res.data.records || []
              total = res.data.total || list.length
            }
            
            // 确保每条数据都有必要的字段
            list = list.map(item => ({
              id: item.id,
              trackingNo: item.trackingNo || item.trackingNumber || item.waybillNo || '',
              company: item.company || item.courierCompany || item.expressCompany || '',
              logo: item.logo || item.companyLogo || `/static/images/icon/${this.getCompanyCode(item.company)}.png`,
              status: item.status || 0,
              packageInfo: item.packageInfo || item.goodsInfo || `${item.packageType || '包裹'} ${item.weight || ''} ${item.goodsDesc || ''}`,
              address: item.address || item.deliveryAddress || '',
              updateTime: item.updateTime || item.lastUpdateTime || new Date().toLocaleString()
            }))
            
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
            // API调用失败时使用临时模拟数据
            this.useTempMockData();
          }
        })
        .catch(err => {
          console.error('获取物流列表失败', err)
          uni.showToast({
            title: '获取物流列表失败，请检查网络连接',
            icon: 'none'
          })
          // 网络错误时使用临时模拟数据
          this.useTempMockData();
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
      const statusMap = {
        1: '已揽收',
        2: '运输中',
        3: '派送中',
        4: '已签收',
        5: '已完成',
        6: '异常',
        0: '未知'
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
    },
    
    // 使用临时模拟数据
    useTempMockData() {
      console.log('使用临时模拟数据')
      
      const mockData = [
        {
          id: 1,
          trackingNo: 'SF1234567890',
          company: '顺丰速运',
          logo: '/static/images/icon/package.png',
          status: 2,
          packageInfo: '文件包裹 2kg',
          address: '江西省赣州市章贡区红旗大道123号',
          updateTime: new Date().toLocaleString()
        },
        {
          id: 2,
          trackingNo: 'YT9876543210',
          company: '圆通快递',
          logo: '/static/images/icon/package.png',
          status: 4,
          packageInfo: '衣服 1kg',
          address: '江西省赣州市南康区健康路456号',
          updateTime: new Date().toLocaleString()
        },
        {
          id: 3,
          trackingNo: 'ZT5678901234',
          company: '中通快递',
          logo: '/static/images/icon/package.png',
          status: 5,
          packageInfo: '电子产品 3kg',
          address: '江西省赣州市赣县区红金大道789号',
          updateTime: new Date(Date.now() - 86400000).toLocaleString()
        }
      ]
      
      if (this.page === 1) {
        this.trackingList = mockData
        this.hasMore = true
      } else if (this.page === 2) {
        // 模拟第二页数据
        const page2Data = [
          {
            id: 4,
            trackingNo: 'JD2345678901',
            company: '京东物流',
            logo: '/static/images/icon/package.png',
            status: 3,
            packageInfo: '日用品 1.5kg',
            address: '江西省赣州市章贡区黄金大道100号',
            updateTime: new Date(Date.now() - 172800000).toLocaleString()
          }
        ]
        this.trackingList = [...this.trackingList, ...page2Data]
        this.hasMore = false
      }
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