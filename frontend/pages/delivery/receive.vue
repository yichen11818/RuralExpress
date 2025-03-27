<template>
  <view class="receive-container">
    <!-- 顶部搜索区域 -->
    <view class="search-header">
      <view class="search-box">
        <uni-icons type="search" size="18" color="#666"></uni-icons>
        <input type="text" v-model="searchText" placeholder="请输入快递单号" class="search-input" confirm-type="search" @confirm="searchPackage" />
        <text class="search-btn" @click="searchPackage">查询</text>
      </view>
    </view>
    
    <!-- 扫码区域 -->
    <view class="scan-section" @click="scanCode">
      <view class="scan-icon">
        <uni-icons type="scan" size="36" color="#3cc51f"></uni-icons>
      </view>
      <text class="scan-text">扫描快递单二维码</text>
    </view>
    
    <!-- 包裹列表区域 -->
    <view class="package-tabs">
      <view class="tab-item" :class="{ active: currentTab === 0 }" @click="switchTab(0)">待收取</view>
      <view class="tab-item" :class="{ active: currentTab === 1 }" @click="switchTab(1)">已收取</view>
    </view>
    
    <!-- 待收取列表 -->
    <view class="package-list" v-if="currentTab === 0">
      <view class="empty-tip" v-if="pendingPackages.length === 0">
        <image src="/static/images/empty.png" mode="aspectFit" class="empty-image"></image>
        <text class="empty-text">暂无待收取的包裹</text>
      </view>
      <view class="package-item" v-for="(item, index) in pendingPackages" :key="index">
        <view class="package-header">
          <view class="package-company">{{ item.company }}</view>
          <view class="package-status">{{ item.statusText }}</view>
        </view>
        <view class="package-info">
          <view class="info-item">
            <text class="info-label">包裹单号</text>
            <text class="info-value">{{ item.trackingNo }}</text>
            <text class="copy-btn" @click="copyTrackingNo(item.trackingNo)">复制</text>
          </view>
          <view class="info-item">
            <text class="info-label">收件地址</text>
            <text class="info-value">{{ item.address }}</text>
          </view>
          <view class="info-row">
            <view class="info-item half">
              <text class="info-label">联系方式</text>
              <text class="info-value">{{ item.phone }}</text>
            </view>
            <view class="info-item half">
              <text class="info-label">预计送达</text>
              <text class="info-value">{{ item.estimatedTime }}</text>
            </view>
          </view>
        </view>
        <view class="package-actions">
          <button class="action-btn track-btn" @click="trackPackage(item.trackingNo)">
            <uni-icons type="location" size="16" color="#3cc51f"></uni-icons>
            <text>物流跟踪</text>
          </button>
          <button class="action-btn receive-btn" v-if="item.status === 4" @click="receivePackage(item.id)">
            <uni-icons type="checkmarkempty" size="16" color="#fff"></uni-icons>
            <text>确认收件</text>
          </button>
          <button class="action-btn contact-btn" v-else @click="contactCourier(item.courierId)">
            <uni-icons type="phone-filled" size="16" color="#fff"></uni-icons>
            <text>联系快递员</text>
          </button>
        </view>
      </view>
    </view>
    
    <!-- 已收取列表 -->
    <view class="package-list" v-if="currentTab === 1">
      <view class="empty-tip" v-if="receivedPackages.length === 0">
        <image src="/static/images/empty.png" mode="aspectFit" class="empty-image"></image>
        <text class="empty-text">暂无已收取的包裹</text>
      </view>
      <view class="package-item" v-for="(item, index) in receivedPackages" :key="index">
        <view class="package-header">
          <view class="package-company">{{ item.company }}</view>
          <view class="package-status received">{{ item.statusText }}</view>
        </view>
        <view class="package-info">
          <view class="info-item">
            <text class="info-label">包裹单号</text>
            <text class="info-value">{{ item.trackingNo }}</text>
            <text class="copy-btn" @click="copyTrackingNo(item.trackingNo)">复制</text>
          </view>
          <view class="info-item">
            <text class="info-label">收件地址</text>
            <text class="info-value">{{ item.address }}</text>
          </view>
          <view class="info-row">
            <view class="info-item half">
              <text class="info-label">收件时间</text>
              <text class="info-value">{{ item.receivedTime }}</text>
            </view>
            <view class="info-item half">
              <text class="info-label">配送员</text>
              <text class="info-value">{{ item.courierName }}</text>
            </view>
          </view>
        </view>
        <view class="package-actions">
          <button class="action-btn track-btn" @click="trackPackage(item.trackingNo)">
            <uni-icons type="location" size="16" color="#3cc51f"></uni-icons>
            <text>物流跟踪</text>
          </button>
          <button class="action-btn review-btn" v-if="!item.hasReviewed" @click="reviewOrder(item.id)">
            <uni-icons type="star" size="16" color="#fff"></uni-icons>
            <text>评价订单</text>
          </button>
          <button class="action-btn reviewed-btn" v-else disabled>
            <uni-icons type="star-filled" size="16" color="#fff"></uni-icons>
            <text>已评价</text>
          </button>
        </view>
      </view>
      
      <!-- 加载更多 -->
      <view class="load-more" v-if="receivedPackages.length > 0 && hasMore" @click="loadMore">
        <text>加载更多</text>
      </view>
      <view class="no-more" v-if="receivedPackages.length > 0 && !hasMore">
        <text>没有更多数据了</text>
      </view>
    </view>
    
    <!-- 查询结果弹窗 -->
    <uni-popup ref="queryPopup" type="center">
      <view class="query-popup">
        <view class="popup-header">
          <text class="popup-title">包裹查询结果</text>
          <uni-icons type="close" size="20" color="#999" @click="closePopup"></uni-icons>
        </view>
        <view class="popup-content" v-if="queryResult">
          <view class="result-item">
            <text class="result-label">快递公司</text>
            <text class="result-value">{{ queryResult.company }}</text>
          </view>
          <view class="result-item">
            <text class="result-label">运单编号</text>
            <text class="result-value">{{ queryResult.trackingNo }}</text>
          </view>
          <view class="result-item">
            <text class="result-label">当前状态</text>
            <text class="result-value">{{ queryResult.statusText }}</text>
          </view>
          <view class="result-item">
            <text class="result-label">收件人</text>
            <text class="result-value">{{ queryResult.receiverName }}</text>
          </view>
          <view class="result-item">
            <text class="result-label">联系电话</text>
            <text class="result-value">{{ queryResult.receiverPhone }}</text>
          </view>
          <view class="result-item">
            <text class="result-label">收件地址</text>
            <text class="result-value">{{ queryResult.address }}</text>
          </view>
          <view class="result-item">
            <text class="result-label">预计送达</text>
            <text class="result-value">{{ queryResult.estimatedTime }}</text>
          </view>
        </view>
        <view class="popup-content empty-result" v-else>
          <uni-icons type="info" size="64" color="#999"></uni-icons>
          <text>未查询到相关包裹信息</text>
          <text class="tip-text">请确认单号是否正确，或联系寄件人确认</text>
        </view>
        <view class="popup-footer">
          <button class="popup-btn cancel-btn" @click="closePopup">关闭</button>
          <button class="popup-btn confirm-btn" v-if="queryResult" @click="addToMyPackage">添加到我的包裹</button>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script>
import uniIcons from '@/uni_modules/uni-icons/components/uni-icons/uni-icons.vue'
import uniPopup from '@/uni_modules/uni-popup/components/uni-popup/uni-popup.vue'
import { isLoggedIn } from '@/api/auth';

export default {
  components: {
    uniIcons,
    uniPopup
  },
  data() {
    return {
      searchText: '',
      currentTab: 0,
      page: 1,
      hasMore: true,
      queryResult: null,
      pendingPackages: [
        {
          id: 1,
          company: '顺丰速运',
          trackingNo: 'SF1234567890',
          status: 3, // 0-待揽收，1-已揽收，2-运输中，3-已到达，4-派送中，5-已签收
          statusText: '已到达',
          address: '江西省南昌市青山湖区艾溪湖北路77号',
          phone: '138****6677',
          estimatedTime: '今天 18:00前',
          courierId: 1,
          courierName: '张师傅'
        },
        {
          id: 2,
          company: '中通快递',
          trackingNo: 'ZT9876543210',
          status: 4, // 0-待揽收，1-已揽收，2-运输中，3-已到达，4-派送中，5-已签收
          statusText: '派送中',
          address: '江西省南昌市青山湖区高新开发区创新一路',
          phone: '152****8899',
          estimatedTime: '今天 16:00前',
          courierId: 2,
          courierName: '李师傅'
        }
      ],
      receivedPackages: [
        {
          id: 3,
          company: '圆通速递',
          trackingNo: 'YT5678901234',
          status: 5, // 0-待揽收，1-已揽收，2-运输中，3-已到达，4-派送中，5-已签收
          statusText: '已签收',
          address: '江西省南昌市青山湖区高新开发区创新一路',
          phone: '152****8899',
          receivedTime: '2023-03-18 15:23',
          courierId: 3,
          courierName: '王师傅',
          hasReviewed: true
        },
        {
          id: 4,
          company: '京东物流',
          trackingNo: 'JD6789012345',
          status: 5, // 0-待揽收，1-已揽收，2-运输中，3-已到达，4-派送中，5-已签收
          statusText: '已签收',
          address: '江西省南昌市青山湖区艾溪湖北路77号',
          phone: '138****6677',
          receivedTime: '2023-03-15 10:45',
          courierId: 1,
          courierName: '张师傅',
          hasReviewed: false
        }
      ]
    };
  },
  
  onLoad() {
    // 检查登录状态
    if (!isLoggedIn()) {
      uni.showModal({
        title: '提示',
        content: '请先登录后再查询包裹',
        showCancel: false,
        success: () => {
          uni.navigateTo({
            url: '/pages/login/login'
          });
        }
      });
      return;
    }
    
    // 加载用户包裹列表
    // this.loadPackages();
  },
  
  onPullDownRefresh() {
    // 重新加载数据
    this.page = 1;
    // this.loadPackages();
    setTimeout(() => {
      uni.stopPullDownRefresh();
    }, 1000);
  },
  
  methods: {
    // 加载包裹列表
    loadPackages() {
      // 这里应该请求后端API获取包裹列表
      // 示例：
      /*
      uni.request({
        url: 'https://api.example.com/packages',
        method: 'GET',
        data: {
          type: this.currentTab === 0 ? 'pending' : 'received',
          page: this.page
        },
        success: (res) => {
          if (res.data.success) {
            if (this.currentTab === 0) {
              this.pendingPackages = res.data.data;
            } else {
              if (this.page === 1) {
                this.receivedPackages = res.data.data;
              } else {
                this.receivedPackages = [...this.receivedPackages, ...res.data.data];
              }
              this.hasMore = res.data.hasMore;
            }
          } else {
            uni.showToast({
              title: res.data.message || '获取包裹列表失败',
              icon: 'none'
            });
          }
        },
        fail: () => {
          uni.showToast({
            title: '网络异常，请稍后重试',
            icon: 'none'
          });
        }
      });
      */
    },
    
    // 切换标签页
    switchTab(index) {
      if (this.currentTab === index) return;
      this.currentTab = index;
      this.page = 1;
      // this.loadPackages();
    },
    
    // 扫描二维码
    scanCode() {
      uni.scanCode({
        scanType: ['qrCode', 'barCode'],
        success: (res) => {
          this.searchText = res.result;
          this.searchPackage();
        },
        fail: () => {
          uni.showToast({
            title: '扫码失败，请重试',
            icon: 'none'
          });
        }
      });
    },
    
    // 搜索包裹
    searchPackage() {
      if (!this.searchText) {
        return uni.showToast({
          title: '请输入或扫描快递单号',
          icon: 'none'
        });
      }
      
      // 显示加载提示
      uni.showLoading({
        title: '正在查询...'
      });
      
      // 模拟查询结果
      setTimeout(() => {
        uni.hideLoading();
        
        // 模拟查询成功
        if (this.searchText.length >= 8) {
          this.queryResult = {
            company: '顺丰速运',
            trackingNo: this.searchText,
            status: 3,
            statusText: '已到达',
            receiverName: '张三',
            receiverPhone: '138****6677',
            address: '江西省南昌市青山湖区艾溪湖北路77号',
            estimatedTime: '今天 18:00前'
          };
        } else {
          this.queryResult = null;
        }
        
        // 显示弹窗
        this.$refs.queryPopup.open();
      }, 1500);
      
      // 实际应用中，这里应该调用API查询包裹
      /*
      uni.request({
        url: 'https://api.example.com/package/query',
        method: 'GET',
        data: {
          trackingNo: this.searchText
        },
        success: (res) => {
          uni.hideLoading();
          if (res.data.success) {
            this.queryResult = res.data.data;
          } else {
            this.queryResult = null;
          }
          this.$refs.queryPopup.open();
        },
        fail: () => {
          uni.hideLoading();
          uni.showToast({
            title: '网络异常，请稍后重试',
            icon: 'none'
          });
        }
      });
      */
    },
    
    // 关闭弹窗
    closePopup() {
      this.$refs.queryPopup.close();
    },
    
    // 添加到我的包裹
    addToMyPackage() {
      // 这里应该调用API添加包裹到用户的包裹列表
      // 示例：
      /*
      uni.request({
        url: 'https://api.example.com/package/add',
        method: 'POST',
        data: {
          trackingNo: this.queryResult.trackingNo
        },
        success: (res) => {
          if (res.data.success) {
            uni.showToast({
              title: '添加成功',
              icon: 'success'
            });
            this.closePopup();
            this.page = 1;
            this.loadPackages();
          } else {
            uni.showToast({
              title: res.data.message || '添加失败',
              icon: 'none'
            });
          }
        },
        fail: () => {
          uni.showToast({
            title: '网络异常，请稍后重试',
            icon: 'none'
          });
        }
      });
      */
      
      // 模拟添加成功
      uni.showToast({
        title: '添加成功',
        icon: 'success'
      });
      this.closePopup();
      
      // 模拟添加到列表
      const index = this.pendingPackages.findIndex(item => item.id === id);
      if (index !== -1) {
        const packageItem = this.pendingPackages[index];
        packageItem.status = 5;
        packageItem.statusText = '已签收';
        packageItem.receivedTime = this.formatDate(new Date());
        packageItem.hasReviewed = false;
        this.receivedPackages.unshift(packageItem);
        this.pendingPackages.splice(index, 1);
      }
    },
    
    // 确认收件
    receivePackage(id) {
      uni.showModal({
        title: '确认收件',
        content: '确认已收到该包裹？',
        success: (res) => {
          if (res.confirm) {
            // 这里应该调用API确认收件
            // 示例：
            /*
            uni.request({
              url: `https://api.example.com/package/receive/${id}`,
              method: 'POST',
              success: (res) => {
                if (res.data.success) {
                  uni.showToast({
                    title: '收件成功',
                    icon: 'success'
                  });
                  this.page = 1;
                  this.loadPackages();
                } else {
                  uni.showToast({
                    title: res.data.message || '收件失败',
                    icon: 'none'
                  });
                }
              },
              fail: () => {
                uni.showToast({
                  title: '网络异常，请稍后重试',
                  icon: 'none'
                });
              }
            });
            */
            
            // 模拟收件成功
            uni.showToast({
              title: '收件成功',
              icon: 'success'
            });
            
            // 模拟更新列表
            const index = this.pendingPackages.findIndex(item => item.id === id);
            if (index !== -1) {
              const packageItem = this.pendingPackages[index];
              packageItem.status = 5;
              packageItem.statusText = '已签收';
              packageItem.receivedTime = this.formatDate(new Date());
              packageItem.hasReviewed = false;
              this.receivedPackages.unshift(packageItem);
              this.pendingPackages.splice(index, 1);
            }
          }
        }
      });
    },
    
    // 追踪包裹
    trackPackage(trackingNo) {
      uni.navigateTo({
        url: `/pages/order/track?trackingNo=${trackingNo}`
      });
    },
    
    // 联系快递员
    contactCourier(courierId) {
      uni.navigateTo({
        url: `/pages/courier/detail?id=${courierId}`
      });
    },
    
    // 评价订单
    reviewOrder(id) {
      uni.navigateTo({
        url: `/pages/order/review?id=${id}`
      });
    },
    
    // 复制单号
    copyTrackingNo(trackingNo) {
      uni.setClipboardData({
        data: trackingNo,
        success: () => {
          uni.showToast({
            title: '复制成功',
            icon: 'success'
          });
        }
      });
    },
    
    // 加载更多
    loadMore() {
      if (!this.hasMore) return;
      this.page++;
      // this.loadPackages();
    },
    
    // 格式化日期
    formatDate(date) {
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      const hour = date.getHours().toString().padStart(2, '0');
      const minute = date.getMinutes().toString().padStart(2, '0');
      return `${year}-${month}-${day} ${hour}:${minute}`;
    }
  }
};
</script>

<style>
.receive-container {
  min-height: 100vh;
  background-color: #f8f8f8;
}

/* 搜索区域样式 */
.search-header {
  background-color: #3cc51f;
  padding: 20rpx 30rpx;
}

.search-box {
  display: flex;
  align-items: center;
  background-color: #fff;
  height: 80rpx;
  border-radius: 40rpx;
  padding: 0 30rpx;
}

.search-input {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  font-size: 28rpx;
  margin: 0 20rpx;
}

.search-btn {
  color: #3cc51f;
  font-size: 28rpx;
  font-weight: bold;
}

/* 扫码区域样式 */
.scan-section {
  background-color: #fff;
  height: 180rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-bottom: 20rpx;
}

.scan-icon {
  margin-bottom: 10rpx;
}

.scan-text {
  font-size: 28rpx;
  color: #666;
}

/* 标签页样式 */
.package-tabs {
  display: flex;
  background-color: #fff;
  margin-bottom: 20rpx;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 24rpx 0;
  font-size: 28rpx;
  color: #666;
  position: relative;
}

.tab-item.active {
  color: #3cc51f;
  font-weight: bold;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 120rpx;
  height: 4rpx;
  background-color: #3cc51f;
  border-radius: 2rpx;
}

/* 包裹列表样式 */
.package-list {
  padding-bottom: 40rpx;
}

.package-item {
  background-color: #fff;
  margin-bottom: 20rpx;
  padding: 30rpx;
  border-radius: 10rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  margin-left: 20rpx;
  margin-right: 20rpx;
}

.package-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.package-company {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.package-status {
  font-size: 26rpx;
  color: #3cc51f;
}

.package-status.received {
  color: #999;
}

.package-info {
  margin-bottom: 20rpx;
}

.info-item {
  margin-bottom: 16rpx;
  display: flex;
  align-items: center;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-row {
  display: flex;
}

.info-item.half {
  flex: 1;
  margin-bottom: 0;
}

.info-label {
  font-size: 26rpx;
  color: #999;
  margin-right: 20rpx;
  min-width: 120rpx;
}

.info-value {
  font-size: 26rpx;
  color: #333;
  flex: 1;
}

.copy-btn {
  font-size: 24rpx;
  color: #3cc51f;
  margin-left: 20rpx;
  flex-shrink: 0;
}

.package-actions {
  display: flex;
}

.action-btn {
  flex: 1;
  height: 70rpx;
  border-radius: 35rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26rpx;
  margin: 0;
  padding: 0;
}

.action-btn text {
  margin-left: 6rpx;
}

.track-btn {
  background-color: #fff;
  color: #3cc51f;
  border: 1rpx solid #3cc51f;
  margin-right: 20rpx;
}

.receive-btn, .review-btn {
  background-color: #3cc51f;
  color: #fff;
}

.contact-btn {
  background-color: #ff9900;
  color: #fff;
}

.reviewed-btn {
  background-color: #ccc;
  color: #fff;
}

/* 弹窗样式 */
.query-popup {
  width: 620rpx;
  background-color: #fff;
  border-radius: 20rpx;
  overflow: hidden;
}

.popup-header {
  padding: 30rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1rpx solid #f0f0f0;
}

.popup-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.popup-content {
  padding: 30rpx;
  max-height: 600rpx;
  overflow-y: auto;
}

.result-item {
  margin-bottom: 20rpx;
  display: flex;
}

.result-label {
  font-size: 28rpx;
  color: #666;
  min-width: 140rpx;
  margin-right: 20rpx;
}

.result-value {
  font-size: 28rpx;
  color: #333;
  flex: 1;
}

.empty-result {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx 0;
  color: #666;
  font-size: 28rpx;
}

.tip-text {
  font-size: 24rpx;
  color: #999;
  margin-top: 10rpx;
}

.popup-footer {
  display: flex;
  border-top: 1rpx solid #f0f0f0;
}

.popup-btn {
  flex: 1;
  height: 90rpx;
  line-height: 90rpx;
  text-align: center;
  font-size: 32rpx;
  margin: 0;
  border-radius: 0;
}

.cancel-btn {
  background-color: #f5f5f5;
  color: #666;
}

.confirm-btn {
  background-color: #3cc51f;
  color: #fff;
}

/* 空列表提示样式 */
.empty-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100rpx 0;
}

.empty-image {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 20rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

/* 加载更多样式 */
.load-more, .no-more {
  text-align: center;
  padding: 30rpx;
  color: #999;
  font-size: 26rpx;
}

.load-more {
  color: #3cc51f;
}
</style> 