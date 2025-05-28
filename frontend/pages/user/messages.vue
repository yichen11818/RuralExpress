<template>
  <view class="message-container">
    <view class="message-header">
      <text class="header-title">消息中心</text>
      <text class="read-all" @click="markAllAsRead">全部已读</text>
    </view>
    
    <view class="message-tabs">
      <view 
        class="tab-item" 
        :class="{ active: activeTab === 'all' }" 
        @click="switchTab('all')"
      >
        全部消息
      </view>
      <view 
        class="tab-item" 
        :class="{ active: activeTab === 'system' }" 
        @click="switchTab('system')"
      >
        系统消息
      </view>
      <view 
        class="tab-item" 
        :class="{ active: activeTab === 'courier' }" 
        @click="switchTab('courier')"
        v-if="isAdmin"
      >
        快递员申请
      </view>
      <view 
        class="tab-item" 
        :class="{ active: activeTab === 'order' }" 
        @click="switchTab('order')"
      >
        订单通知
      </view>
    </view>
    
    <view class="message-list">
      <view v-if="loading" class="loading">
        <uni-icons type="spinner-cycle" size="30" color="#3cc51f"></uni-icons>
        <text class="loading-text">加载中...</text>
      </view>
      
      <view v-else-if="messageList.length === 0" class="empty">
        <image class="empty-image" src="/static/images/empty.png" mode="aspectFit"></image>
        <text class="empty-text">暂无消息</text>
      </view>
      
      <view v-else>
        <view 
          class="message-item" 
          v-for="(item, index) in messageList" 
          :key="index"
          :class="{ 'unread': item.status === 0 }"
          @click="viewMessage(item)"
        >
          <view class="message-dot" v-if="item.status === 0"></view>
          <view class="message-icon">
            <uni-icons :type="getMessageIcon(item.type)" size="24" color="#3cc51f"></uni-icons>
          </view>
          <view class="message-content">
            <view class="message-title">{{ item.title }}</view>
            <view class="message-brief">{{ item.content }}</view>
            <view class="message-time">{{ formatTime(item.createdAt) }}</view>
          </view>
          <view class="message-action" v-if="item.type === 1 && isAdmin">
            <view class="action-btn approve" @click.stop="handleApprove(item)">通过</view>
            <view class="action-btn reject" @click.stop="handleReject(item)">拒绝</view>
          </view>
        </view>
      </view>
    </view>
    
    <uni-load-more :status="loadMoreStatus" v-if="messageList.length > 0"></uni-load-more>
  </view>
</template>

<script>
import { getUserMessages, markMessageAsRead, getPendingCourierApplications } from '@/api/message';
import { auditCourier } from '@/api/courier';
import { getUserInfo } from '@/api/auth';

export default {
  data() {
    return {
      activeTab: 'all',
      messageList: [],
      page: 1,
      pageSize: 10,
      total: 0,
      loading: true,
      loadMoreStatus: 'more',
      isAdmin: false
    };
  },
  
  onLoad() {
    const userInfo = getUserInfo();
    this.isAdmin = userInfo && userInfo.userType === 2;
    this.loadMessages();
  },
  
  onPullDownRefresh() {
    this.page = 1;
    this.messageList = [];
    this.loadMessages(() => {
      uni.stopPullDownRefresh();
    });
  },
  
  onReachBottom() {
    if (this.messageList.length < this.total) {
      this.loadMoreStatus = 'loading';
      this.page += 1;
      this.loadMessages();
    }
  },
  
  methods: {
    // 加载消息列表
    loadMessages(callback) {
      this.loading = true;
      
      let apiCall;
      if (this.activeTab === 'courier' && this.isAdmin) {
        apiCall = getPendingCourierApplications(this.page, this.pageSize);
      } else {
        // 根据不同标签筛选消息类型
        const typeMap = {
          'all': -1, // 全部
          'system': 0, // 系统消息
          'courier': 1, // 快递员申请
          'order': 2 // 订单通知
        };
        
        const type = typeMap[this.activeTab];
        apiCall = getUserMessages(this.page, this.pageSize, type !== -1 ? type : null);
      }
      
      apiCall.then(res => {
        if (res.code === 200) {
          const { records, total } = res.data;
          
          if (this.page === 1) {
            this.messageList = records;
          } else {
            this.messageList = [...this.messageList, ...records];
          }
          
          this.total = total;
          this.loadMoreStatus = this.messageList.length >= total ? 'noMore' : 'more';
        } else {
          uni.showToast({
            title: res.msg || '加载消息失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        console.error('加载消息失败', err);
        uni.showToast({
          title: '加载消息失败',
          icon: 'none'
        });
      }).finally(() => {
        this.loading = false;
        callback && callback();
      });
    },
    
    // 切换标签
    switchTab(tab) {
      if (this.activeTab !== tab) {
        this.activeTab = tab;
        this.page = 1;
        this.messageList = [];
        this.loadMessages();
      }
    },
    
    // 查看消息
    viewMessage(item) {
      if (item.status === 0) {
        // 标记为已读
        markMessageAsRead(item.id).then(res => {
          if (res.code === 200) {
            item.status = 1;
          }
        });
      }
      
      // 根据消息类型和targetId跳转到相应页面
      if (item.type === 1 && item.targetId && this.isAdmin) {
        // 快递员申请，跳转到审核页面
        uni.navigateTo({
          url: `/pages/admin/couriers/audit?id=${item.targetId}`
        });
      } else if (item.type === 2 && item.targetId) {
        // 订单通知，跳转到订单详情
        uni.navigateTo({
          url: `/pages/order/detail?id=${item.targetId}`
        });
      }
    },
    
    // 全部标记为已读
    markAllAsRead() {
      uni.showModal({
        title: '提示',
        content: '确定将全部消息标记为已读吗？',
        success: (res) => {
          if (res.confirm) {
            // 调用全部已读API
            // 这里需要后端提供一个批量标记已读的接口
            uni.showToast({
              title: '已全部标记为已读',
              icon: 'success'
            });
            
            // 更新本地消息状态
            this.messageList.forEach(item => {
              item.status = 1;
            });
          }
        }
      });
    },
    
    // 审核通过
    handleApprove(item) {
      if (!item.targetId) return;
      
      uni.showModal({
        title: '审核确认',
        content: '确定通过该快递员申请吗？',
        success: (res) => {
          if (res.confirm) {
            auditCourier(item.targetId, 1).then(res => {
              if (res.code === 200) {
                uni.showToast({
                  title: '审核通过成功',
                  icon: 'success'
                });
                
                // 刷新消息列表
                this.page = 1;
                this.messageList = [];
                this.loadMessages();
              } else {
                uni.showToast({
                  title: res.msg || '操作失败',
                  icon: 'none'
                });
              }
            });
          }
        }
      });
    },
    
    // 审核拒绝
    handleReject(item) {
      if (!item.targetId) return;
      
      uni.showModal({
        title: '审核确认',
        content: '确定拒绝该快递员申请吗？',
        success: (res) => {
          if (res.confirm) {
            auditCourier(item.targetId, 2).then(res => {
              if (res.code === 200) {
                uni.showToast({
                  title: '已拒绝申请',
                  icon: 'success'
                });
                
                // 刷新消息列表
                this.page = 1;
                this.messageList = [];
                this.loadMessages();
              } else {
                uni.showToast({
                  title: res.msg || '操作失败',
                  icon: 'none'
                });
              }
            });
          }
        }
      });
    },
    
    // 获取消息图标
    getMessageIcon(type) {
      const iconMap = {
        0: 'info', // 系统消息
        1: 'staff', // 快递员申请
        2: 'list' // 订单通知
      };
      return iconMap[type] || 'info';
    },
    
    // 格式化时间
    formatTime(timestamp) {
      if (!timestamp) return '';
      
      const date = new Date(timestamp);
      const now = new Date();
      const diff = now - date;
      
      // 一小时内
      if (diff < 3600000) {
        const minutes = Math.floor(diff / 60000);
        return minutes <= 0 ? '刚刚' : `${minutes}分钟前`;
      }
      
      // 一天内
      if (diff < 86400000) {
        const hours = Math.floor(diff / 3600000);
        return `${hours}小时前`;
      }
      
      // 一周内
      if (diff < 604800000) {
        const days = Math.floor(diff / 86400000);
        return `${days}天前`;
      }
      
      // 超过一周，显示具体日期
      const year = date.getFullYear();
      const month = date.getMonth() + 1;
      const day = date.getDate();
      return `${year}-${month < 10 ? '0' + month : month}-${day < 10 ? '0' + day : day}`;
    }
  }
};
</script>

<style>
.message-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 30rpx;
}

.message-header {
  padding: 30rpx;
  background-color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1rpx solid #f0f0f0;
}

.header-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.read-all {
  font-size: 28rpx;
  color: #3cc51f;
}

.message-tabs {
  display: flex;
  background-color: #fff;
  border-bottom: 1rpx solid #f0f0f0;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 20rpx 0;
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
  width: 60rpx;
  height: 4rpx;
  background-color: #3cc51f;
  border-radius: 2rpx;
}

.message-list {
  padding: 20rpx 0;
}

.loading, .empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
}

.loading-text, .empty-text {
  font-size: 28rpx;
  color: #999;
  margin-top: 20rpx;
}

.empty-image {
  width: 200rpx;
  height: 200rpx;
}

.message-item {
  display: flex;
  align-items: center;
  padding: 30rpx;
  background-color: #fff;
  margin-bottom: 2rpx;
  position: relative;
}

.message-item.unread {
  background-color: #f8fffa;
}

.message-dot {
  position: absolute;
  top: 30rpx;
  left: 20rpx;
  width: 16rpx;
  height: 16rpx;
  background-color: #ff5a5f;
  border-radius: 50%;
}

.message-icon {
  margin-right: 20rpx;
}

.message-content {
  flex: 1;
}

.message-title {
  font-size: 32rpx;
  color: #333;
  margin-bottom: 10rpx;
  font-weight: 500;
}

.message-brief {
  font-size: 28rpx;
  color: #666;
  margin-bottom: 10rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.message-time {
  font-size: 24rpx;
  color: #999;
}

.message-action {
  display: flex;
  margin-left: 20rpx;
}

.action-btn {
  padding: 10rpx 20rpx;
  border-radius: 30rpx;
  font-size: 24rpx;
  margin-left: 10rpx;
}

.approve {
  color: #3cc51f;
  background-color: rgba(60, 197, 31, 0.1);
}

.reject {
  color: #ff5a5f;
  background-color: rgba(255, 90, 95, 0.1);
}
</style> 