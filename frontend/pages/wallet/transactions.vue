<template>
  <view class="transactions-container">
    <!-- 头部信息 -->
    <view class="header">
      <view class="back-btn" @click="goBack">
        <uni-icons type="left" size="18" color="#333"></uni-icons>
      </view>
      <text class="title">交易记录</text>
    </view>
    
    <!-- 筛选选项 -->
    <view class="filter-section">
      <view 
        class="filter-item" 
        v-for="(item, index) in filterOptions" 
        :key="index"
        :class="{ active: currentFilter === item.value }"
        @click="switchFilter(item.value)"
      >
        {{ item.label }}
      </view>
    </view>
    
    <!-- 交易记录列表 -->
    <scroll-view 
      scroll-y 
      class="transaction-list-wrapper"
      @scrolltolower="loadMoreTransactions"
      refresher-enabled
      :refresher-triggered="refreshing"
      @refresherrefresh="refreshTransactions"
    >
      <view class="transaction-list" v-if="transactions.length > 0">
        <view 
          class="transaction-item" 
          v-for="(item, index) in transactions" 
          :key="index"
          @click="showTransactionDetail(item)"
        >
          <view class="transaction-info">
            <text class="transaction-type">{{ getTransactionTypeName(item.type) }}</text>
            <text class="transaction-desc">{{ item.description || getDefaultDescription(item) }}</text>
            <text class="transaction-time">{{ formatDate(item.createTime) }}</text>
          </view>
          <view class="transaction-amount" :class="item.amount > 0 ? 'income' : 'expense'">
            {{ item.amount > 0 ? '+' : '' }}{{ item.amount.toFixed(2) }}
          </view>
        </view>
      </view>
      
      <!-- 加载更多 -->
      <view class="load-more" v-if="transactions.length > 0 && hasMore">
        <text v-if="loading">加载中...</text>
        <text v-else @click="loadMoreTransactions">点击加载更多</text>
      </view>
      
      <!-- 无更多数据 -->
      <view class="no-more" v-if="transactions.length > 0 && !hasMore">
        <text>没有更多数据了</text>
      </view>
      
      <!-- 空状态 -->
      <view class="empty-state" v-if="transactions.length === 0 && !loading">
        <image class="empty-image" src="/static/images/empty.png" mode="aspectFit"></image>
        <text class="empty-text">暂无交易记录</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import uniIcons from '@/uni_modules/uni-icons/components/uni-icons/uni-icons.vue';
import { isLoggedIn } from '@/api/auth';

export default {
  components: {
    uniIcons
  },
  data() {
    return {
      currentFilter: 'all',
      filterOptions: [
        { label: '全部', value: 'all' },
        { label: '充值', value: 'RECHARGE' },
        { label: '提现', value: 'WITHDRAW' },
        { label: '消费', value: 'PAYMENT' },
        { label: '退款', value: 'REFUND' }
      ],
      transactions: [],
      loading: false,
      refreshing: false,
      hasMore: true,
      pageInfo: {
        page: 1,
        size: 20,
        total: 0
      }
    };
  },
  
  onLoad() {
    // 检查登录状态
    if (!isLoggedIn()) {
      uni.navigateTo({
        url: '/pages/login/login'
      });
      return;
    }
    
    // 加载交易记录
    this.loadTransactions();
  },
  
  methods: {
    // 加载交易记录
    loadTransactions(append = false) {
      if (this.loading) return;
      this.loading = true;
      
      if (!append) {
        uni.showLoading({
          title: '加载中...'
        });
      }
      
      // 模拟API获取交易记录
      setTimeout(() => {
        // 模拟数据
        const mockData = this.generateMockTransactions(this.currentFilter);
        
        // 更新数据
        if (append) {
          this.transactions = [...this.transactions, ...mockData.records];
        } else {
          this.transactions = mockData.records;
        }
        
        this.pageInfo.total = mockData.total;
        this.hasMore = this.transactions.length < mockData.total;
        
        uni.hideLoading();
        this.loading = false;
        
        if (this.refreshing) {
          this.refreshing = false;
        }
      }, 1000);
      
      /*
      // 调用API获取交易记录
      getTransactions({
        page: this.pageInfo.page,
        size: this.pageInfo.size,
        type: this.currentFilter === 'all' ? null : this.currentFilter
      })
        .then(res => {
          if (res.code === 200 && res.data) {
            if (append) {
              this.transactions = [...this.transactions, ...(res.data.records || [])];
            } else {
              this.transactions = res.data.records || [];
            }
            
            this.pageInfo.total = res.data.total || 0;
            this.hasMore = this.transactions.length < this.pageInfo.total;
          } else {
            uni.showToast({
              title: res.message || '获取交易记录失败',
              icon: 'none'
            });
          }
        })
        .catch(err => {
          console.error('获取交易记录失败', err);
          uni.showToast({
            title: '获取交易记录失败',
            icon: 'none'
          });
        })
        .finally(() => {
          uni.hideLoading();
          this.loading = false;
          
          if (this.refreshing) {
            this.refreshing = false;
          }
        });
      */
    },
    
    // 切换筛选条件
    switchFilter(filter) {
      if (this.currentFilter === filter) return;
      
      this.currentFilter = filter;
      this.pageInfo.page = 1;
      this.transactions = [];
      this.hasMore = true;
      this.loadTransactions();
    },
    
    // 加载更多
    loadMoreTransactions() {
      if (!this.hasMore || this.loading) return;
      
      this.pageInfo.page++;
      this.loadTransactions(true);
    },
    
    // 刷新列表
    refreshTransactions() {
      this.refreshing = true;
      this.pageInfo.page = 1;
      this.loadTransactions();
    },
    
    // 获取交易类型名称
    getTransactionTypeName(type) {
      const typeMap = {
        'RECHARGE': '余额充值',
        'WITHDRAW': '余额提现',
        'PAYMENT': '订单支付',
        'REFUND': '退款',
        'BONUS': '活动奖励',
        'OTHER': '其他'
      };
      return typeMap[type] || '未知类型';
    },
    
    // 获取默认描述
    getDefaultDescription(item) {
      const descMap = {
        'RECHARGE': '账户充值',
        'WITHDRAW': '账户提现',
        'PAYMENT': '订单支付',
        'REFUND': '订单退款',
        'BONUS': '活动奖励',
        'OTHER': '账户变更'
      };
      return descMap[item.type] || '';
    },
    
    // 格式化日期
    formatDate(dateStr) {
      if (!dateStr) return '';
      
      const date = new Date(dateStr);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hour = String(date.getHours()).padStart(2, '0');
      const minute = String(date.getMinutes()).padStart(2, '0');
      
      return `${year}-${month}-${day} ${hour}:${minute}`;
    },
    
    // 显示交易详情
    showTransactionDetail(item) {
      uni.navigateTo({
        url: `/pages/wallet/transaction-detail?id=${item.id}`
      });
    },
    
    // 返回上一页
    goBack() {
      uni.navigateBack();
    },
    
    // 生成模拟交易记录数据
    generateMockTransactions(filterType) {
      const records = [];
      const total = 35;
      const types = ['RECHARGE', 'WITHDRAW', 'PAYMENT', 'REFUND', 'BONUS'];
      
      // 计算应该生成的记录数量
      const count = Math.min(this.pageInfo.size, total - (this.pageInfo.page - 1) * this.pageInfo.size);
      
      for (let i = 0; i < count; i++) {
        const id = (this.pageInfo.page - 1) * this.pageInfo.size + i + 1;
        // 如果筛选了特定类型，只生成该类型的记录
        const type = filterType !== 'all' ? filterType : types[Math.floor(Math.random() * types.length)];
        
        // 根据类型决定金额的正负
        let amount = 0;
        if (type === 'RECHARGE' || type === 'REFUND' || type === 'BONUS') {
          amount = Math.floor(Math.random() * 500) + 10; // 正数
        } else {
          amount = -Math.floor(Math.random() * 200) - 10; // 负数
        }
        
        // 生成日期，越近的交易越新
        const date = new Date();
        date.setDate(date.getDate() - id);
        
        // 生成订单号
        const orderNo = `TX${Date.now().toString().substring(5)}${id.toString().padStart(4, '0')}`;
        
        records.push({
          id,
          type,
          amount,
          createTime: date.toISOString(),
          description: this.getDefaultDescription({ type }) + (type === 'PAYMENT' ? ` - 订单号: ${orderNo}` : ''),
          orderNo: type === 'PAYMENT' || type === 'REFUND' ? orderNo : null,
          status: 'SUCCESS'
        });
      }
      
      return {
        records,
        total
      };
    }
  }
};
</script>

<style>
.transactions-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  display: flex;
  flex-direction: column;
}

.header {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 90rpx;
  background-color: #fff;
  border-bottom: 1rpx solid #f0f0f0;
}

.back-btn {
  position: absolute;
  left: 30rpx;
}

.title {
  font-size: 34rpx;
  font-weight: bold;
  color: #333;
}

/* 筛选部分 */
.filter-section {
  display: flex;
  background-color: #fff;
  padding: 20rpx 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
  overflow-x: auto;
  white-space: nowrap;
}

.filter-item {
  padding: 10rpx 30rpx;
  margin-right: 20rpx;
  border-radius: 30rpx;
  font-size: 28rpx;
  color: #666;
  background-color: #f5f5f5;
}

.filter-item.active {
  color: #fff;
  background-color: #3cc51f;
}

/* 交易记录列表 */
.transaction-list-wrapper {
  flex: 1;
  padding: 0 30rpx;
}

.transaction-list {
  padding-top: 20rpx;
}

.transaction-item {
  display: flex;
  justify-content: space-between;
  background-color: #fff;
  padding: 30rpx;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.05);
}

.transaction-info {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.transaction-type {
  font-size: 30rpx;
  color: #333;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.transaction-desc {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 10rpx;
}

.transaction-time {
  font-size: 24rpx;
  color: #999;
}

.transaction-amount {
  font-size: 34rpx;
  font-weight: bold;
  margin-left: 20rpx;
  align-self: center;
}

.transaction-amount.income {
  color: #3cc51f;
}

.transaction-amount.expense {
  color: #ff7043;
}

/* 加载更多 */
.load-more, .no-more {
  text-align: center;
  padding: 30rpx 0;
  color: #999;
  font-size: 26rpx;
}

/* 空状态 */
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
  color: #999;
}
</style> 