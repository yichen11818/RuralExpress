<template>
  <view class="container">
    <view class="header">
      <view class="title">订单管理</view>
      <view class="header-actions">
        <button class="action-btn export-btn" @click="exportOrders">导出</button>
        <button class="action-btn diagnose-btn" @click="runDiagnostics">诊断</button>
      </view>
    </view>
    
    <view class="search-bar">
      <view class="search-input">
        <uni-icons type="search" size="18" color="#999"></uni-icons>
        <input 
          type="text" 
          v-model="searchKeyword" 
          placeholder="搜索订单号/用户名/手机号" 
          @confirm="handleSearch"
        />
      </view>
      <view class="filter-section">
        <picker @change="handleStatusChange" :value="statusIndex" :range="statusOptions">
          <view class="picker-item">
            <text>状态: {{ statusOptions[statusIndex] }}</text>
            <uni-icons type="down" size="14" color="#666"></uni-icons>
          </view>
        </picker>
        <picker @change="handleTypeChange" :value="typeIndex" :range="typeOptions">
          <view class="picker-item">
            <text>类型: {{ typeOptions[typeIndex] }}</text>
            <uni-icons type="down" size="14" color="#666"></uni-icons>
          </view>
        </picker>
      </view>
    </view>
    
    <view class="statistics-cards" v-if="statistics">
      <view class="stat-card">
        <view class="stat-number">{{ statistics.todayOrders || 0 }}</view>
        <view class="stat-label">今日订单</view>
      </view>
      <view class="stat-card">
        <view class="stat-number">{{ statistics.todayCompletedOrders || 0 }}</view>
        <view class="stat-label">今日完成</view>
      </view>
      <view class="stat-card">
        <view class="stat-number">{{ statistics.monthOrders || 0 }}</view>
        <view class="stat-label">本月订单</view>
      </view>
      <view class="stat-card">
        <view class="stat-number">{{ statistics.monthCompletedOrders || 0 }}</view>
        <view class="stat-label">本月完成</view>
      </view>
    </view>
    
    <view class="order-list" v-if="orders.length > 0">
      <view class="order-item" v-for="(order, index) in orders" :key="index" @click="openOrderDetail(order)">
        <view class="order-header">
          <view class="order-id">订单号: {{ order.orderNo }}</view>
          <view class="order-status" :class="getStatusClass(order.status)">
            {{ getStatusText(order.status) }}
          </view>
        </view>
        
        <view class="order-info">
          <view class="info-row">
            <text class="info-label">下单时间:</text>
            <text class="info-value">{{ formatDate(order.createdAt) }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">寄件人:</text>
            <text class="info-value">{{ order.senderName }} ({{ order.senderPhone }})</text>
          </view>
          <view class="info-row">
            <text class="info-label">收件人:</text>
            <text class="info-value">{{ order.receiverName }} ({{ order.receiverPhone }})</text>
          </view>
          <view class="info-row">
            <text class="info-label">包裹类型:</text>
            <text class="info-value">{{ getPackageTypeText(order.packageType) }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">金额:</text>
            <text class="info-value price">¥{{ order.price.toFixed(2) }}</text>
          </view>
        </view>
        
        <view class="order-footer">
          <button 
            class="action-btn status-btn"
            @click.stop="openStatusChange(order)"
          >
            修改状态
          </button>
          <button 
            class="action-btn danger-btn"
            @click.stop="deleteOrder(order.id)"
          >
            删除
          </button>
        </view>
      </view>
    </view>
    
    <view class="empty-list" v-else>
      <image src="/static/images/empty.png" mode="aspectFit"></image>
      <text>暂无订单数据</text>
    </view>
    
    <uni-pagination
      :total="total"
      :pageSize="pageSize"
      :current="currentPage"
      @change="handlePageChange"
    ></uni-pagination>
    
    <!-- 订单详情弹窗 -->
    <uni-popup ref="orderDetailPopup" type="center">
      <view class="detail-popup" v-if="currentOrder">
        <view class="popup-header">
          <text class="popup-title">订单详情</text>
          <uni-icons type="close" size="20" color="#999" @click="closeOrderDetail"></uni-icons>
        </view>
        <view class="popup-body">
          <view class="detail-section">
            <view class="section-title">基本信息</view>
            <view class="detail-row">
              <text class="detail-label">订单号:</text>
              <text class="detail-value">{{ currentOrder.orderNo }}</text>
            </view>
            <view class="detail-row">
              <text class="detail-label">下单时间:</text>
              <text class="detail-value">{{ formatDate(currentOrder.createdAt) }}</text>
            </view>
            <view class="detail-row">
              <text class="detail-label">订单状态:</text>
              <text class="detail-value" :class="getStatusColorClass(currentOrder.status)">
                {{ getStatusText(currentOrder.status) }}
              </text>
            </view>
            <view class="detail-row">
              <text class="detail-label">包裹类型:</text>
              <text class="detail-value">{{ getPackageTypeText(currentOrder.packageType) }}</text>
            </view>
            <view class="detail-row">
              <text class="detail-label">重量:</text>
              <text class="detail-value">{{ currentOrder.weight }}kg</text>
            </view>
            <view class="detail-row">
              <text class="detail-label">订单金额:</text>
              <text class="detail-value price">¥{{ currentOrder.price ? currentOrder.price.toFixed(2) : '0.00' }}</text>
            </view>
          </view>
          
          <view class="detail-section">
            <view class="section-title">用户信息</view>
            <view class="detail-row">
              <text class="detail-label">用户名:</text>
              <text class="detail-value">{{ currentOrder.userUsername }}</text>
            </view>
            <view class="detail-row">
              <text class="detail-label">联系电话:</text>
              <text class="detail-value">{{ currentOrder.userPhone }}</text>
            </view>
          </view>
          
          <view class="detail-section">
            <view class="section-title">寄件信息</view>
            <view class="detail-row">
              <text class="detail-label">寄件人:</text>
              <text class="detail-value">{{ currentOrder.senderName }}</text>
            </view>
            <view class="detail-row">
              <text class="detail-label">联系电话:</text>
              <text class="detail-value">{{ currentOrder.senderPhone }}</text>
            </view>
            <view class="detail-row">
              <text class="detail-label">寄件地址:</text>
              <text class="detail-value">{{ currentOrder.senderAddress }}</text>
            </view>
          </view>
          
          <view class="detail-section">
            <view class="section-title">收件信息</view>
            <view class="detail-row">
              <text class="detail-label">收件人:</text>
              <text class="detail-value">{{ currentOrder.receiverName }}</text>
            </view>
            <view class="detail-row">
              <text class="detail-label">联系电话:</text>
              <text class="detail-value">{{ currentOrder.receiverPhone }}</text>
            </view>
            <view class="detail-row">
              <text class="detail-label">收件地址:</text>
              <text class="detail-value">{{ currentOrder.receiverAddress }}</text>
            </view>
          </view>
          
          <view class="detail-section" v-if="currentOrder.courierId">
            <view class="section-title">快递员信息</view>
            <view class="detail-row">
              <text class="detail-label">快递员:</text>
              <text class="detail-value">{{ currentOrder.courierName || '未分配' }}</text>
            </view>
            <view class="detail-row">
              <text class="detail-label">联系电话:</text>
              <text class="detail-value">{{ currentOrder.courierPhone || '未分配' }}</text>
            </view>
          </view>
          
          <view class="detail-section">
            <view class="section-title">时间信息</view>
            <view class="detail-row">
              <text class="detail-label">预计取件时间:</text>
              <text class="detail-value">{{ formatDate(currentOrder.expectedPickupTime) || '未设置' }}</text>
            </view>
            <view class="detail-row">
              <text class="detail-label">实际取件时间:</text>
              <text class="detail-value">{{ formatDate(currentOrder.actualPickupTime) || '未取件' }}</text>
            </view>
            <view class="detail-row">
              <text class="detail-label">预计送达时间:</text>
              <text class="detail-value">{{ formatDate(currentOrder.expectedDeliveryTime) || '未设置' }}</text>
            </view>
            <view class="detail-row">
              <text class="detail-label">实际送达时间:</text>
              <text class="detail-value">{{ formatDate(currentOrder.actualDeliveryTime) || '未送达' }}</text>
            </view>
          </view>
          
          <view class="detail-section" v-if="currentOrder.remark">
            <view class="section-title">备注</view>
            <view class="detail-remark">{{ currentOrder.remark }}</view>
          </view>
          
          <view class="detail-section" v-if="currentOrder.cancelReason">
            <view class="section-title">取消原因</view>
            <view class="detail-remark">{{ currentOrder.cancelReason }}</view>
          </view>
        </view>
        <view class="popup-footer">
          <button class="cancel-btn" @click="closeOrderDetail">关闭</button>
          <button 
            class="action-btn status-btn"
            @click="openStatusChange(currentOrder, true)"
          >
            修改状态
          </button>
        </view>
      </view>
    </uni-popup>
    
    <!-- 状态修改弹窗 -->
    <uni-popup ref="statusPopup" type="center">
      <view class="status-popup">
        <view class="popup-header">
          <text class="popup-title">修改订单状态</text>
          <uni-icons type="close" size="20" color="#999" @click="closeStatusPopup"></uni-icons>
        </view>
        <view class="popup-body">
          <picker @change="handleStatusPickerChange" :value="statusPickerIndex" :range="statusPickerOptions">
            <view class="status-picker">
              <text>{{ statusPickerOptions[statusPickerIndex] }}</text>
              <uni-icons type="down" size="14" color="#666"></uni-icons>
            </view>
          </picker>
        </view>
        <view class="popup-footer">
          <button class="cancel-btn" @click="closeStatusPopup">取消</button>
          <button class="confirm-btn" @click="confirmStatusChange">确认</button>
        </view>
      </view>
    </uni-popup>
    
    <!-- 批量删除弹窗 -->
    <uni-popup ref="deletePopup" type="center">
      <view class="delete-popup">
        <view class="popup-header">
          <text class="popup-title">确认删除</text>
          <uni-icons type="close" size="20" color="#999" @click="closeDeletePopup"></uni-icons>
        </view>
        <view class="popup-body">
          <text>确认要删除该订单吗？此操作无法撤销。</text>
        </view>
        <view class="popup-footer">
          <button class="cancel-btn" @click="closeDeletePopup">取消</button>
          <button class="danger-btn" @click="confirmDelete">确认删除</button>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script>
import request from '@/utils/request.js';

export default {
  data() {
    return {
      searchKeyword: '',
      statusIndex: 0,
      statusOptions: ['全部', '待接单', '已接单', '取件中', '已取件', '配送中', '已送达', '已完成', '已取消'],
      typeIndex: 0,
      typeOptions: ['全部', '小件', '中件', '大件'],
      orders: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      currentOrder: null,
      // 状态修改
      statusPickerIndex: 0,
      statusPickerOptions: ['待接单', '已接单', '取件中', '已取件', '配送中', '已送达', '已完成', '已取消'],
      statusChangeOrderId: null,
      // 统计数据
      statistics: null,
      // 删除相关
      deleteOrderId: null
    };
  },
  
  beforeCreate() {
    this.$request = request;
  },
  
  onLoad() {
    console.log('[订单页面] 页面加载, 测试服务器连接');
    this.$request.testConnection()
      .then(result => {
        if (result.success) {
          console.log('[订单页面] 服务器连接正常, 开始加载数据');
          if (result.warning) {
            console.warn('[订单页面] 连接测试警告:', result.warning);
          }
          
          if (result.statusCode === 403) {
            console.warn('[订单页面] 健康检查返回403, 但仍继续数据加载');
          }
          
          this.loadOrders();
          this.loadStatistics();
        } else {
          console.error('[订单页面] 服务器连接失败:', result.error);
          uni.showToast({
            title: '服务器连接失败，请检查网络设置或联系管理员',
            icon: 'none',
            duration: 3000
          });
        }
      });
  },
  
  methods: {
    // 加载订单列表
    loadOrders() {
      console.log('[订单页面] 开始加载订单数据');
      uni.showLoading({
        title: '加载中...'
      });
      
      // 构建请求参数
      const params = {
        page: this.currentPage,
        size: this.pageSize,
        keyword: this.searchKeyword || undefined
      };
      
      // 添加状态过滤
      if (this.statusIndex > 0) {
        params.status = this.statusIndex - 1;
      }
      
      // 添加类型过滤
      if (this.typeIndex > 0) {
        params.packageType = this.typeIndex - 1;
      }
      
      console.log('[订单页面] 请求参数:', params);
      
      // 发送API请求
      this.$request.post('/api/admin/orders', params)
        .then(response => {
          console.log('[订单页面] 订单数据加载成功:', response);
          if (response && response.code === 200) {
            this.orders = response.data.records || [];
            this.total = response.data.total || 0;
          } else {
            console.error('[订单页面] 加载订单失败:', response);
            this.handleApiError(response);
          }
        })
        .catch(error => {
          console.error('[订单页面] 加载订单出错:', error);
          this.handleRequestError(error);
        })
        .finally(() => {
          uni.hideLoading();
        });
    },
    
    // 加载统计数据
    loadStatistics() {
      this.$request.get('/api/admin/orders/statistics')
        .then(response => {
          console.log('[订单页面] 统计数据加载成功:', response);
          if (response && response.code === 200) {
            this.statistics = response.data;
          } else {
            console.error('[订单页面] 加载统计数据失败:', response);
          }
        })
        .catch(error => {
          console.error('[订单页面] 加载统计数据出错:', error);
        });
    },
    
    // 处理搜索
    handleSearch() {
      this.currentPage = 1;
      this.loadOrders();
    },
    
    // 处理状态筛选变化
    handleStatusChange(e) {
      this.statusIndex = e.detail.value;
      this.currentPage = 1;
      this.loadOrders();
    },
    
    // 处理类型筛选变化
    handleTypeChange(e) {
      this.typeIndex = e.detail.value;
      this.currentPage = 1;
      this.loadOrders();
    },
    
    // 处理页码变化
    handlePageChange(e) {
      this.currentPage = e.current;
      this.loadOrders();
    },
    
    // 打开订单详情
    openOrderDetail(order) {
      console.log('[订单页面] 查看订单详情:', order.id);
      // 先请求详细信息
      this.$request.get(`/api/admin/orders/${order.id}`)
        .then(response => {
          if (response && response.code === 200) {
            console.log('[订单页面] 订单详情加载成功:', response.data);
            this.currentOrder = response.data;
            this.$refs.orderDetailPopup.open();
          } else {
            console.error('[订单页面] 加载订单详情失败:', response);
            this.handleApiError(response);
          }
        })
        .catch(error => {
          console.error('[订单页面] 加载订单详情出错:', error);
          this.handleRequestError(error);
        });
    },
    
    // 关闭订单详情
    closeOrderDetail() {
      this.$refs.orderDetailPopup.close();
    },
    
    // 打开状态修改弹窗
    openStatusChange(order, fromDetail) {
      this.statusChangeOrderId = order.id;
      this.statusPickerIndex = order.status || 0;
      
      if (fromDetail) {
        // 如果是从详情页打开，先关闭详情弹窗
        this.$refs.orderDetailPopup.close();
        setTimeout(() => {
          this.$refs.statusPopup.open();
        }, 300);
      } else {
        this.$refs.statusPopup.open();
      }
    },
    
    // 关闭状态修改弹窗
    closeStatusPopup() {
      this.$refs.statusPopup.close();
    },
    
    // 处理状态选择器变化
    handleStatusPickerChange(e) {
      this.statusPickerIndex = e.detail.value;
    },
    
    // 确认修改状态
    confirmStatusChange() {
      if (this.statusChangeOrderId) {
        uni.showLoading({
          title: '更新中...'
        });
        
        this.$request.put(`/api/admin/orders/${this.statusChangeOrderId}/status`, {
          status: parseInt(this.statusPickerIndex)
        })
          .then(response => {
            if (response && response.code === 200) {
              uni.showToast({
                title: '状态更新成功',
                icon: 'success'
              });
              this.closeStatusPopup();
              this.loadOrders();
            } else {
              console.error('[订单页面] 更新订单状态失败:', response);
              this.handleApiError(response);
            }
          })
          .catch(error => {
            console.error('[订单页面] 更新订单状态出错:', error);
            this.handleRequestError(error);
          })
          .finally(() => {
            uni.hideLoading();
          });
      }
    },
    
    // 导出订单数据
    exportOrders() {
      uni.showLoading({
        title: '导出中...'
      });
      
      // 构建导出参数
      const params = {};
      
      // 添加状态过滤
      if (this.statusIndex > 0) {
        params.status = this.statusIndex - 1;
      }
      
      this.$request.post('/api/admin/orders/export', params)
        .then(response => {
          if (response && response.code === 200) {
            uni.showToast({
              title: '导出成功',
              icon: 'success'
            });
            console.log('[订单页面] 导出文件URL:', response.data);
            // 在实际应用中，这里应该提供下载链接
          } else {
            console.error('[订单页面] 导出订单失败:', response);
            this.handleApiError(response);
          }
        })
        .catch(error => {
          console.error('[订单页面] 导出订单出错:', error);
          this.handleRequestError(error);
        })
        .finally(() => {
          uni.hideLoading();
        });
    },
    
    // 删除订单
    deleteOrder(id) {
      this.deleteOrderId = id;
      this.$refs.deletePopup.open();
    },
    
    // 关闭删除弹窗
    closeDeletePopup() {
      this.$refs.deletePopup.close();
    },
    
    // 确认删除
    confirmDelete() {
      if (this.deleteOrderId) {
        uni.showLoading({
          title: '删除中...'
        });
        
        this.$request.delete(`/api/admin/orders/batch`, {
          ids: [this.deleteOrderId]
        })
          .then(response => {
            if (response && response.code === 200) {
              uni.showToast({
                title: '删除成功',
                icon: 'success'
              });
              this.closeDeletePopup();
              this.loadOrders();
            } else {
              console.error('[订单页面] 删除订单失败:', response);
              this.handleApiError(response);
            }
          })
          .catch(error => {
            console.error('[订单页面] 删除订单出错:', error);
            this.handleRequestError(error);
          })
          .finally(() => {
            uni.hideLoading();
          });
      }
    },
    
    // 运行诊断
    runDiagnostics() {
      uni.showLoading({
        title: '诊断中...'
      });
      console.log('[订单页面] 开始运行诊断');
      
      this.$request.testConnection()
        .then(result => {
          if (result.success) {
            uni.showToast({
              title: '服务器连接正常',
              icon: 'success'
            });
            console.log('[订单页面] 诊断结果: 服务器连接正常');
          } else {
            uni.showModal({
              title: '连接问题',
              content: `服务器连接异常: ${result.error || '未知错误'}`,
              showCancel: false
            });
            console.error('[订单页面] 诊断结果: 服务器连接异常:', result.error);
          }
        })
        .catch(error => {
          uni.showModal({
            title: '诊断失败',
            content: `诊断过程发生错误: ${error.message || '未知错误'}`,
            showCancel: false
          });
          console.error('[订单页面] 诊断过程出错:', error);
        })
        .finally(() => {
          uni.hideLoading();
        });
    },
    
    // 处理API错误
    handleApiError(response) {
      let message = '未知错误';
      if (response) {
        if (response.message) {
          message = response.message;
        } else if (response.code) {
          message = `错误码: ${response.code}`;
        }
      }
      
      uni.showToast({
        title: message,
        icon: 'none',
        duration: 3000
      });
    },
    
    // 处理请求错误
    handleRequestError(error) {
      let message = '网络请求错误';
      if (error.message) {
        message = error.message;
      }
      
      uni.showToast({
        title: message,
        icon: 'none',
        duration: 3000
      });
    },
    
    // 格式化日期
    formatDate(dateStr) {
      if (!dateStr) return '';
      const date = new Date(dateStr);
      return date.toLocaleString();
    },
    
    // 获取状态文本
    getStatusText(status) {
      const statusTexts = ['待接单', '已接单', '取件中', '已取件', '配送中', '已送达', '已完成', '已取消'];
      return statusTexts[status] || '未知状态';
    },
    
    // 获取状态CSS类
    getStatusClass(status) {
      const statusClasses = ['status-pending', 'status-accepted', 'status-picking', 'status-picked', 'status-delivering', 'status-delivered', 'status-completed', 'status-canceled'];
      return statusClasses[status] || '';
    },
    
    // 获取状态颜色CSS类
    getStatusColorClass(status) {
      const colorClasses = ['color-pending', 'color-accepted', 'color-picking', 'color-picked', 'color-delivering', 'color-delivered', 'color-completed', 'color-canceled'];
      return colorClasses[status] || '';
    },
    
    // 获取包裹类型文本
    getPackageTypeText(type) {
      const typeTexts = ['小件', '中件', '大件'];
      return typeTexts[type] || '未知类型';
    }
  }
}
</script>

<style scoped>
.container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.title {
  font-size: 20px;
  font-weight: bold;
}

.header-actions {
  display: flex;
}

.action-btn {
  margin-left: 10px;
  font-size: 14px;
  padding: 5px 15px;
  border-radius: 4px;
  background-color: #007AFF;
  color: #fff;
}

.diagnose-btn {
  background-color: #6c757d;
}

.export-btn {
  background-color: #28a745;
}

.search-bar {
  margin-bottom: 20px;
}

.search-input {
  display: flex;
  align-items: center;
  padding: 10px;
  background-color: #f8f8f8;
  border-radius: 4px;
  margin-bottom: 10px;
}

.search-input input {
  flex: 1;
  margin-left: 10px;
  height: 30px;
}

.filter-section {
  display: flex;
  justify-content: space-between;
}

.picker-item {
  padding: 8px 15px;
  background-color: #f8f8f8;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 48%;
}

.statistics-cards {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.stat-card {
  width: 23%;
  padding: 15px;
  border-radius: 8px;
  background-color: #f0f7ff;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #007AFF;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 12px;
  color: #666;
}

.order-list {
  margin-bottom: 20px;
}

.order-item {
  padding: 15px;
  background-color: #fff;
  border-radius: 8px;
  margin-bottom: 15px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.05);
}

.order-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.order-id {
  font-weight: bold;
}

.order-status {
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 12px;
  color: #fff;
}

.status-pending {
  background-color: #ffc107;
}

.status-accepted {
  background-color: #17a2b8;
}

.status-picking, .status-picked, .status-delivering {
  background-color: #007bff;
}

.status-delivered {
  background-color: #28a745;
}

.status-completed {
  background-color: #28a745;
}

.status-canceled {
  background-color: #dc3545;
}

.order-info {
  margin-bottom: 15px;
}

.info-row {
  display: flex;
  margin-bottom: 8px;
}

.info-label {
  width: 80px;
  color: #666;
}

.info-value {
  flex: 1;
}

.price {
  color: #e74c3c;
  font-weight: bold;
}

.order-footer {
  display: flex;
  justify-content: flex-end;
}

.status-btn {
  background-color: #17a2b8;
}

.danger-btn {
  background-color: #dc3545;
  margin-left: 10px;
}

.empty-list {
  padding: 40px 0;
  text-align: center;
  color: #999;
}

.empty-list image {
  width: 100px;
  height: 100px;
  margin-bottom: 10px;
}

/* 详情弹窗样式 */
.detail-popup {
  width: 90vw;
  max-width: 500px;
  max-height: 80vh;
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.popup-header {
  padding: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;
}

.popup-title {
  font-size: 16px;
  font-weight: bold;
}

.popup-body {
  padding: 15px;
  flex: 1;
  overflow-y: auto;
}

.detail-section {
  margin-bottom: 20px;
}

.section-title {
  font-weight: bold;
  margin-bottom: 10px;
  padding-bottom: 5px;
  border-bottom: 1px solid #f0f0f0;
}

.detail-row {
  display: flex;
  margin-bottom: 8px;
}

.detail-label {
  width: 100px;
  color: #666;
}

.detail-value {
  flex: 1;
}

.detail-remark {
  padding: 10px;
  background-color: #f8f8f8;
  border-radius: 4px;
  font-size: 14px;
}

.popup-footer {
  padding: 15px;
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid #f0f0f0;
}

.cancel-btn {
  padding: 8px 15px;
  border-radius: 4px;
  font-size: 14px;
  background-color: #f0f0f0;
  color: #333;
}

.confirm-btn {
  padding: 8px 15px;
  border-radius: 4px;
  font-size: 14px;
  background-color: #007AFF;
  color: #fff;
  margin-left: 10px;
}

/* 状态颜色类 */
.color-pending {
  color: #ffc107;
}

.color-accepted {
  color: #17a2b8;
}

.color-picking, .color-picked, .color-delivering {
  color: #007bff;
}

.color-delivered, .color-completed {
  color: #28a745;
}

.color-canceled {
  color: #dc3545;
}

/* 状态修改弹窗 */
.status-popup {
  width: 80vw;
  max-width: 400px;
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.status-picker {
  padding: 15px;
  border: 1px solid #f0f0f0;
  border-radius: 4px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 删除弹窗 */
.delete-popup {
  width: 80vw;
  max-width: 400px;
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.delete-popup .popup-body {
  padding: 20px 15px;
  text-align: center;
}
</style> 