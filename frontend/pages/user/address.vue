<template>
  <view class="address-container">
    <!-- 增加顶部状态栏占位 -->
    <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
    
    <view class="address-header">
      <view class="back-btn" @click="goBack">
        <uni-icons type="left" size="18" color="#333"></uni-icons>
      </view>
      <text class="page-title">我的地址</text>
    </view>
    
    <!-- 地址列表 -->
    <view class="address-list" v-if="addressList.length > 0">
      <view class="address-item" v-for="(item, index) in addressList" :key="index">
        <view class="address-info" @click="selectAddress(item)">
          <view class="address-top">
            <text class="name">{{ item.name }}</text>
            <text class="phone">{{ formatPhone(item.phone) }}</text>
            <text class="tag" v-if="item.isDefault">默认</text>
          </view>
          <text class="address-text">{{ item.province + item.city + item.district + ' ' + item.detailAddress }}</text>
          <text class="address-type">{{ item.addressType }}</text>
        </view>
        <view class="address-actions">
          <view class="action-btn" @click="editAddress(item)">
            <uni-icons type="compose" size="16" color="#666"></uni-icons>
            <text class="action-text">编辑</text>
          </view>
          <view class="action-btn" @click="deleteAddress(item.id)">
            <uni-icons type="trash" size="16" color="#666"></uni-icons>
            <text class="action-text">删除</text>
          </view>
          <view class="action-btn" v-if="!item.isDefault" @click="setDefault(item.id)">
            <uni-icons type="star" size="16" color="#666"></uni-icons>
            <text class="action-text">设为默认</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 空状态 - 修改为使用统一的empty.png -->
    <view class="empty-state" v-else>
      <image class="empty-image" src="/static/images/empty.png" mode="aspectFit"></image>
      <text class="empty-text">暂无地址信息</text>
    </view>
    
    <!-- 底部添加按钮 -->
    <view class="add-btn" @click="showAddressForm('add')">
      <text class="add-text">添加新地址</text>
    </view>
    
    <!-- 地址表单弹窗 -->
    <uni-popup ref="addressPopup" type="bottom">
      <view class="popup-container">
        <view class="popup-header">
          <text class="popup-title">{{ formType === 'add' ? '添加新地址' : '编辑地址' }}</text>
          <view class="popup-close" @click="hideAddressForm">
            <uni-icons type="close" size="20" color="#333"></uni-icons>
          </view>
        </view>
        <view class="popup-content">
          <address-form 
            :type="formType" 
            :address="currentAddress" 
            @submit="handleAddressSubmit"
          ></address-form>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script>
import AddressForm from '@/components/AddressForm.vue';
import { getAddressList, addAddress, updateAddress, deleteAddress, setDefaultAddress } from '@/api/user.js';
import { checkLogin } from '@/utils/auth.js';

export default {
  components: {
    AddressForm
  },
  data() {
    return {
      addressList: [],
      statusBarHeight: 20, // 默认值
      formType: 'add', // add 或 edit
      currentAddress: null, // 当前编辑的地址对象
      isLoading: false // 加载状态
    };
  },
  
  onLoad() {
    // 获取状态栏高度
    this.getStatusBarHeight();
    
    // 检查登录状态
    this.checkLogin();
  },
  
  onShow() {
    // 每次进入页面时加载地址列表
    this.loadAddressList();
  },
  
  methods: {
    // 获取状态栏高度
    getStatusBarHeight() {
      // 使用新的API替代过时的getSystemInfo
      try {
        // 使用新API
        const res = wx.getWindowInfo();
        this.statusBarHeight = res.statusBarHeight || 20;
      } catch (error) {
        // 兼容处理：如果新API不可用，回退到旧API
        try {
          const res = uni.getSystemInfoSync();
          this.statusBarHeight = res.statusBarHeight || 20;
        } catch (e) {
          // 如果都失败，使用默认值
          this.statusBarHeight = 20;
          console.error('获取状态栏高度失败', e);
        }
      }
    },
    
    goBack() {
      uni.navigateBack();
    },
    
    // 加载地址列表
    async loadAddressList() {
      this.isLoading = true;
      
      try {
        // 调用API获取数据
        const res = await getAddressList();
        this.addressList = res.data;
        this.isLoading = false;
      } catch (error) {
        console.error('加载地址列表失败', error);
        uni.showToast({
          title: '加载地址列表失败',
          icon: 'none'
        });
        this.isLoading = false;
      }
    },
    
    // 格式化手机号，中间4位显示*
    formatPhone(phone) {
      if (!phone) return '';
      return phone.substring(0, 3) + '****' + phone.substring(7);
    },
    
    // 显示地址表单弹窗
    showAddressForm(type, address = null) {
      this.formType = type;
      this.currentAddress = address;
      this.$refs.addressPopup.open();
    },
    
    // 隐藏地址表单弹窗
    hideAddressForm() {
      this.$refs.addressPopup.close();
    },
    
    // 编辑地址
    editAddress(item) {
      this.showAddressForm('edit', item);
    },
    
    // 处理地址表单提交
    async handleAddressSubmit(addressData) {
      try {
        if (this.formType === 'add') {
          // 添加地址
          // 调用API添加地址
          const result = await addAddress(addressData);
          if (result.code === 200) {
            // 重新加载地址列表以获取最新数据
            await this.loadAddressList();
            
            uni.showToast({
              title: '添加成功',
              icon: 'success'
            });
          } else {
            throw new Error(result.message || '添加失败');
          }
        } else {
          // 编辑地址
          // 调用API更新地址
          const result = await updateAddress(addressData.id, addressData);
          if (result.code === 200) {
            // 重新加载地址列表以获取最新数据
            await this.loadAddressList();
            
            uni.showToast({
              title: '更新成功',
              icon: 'success'
            });
          } else {
            throw new Error(result.message || '更新失败');
          }
        }
        
        // 关闭弹窗
        this.hideAddressForm();
      } catch (error) {
        console.error('提交地址失败', error);
        uni.showToast({
          title: error.message || '提交失败，请重试',
          icon: 'none'
        });
      }
    },
    
    // 删除地址
    deleteAddress(id) {
      uni.showModal({
        title: '提示',
        content: '确定要删除该地址吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              // 调用API删除地址
              const result = await deleteAddress(id);
              if (result.code === 200) {
                // 重新加载地址列表
                await this.loadAddressList();
              
                uni.showToast({
                  title: '删除成功',
                  icon: 'success'
                });
              } else {
                throw new Error(result.message || '删除失败');
              }
            } catch (error) {
              console.error('删除地址失败', error);
              uni.showToast({
                title: error.message || '删除失败，请重试',
                icon: 'none'
              });
            }
          }
        }
      });
    },
    
    // 设置默认地址
    async setDefault(id) {
      try {
        // 调用API设置默认地址
        const result = await setDefaultAddress(id);
        if (result.code === 200) {
          // 重新加载地址列表
          await this.loadAddressList();
        
          uni.showToast({
            title: '设置成功',
            icon: 'success'
          });
        } else {
          throw new Error(result.message || '设置失败');
        }
      } catch (error) {
        console.error('设置默认地址失败', error);
        uni.showToast({
          title: error.message || '设置失败，请重试',
          icon: 'none'
        });
      }
    },
    
    // 选择地址
    selectAddress(item) {
      // 若有页面参数type=select，表示从选择地址页面进入
      const pages = getCurrentPages();
      const currentPage = pages[pages.length - 1];
      
      if (currentPage.options && currentPage.options.type === 'select') {
        console.log('选择地址：', item);
        
        // 准备地址数据
        const addressData = {
          id: item.id,
          name: item.name,
          phone: item.phone,
          // 完整地址字符串，用于显示
          address: item.province + item.city + item.district + ' ' + item.detailAddress,
          // 保留原始数据，以便需要时使用
          province: item.province,
          city: item.city,
          district: item.district,
          detailAddress: item.detailAddress,
          addressType: item.addressType,
          isDefault: item.isDefault
        };
        
        try {
          // 获取上一页
          const prevPage = pages[pages.length - 2];
          
          // 方法1：通过页面实例直接传值
          if (prevPage && prevPage.$vm) {
            prevPage.$vm.selectedAddress = addressData;
          }
          
          // 方法2：通过事件通道发送数据回上一页
          const eventChannel = this.getOpenerEventChannel();
          if (eventChannel) {
            console.log('发送地址数据通过eventChannel');
            eventChannel.emit('selectAddress', addressData);
          } else {
            console.log('获取eventChannel失败');
          }
          
          // 方法3：通过uni.$emit全局事件
          uni.$emit('addressSelected', addressData);
          
          // 显示提示
          uni.showToast({
            title: '已选择地址',
            icon: 'success',
            duration: 1500
          });
          
          // 延迟返回，确保数据传递完成
          setTimeout(() => {
            uni.navigateBack();
          }, 300);
        } catch (error) {
          console.error('地址选择错误:', error);
          uni.showToast({
            title: '选择地址失败',
            icon: 'none'
          });
          
          // 出错时也返回
          uni.navigateBack();
        }
      }
    },
    
    // 检查登录状态
    checkLogin() {
      // 使用auth.js中的方法检查登录状态
      checkLogin(true);
    }
  }
};
</script>

<style lang="scss">
.address-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 120rpx;
}

/* 状态栏占位 */
.status-bar {
  width: 100%;
  background-color: #fff;
}

.address-header {
  position: relative;
  padding: 30rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
  margin-bottom: 20rpx;
  /* 调整顶部和底部的padding更合理 */
  padding-top: 20rpx;
  padding-bottom: 20rpx;
}

.back-btn {
  position: absolute;
  left: 30rpx;
}

.page-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.address-list {
  margin: 0 20rpx;
}

.address-item {
  background-color: #fff;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
  overflow: hidden;
  /* 添加阴影效果 */
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.address-info {
  padding: 30rpx;
  border-bottom: 1rpx solid #f5f5f5;
}

.address-top {
  display: flex;
  align-items: center;
  margin-bottom: 10rpx;
}

.name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-right: 20rpx;
}

.phone {
  font-size: 28rpx;
  color: #666;
}

.tag {
  font-size: 24rpx;
  color: #ff6600;
  background-color: rgba(255, 102, 0, 0.1);
  padding: 4rpx 12rpx;
  border-radius: 20rpx;
  margin-left: auto;
}

.address-text {
  font-size: 28rpx;
  color: #333;
  line-height: 1.5;
}

.address-type {
  font-size: 24rpx;
  color: #999;
  margin-top: 8rpx;
  display: block;
}

.address-actions {
  display: flex;
  padding: 20rpx 0;
  background-color: #fff;
}

.action-btn {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.action-text {
  font-size: 28rpx;
  color: #666;
  margin-left: 10rpx;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
  margin-top: 60rpx;
}

.empty-image {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 30rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

.add-btn {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 100rpx;
  background-color: #ff6600;
  display: flex;
  align-items: center;
  justify-content: center;
}

.add-text {
  font-size: 32rpx;
  color: #fff;
  font-weight: bold;
}

/* 弹窗样式 */
.popup-container {
  background-color: #fff;
  border-radius: 24rpx 24rpx 0 0;
  padding-bottom: 30rpx;
  max-height: 90vh;
  overflow-y: auto;
}

.popup-header {
  position: relative;
  padding: 30rpx;
  text-align: center;
  border-bottom: 1rpx solid #eee;
}

.popup-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.popup-close {
  position: absolute;
  right: 30rpx;
  top: 30rpx;
}

.popup-content {
  padding: 20rpx 0;
}
</style> 