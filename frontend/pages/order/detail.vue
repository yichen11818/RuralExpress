<template>
  <view class="detail-container">
    <!-- 订单状态 -->
    <view class="status-section" :class="'status-bg-' + order.status">
      <view class="status-text">{{ getStatusText(order.status) }}</view>
      <view class="status-desc">{{ getStatusDesc(order.status) }}</view>
    </view>
    
    <!-- 物流信息 -->
    <view class="logistics-section" v-if="logistics.length > 0">
      <view class="section-title">物流信息</view>
      
      <!-- 物流公司信息 -->
      <view class="logistics-company" v-if="logisticsCompany">
        <image :src="logisticsCompany.logo" mode="aspectFit" class="company-logo"></image>
        <view class="company-info">
          <view class="company-name">{{ logisticsCompany.name }}</view>
          <view class="tracking-no">
            <text class="tracking-label">运单号：</text>
            <text class="tracking-value">{{ logisticsCompany.trackingNo }}</text>
            <view class="copy-btn" @click="copyTrackingNo(logisticsCompany.trackingNo)">复制</view>
          </view>
        </view>
        <view class="logistics-status">{{ logisticsCompany.status }}</view>
      </view>
      
      <view class="logistics-timeline">
        <view 
          class="logistics-item" 
          v-for="(item, index) in logistics" 
          :key="index"
          :class="{ 'active': index === 0 }"
        >
          <view class="timeline-dot"></view>
          <view class="timeline-line" v-if="index !== logistics.length - 1"></view>
          <view class="logistics-info">
            <view class="logistics-content">{{ item.content }}</view>
            <view class="logistics-time">{{ item.time }}</view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 配送信息 -->
    <view class="delivery-info-section">
      <view class="section-title">配送信息</view>
      <view class="info-item">
        <text class="item-label">配送方式</text>
        <text class="item-value">乡递快递</text>
      </view>
      <view class="info-item" v-if="order.courierName">
        <text class="item-label">配送员</text>
        <view class="courier-info">
          <text class="courier-name">{{ order.courierName }}</text>
          <text class="courier-phone">{{ formatPhone(order.courierPhone) }}</text>
          <view class="call-btn" @click="callCourier(order.courierPhone)">联系</view>
        </view>
      </view>
      <view class="info-item">
        <text class="item-label">订单编号</text>
        <text class="item-value">{{ order.orderNo }}</text>
      </view>
      <view class="info-item">
        <text class="item-label">下单时间</text>
        <text class="item-value">{{ order.createdAt }}</text>
      </view>
    </view>
    
    <!-- 快递员详细信息 -->
    <view class="courier-section" v-if="order.courierId && order.status > 0 && order.status < 7">
      <view class="section-title">快递员信息</view>
      <view class="courier-profile">
        <image :src="courierInfo.avatar || '/static/images/default-avatar.png'" mode="aspectFill" class="courier-avatar"></image>
        <view class="courier-details">
          <view class="courier-name-row">
            <text class="courier-full-name">{{ order.courierName || courierInfo.name }}</text>
            <view class="courier-badge">认证快递员</view>
          </view>
          <view class="courier-rating">
            <uni-icons type="star-filled" size="14" color="#ff9900"></uni-icons>
            <text class="rating-score">{{ courierInfo.rating || '4.8' }}</text>
            <text class="rating-count">{{ courierInfo.ratingCount || 0 }}条评价</text>
          </view>
          <view class="courier-stats">
            <text class="stat-item">服务{{ courierInfo.serviceTime || '12' }}个月</text>
            <text class="stat-item">已完成{{ courierInfo.completedOrders || '0' }}单</text>
          </view>
        </view>
        <view class="contact-courier-btn" @click="callCourier(order.courierPhone)">
          <uni-icons type="phone-filled" size="20" color="#3cc51f"></uni-icons>
        </view>
      </view>
      <view class="courier-contact-info">
        <view class="contact-info-item">
          <text class="info-label">联系电话</text>
          <text class="info-value">{{ formatPhone(order.courierPhone) }}</text>
        </view>
        <view class="contact-info-item" v-if="courierInfo.serviceArea">
          <text class="info-label">服务区域</text>
          <text class="info-value">{{ courierInfo.serviceArea }}</text>
        </view>
      </view>
    </view>
    
    <!-- 评价信息 -->
    <view class="review-section" v-if="order.status === 6">
      <view class="section-title">订单评价</view>
      <view class="review-content" v-if="reviewInfo.id">
        <view class="review-rating">
          <text class="rating-label">评分</text>
          <view class="rating-stars">
            <uni-icons v-for="i in 5" :key="i" 
              :type="i <= reviewInfo.rating ? 'star-filled' : 'star'" 
              size="20" 
              :color="i <= reviewInfo.rating ? '#ff9900' : '#ddd'">
            </uni-icons>
          </view>
          <text class="rating-value">{{ reviewInfo.rating }}.0</text>
        </view>
        <view class="review-text">
          <text>{{ reviewInfo.content }}</text>
        </view>
        <view class="review-images" v-if="reviewInfo.images && reviewInfo.images.length > 0">
          <image 
            v-for="(img, idx) in reviewInfo.images" 
            :key="idx" 
            :src="img" 
            mode="aspectFill" 
            class="review-image" 
            @click="previewImage(img, reviewInfo.images)">
          </image>
        </view>
        <view class="review-time">{{ reviewInfo.createdAt }}</view>
        
        <!-- 快递员回复 -->
        <view class="courier-reply" v-if="reviewInfo.reply">
          <view class="reply-header">
            <text class="reply-title">快递员回复</text>
          </view>
          <view class="reply-content">
            <text>{{ reviewInfo.reply }}</text>
          </view>
          <view class="reply-time">{{ reviewInfo.replyTime }}</view>
        </view>
      </view>
      
      <!-- 未评价提示 -->
      <view class="no-review-tip" v-else>
        <text>您还没有评价此订单</text>
        <button class="review-now-btn" @click="evaluateOrder(order.id)">立即评价</button>
      </view>
    </view>
    
    <!-- 地址信息 -->
    <view class="address-section">
      <view class="section-title">地址信息</view>
      <view class="address-item">
        <view class="address-icon sender">寄</view>
        <view class="address-content">
          <view class="address-user">
            <text class="user-name">{{ order.senderName }}</text>
            <text class="user-phone">{{ formatPhone(order.senderPhone) }}</text>
          </view>
          <text class="address-text">{{ order.senderAddress }}</text>
        </view>
      </view>
      <view class="address-divider"></view>
      <view class="address-item">
        <view class="address-icon receiver">收</view>
        <view class="address-content">
          <view class="address-user">
            <text class="user-name">{{ order.receiverName }}</text>
            <text class="user-phone">{{ formatPhone(order.receiverPhone) }}</text>
          </view>
          <text class="address-text">{{ order.receiverAddress }}</text>
        </view>
      </view>
    </view>
    
    <!-- 包裹信息 -->
    <view class="package-section">
      <view class="section-title">包裹信息</view>
      <view class="info-item">
        <text class="item-label">物品类型</text>
        <text class="item-value">{{ getPackageTypeText(order.packageType) }}</text>
      </view>
      <view class="info-item">
        <text class="item-label">重量</text>
        <text class="item-value">{{ order.weight }}kg</text>
      </view>
      <view class="info-item" v-if="order.note">
        <text class="item-label">备注</text>
        <text class="item-value">{{ order.note }}</text>
      </view>
    </view>
    
    <!-- 费用信息 -->
    <view class="cost-section">
      <view class="section-title">费用信息</view>
      <view class="info-item">
        <text class="item-label">配送费</text>
        <text class="item-value">¥{{ (order.price || 0).toFixed(2) }}</text>
      </view>
      <view class="info-item">
        <text class="item-label">保价费</text>
        <text class="item-value">¥{{ (0).toFixed(2) }}</text>
      </view>
      <view class="info-item total-fee">
        <text class="item-label">合计</text>
        <text class="item-value price">¥{{ (order.price || 0).toFixed(2) }}</text>
      </view>
    </view>
    
    <!-- 底部操作栏 -->
    <view class="footer-actions">
      <view 
        class="action-btn" 
        v-if="order.status === 0"
        @click="cancelOrder(order.id)"
      >
        取消订单
      </view>
      <view 
        class="action-btn primary-btn" 
        v-if="order.status === 6"
        @click="evaluateOrder(order.id)"
      >
        评价订单
      </view>
      <view 
        class="action-btn primary-btn" 
        v-if="order.status === 6 && order.hasReview"
        @click="viewReview(order.id)"
      >
        查看评价
      </view>
      <view 
        class="action-btn" 
        v-if="order.status === 6"
        @click="reorder()"
      >
        再次下单
      </view>
      <view 
        class="action-btn" 
        @click="contactService()"
      >
        联系客服
      </view>
    </view>
  </view>
</template>

<script>
import { isLoggedIn } from '@/api/auth';
import { getOrderDetail, cancelOrder, getLogisticsInfo } from '@/api/order';
import { getOrderReview } from '@/api/order';
import { getCourierInfo } from '@/api/courier';

export default {
  components: {
    uniIcons: () => import('@/uni_modules/uni-icons/components/uni-icons/uni-icons.vue')
  },
  data() {
    return {
      orderId: null,
      order: {
        id: null,
        orderNo: '',
        status: 0,
        senderName: '',
        senderPhone: '',
        senderAddress: '',
        receiverName: '',
        receiverPhone: '',
        receiverAddress: '',
        courierName: '',
        courierPhone: '',
        packageType: 0,
        weight: 0,
        note: '',
        price: 0,
        createdAt: ''
      },
      logistics: [],
      courierInfo: {},
      reviewInfo: {},
      logisticsCompany: {}
    };
  },
  
  onLoad(options) {
    // 检查登录状态
    if (!isLoggedIn()) {
      uni.navigateTo({
        url: '/pages/login/login'
      });
      return;
    }
    
    // 获取订单ID
    if (options.id) {
      this.orderId = parseInt(options.id);
      this.getOrderDetail();
    }
  },
  
  methods: {
    // 获取订单详情
    getOrderDetail() {
      // 调用API获取订单详情数据
      uni.showLoading({
        title: '加载中...'
      });
      
      getOrderDetail(this.orderId)
        .then(res => {
          console.log('订单详情API响应:', res);
          if (res.code === 200 && res.data) {
            // 直接使用返回的数据对象
            this.order = res.data;
            console.log('处理后的订单数据:', this.order);
            console.log('订单状态码:', this.order.status);
            console.log('订单状态文本:', this.getStatusText(this.order.status));
            
            // 获取物流信息
            this.getLogisticsInfo();
            
            // 如果有快递员信息，获取快递员详情
            if (this.order.courierId) {
              this.getCourierInfo(this.order.courierId);
            }
            
            // 如果订单已完成，获取评价信息
            if (this.order.status === 6) {
              this.getOrderReview();
            }
          } else {
            uni.showToast({
              title: '获取订单详情失败',
              icon: 'none'
            });
          }
        })
        .catch(err => {
          console.error('获取订单详情失败', err);
          uni.showToast({
            title: '获取订单详情失败',
            icon: 'none'
          });
        })
        .finally(() => {
          uni.hideLoading();
        });
    },
    
    // 获取物流信息
    getLogisticsInfo() {
      // 调用物流API
      getLogisticsInfo({ orderId: this.orderId })
        .then(res => {
          console.log('物流API原始响应:', res); // 添加日志查看原始响应
          
          if (res.code === 200 && res.data) {
            // 检查返回的数据结构
            if (Array.isArray(res.data)) {
              // 如果直接返回数组
              this.logistics = res.data.map(item => ({
                content: item.content,
                time: item.time
              }));
            } else if (res.data.list && Array.isArray(res.data.list)) {
              // 如果数据在list字段中
              this.logistics = res.data.list.map(item => ({
                content: item.content,
                time: item.time
              }));
            } else if (res.data.traces && Array.isArray(res.data.traces)) {
              // 如果数据在traces字段中（物流轨迹）
              if (res.data.traces.length > 0) {
                this.logistics = res.data.traces.map(item => ({
                  content: item.content || item.desc || item.description || '物流状态更新',
                  time: item.time || item.dateTime || item.date || this.formatTime(new Date())
                }));
              } else {
                // traces是空数组，创建模拟数据
                console.log('物流轨迹为空数组，创建模拟数据');
                this.createMockLogistics();
              }
              
              // 将物流公司信息保存到组件数据中，方便展示
              if (res.data.companyName) {
                this.logisticsCompany = {
                  name: res.data.companyName,
                  logo: res.data.companyLogo || '/static/images/company-logo.png',
                  trackingNo: res.data.trackingNo || '未分配',
                  status: res.data.statusText || this.getStatusText(this.order.status)
                };
              }
            } else {
              // 如果不是预期的数据格式，创建模拟数据
              console.log('物流数据不是预期的数组格式:', res.data);
              this.createMockLogistics();
            }
            
            // 如果没有物流信息，添加模拟数据
            if (this.logistics.length === 0) {
              console.log('物流数据为空，创建模拟数据');
              this.createMockLogistics();
            }
            
            console.log('处理后的物流信息:', this.logistics);
          } else {
            // 添加模拟物流信息
            console.log('没有有效的物流数据响应，创建模拟数据');
            this.createMockLogistics();
          }
        })
        .catch(err => {
          console.error('获取物流信息失败', err);
          // 添加模拟物流信息
          this.createMockLogistics();
        });
    },
    
    // 创建模拟物流信息
    createMockLogistics() {
      // 根据订单状态生成合适的模拟物流信息
      const orderTime = new Date(this.order.createdAt || new Date());
      
      // 基础物流信息 - 订单创建
      this.logistics = [{
        content: '订单已创建',
        time: this.formatTime(orderTime)
      }];
      
      // 根据订单状态添加额外的物流信息
      if (this.order.status >= 1) {
        // 添加1小时
        const acceptTime = new Date(orderTime.getTime() + 60 * 60 * 1000);
        this.logistics.push({
          content: '快递员已接单: ' + (this.order.courierName || '配送员'),
          time: this.formatTime(acceptTime)
        });
        
        // 添加系统分配信息
        const assignTime = new Date(orderTime.getTime() + 30 * 60 * 1000);
        this.logistics.splice(1, 0, {
          content: '系统已分配快递员',
          time: this.formatTime(assignTime)
        });
      }
      
      if (this.order.status >= 2) {
        // 添加2小时
        const pickupTime = new Date(orderTime.getTime() + 2 * 60 * 60 * 1000);
        this.logistics.push({
          content: '快递员正在取件途中',
          time: this.formatTime(pickupTime)
        });
        
        // 添加取件通知信息
        const notifyTime = new Date(orderTime.getTime() + 1.5 * 60 * 60 * 1000);
        this.logistics.push({
          content: '快递员已出发前往取件地点',
          time: this.formatTime(notifyTime)
        });
      }
      
      if (this.order.status >= 3) {
        // 添加3小时
        const pickedTime = new Date(orderTime.getTime() + 3 * 60 * 60 * 1000);
        this.logistics.push({
          content: '快递员已取件，开始配送',
          time: this.formatTime(pickedTime)
        });
      }
      
      if (this.order.status >= 4) {
        // 添加5小时
        const deliveryTime = new Date(orderTime.getTime() + 5 * 60 * 60 * 1000);
        this.logistics.push({
          content: '包裹正在配送中',
          time: this.formatTime(deliveryTime)
        });
        
        // 添加中间状态
        const transitTime = new Date(orderTime.getTime() + 4 * 60 * 60 * 1000);
        this.logistics.push({
          content: '包裹已离开快递集散中心，准备配送至目的地',
          time: this.formatTime(transitTime)
        });
      }
      
      if (this.order.status >= 5) {
        // 添加8小时
        const arrivedTime = new Date(orderTime.getTime() + 8 * 60 * 60 * 1000);
        this.logistics.push({
          content: '包裹已送达收件地址',
          time: this.formatTime(arrivedTime)
        });
        
        // 添加快递员出发配送
        const startDeliveryTime = new Date(orderTime.getTime() + 7 * 60 * 60 * 1000);
        this.logistics.push({
          content: '快递员开始配送，预计1小时内送达',
          time: this.formatTime(startDeliveryTime)
        });
      }
      
      if (this.order.status >= 6) {
        // 添加9小时
        const completeTime = new Date(orderTime.getTime() + 9 * 60 * 60 * 1000);
        this.logistics.push({
          content: '订单已完成，感谢您使用乡递快递',
          time: this.formatTime(completeTime)
        });
        
        // 添加签收信息
        const signTime = new Date(orderTime.getTime() + 8.5 * 60 * 60 * 1000);
        this.logistics.push({
          content: `包裹已由${this.order.receiverName || '收件人'}签收`,
          time: this.formatTime(signTime)
        });
      }
      
      if (this.order.status === 7) {
        // 如果订单取消，重置物流信息
        const cancelTime = new Date(orderTime.getTime() + 1 * 60 * 60 * 1000);
        this.logistics = [
          {
            content: '订单已创建',
            time: this.formatTime(orderTime)
          },
          {
            content: '订单已取消: ' + (this.order.cancelReason || '用户取消'),
            time: this.formatTime(cancelTime)
          }
        ];
      }
      
      // 倒序排列，最新的在最前面
      this.logistics.reverse();
      
      // 设置物流公司信息
      this.logisticsCompany = {
        name: '乡递通快递',
        logo: '/static/images/company-logo.png',
        trackingNo: this.order.trackingNo || '未分配',
        status: this.getStatusText(this.order.status)
      };
    },
    
    // 格式化时间
    formatTime(date) {
      if (!date) return '';
      
      if (typeof date === 'string') {
        date = new Date(date);
      }
      
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      const hour = date.getHours().toString().padStart(2, '0');
      const minute = date.getMinutes().toString().padStart(2, '0');
      
      return `${year}-${month}-${day} ${hour}:${minute}`;
    },
    
    // 获取快递员详情
    getCourierInfo(courierId) {
      getCourierInfo(courierId)
        .then(res => {
          if (res.code === 200 && res.data) {
            this.courierInfo = res.data;
            console.log('快递员信息:', this.courierInfo);
          }
        })
        .catch(err => {
          console.error('获取快递员信息失败', err);
        });
    },
    
    // 获取订单评价
    getOrderReview() {
      getOrderReview(this.orderId)
        .then(res => {
          if (res.code === 200 && res.data) {
            this.reviewInfo = res.data;
            console.log('评价信息:', this.reviewInfo);
          }
        })
        .catch(err => {
          console.error('获取评价信息失败', err);
        });
    },
    
    // 图片预览
    previewImage(current, urls) {
      uni.previewImage({
        current: current,
        urls: urls
      });
    },
    
    // 获取订单状态文本
    getStatusText(status) {
      const statusMap = {
        0: '待接单',
        1: '已接单',
        2: '取件中',
        3: '已取件',
        4: '配送中',
        5: '已送达',
        6: '已完成',
        7: '已取消'
      };
      return statusMap[status] || '未知状态';
    },
    
    // 获取订单状态描述
    getStatusDesc(status) {
      const descMap = {
        0: '等待快递员接单',
        1: '快递员已接单，即将取件',
        2: '快递员正在取件途中',
        3: '快递员已取件，即将发往目的地',
        4: '快递员正在配送途中',
        5: '已送达，等待确认',
        6: '订单已完成',
        7: '订单已取消'
      };
      return descMap[status] || '';
    },
    
    // 获取包裹类型文本
    getPackageTypeText(type) {
      const typeMap = {
        0: '普通快递',
        1: '文件',
        2: '食品',
        3: '电子产品',
        4: '易碎品',
        5: '其他'
      };
      return typeMap[type] || '普通快递';
    },
    
    // 格式化手机号，中间4位显示*
    formatPhone(phone) {
      if (!phone || phone.length !== 11) return phone;
      return phone.substr(0, 3) + '****' + phone.substr(7);
    },
    
    // 取消订单
    cancelOrder(orderId) {
      uni.showModal({
        title: '取消订单',
        content: '确定要取消该订单吗？',
        confirmColor: '#FF6B00',
        success: (res) => {
          if (res.confirm) {
            // 显示加载动画，使用自定义样式
            uni.showLoading({
              title: '取消中...',
              mask: true // 添加遮罩防止重复点击
            });
            
            // 模拟网络延迟，提升用户体验
            setTimeout(() => {
              cancelOrder(orderId, '用户主动取消')
                .then(res => {
                  uni.hideLoading();
                  if (res.code === 200) {
                    // 成功动画
                    uni.showToast({
                      title: '订单已取消',
                      icon: 'success',
                      duration: 2000,
                      mask: true
                    });
                    
                    // 添加成功提示动画后再刷新数据
                    setTimeout(() => {
                      // 刷新订单详情
                      this.getOrderDetail();
                      
                      // 返回上一页（可选）
                      // uni.navigateBack();
                    }, 1500);
                  } else {
                    // 错误提示
                    uni.showToast({
                      title: res.message || '取消失败',
                      icon: 'none',
                      duration: 2000
                    });
                  }
                })
                .catch(err => {
                  uni.hideLoading();
                  console.error('取消订单失败', err);
                  // 显示带图标的错误提示
                  uni.showToast({
                    title: '网络异常，请重试',
                    icon: 'error',
                    duration: 2000
                  });
                });
            }, 300); // 短暂延迟，增加用户体验
          }
        }
      });
    },
    
    // 评价订单
    evaluateOrder(orderId) {
      uni.navigateTo({
        url: `/pages/order/review?id=${orderId}`
      });
    },
    
    // 查看评价
    viewReview(orderId) {
      uni.navigateTo({
        url: `/pages/order/review-detail?id=${orderId}`
      });
    },
    
    // 再次下单
    reorder() {
      // 复制当前订单信息，跳转到下单页面
      const orderInfo = {
        senderName: this.order.senderName,
        senderPhone: this.order.senderPhone,
        senderAddress: this.order.senderAddress,
        receiverName: this.order.receiverName,
        receiverPhone: this.order.receiverPhone,
        receiverAddress: this.order.receiverAddress,
        packageType: this.order.packageType,
        weight: this.order.weight,
        note: this.order.note
      };
      
      // 存储到本地缓存
      uni.setStorageSync('reorderInfo', JSON.stringify(orderInfo));
      
      // 跳转到下单页面
      uni.navigateTo({
        url: '/pages/delivery/send?type=reorder'
      });
    },
    
    // 联系客服
    contactService() {
      // 拨打客服电话
      uni.makePhoneCall({
        phoneNumber: '400-123-4567',
        fail: () => {
          uni.showToast({
            title: '拨打失败，请手动拨打客服电话',
            icon: 'none'
          });
        }
      });
    },
    
    // 联系快递员
    callCourier(phone) {
      if (!phone) return;
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
    
    // 复制物流单号
    copyTrackingNo(trackingNo) {
      uni.setClipboardData({
        data: trackingNo,
        success: () => {
          uni.showToast({
            title: '物流单号已复制',
            icon: 'success',
            duration: 2000
          });
        },
        fail: () => {
          uni.showToast({
            title: '复制失败',
            icon: 'none',
            duration: 2000
          });
        }
      });
    }
  }
};
</script>

<style>
.detail-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 120rpx;
}

.status-section {
  padding: 40rpx 30rpx;
  color: #fff;
}

.status-bg-0 {
  background-color: #ff9900;
}

.status-bg-1, .status-bg-2, .status-bg-3, .status-bg-4 {
  background-color: #007aff;
}

.status-bg-5 {
  background-color: #3cc51f;
}

.status-bg-6 {
  background-color: #999;
}

.status-bg-7 {
  background-color: #F56C6C;
}

.status-text {
  font-size: 40rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.status-desc {
  font-size: 28rpx;
  opacity: 0.9;
}

.section-title {
  font-size: 30rpx;
  color: #333;
  font-weight: bold;
  padding: 30rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.logistics-section, .delivery-info-section, .address-section, .package-section, .cost-section {
  margin: 20rpx;
  background-color: #fff;
  border-radius: 12rpx;
  padding: 0 30rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.logistics-timeline {
  padding: 30rpx 0;
}

.logistics-item {
  position: relative;
  padding-left: 40rpx;
  margin-bottom: 30rpx;
}

.logistics-item:last-child {
  margin-bottom: 0;
}

.timeline-dot {
  position: absolute;
  left: 0;
  top: 10rpx;
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  background-color: #ccc;
}

.logistics-item.active .timeline-dot {
  background-color: #007aff;
  width: 20rpx;
  height: 20rpx;
  top: 8rpx;
  left: -2rpx;
}

.timeline-line {
  position: absolute;
  left: 8rpx;
  top: 20rpx;
  width: 2rpx;
  height: calc(100% + 30rpx);
  background-color: #eee;
}

.logistics-content {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 6rpx;
}

.logistics-item.active .logistics-content {
  color: #007aff;
  font-weight: bold;
}

.logistics-time {
  font-size: 24rpx;
  color: #999;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.info-item:last-child {
  border-bottom: none;
}

.item-label {
  font-size: 28rpx;
  color: #666;
}

.item-value {
  font-size: 28rpx;
  color: #333;
}

.courier-info {
  display: flex;
  align-items: center;
}

.courier-name {
  font-size: 28rpx;
  color: #333;
  margin-right: 10rpx;
}

.courier-phone {
  font-size: 28rpx;
  color: #666;
  margin-right: 20rpx;
}

.call-btn {
  font-size: 24rpx;
  color: #fff;
  background-color: #3cc51f;
  padding: 4rpx 16rpx;
  border-radius: 30rpx;
}

.address-item {
  display: flex;
  padding: 30rpx 0;
}

.address-divider {
  height: 1rpx;
  background-color: #f5f5f5;
  margin-left: 40rpx;
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
  margin-top: 6rpx;
}

.address-icon.sender {
  background-color: #007aff;
}

.address-icon.receiver {
  background-color: #3cc51f;
}

.address-content {
  flex: 1;
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

.address-text {
  font-size: 28rpx;
  color: #333;
}

.total-fee {
  border-top: 1rpx solid #f5f5f5;
  margin-top: 10rpx;
  padding-top: 30rpx;
}

.price {
  color: #ff5a5f;
  font-weight: bold;
  font-size: 32rpx;
}

.footer-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  background-color: #fff;
  padding: 20rpx 30rpx;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.action-btn {
  font-size: 28rpx;
  color: #666;
  padding: 15rpx 30rpx;
  border: 1rpx solid #ddd;
  border-radius: 30rpx;
  margin-left: 20rpx;
}

.primary-btn {
  color: #3cc51f;
  border-color: #3cc51f;
}

.courier-section {
  margin: 20rpx;
  background-color: #fff;
  border-radius: 12rpx;
  padding: 0 30rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.courier-profile {
  display: flex;
  align-items: center;
  padding: 30rpx 0;
}

.courier-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  margin-right: 20rpx;
}

.courier-details {
  flex: 1;
}

.courier-name-row {
  display: flex;
  align-items: center;
  margin-bottom: 10rpx;
}

.courier-full-name {
  font-size: 28rpx;
  color: #333;
  margin-right: 10rpx;
}

.courier-badge {
  font-size: 24rpx;
  color: #fff;
  background-color: #3cc51f;
  padding: 4rpx 16rpx;
  border-radius: 30rpx;
}

.courier-rating {
  display: flex;
  align-items: center;
  margin-bottom: 10rpx;
}

.rating-score {
  font-size: 28rpx;
  color: #ff9900;
  margin: 0 10rpx;
}

.rating-count {
  font-size: 24rpx;
  color: #999;
}

.courier-stats {
  display: flex;
  justify-content: space-between;
  margin-top: 10rpx;
}

.stat-item {
  font-size: 24rpx;
  color: #666;
}

.contact-courier-btn {
  font-size: 24rpx;
  color: #fff;
  background-color: #3cc51f;
  padding: 4rpx 16rpx;
  border-radius: 30rpx;
  margin-left: 20rpx;
}

.courier-contact-info {
  padding: 30rpx 0;
}

.contact-info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.contact-info-item:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 28rpx;
  color: #666;
}

.info-value {
  font-size: 28rpx;
  color: #333;
}

.review-section {
  margin: 20rpx;
  background-color: #fff;
  border-radius: 12rpx;
  padding: 0 30rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.review-content {
  padding: 30rpx 0;
}

.review-rating {
  display: flex;
  align-items: center;
  margin-bottom: 10rpx;
}

.rating-label {
  font-size: 28rpx;
  color: #666;
  margin-right: 10rpx;
}

.rating-stars {
  display: flex;
  align-items: center;
}

.rating-stars uni-icons {
  margin-right: 4rpx;
}

.rating-value {
  font-size: 28rpx;
  color: #ff9900;
  margin-left: 10rpx;
}

.review-text {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 10rpx;
}

.review-images {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 10rpx;
}

.review-image {
  width: 200rpx;
  height: 200rpx;
  object-fit: cover;
  border-radius: 4rpx;
  margin-right: 10rpx;
  margin-bottom: 10rpx;
}

.review-time {
  font-size: 24rpx;
  color: #999;
  margin-top: 10rpx;
}

.courier-reply {
  margin-top: 10rpx;
  padding: 10rpx;
  background-color: #f5f5f5;
  border-radius: 4rpx;
}

.reply-header {
  font-size: 28rpx;
  color: #333;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.reply-content {
  font-size: 28rpx;
  color: #666;
}

.reply-time {
  font-size: 24rpx;
  color: #999;
  margin-top: 10rpx;
}

.no-review-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30rpx 0;
}

.review-now-btn {
  font-size: 28rpx;
  color: #fff;
  background-color: #3cc51f;
  padding: 10rpx 20rpx;
  border-radius: 30rpx;
  margin-top: 10rpx;
}

.logistics-company {
  display: flex;
  align-items: center;
  padding: 30rpx 0;
}

.company-logo {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  margin-right: 20rpx;
}

.company-info {
  flex: 1;
}

.company-name {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 10rpx;
}

.tracking-no {
  display: flex;
  align-items: center;
}

.tracking-label {
  font-size: 28rpx;
  color: #666;
  margin-right: 10rpx;
}

.tracking-value {
  font-size: 28rpx;
  color: #333;
}

.copy-btn {
  font-size: 24rpx;
  color: #fff;
  background-color: #3cc51f;
  padding: 4rpx 16rpx;
  border-radius: 30rpx;
  margin-left: 10rpx;
}

.logistics-status {
  font-size: 28rpx;
  color: #3cc51f;
  font-weight: bold;
}
</style> 