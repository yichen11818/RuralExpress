<template>
  <view class="container">
    <view class="header">
      <view class="title">快递公司管理</view>
      <button class="add-btn" @click="openCompanyForm('add')">添加快递公司</button>
    </view>
    
    <view class="search-bar">
      <view class="search-input">
        <uni-icons type="search" size="18" color="#999"></uni-icons>
        <input 
          type="text" 
          v-model="searchKeyword" 
          placeholder="搜索公司名称/编码" 
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
      </view>
    </view>
    
    <view class="company-list" v-if="companies.length > 0">
      <view class="company-item" v-for="(company, index) in companies" :key="index">
        <view class="company-basic">
          <image class="company-logo" :src="company.logo || '/static/images/default-logo.png'" mode="aspectFill"></image>
          <view class="company-info">
            <view class="company-name">{{ company.name }}</view>
            <view class="company-code">编码: {{ company.code }}</view>
            <view class="company-tags">
              <text class="company-tag" :class="{'active': company.status === 0, 'disabled': company.status === 1}">
                {{ company.status === 0 ? '启用' : '禁用' }}
              </text>
            </view>
          </view>
        </view>
        <view class="company-detail">
          <view class="detail-item" v-if="company.phone">
            <uni-icons type="phone" size="16" color="#666"></uni-icons>
            <text>{{ company.phone }}</text>
          </view>
          <view class="detail-item" v-if="company.website">
            <uni-icons type="link" size="16" color="#666"></uni-icons>
            <text>{{ company.website }}</text>
          </view>
        </view>
        <view class="company-actions">
          <button class="action-btn edit-btn" @click="openCompanyForm('edit', company)">编辑</button>
          <button class="action-btn" :class="company.status === 0 ? 'disable-btn' : 'enable-btn'" @click="toggleStatus(company)">
            {{ company.status === 0 ? '禁用' : '启用' }}
          </button>
          <button class="action-btn delete-btn" @click="handleDelete(company)">删除</button>
        </view>
      </view>
    </view>
    
    <view class="empty-list" v-else>
      <image src="/static/images/empty.png" mode="aspectFit"></image>
      <text>暂无快递公司数据</text>
    </view>
    
    <uni-pagination
      :total="total"
      :pageSize="pageSize"
      :current="currentPage"
      @change="handlePageChange"
    ></uni-pagination>
    
    <!-- 快递公司表单弹窗 -->
    <uni-popup ref="companyFormPopup" type="center">
      <view class="form-popup">
        <view class="form-header">
          <text class="form-title">{{ formType === 'add' ? '添加快递公司' : '编辑快递公司' }}</text>
          <uni-icons type="close" size="20" color="#999" @click="closeCompanyForm"></uni-icons>
        </view>
        <view class="form-body">
          <view class="form-item">
            <text class="form-label">公司名称</text>
            <input 
              class="form-input" 
              type="text" 
              v-model="companyForm.name" 
              placeholder="请输入公司名称"
            />
          </view>
          <view class="form-item">
            <text class="form-label">公司编码</text>
            <input 
              class="form-input" 
              type="text" 
              v-model="companyForm.code" 
              placeholder="请输入公司编码"
              :disabled="formType === 'edit'"
            />
          </view>
          <view class="form-item">
            <text class="form-label">公司LOGO</text>
            <input 
              class="form-input" 
              type="text" 
              v-model="companyForm.logo" 
              placeholder="请输入LOGO图片地址"
            />
          </view>
          <view class="form-item">
            <text class="form-label">联系电话</text>
            <input 
              class="form-input" 
              type="text" 
              v-model="companyForm.phone" 
              placeholder="请输入联系电话"
            />
          </view>
          <view class="form-item">
            <text class="form-label">公司官网</text>
            <input 
              class="form-input" 
              type="text" 
              v-model="companyForm.website" 
              placeholder="请输入公司官网"
            />
          </view>
          <view class="form-item">
            <text class="form-label">状态</text>
            <switch 
              :checked="companyForm.status === 0" 
              @change="(e) => companyForm.status = e.detail.value ? 0 : 1"
            />
          </view>
        </view>
        <view class="form-footer">
          <button class="cancel-btn" @click="closeCompanyForm">取消</button>
          <button class="submit-btn" @click="submitCompanyForm">确认</button>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script>
export default {
  data() {
    return {
      searchKeyword: '',
      statusIndex: 0,
      statusOptions: ['全部', '启用', '禁用'],
      companies: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      formType: 'add', // add 或 edit
      companyForm: {
        id: null,
        name: '',
        code: '',
        logo: '',
        phone: '',
        website: '',
        status: 0
      },
      originalCompany: null,
      loading: false
    };
  },
  
  onLoad() {
    this.loadCompanies();
  },
  
  methods: {
    // 加载快递公司列表
    loadCompanies() {
      if (this.loading) return;
      this.loading = true;
      
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
      
      // 发起请求
      uni.request({
        url: '/api/admin/companies',
        method: 'GET',
        data: params,
        success: (res) => {
          uni.hideLoading();
          this.loading = false;
          
          if (res.data.code === 200) {
            const data = res.data.data;
            this.companies = data.list;
            this.total = data.total;
          } else {
            uni.showToast({
              title: res.data.message || '获取快递公司列表失败',
              icon: 'none'
            });
          }
        },
        fail: () => {
          uni.hideLoading();
          this.loading = false;
          
          uni.showToast({
            title: '网络错误，请稍后再试',
            icon: 'none'
          });
        }
      });
    },
    
    // 搜索快递公司
    handleSearch() {
      this.currentPage = 1;
      this.loadCompanies();
    },
    
    // 切换状态筛选
    handleStatusChange(e) {
      this.statusIndex = e.detail.value;
      this.currentPage = 1;
      this.loadCompanies();
    },
    
    // 分页切换
    handlePageChange(e) {
      this.currentPage = e.current;
      this.loadCompanies();
    },
    
    // 打开快递公司表单
    openCompanyForm(type, company = null) {
      this.formType = type;
      
      if (type === 'add') {
        this.companyForm = {
          id: null,
          name: '',
          code: '',
          logo: '',
          phone: '',
          website: '',
          status: 0
        };
      } else {
        this.companyForm = { ...company };
        this.originalCompany = { ...company };
      }
      
      this.$refs.companyFormPopup.open();
    },
    
    // 关闭快递公司表单
    closeCompanyForm() {
      this.$refs.companyFormPopup.close();
    },
    
    // 提交快递公司表单
    submitCompanyForm() {
      // 表单验证
      if (!this.companyForm.name) {
        uni.showToast({
          title: '请输入公司名称',
          icon: 'none'
        });
        return;
      }
      
      if (!this.companyForm.code) {
        uni.showToast({
          title: '请输入公司编码',
          icon: 'none'
        });
        return;
      }
      
      // 实际API调用
      const url = this.formType === 'add' ? '/api/admin/companies' : `/api/admin/companies/${this.companyForm.id}`;
      const method = this.formType === 'add' ? 'POST' : 'PUT';
      
      uni.showLoading({
        title: '提交中...'
      });
      
      uni.request({
        url,
        method,
        data: this.companyForm,
        success: (res) => {
          uni.hideLoading();
          if (res.data.code === 200) {
            uni.showToast({
              title: this.formType === 'add' ? '添加成功' : '更新成功'
            });
            this.closeCompanyForm();
            this.loadCompanies();
          } else {
            uni.showToast({
              title: res.data.message || (this.formType === 'add' ? '添加失败' : '更新失败'),
              icon: 'none'
            });
          }
        },
        fail: () => {
          uni.hideLoading();
          uni.showToast({
            title: '网络错误，请稍后再试',
            icon: 'none'
          });
        }
      });
    },
    
    // 切换快递公司状态
    toggleStatus(company) {
      uni.showModal({
        title: '提示',
        content: company.status === 0 ? '确定要禁用该快递公司吗？' : '确定要启用该快递公司吗？',
        success: (res) => {
          if (res.confirm) {
            // 实际API调用
            uni.showLoading({
              title: '处理中...'
            });
            
            uni.request({
              url: `/api/admin/companies/${company.id}/status`,
              method: 'PUT',
              data: {
                status: company.status === 0 ? 1 : 0
              },
              success: (res) => {
                uni.hideLoading();
                if (res.data.code === 200) {
                  uni.showToast({
                    title: company.status === 0 ? '禁用成功' : '启用成功'
                  });
                  this.loadCompanies();
                } else {
                  uni.showToast({
                    title: res.data.message || '操作失败',
                    icon: 'none'
                  });
                }
              },
              fail: () => {
                uni.hideLoading();
                uni.showToast({
                  title: '网络错误，请稍后再试',
                  icon: 'none'
                });
              }
            });
          }
        }
      });
    },
    
    // 删除快递公司
    handleDelete(company) {
      uni.showModal({
        title: '提示',
        content: '确定要删除该快递公司吗？此操作不可恢复！',
        success: (res) => {
          if (res.confirm) {
            // 实际API调用
            uni.showLoading({
              title: '删除中...'
            });
            
            uni.request({
              url: `/api/admin/companies/${company.id}`,
              method: 'DELETE',
              success: (res) => {
                uni.hideLoading();
                if (res.data.code === 200) {
                  uni.showToast({
                    title: '删除成功'
                  });
                  this.loadCompanies();
                } else {
                  uni.showToast({
                    title: res.data.message || '删除失败',
                    icon: 'none'
                  });
                }
              },
              fail: () => {
                uni.hideLoading();
                uni.showToast({
                  title: '网络错误，请稍后再试',
                  icon: 'none'
                });
              }
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

.company-list {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 10rpx;
  margin-bottom: 30rpx;
}

.company-item {
  border-bottom: 1rpx solid #f5f5f5;
  padding: 20rpx;
}

.company-item:last-child {
  border-bottom: none;
}

.company-basic {
  display: flex;
  margin-bottom: 20rpx;
}

.company-logo {
  width: 100rpx;
  height: 100rpx;
  border-radius: 12rpx;
  margin-right: 20rpx;
  border: 1rpx solid #eee;
}

.company-info {
  flex: 1;
}

.company-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 8rpx;
}

.company-code {
  font-size: 28rpx;
  color: #666;
  margin-bottom: 10rpx;
}

.company-tags {
  display: flex;
  flex-wrap: wrap;
}

.company-tag {
  font-size: 24rpx;
  padding: 4rpx 12rpx;
  border-radius: 20rpx;
  margin-right: 10rpx;
  margin-bottom: 10rpx;
}

.active {
  color: #3cc51f;
  background-color: rgba(60, 197, 31, 0.1);
}

.disabled {
  color: #ff5722;
  background-color: rgba(255, 87, 34, 0.1);
}

.company-detail {
  margin-bottom: 20rpx;
}

.detail-item {
  display: flex;
  align-items: center;
  font-size: 26rpx;
  color: #666;
  margin-bottom: 10rpx;
}

.detail-item text {
  margin-left: 10rpx;
}

.company-actions {
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