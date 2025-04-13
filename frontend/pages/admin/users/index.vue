<template>
  <view class="container">
    <view class="header">
      <view class="title">用户管理</view>
      <button class="add-btn" @click="openUserForm('add')">添加用户</button>
    </view>
    
    <view class="search-bar">
      <view class="search-input">
        <uni-icons type="search" size="18" color="#999"></uni-icons>
        <input 
          type="text" 
          v-model="searchKeyword" 
          placeholder="搜索用户手机号/姓名" 
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
    
    <view class="user-list" v-if="users.length > 0">
      <view class="user-item" v-for="(user, index) in users" :key="index">
        <view class="user-basic">
          <image class="user-avatar" :src="user.avatar || '/static/images/default-avatar.png'" mode="aspectFill"></image>
          <view class="user-info">
            <view class="user-name">{{ user.nickname || user.realName || '未设置昵称' }}</view>
            <view class="user-phone">{{ user.phone }}</view>
            <view class="user-tags">
              <text class="user-tag" :class="{'verified': user.verified, 'not-verified': !user.verified}">
                {{ user.verified ? '已认证' : '未认证' }}
              </text>
              <text class="user-tag" :class="getTypeClass(user.userType)">
                {{ getUserTypeText(user.userType) }}
              </text>
              <text class="user-tag" :class="{'active': user.status === 0, 'disabled': user.status === 1}">
                {{ user.status === 0 ? '正常' : '禁用' }}
              </text>
            </view>
          </view>
        </view>
        <view class="user-actions">
          <button class="action-btn edit-btn" @click="openUserForm('edit', user)">编辑</button>
          <button class="action-btn" :class="user.status === 0 ? 'disable-btn' : 'enable-btn'" @click="toggleStatus(user)">
            {{ user.status === 0 ? '禁用' : '启用' }}
          </button>
          <button class="action-btn delete-btn" @click="handleDelete(user)">删除</button>
        </view>
      </view>
    </view>
    
    <view class="empty-list" v-else>
      <image src="/static/images/empty.png" mode="aspectFit"></image>
      <text>暂无用户数据</text>
    </view>
    
    <uni-pagination
      :total="total"
      :pageSize="pageSize"
      :current="currentPage"
      @change="handlePageChange"
    ></uni-pagination>
    
    <!-- 用户表单弹窗 -->
    <uni-popup ref="userFormPopup" type="center">
      <view class="form-popup">
        <view class="form-header">
          <text class="form-title">{{ formType === 'add' ? '添加用户' : '编辑用户' }}</text>
          <uni-icons type="close" size="20" color="#999" @click="closeUserForm"></uni-icons>
        </view>
        <view class="form-body">
          <view class="form-item">
            <text class="form-label">手机号</text>
            <input 
              class="form-input" 
              type="text" 
              v-model="userForm.phone" 
              placeholder="请输入手机号"
              :disabled="formType === 'edit'"
            />
          </view>
          <view class="form-item">
            <text class="form-label">密码</text>
            <input 
              class="form-input" 
              type="password" 
              v-model="userForm.password" 
              placeholder="请输入密码"
            />
          </view>
          <view class="form-item">
            <text class="form-label">昵称</text>
            <input 
              class="form-input" 
              type="text" 
              v-model="userForm.nickname" 
              placeholder="请输入昵称"
            />
          </view>
          <view class="form-item">
            <text class="form-label">真实姓名</text>
            <input 
              class="form-input" 
              type="text" 
              v-model="userForm.realName" 
              placeholder="请输入真实姓名"
            />
          </view>
          <view class="form-item">
            <text class="form-label">用户类型</text>
            <picker @change="handleFormTypeChange" :value="formTypeIndex" :range="userTypeOptions">
              <view class="form-picker">
                <text>{{ userTypeOptions[formTypeIndex] }}</text>
                <uni-icons type="down" size="14" color="#666"></uni-icons>
              </view>
            </picker>
          </view>
          <view class="form-item">
            <text class="form-label">实名认证</text>
            <switch 
              :checked="userForm.verified" 
              @change="(e) => userForm.verified = e.detail.value"
            />
          </view>
          <view class="form-item">
            <text class="form-label">状态</text>
            <switch 
              :checked="userForm.status === 0" 
              @change="(e) => userForm.status = e.detail.value ? 0 : 1"
            />
          </view>
        </view>
        <view class="form-footer">
          <button class="cancel-btn" @click="closeUserForm">取消</button>
          <button class="submit-btn" @click="submitUserForm">确认</button>
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
      statusOptions: ['全部', '正常', '禁用'],
      typeIndex: 0,
      typeOptions: ['全部', '普通用户', '快递员', '管理员'],
      userTypeOptions: ['普通用户', '快递员', '管理员'],
      users: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      formType: 'add', // add 或 edit
      formTypeIndex: 0,
      userForm: {
        id: null,
        phone: '',
        password: '',
        nickname: '',
        realName: '',
        userType: 0,
        verified: false,
        status: 0
      },
      originalUser: null
    };
  },
  
  // 添加request到组件
  beforeCreate() {
    // 将request挂载到组件实例
    this.$request = request;
  },
  
  onLoad() {
    this.loadUsers();
  },
  
  methods: {
    // 加载用户列表
    loadUsers() {
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
      
      if (this.typeIndex > 0) {
        // typeIndex: 0=全部, 1=普通用户, 2=快递员, 3=管理员
        // userType: 0=普通用户, 1=快递员, 2=管理员
        params.userType = this.typeIndex - 1;
      }
      
      if (this.statusIndex > 0) {
        params.status = this.statusIndex - 1;
      }
      
      // 发起请求
      this.$request.get('/api/admin/users', params)
        .then(res => {
          uni.hideLoading();
          if (res.code === 200) {
            const data = res.data;
            this.users = data.list;
            this.total = data.total;
          } else {
            uni.showToast({
              title: res.message || '获取用户列表失败',
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
    
    // 搜索用户
    handleSearch() {
      this.currentPage = 1;
      this.loadUsers();
    },
    
    // 切换用户状态筛选
    handleStatusChange(e) {
      this.statusIndex = e.detail.value;
      this.currentPage = 1;
      this.loadUsers();
    },
    
    // 切换用户类型筛选
    handleTypeChange(e) {
      this.typeIndex = e.detail.value;
      this.currentPage = 1;
      this.loadUsers();
    },
    
    // 分页切换
    handlePageChange(e) {
      this.currentPage = e.current;
      this.loadUsers();
    },
    
    // 获取用户类型文本
    getUserTypeText(type) {
      const types = ['普通用户', '快递员', '管理员'];
      return types[type] || '未知';
    },
    
    // 获取用户类型样式
    getTypeClass(type) {
      const classes = ['normal-user', 'courier', 'admin'];
      return classes[type] || '';
    },
    
    // 打开用户表单
    openUserForm(type, user = null) {
      this.formType = type;
      
      if (type === 'add') {
        this.userForm = {
          id: null,
          phone: '',
          password: '',
          nickname: '',
          realName: '',
          userType: 0,
          verified: false,
          status: 0
        };
        this.formTypeIndex = 0;
      } else {
        this.userForm = { ...user };
        this.originalUser = { ...user };
        this.formTypeIndex = user.userType;
      }
      
      this.$refs.userFormPopup.open();
    },
    
    // 关闭用户表单
    closeUserForm() {
      this.$refs.userFormPopup.close();
    },
    
    // 提交用户表单
    submitUserForm() {
      // 表单验证
      if (!this.userForm.phone) {
        uni.showToast({
          title: '请输入手机号',
          icon: 'none'
        });
        return;
      }
      
      if (this.formType === 'add' && !this.userForm.password) {
        uni.showToast({
          title: '请输入密码',
          icon: 'none'
        });
        return;
      }
      
      // 实际API调用
      const url = this.formType === 'add' ? '/api/admin/users' : `/api/admin/users/${this.userForm.id}`;
      
      uni.showLoading({
        title: '提交中...'
      });
      
      const apiMethod = this.formType === 'add' ? this.$request.post : this.$request.put;
      
      apiMethod(url, this.userForm)
        .then(res => {
          uni.hideLoading();
          if (res.code === 200) {
            uni.showToast({
              title: this.formType === 'add' ? '添加成功' : '更新成功'
            });
            this.closeUserForm();
            this.loadUsers();
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
    
    // 表单中的用户类型选择
    handleFormTypeChange(e) {
      this.formTypeIndex = e.detail.value;
      this.userForm.userType = parseInt(e.detail.value);
    },
    
    // 切换用户状态
    toggleStatus(user) {
      uni.showModal({
        title: '提示',
        content: user.status === 0 ? '确定要禁用该用户吗？' : '确定要启用该用户吗？',
        success: (res) => {
          if (res.confirm) {
            // 实际API调用
            uni.showLoading({
              title: '处理中...'
            });
            
            this.$request.put(`/api/admin/users/${user.id}/status`, {
              status: user.status === 0 ? 1 : 0
            })
            .then(res => {
              uni.hideLoading();
              if (res.code === 200) {
                uni.showToast({
                  title: user.status === 0 ? '禁用成功' : '启用成功'
                });
                this.loadUsers();
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
    
    // 删除用户
    handleDelete(user) {
      uni.showModal({
        title: '提示',
        content: '确定要删除该用户吗？此操作不可恢复！',
        success: (res) => {
          if (res.confirm) {
            // 实际API调用
            uni.showLoading({
              title: '删除中...'
            });
            
            this.$request.delete(`/api/admin/users/${user.id}`)
              .then(res => {
                uni.hideLoading();
                if (res.code === 200) {
                  uni.showToast({
                    title: '删除成功'
                  });
                  this.loadUsers();
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

.user-list {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 10rpx;
  margin-bottom: 30rpx;
}

.user-item {
  border-bottom: 1rpx solid #f5f5f5;
  padding: 20rpx;
}

.user-item:last-child {
  border-bottom: none;
}

.user-basic {
  display: flex;
  margin-bottom: 20rpx;
}

.user-avatar {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  margin-right: 20rpx;
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 8rpx;
}

.user-phone {
  font-size: 28rpx;
  color: #666;
  margin-bottom: 10rpx;
}

.user-tags {
  display: flex;
  flex-wrap: wrap;
}

.user-tag {
  font-size: 24rpx;
  padding: 4rpx 12rpx;
  border-radius: 20rpx;
  margin-right: 10rpx;
  margin-bottom: 10rpx;
}

.verified {
  color: #3cc51f;
  background-color: rgba(60, 197, 31, 0.1);
}

.not-verified {
  color: #ff9900;
  background-color: rgba(255, 153, 0, 0.1);
}

.normal-user {
  color: #666;
  background-color: rgba(102, 102, 102, 0.1);
}

.courier {
  color: #007aff;
  background-color: rgba(0, 122, 255, 0.1);
}

.admin {
  color: #ff5722;
  background-color: rgba(255, 87, 34, 0.1);
}

.active {
  color: #3cc51f;
  background-color: rgba(60, 197, 31, 0.1);
}

.disabled {
  color: #ff5722;
  background-color: rgba(255, 87, 34, 0.1);
}

.user-actions {
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