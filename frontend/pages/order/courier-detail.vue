<template>
  <view class="courier-detail-container">
    <!-- 订单状态栏 -->
    <view class="status-bar">
      <view class="status-icon" :class="'status-' + orderDetail.status">
        <text class="iconfont" :class="getStatusIcon(orderDetail.status)"></text>
      </view>
      <view class="status-info">
        <text class="status-text">{{ getStatusText(orderDetail.status) }}</text>
        <text class="status-desc">{{ getStatusDesc(orderDetail.status) }}</text>
      </view>
    </view>
    
    <!-- 订单基本信息 -->
    <view class="order-info-card">
      <view class="order-header">
        <text class="order-no">订单号：{{ orderDetail.orderNo }}</text>
        <text class="order-fee">配送费：¥{{ orderDetail.price }}</text>
      </view>
      <view class="order-time">
        <text>下单时间：{{ formatDate(orderDetail.createdAt) }}</text>
      </view>
    </view>
    
    <!-- 地址信息 -->
    <view class="address-card">
      <!-- 寄件人信息 -->
      <view class="address-section">
        <view class="address-header">
          <view class="address-icon sender">寄</view>
          <text class="address-title">寄件人信息</text>
          <view class="call-btn" @click="makePhoneCall(orderDetail.senderPhone)">
            <text class="iconfont icon-phone"></text>
          </view>
        </view>
        <view class="address-content">
          <view class="address-user">
            <text class="user-name">{{ orderDetail.senderName }}</text>
            <text class="user-phone">{{ orderDetail.senderPhone }}</text>
          </view>
          <view class="address-detail">
            <text class="address-text">{{ orderDetail.senderAddress }}</text>
          </view>
          <view class="map-btn" @click="openMap(orderDetail.senderAddress, true)">
            <text class="iconfont icon-map"></text>
            <text>导航</text>
          </view>
        </view>
      </view>
      
      <!-- 分隔线 -->
      <view class="divider">
        <view class="divider-line"></view>
        <view class="divider-arrow">
          <text class="iconfont icon-arrow-down"></text>
        </view>
        <view class="divider-line"></view>
      </view>
      
      <!-- 收件人信息 -->
      <view class="address-section">
        <view class="address-header">
          <view class="address-icon receiver">收</view>
          <text class="address-title">收件人信息</text>
          <view class="call-btn" @click="makePhoneCall(orderDetail.receiverPhone)">
            <text class="iconfont icon-phone"></text>
          </view>
        </view>
        <view class="address-content">
          <view class="address-user">
            <text class="user-name">{{ orderDetail.receiverName }}</text>
            <text class="user-phone">{{ orderDetail.receiverPhone }}</text>
          </view>
          <view class="address-detail">
            <text class="address-text">{{ orderDetail.receiverAddress }}</text>
          </view>
          <view class="map-btn" @click="openMap(orderDetail.receiverAddress, false)">
            <text class="iconfont icon-map"></text>
            <text>导航</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 包裹信息 -->
    <view class="package-card">
      <view class="card-title">包裹信息</view>
      <view class="package-info">
        <view class="info-item">
          <text class="info-label">包裹类型</text>
          <text class="info-value">{{ getPackageTypeText(orderDetail.packageType) }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">重量</text>
          <text class="info-value">{{ orderDetail.weight }}kg</text>
        </view>
        <view class="info-item" v-if="orderDetail.insuranceValue > 0">
          <text class="info-label">保价金额</text>
          <text class="info-value">¥{{ orderDetail.insuranceValue }}</text>
        </view>
        <view class="info-item" v-if="orderDetail.note">
          <text class="info-label">备注</text>
          <text class="info-value">{{ orderDetail.note }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">配送服务</text>
          <view class="service-tags">
            <text class="service-tag" v-if="orderDetail.isUrgent">加急配送</text>
            <text class="service-tag" v-if="orderDetail.needReceipt">签收回执</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 操作按钮区域 -->
    <view class="action-bar">
      <block v-if="orderDetail.status === 1">
        <button class="primary-btn" @click="updateStatus(2)">开始取件</button>
      </block>
      <block v-if="orderDetail.status === 2">
        <button class="primary-btn" @click="updateStatus(3)">已取到件</button>
      </block>
      <block v-if="orderDetail.status === 3">
        <button class="primary-btn" @click="updateStatus(4)">开始配送</button>
      </block>
      <block v-if="orderDetail.status === 4">
        <button class="primary-btn" @click="showSignatureModal">确认送达</button>
      </block>
      <block v-if="orderDetail.status === 0">
        <button class="primary-btn" @click="acceptOrder">立即接单</button>
      </block>
      <button class="secondary-btn" @click="contactCustomerService">联系客服</button>
    </view>
    
    <!-- 签名确认弹窗 -->
    <uni-popup ref="signaturePopup" type="center">
      <view class="signature-popup">
        <view class="popup-title">收件人签名</view>
        <view class="signature-area" @touchstart="touchStart" @touchmove="touchMove" @touchend="touchEnd">
          <canvas canvas-id="signatureCanvas" id="signatureCanvas" class="signature-canvas"></canvas>
          <text v-if="!hasSignature" class="signature-tip">请让收件人在此签名</text>
          <image v-if="signatureImage" :src="signatureImage" mode="aspectFit" class="signature-image"></image>
        </view>
        <view class="popup-buttons">
          <button class="popup-btn cancel-btn" @click="clearSignature">清除</button>
          <button class="popup-btn confirm-btn" @click="confirmDelivery" :disabled="!hasSignature">确认送达</button>
        </view>
      </view>
    </uni-popup>
    
    <!-- 加载提示 -->
    <view class="loading-overlay" v-if="loading">
      <view class="loading-content">
        <view class="loading-spinner"></view>
        <text>处理中...</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getOrderDetail, acceptOrder, updateOrderStatus, getOrderStatusText, getPackageTypeText } from '@/api/order';
import uniPopup from '@/uni_modules/uni-popup/components/uni-popup/uni-popup.vue';
import { getCourierByUserId } from '@/api/courier';

export default {
  components: {
    uniPopup
  },
  data() {
    return {
      orderId: null,
      orderDetail: {
        id: null,
        orderNo: '',
        status: 0,
        senderName: '',
        senderPhone: '',
        senderAddress: '',
        receiverName: '',
        receiverPhone: '',
        receiverAddress: '',
        packageType: 0,
        weight: 0,
        price: 0,
        insuranceValue: 0,
        note: '',
        isUrgent: false,
        needReceipt: false,
        createdAt: '',
        updatedAt: ''
      },
      loading: false,
      signatureImage: '',
      courierId: null,
      hasSignature: false,
      ctx: null,
      points: [],
      canvasWidth: 0,
      canvasHeight: 0,
      isDrawing: false
    };
  },
  onLoad(options) {
    if (options.id) {
      this.orderId = options.id;
      this.loadOrderDetail();
      this.loadCourierInfo();
    } else {
      uni.showToast({
        title: '缺少订单ID',
        icon: 'none'
      });
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
    }
  },
  onReady() {
    this.ctx = uni.createCanvasContext('signatureCanvas', this);
    
    this.ctx.strokeStyle = '#000000';
    this.ctx.lineWidth = 5;
    this.ctx.lineCap = 'round';
  },
  methods: {
    // 加载快递员信息
    loadCourierInfo() {
      // 先尝试从本地存储获取快递员信息
      const courierInfo = uni.getStorageSync('courierInfo');
      if (courierInfo && courierInfo.id) {
        this.courierId = courierInfo.id;
        console.log('从本地存储获取到快递员信息:', this.courierId);
        return;
      }
      
      // 如果本地存储没有，则从API获取
      const userInfo = uni.getStorageSync('userInfo');
      if (!userInfo || !userInfo.id) {
        console.error('未找到用户信息');
        return;
      }
      
      // 调用 getCourierByUserId API
      getCourierByUserId(userInfo.id)
        .then(res => {
          if (res.code === 200 && res.data) {
            this.courierId = res.data.id;
            // 保存到本地存储，便于下次使用
            uni.setStorageSync('courierInfo', res.data);
            console.log('已从API加载快递员信息:', this.courierId);
          } else {
            console.error('获取快递员信息失败:', res.message);
            uni.showToast({
              title: '获取快递员信息失败，请返回重试',
              icon: 'none',
              duration: 2000
            });
          }
        })
        .catch(err => {
          console.error('获取快递员信息失败:', err);
          uni.showToast({
            title: '网络异常，请检查连接后重试',
            icon: 'none',
            duration: 2000
          });
        });
    },
    
    // 加载订单详情
    loadOrderDetail() {
      this.loading = true;
      
      getOrderDetail(this.orderId)
        .then(res => {
          if (res.code === 200 && res.data) {
            this.orderDetail = res.data;
            console.log('订单详情:', this.orderDetail);
          } else {
            uni.showToast({
              title: '获取订单详情失败',
              icon: 'none'
            });
          }
        })
        .catch(err => {
          console.error('获取订单详情失败:', err);
          uni.showToast({
            title: '获取订单详情失败',
            icon: 'none'
          });
        })
        .finally(() => {
          this.loading = false;
        });
    },
    
    // 获取状态图标
    getStatusIcon(status) {
      const iconMap = {
        0: 'icon-wait',
        1: 'icon-accepted',
        2: 'icon-pickup',
        3: 'icon-package',
        4: 'icon-delivery',
        5: 'icon-delivered',
        6: 'icon-completed',
        7: 'icon-cancelled'
      };
      return iconMap[status] || 'icon-wait';
    },
    
    // 获取状态文字
    getStatusText(status) {
      return getOrderStatusText(status);
    },
    
    // 获取状态描述
    getStatusDesc(status) {
      const descMap = {
        0: '订单等待快递员接单中',
        1: '订单已接单，请准备取件',
        2: '正在前往取件地址',
        3: '已成功取件，准备配送',
        4: '包裹正在配送途中',
        5: '包裹已送达目的地',
        6: '订单已完成',
        7: '订单已取消'
      };
      return descMap[status] || '';
    },
    
    // 获取包裹类型文字
    getPackageTypeText(type) {
      return getPackageTypeText(type);
    },
    
    // 格式化日期
    formatDate(dateStr) {
      if (!dateStr) return '';
      return dateStr;
    },
    
    // 拨打电话
    makePhoneCall(phone) {
      if (!phone) {
        uni.showToast({
          title: '电话号码不存在',
          icon: 'none'
        });
        return;
      }
      
      uni.makePhoneCall({
        phoneNumber: phone,
        fail: () => {
          uni.showToast({
            title: '拨打电话失败',
            icon: 'none'
          });
        }
      });
    },
    
    // 打开地图导航
    openMap(address, isSender) {
      if (!address) {
        uni.showToast({
          title: '地址信息不完整',
          icon: 'none'
        });
        return;
      }
      
      const title = isSender ? '寄件人位置' : '收件人位置';
      
      // 实际项目中应该有经纬度信息，这里仅用地址文本演示
      uni.showModal({
        title: '导航提示',
        content: `即将导航到${title}: ${address}`,
        confirmText: '确认导航',
        success: (res) => {
          if (res.confirm) {
            // 这里应该有调用地图 API 的逻辑
            uni.showToast({
              title: '正在为您导航',
              icon: 'success'
            });
          }
        }
      });
    },
    
    // 更新订单状态
    updateStatus(newStatus) {
      uni.showModal({
        title: '状态更新',
        content: `确认将订单状态更新为"${this.getStatusText(newStatus)}"吗？`,
        success: (res) => {
          if (res.confirm) {
            this.loading = true;
            
            updateOrderStatus(this.orderId, newStatus)
              .then(res => {
                if (res.code === 200) {
                  uni.showToast({
                    title: '状态更新成功',
                    icon: 'success'
                  });
                  
                  // 刷新订单详情
                  setTimeout(() => {
                    this.loadOrderDetail();
                  }, 1000);
                } else {
                  uni.showToast({
                    title: res.message || '状态更新失败',
                    icon: 'none'
                  });
                }
              })
              .catch(err => {
                console.error('状态更新失败:', err);
                uni.showToast({
                  title: '状态更新失败',
                  icon: 'none'
                });
              })
              .finally(() => {
                this.loading = false;
              });
          }
        }
      });
    },
    
    // 接单
    acceptOrder() {
      uni.showModal({
        title: '接单确认',
        content: '确认接此订单吗？',
        success: (res) => {
          if (res.confirm) {
            this.loading = true;
            
            // 使用data中保存的快递员ID
            if (!this.courierId) {
              // 如果没有courierId，尝试再次加载
              this.loadCourierInfo();
              
              // 如果仍然没有，则提示错误
              if (!this.courierId) {
                uni.showToast({
                  title: '未找到快递员信息',
                  icon: 'none'
                });
                this.loading = false;
                return;
              }
            }
            
            acceptOrder(this.orderId, this.courierId)
              .then(res => {
                if (res.code === 200) {
                  uni.showToast({
                    title: '接单成功',
                    icon: 'success'
                  });
                  
                  // 刷新订单详情
                  setTimeout(() => {
                    this.loadOrderDetail();
                  }, 1000);
                } else {
                  uni.showToast({
                    title: res.message || '接单失败',
                    icon: 'none'
                  });
                }
              })
              .catch(err => {
                console.error('接单失败:', err);
                uni.showToast({
                  title: '接单失败',
                  icon: 'none'
                });
              })
              .finally(() => {
                this.loading = false;
              });
          }
        }
      });
    },
    
    // 显示签名弹窗
    showSignatureModal() {
      this.$refs.signaturePopup.open();
      
      setTimeout(() => {
        const query = uni.createSelectorQuery().in(this);
        query.select('.signature-area').boundingClientRect(data => {
          this.canvasWidth = data.width;
          this.canvasHeight = data.height;
          
          this.clearCanvas();
        }).exec();
      }, 100);
    },
    
    // 清除签名
    clearSignature() {
      this.clearCanvas();
      this.signatureImage = '';
      this.hasSignature = false;
      this.points = [];
    },
    
    // 清空画布
    clearCanvas() {
      if (this.ctx) {
        this.ctx.clearRect(0, 0, this.canvasWidth, this.canvasHeight);
        this.ctx.draw();
      }
    },
    
    // 触摸开始事件
    touchStart(e) {
      const touch = e.touches[0];
      this.isDrawing = true;
      
      this.points = [{
        x: touch.pageX,
        y: touch.pageY
      }];
      
      this.ctx.beginPath();
      this.ctx.moveTo(touch.pageX, touch.pageY);
    },
    
    // 触摸移动事件
    touchMove(e) {
      if (!this.isDrawing) return;
      
      const touch = e.touches[0];
      
      this.points.push({
        x: touch.pageX,
        y: touch.pageY
      });
      
      if (this.points.length >= 2) {
        this.hasSignature = true;
        
        const lastPoint = this.points[this.points.length - 1];
        const prevPoint = this.points[this.points.length - 2];
        
        this.ctx.moveTo(prevPoint.x, prevPoint.y);
        this.ctx.lineTo(lastPoint.x, lastPoint.y);
        this.ctx.stroke();
        this.ctx.draw(true);
      }
    },
    
    // 触摸结束事件
    touchEnd() {
      if (!this.isDrawing) return;
      this.isDrawing = false;
      
      if (this.hasSignature) {
        setTimeout(() => {
          uni.canvasToTempFilePath({
            canvasId: 'signatureCanvas',
            success: (res) => {
              this.signatureImage = res.tempFilePath;
              console.log('签名图片已保存');
            },
            fail: (err) => {
              console.error('签名图片保存失败:', err);
            }
          }, this);
        }, 100);
      }
    },
    
    // 确认送达
    confirmDelivery() {
      if (!this.hasSignature || !this.signatureImage) {
        uni.showToast({
          title: '请先让收件人签名',
          icon: 'none'
        });
        return;
      }
      
      this.$refs.signaturePopup.close();
      this.loading = true;
      
      uni.showLoading({
        title: '正在上传签名...',
        mask: true
      });
      
      setTimeout(() => {
        updateOrderStatus(this.orderId, 5)
          .then(res => {
            uni.hideLoading();
            if (res.code === 200) {
              uni.showToast({
                title: '订单已送达',
                icon: 'success'
              });
              
              setTimeout(() => {
                this.loadOrderDetail();
              }, 1000);
            } else {
              uni.showToast({
                title: res.message || '确认送达失败',
                icon: 'none'
              });
            }
          })
          .catch(err => {
            uni.hideLoading();
            console.error('确认送达失败:', err);
            uni.showToast({
              title: '确认送达失败',
              icon: 'none'
            });
          })
          .finally(() => {
            this.loading = false;
          });
      }, 1000);
    },
    
    // 联系客服
    contactCustomerService() {
      uni.makePhoneCall({
        phoneNumber: '400-888-8888', // 这里应该填写实际的客服电话
        fail: () => {
          uni.showToast({
            title: '拨打电话失败',
            icon: 'none'
          });
        }
      });
    }
  }
};
</script>

<style>
/* 旋转动画 */
@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.courier-detail-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 140rpx;
}

/* 状态栏样式 */
.status-bar {
  display: flex;
  align-items: center;
  padding: 40rpx 30rpx;
  background-color: #3cc51f;
  color: #fff;
}

.status-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.2);
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 20rpx;
}

.status-icon .iconfont {
  font-size: 40rpx;
}

.status-info {
  flex: 1;
}

.status-text {
  font-size: 36rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
  display: block;
}

.status-desc {
  font-size: 26rpx;
  opacity: 0.8;
}

.status-0 {
  background-color: #ff9900;
}
.status-1 {
  background-color: #3cc51f;
}
.status-2, .status-3, .status-4 {
  background-color: #007aff;
}
.status-5, .status-6 {
  background-color: #8f8f94;
}
.status-7 {
  background-color: #dd524d;
}

/* 卡片通用样式 */
.order-info-card, .address-card, .package-card {
  margin: 20rpx;
  background-color: #fff;
  border-radius: 12rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

/* 订单信息卡片 */
.order-header {
  display: flex;
  justify-content: space-between;
  padding-bottom: 20rpx;
}

.order-no {
  font-size: 28rpx;
  color: #666;
}

.order-fee {
  font-size: 28rpx;
  color: #ff6b00;
  font-weight: bold;
}

.order-time {
  font-size: 26rpx;
  color: #999;
}

/* 地址卡片样式 */
.address-section {
  margin-bottom: 20rpx;
}

.address-section:last-child {
  margin-bottom: 0;
}

.address-header {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.address-icon {
  width: 40rpx;
  height: 40rpx;
  border-radius: 20rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 24rpx;
  color: #fff;
  margin-right: 20rpx;
}

.address-icon.sender {
  background-color: #007aff;
}

.address-icon.receiver {
  background-color: #3cc51f;
}

.address-title {
  flex: 1;
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
}

.call-btn {
  width: 60rpx;
  height: 60rpx;
  background-color: #f5f5f5;
  border-radius: 30rpx;
  display: flex;
  justify-content: center;
  align-items: center;
}

.call-btn .iconfont {
  font-size: 36rpx;
  color: #3cc51f;
}

.address-user {
  display: flex;
  margin-bottom: 10rpx;
}

.user-name {
  font-size: 28rpx;
  color: #333;
  margin-right: 20rpx;
}

.user-phone {
  font-size: 28rpx;
  color: #666;
}

.address-detail {
  margin-bottom: 15rpx;
}

.address-text {
  font-size: 28rpx;
  color: #333;
  line-height: 1.5;
}

.map-btn {
  display: inline-flex;
  align-items: center;
  padding: 10rpx 20rpx;
  background-color: #f5f5f5;
  border-radius: 30rpx;
  font-size: 24rpx;
  color: #3cc51f;
}

.map-btn .iconfont {
  font-size: 28rpx;
  margin-right: 10rpx;
}

.divider {
  display: flex;
  align-items: center;
  margin: 30rpx 0;
}

.divider-line {
  flex: 1;
  height: 2rpx;
  background-color: #f0f0f0;
}

.divider-arrow {
  margin: 0 20rpx;
  color: #ccc;
}

/* 包裹信息卡片 */
.card-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}

.package-info {
  
}

.info-item {
  display: flex;
  margin-bottom: 20rpx;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-label {
  width: 150rpx;
  font-size: 28rpx;
  color: #666;
}

.info-value {
  flex: 1;
  font-size: 28rpx;
  color: #333;
}

.service-tags {
  display: flex;
  flex-wrap: wrap;
}

.service-tag {
  font-size: 24rpx;
  color: #3cc51f;
  background-color: rgba(60, 197, 31, 0.1);
  padding: 6rpx 16rpx;
  border-radius: 20rpx;
  margin-right: 20rpx;
  margin-bottom: 10rpx;
}

/* 操作按钮区域 */
.action-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 20rpx;
  background-color: #fff;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
  display: flex;
  z-index: 100;
}

.primary-btn, .secondary-btn {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  font-size: 30rpx;
  border-radius: 40rpx;
  margin: 0 10rpx;
}

.primary-btn {
  background-color: #3cc51f;
  color: #fff;
}

.secondary-btn {
  background-color: #f5f5f5;
  color: #666;
}

/* 签名弹窗 */
.signature-popup {
  width: 600rpx;
  background-color: #fff;
  border-radius: 12rpx;
  overflow: hidden;
}

.popup-title {
  text-align: center;
  font-size: 32rpx;
  color: #333;
  padding: 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.signature-area {
  height: 400rpx;
  border: 1rpx dashed #ddd;
  margin: 30rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}

.signature-canvas {
  width: 100%;
  height: 100%;
  position: absolute;
  left: 0;
  top: 0;
  z-index: 1;
}

.signature-tip {
  font-size: 28rpx;
  color: #999;
  position: absolute;
  z-index: 0;
}

.signature-image {
  width: 100%;
  height: 100%;
  position: absolute;
  left: 0;
  top: 0;
  z-index: 2;
}

.popup-buttons {
  display: flex;
  border-top: 1rpx solid #f0f0f0;
}

.popup-btn {
  flex: 1;
  height: 100rpx;
  line-height: 100rpx;
  text-align: center;
  font-size: 32rpx;
}

.cancel-btn {
  border-right: 1rpx solid #f0f0f0;
  color: #999;
}

.confirm-btn {
  color: #3cc51f;
}

.confirm-btn[disabled] {
  color: #ccc;
}

/* 加载中动画 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.loading-content {
  background-color: #fff;
  padding: 40rpx;
  border-radius: 12rpx;
  text-align: center;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 6rpx solid #f3f3f3;
  border-top: 6rpx solid #3cc51f;
  border-radius: 50%;
  animation: rotate 1s linear infinite;
  margin: 0 auto 20rpx;
}
</style> 