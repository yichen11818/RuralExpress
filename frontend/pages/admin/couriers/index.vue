<template>
  <view class="container">
    <view class="header">
      <view class="title">快递员管理</view>
      <button class="add-btn" @click="openCourierForm('add')">添加快递员</button>
    </view>
    
    <view class="search-bar">
      <view class="search-input">
        <uni-icons type="search" size="18" color="#999"></uni-icons>
        <input 
          type="text" 
          v-model="searchKeyword" 
          placeholder="搜索快递员手机号/姓名" 
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
        <picker @change="handleRatingChange" :value="ratingIndex" :range="ratingOptions">
          <view class="picker-item">
            <text>评分: {{ ratingOptions[ratingIndex] }}</text>
            <uni-icons type="down" size="14" color="#666"></uni-icons>
          </view>
        </picker>
      </view>
    </view>
    
    <view class="courier-list" v-if="couriers.length > 0">
      <view class="courier-item" v-for="(courier, index) in couriers" :key="index">
        <view class="courier-basic">
          <image class="courier-avatar" :src="courier.avatar || '/static/images/default-avatar.png'" mode="aspectFill"></image>
          <view class="courier-info">
            <view class="courier-name">{{ courier.name }}</view>
            <view class="courier-phone">{{ courier.phone }}</view>
            <view class="courier-meta">
              <view class="meta-item">
                <uni-icons type="star-filled" size="14" color="#ff9900"></uni-icons>
                <text>{{ courier.rating.toFixed(1) }}</text>
              </view>
              <view class="meta-item">
                <uni-icons type="medal" size="14" color="#007aff"></uni-icons>
                <text>{{ courier.deliveryCount }}单</text>
              </view>
            </view>
          </view>
        </view>
        
        <view class="courier-details">
          <view class="detail-item">
            <text class="detail-label">工号:</text>
            <text class="detail-value">{{ courier.workNo }}</text>
          </view>
          <view class="detail-item">
            <text class="detail-label">身份证:</text>
            <text class="detail-value">{{ maskIdCard(courier.idCard) }}</text>
          </view>
          <view class="detail-item">
            <text class="detail-label">服务区域:</text>
            <text class="detail-value">{{ courier.serviceArea }}</text>
          </view>
          <view class="detail-item">
            <text class="detail-label">注册时间:</text>
            <text class="detail-value">{{ formatDate(courier.registerTime) }}</text>
          </view>
          <view class="detail-item">
            <text class="detail-label">状态:</text>
            <text class="detail-value" :class="getStatusClass(courier.status)">
              {{ getStatusText(courier.status) }}
            </text>
          </view>
        </view>
        
        <view class="courier-actions">
          <button class="action-btn edit-btn" @click="openCourierForm('edit', courier)">编辑</button>
          <button 
            class="action-btn" 
            :class="courier.status === 0 ? 'disable-btn' : courier.status === 1 ? 'enable-btn' : 'review-btn'" 
            @click="handleStatusAction(courier)"
          >
            {{ getActionText(courier.status) }}
          </button>
          <button class="action-btn delete-btn" @click="handleDelete(courier)">删除</button>
        </view>
      </view>
    </view>
    
    <view class="empty-list" v-else>
      <image src="/static/images/empty.png" mode="aspectFit"></image>
      <text>暂无快递员数据</text>
    </view>
    
    <uni-pagination
      :total="total"
      :pageSize="pageSize"
      :current="currentPage"
      @change="handlePageChange"
    ></uni-pagination>
    
    <!-- 快递员表单弹窗 -->
    <uni-popup ref="courierFormPopup" type="center">
      <view class="form-popup">
        <view class="form-header">
          <text class="form-title">{{ formType === 'add' ? '添加快递员' : '编辑快递员' }}</text>
          <uni-icons type="close" size="20" color="#999" @click="closeCourierForm"></uni-icons>
        </view>
        <view class="form-body">
          <view class="form-item">
            <text class="form-label">手机号</text>
            <input 
              class="form-input" 
              type="text" 
              v-model="courierForm.phone" 
              placeholder="请输入手机号"
              :disabled="formType === 'edit'"
            />
          </view>
          <view class="form-item" v-if="formType === 'add'">
            <text class="form-label">用户密码</text>
            <input 
              class="form-input" 
              type="password" 
              v-model="courierForm.password" 
              placeholder="请输入密码"
            />
          </view>
          <view class="form-item">
            <text class="form-label">姓名</text>
            <input 
              class="form-input" 
              type="text" 
              v-model="courierForm.name" 
              placeholder="请输入姓名"
            />
          </view>
          <view class="form-item">
            <text class="form-label">身份证号</text>
            <input 
              class="form-input" 
              type="text" 
              v-model="courierForm.idCard" 
              placeholder="请输入身份证号"
            />
          </view>
          <view class="form-item">
            <text class="form-label">工号</text>
            <input 
              class="form-input" 
              type="text" 
              v-model="courierForm.workNo" 
              placeholder="请输入工号"
            />
          </view>
          <view class="form-item">
            <text class="form-label">服务区域</text>
            <input 
              class="form-input" 
              type="text" 
              v-model="courierForm.serviceArea" 
              placeholder="请输入服务区域"
            />
          </view>
          <view class="form-item">
            <text class="form-label">状态</text>
            <picker @change="handleFormStatusChange" :value="formStatusIndex" :range="statusOptions">
              <view class="form-picker">
                <text>{{ statusOptions[formStatusIndex] }}</text>
                <uni-icons type="down" size="14" color="#666"></uni-icons>
              </view>
            </picker>
          </view>
        </view>
        <view class="form-footer">
          <button class="cancel-btn" @click="closeCourierForm">取消</button>
          <button class="submit-btn" @click="submitCourierForm">确认</button>
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
      statusOptions: ['全部', '正常', '禁用', '待审核'],
      ratingIndex: 0,
      ratingOptions: ['全部', '5星', '4星以上', '3星以上'],
      couriers: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      formType: 'add', // add 或 edit
      formStatusIndex: 1, // 默认为"正常"
      courierForm: {
        id: null,
        phone: '',
        password: '',
        name: '',
        idCard: '',
        workNo: '',
        serviceArea: '',
        status: 0
      },
      originalCourier: null
    };
  },
  
  // 添加request到组件
  beforeCreate() {
    // 将request挂载到组件实例
    this.$request = request;
  },
  
  onLoad() {
    this.loadCouriers();
  },
  
  methods: {
    // 加载快递员列表
    loadCouriers() {
      uni.showLoading({
        title: '加载中...'
      });
      
      // 构建请求参数
      const params = {
        page: this.currentPage,
        pageSize: this.pageSize
      };
      
      // 添加可选参数
      if (this.searchKeyword) {
        params.keyword = this.searchKeyword;
      }
      
      if (this.statusIndex > 0) {
        params.status = this.statusIndex - 1;
      }
      
      if (this.ratingIndex > 0) {
        // 根据星级过滤评分
        const ratings = [0, 5.0, 4.0, 3.0];
        params.rating = ratings[this.ratingIndex];
      }
      
      console.log('请求URL: /api/admin/couriers');
      console.log('请求参数:', params);
      
      // 发起请求
      this.$request.get('/api/admin/couriers', params)
        .then(res => {
          uni.hideLoading();
          if (res.code === 200) {
            const data = res.data;
            this.couriers = data.list;
            this.total = data.total;
          } else {
            uni.showToast({
              title: res.message || '获取快递员列表失败',
              icon: 'none'
            });
          }
        })
        .catch(() => {
          uni.hideLoading();
          uni.showToast({
            title: '网络错误，请稍后再试',
            icon: 'none'
          });
        });
    },
    
    // 搜索快递员
    handleSearch() {
      this.currentPage = 1;
      this.loadCouriers();
    },
    
    // 切换状态筛选
    handleStatusChange(e) {
      this.statusIndex = e.detail.value;
      this.currentPage = 1;
      this.loadCouriers();
    },
    
    // 切换评分筛选
    handleRatingChange(e) {
      this.ratingIndex = e.detail.value;
      this.currentPage = 1;
      this.loadCouriers();
    },
    
    // 分页切换
    handlePageChange(e) {
      this.currentPage = e.current;
      this.loadCouriers();
    },
    
    // 获取状态文本
    getStatusText(status) {
      const statusTexts = ['正常', '禁用', '待审核'];
      return statusTexts[status] || '未知';
    },
    
    // 获取状态样式
    getStatusClass(status) {
      const classes = ['status-normal', 'status-disabled', 'status-pending'];
      return classes[status] || '';
    },
    
    // 获取操作按钮文本
    getActionText(status) {
      const actionTexts = ['禁用', '启用', '审核通过'];
      return actionTexts[status] || '操作';
    },
    
    // 处理状态操作
    handleStatusAction(courier) {
      let actionText = '';
      let newStatus = 0;
      
      if (courier.status === 0) {
        actionText = '禁用';
        newStatus = 1;
      } else if (courier.status === 1) {
        actionText = '启用';
        newStatus = 0;
      } else if (courier.status === 2) {
        actionText = '审核通过';
        newStatus = 0;
      }
      
      uni.showModal({
        title: '提示',
        content: `确定要${actionText}该快递员吗？`,
        success: (res) => {
          if (res.confirm) {
            // 实际API调用
            uni.showLoading({
              title: '处理中...'
            });
            
            this.$request.put(`/api/admin/couriers/${courier.id}/status`, {
              status: newStatus
            })
            .then(res => {
              uni.hideLoading();
              if (res.code === 200) {
                uni.showToast({
                  title: `${actionText}成功`
                });
                this.loadCouriers();
              } else {
                uni.showToast({
                  title: res.message || '操作失败',
                  icon: 'none'
                });
              }
            })
            .catch(() => {
              uni.hideLoading();
              uni.showToast({
                title: '网络错误，请稍后再试',
                icon: 'none'
              });
            });
          }
        }
      });
    },
    
    // 掩码身份证号
    maskIdCard(idCard) {
      if (!idCard || idCard.length < 18) return idCard;
      return idCard.substring(0, 6) + '********' + idCard.substring(14);
    },
    
    // 格式化日期
    formatDate(dateStr) {
      if (!dateStr) return '';
      const date = new Date(dateStr);
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
    },
    
    // 打开快递员表单
    openCourierForm(type, courier = null) {
      this.formType = type;
      
      if (type === 'add') {
        this.courierForm = {
          id: null,
          phone: '',
          password: '',
          name: '',
          idCard: '',
          workNo: '',
          serviceArea: '',
          status: 0
        };
        this.formStatusIndex = 1; // 默认为"正常"
      } else {
        this.courierForm = { 
          id: courier.id,
          phone: courier.phone,
          name: courier.name,
          idCard: courier.idCard,
          workNo: courier.workNo,
          serviceArea: courier.serviceArea,
          status: courier.status
        };
        this.originalCourier = { ...courier };
        this.formStatusIndex = courier.status + 1;
      }
      
      this.$refs.courierFormPopup.open();
    },
    
    // 关闭快递员表单
    closeCourierForm() {
      this.$refs.courierFormPopup.close();
    },
    
    // 表单中的状态选择
    handleFormStatusChange(e) {
      this.formStatusIndex = e.detail.value;
      this.courierForm.status = parseInt(e.detail.value) - 1;
    },
    
    // 提交快递员表单
    submitCourierForm() {
      // 表单验证
      if (!this.courierForm.phone) {
        uni.showToast({
          title: '请输入手机号',
          icon: 'none'
        });
        return;
      }
      
      if (this.formType === 'add' && !this.courierForm.password) {
        uni.showToast({
          title: '请输入密码',
          icon: 'none'
        });
        return;
      }
      
      if (!this.courierForm.name) {
        uni.showToast({
          title: '请输入姓名',
          icon: 'none'
        });
        return;
      }
      
      if (!this.courierForm.idCard) {
        uni.showToast({
          title: '请输入身份证号',
          icon: 'none'
        });
        return;
      }
      
      // 实际API调用
      const url = this.formType === 'add' ? '/api/admin/couriers' : `/api/admin/couriers/${this.courierForm.id}`;
      
      uni.showLoading({
        title: '提交中...'
      });
      
      const apiMethod = this.formType === 'add' ? this.$request.post : this.$request.put;
      
      apiMethod(url, this.courierForm)
        .then(res => {
          uni.hideLoading();
          if (res.code === 200) {
            uni.showToast({
              title: this.formType === 'add' ? '添加成功' : '更新成功'
            });
            this.closeCourierForm();
            this.loadCouriers();
          } else {
            uni.showToast({
              title: res.message || (this.formType === 'add' ? '添加失败' : '更新失败'),
              icon: 'none'
            });
          }
        })
        .catch(() => {
          uni.hideLoading();
          uni.showToast({
            title: '网络错误，请稍后再试',
            icon: 'none'
          });
        });
    },
    
    // 删除快递员
    handleDelete(courier) {
      uni.showModal({
        title: '提示',
        content: '确定要删除该快递员吗？此操作不可恢复！',
        success: (res) => {
          if (res.confirm) {
            // 实际API调用
            uni.showLoading({
              title: '删除中...'
            });
            
            this.$request.delete(`/api/admin/couriers/${courier.id}`)
              .then(res => {
                uni.hideLoading();
                if (res.code === 200) {
                  uni.showToast({
                    title: '删除成功'
                  });
                  this.loadCouriers();
                } else {
                  uni.showToast({
                    title: res.message || '删除失败',
                    icon: 'none'
                  });
                }
              })
              .catch(() => {
                uni.hideLoading();
                uni.showToast({
                  title: '网络错误，请稍后再试',
                  icon: 'none'
                });
              });
          }
        }
      });
    }
  }
}
</script>

<style>
.container {
  padding: 30rpx;
  background-color: #f8f8f8;
  min-height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
}

.title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.add-btn {
  background-color: #3cc51f;
  color: #fff;
  font-size: 28rpx;
  padding: 10rpx 30rpx;
  border-radius: 30rpx;
}

.search-bar {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 20rpx;
  margin-bottom: 30rpx;
}

.search-input {
  display: flex;
  align-items: center;
  background-color: #f5f5f5;
  border-radius: 30rpx;
  padding: 10rpx 20rpx;
  margin-bottom: 20rpx;
}

.search-input input {
  flex: 1;
  height: 60rpx;
  margin-left: 10rpx;
}

.filter-section {
  display: flex;
  justify-content: space-between;
  padding-top: 10rpx;
}

.picker-item {
  display: flex;
  align-items: center;
  font-size: 28rpx;
  color: #666;
}

.picker-item text {
  margin-right: 10rpx;
}

.courier-list {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 10rpx;
  margin-bottom: 30rpx;
}

.courier-item {
  border-bottom: 1rpx solid #f5f5f5;
  padding: 20rpx;
}

.courier-item:last-child {
  border-bottom: none;
}

.courier-basic {
  display: flex;
  margin-bottom: 20rpx;
}

.courier-avatar {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  margin-right: 20rpx;
}

.courier-info {
  flex: 1;
}

.courier-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 8rpx;
}

.courier-phone {
  font-size: 28rpx;
  color: #666;
  margin-bottom: 10rpx;
}

.courier-meta {
  display: flex;
}

.meta-item {
  display: flex;
  align-items: center;
  margin-right: 20rpx;
  font-size: 24rpx;
  color: #666;
}

.meta-item text {
  margin-left: 4rpx;
}

.courier-details {
  background-color: #f9f9f9;
  border-radius: 8rpx;
  padding: 16rpx;
  margin-bottom: 20rpx;
}

.detail-item {
  display: flex;
  font-size: 26rpx;
  margin-bottom: 10rpx;
}

.detail-label {
  width: 140rpx;
  color: #999;
}

.detail-value {
  flex: 1;
  color: #333;
}

.status-normal {
  color: #3cc51f;
}

.status-disabled {
  color: #ff5722;
}

.status-pending {
  color: #ff9900;
}

.courier-actions {
  display: flex;
  justify-content: flex-end;
}

.action-btn {
  font-size: 24rpx;
  padding: 6rpx 20rpx;
  border-radius: 30rpx;
  margin-left: 20rpx;
  background-color: #f5f5f5;
  color: #666;
}

.edit-btn {
  background-color: #007aff;
  color: #fff;
}

.delete-btn {
  background-color: #ff5722;
  color: #fff;
}

.enable-btn {
  background-color: #3cc51f;
  color: #fff;
}

.disable-btn {
  background-color: #ff9900;
  color: #fff;
}

.review-btn {
  background-color: #007aff;
  color: #fff;
}

.empty-list {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 60rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.empty-list image {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 20rpx;
}

.empty-list text {
  font-size: 30rpx;
  color: #999;
}

/* 弹窗样式 */
.form-popup {
  background-color: #fff;
  border-radius: 12rpx;
  width: 650rpx;
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #f5f5f5;
}

.form-title {
  font-size: 32rpx;
  font-weight: bold;
}

.form-body {
  padding: 30rpx;
  max-height: 700rpx;
  overflow-y: auto;
}

.form-item {
  margin-bottom: 30rpx;
  display: flex;
  align-items: center;
}

.form-label {
  width: 180rpx;
  font-size: 28rpx;
  color: #333;
}

.form-input {
  flex: 1;
  height: 70rpx;
  border: 1rpx solid #eee;
  border-radius: 6rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
}

.form-picker {
  flex: 1;
  height: 70rpx;
  border: 1rpx solid #eee;
  border-radius: 6rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.form-footer {
  display: flex;
  border-top: 1rpx solid #f5f5f5;
}

.cancel-btn, .submit-btn {
  flex: 1;
  height: 90rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30rpx;
}

.cancel-btn {
  color: #666;
  border-right: 1rpx solid #f5f5f5;
}

.submit-btn {
  color: #3cc51f;
}
</style> 