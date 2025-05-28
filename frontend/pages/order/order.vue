<template>
  <view class="order-container">
    <!-- 标签页 -->
    <view class="tabs">
      <view 
        class="tab-item" 
        v-for="(item, index) in currentTabs" 
        :key="index"
        :class="{ active: currentTab === index }"
        @click="switchTab(index)"
      >
        <text>{{ item.name }}</text>
      </view>
    </view>
    
    <!-- 订单列表 -->
    <view class="order-list" v-if="orderList.length > 0">
      <view 
        class="order-item" 
        v-for="(item, index) in orderList" 
        :key="index"
        @click="navigateToDetail(item.id)"
      >
        <view class="order-header">
          <text class="order-no">订单号：{{ item.orderNo }}</text>
          <text class="order-status" :class="'status-' + item.status">{{ getStatusText(item.status) }}</text>
        </view>
        
        <view class="order-info">
          <view class="address-item">
            <view class="address-icon sender">寄</view>
            <view class="address-content">
              <view class="address-user">
                <text class="user-name">{{ item.senderName }}</text>
                <text class="user-phone">{{ formatPhone(item.senderPhone) }}</text>
              </view>
              <text class="address-text">{{ item.senderAddress }}</text>
            </view>
          </view>
          
          <view class="address-item">
            <view class="address-icon receiver">收</view>
            <view class="address-content">
              <view class="address-user">
                <text class="user-name">{{ item.receiverName }}</text>
                <text class="user-phone">{{ formatPhone(item.receiverPhone) }}</text>
              </view>
              <text class="address-text">{{ item.receiverAddress }}</text>
            </view>
          </view>
          
          <!-- 订单价格 -->
          <view class="price-info" v-if="viewMode === 'courier' && item.status === 0">
            <text class="price-label">配送费：</text>
            <text class="price-value">¥{{ item.price }}</text>
          </view>
        </view>
        
        <view class="order-footer">
          <view class="order-time">
            <text>下单时间：{{ formatDate(item.createdAt) }}</text>
          </view>
          <view class="order-actions">
            <!-- 用户操作按钮 -->
            <template v-if="viewMode === 'user'">
            <view 
              class="action-btn" 
              v-if="item.status === 0"
              @click.stop="cancelOrder(item.id)"
            >
              取消订单
            </view>
            <view 
              class="action-btn primary-btn" 
              v-if="item.status === 5"
              @click.stop="evaluateOrder(item.id)"
            >
              评价订单
            </view>
            <view 
              class="action-btn" 
              v-if="item.status === 5"
              @click.stop="navigateToDetail(item.id)"
            >
              再次下单
            </view>
            </template>
            
            <!-- 快递员操作按钮 -->
            <template v-else>
              <view 
                class="action-btn primary-btn" 
                v-if="item.status === 0"
                @click.stop="handleAcceptOrder(item.id)"
              >
                接单
          </view>
              <view 
                class="action-btn" 
                v-if="[1, 2, 3, 4].includes(item.status)"
                @click.stop="updateStatus(item.id, item.status + 1)"
              >
                {{ getNextStatusText(item.status) }}
              </view>
            </template>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 空状态 -->
    <view class="empty-state" v-else>
      <image class="empty-image" src="/static/images/empty.png" mode="aspectFit"></image>
      <text class="empty-text">{{ viewMode === 'courier' ? '暂无可接订单' : '暂无订单数据' }}</text>
      <button class="empty-btn" type="primary" @click="navigateTo('/pages/index/index')" v-if="viewMode === 'user'">去下单</button>
      <button class="empty-btn" type="primary" @click="refreshPendingOrders()" v-else>刷新订单</button>
    </view>
    
    <!-- 加载提示 -->
    <view class="loading-overlay" v-if="loading">
      <view class="loading-content">
        <view class="loading-spinner"></view>
        <text>正在加载订单数据...</text>
      </view>
    </view>
    
    <!-- 位置信息提示 -->
    <view class="location-tips" v-if="viewMode === 'courier' && showLocationTips">
      <text>需要开启位置权限才能获取附近订单</text>
      <button class="location-btn" type="primary" size="mini" @click="getLocation">开启位置</button>
    </view>
    
    <!-- 管理员视图切换悬浮按钮 -->
    <view class="admin-switch-btn" @click="switchViewMode">
      <view class="switch-icon">
        <text>{{ viewMode === 'user' ? '快' : '用' }}</text>
      </view>
      <text class="switch-text">当前{{ viewMode === 'user' ? '用户' : '快递员' }}视图</text>
    </view>
  </view>
</template>

<script>
import { isLoggedIn } from '@/api/auth';
import { getUserOrders, cancelOrder, getOrderStatusText, getCourierOrders, getPendingOrders, acceptOrder, updateOrderStatus } from '@/api/order';
import { getUserProfile } from '@/api/user';
import { getCourierByUserId } from '@/api/courier';

export default {
  data() {
    return {
      // 用户标签页
      userTabs: [
        { name: '全部' },
        { name: '待接单' },
        { name: '处理中' },
        { name: '已完成' }
      ],
      
      // 快递员标签页
      courierTabs: [
        { name: '附近订单' },
        { name: '我的订单' },
        { name: '配送中' },
        { name: '已完成' }
      ],
      
      // 当前标签页
      currentTab: 0,
      
      // 订单列表
      orderList: [],
      
      // 用户信息
      userInfo: null,
      
      // 快递员信息
      courierInfo: null,
      
      // 是否为快递员角色
      isCourier: false,
      
      // 管理员视图模式 ('user' 或 'courier')
      viewMode: 'user',
      
      // 是否为管理员
      isAdmin: false,
      
      // 分页信息
      page: 1,
      size: 10,
      total: 0,
      
      // 位置信息
      location: {
        longitude: null,
        latitude: null
      },
      
      // 显示位置提示
      showLocationTips: false,
      
      // 加载状态
      loading: false,
      userLoading: false
    };
  },
  
  computed: {
    // 根据视图模式返回对应的标签页
    currentTabs() {
      return this.viewMode === 'courier' ? this.courierTabs : this.userTabs;
    }
  },
  
  onLoad() {
    // 检查登录状态
    if (!isLoggedIn()) {
      uni.navigateTo({
        url: '/pages/login/login'
      });
      return;
    }
    
    // 获取用户信息
    this.loadUserProfile();
  },
  
  onShow() {
    // 如果已加载用户信息，则刷新订单数据
    if (this.userInfo) {
      this.refreshData();
    }
  },
  
  onPullDownRefresh() {
    // 刷新数据
    this.refreshData();
    setTimeout(() => {
      uni.stopPullDownRefresh();
    }, 1000);
  },
  
  methods: {
    // 切换视图模式（用户/快递员）
    switchViewMode() {
      this.viewMode = this.viewMode === 'user' ? 'courier' : 'user';
      this.currentTab = 0;
      this.page = 1;
      this.orderList = [];
      
      // 重新加载数据
      this.refreshData();
      
      // 显示提示
      uni.showToast({
        title: `已切换到${this.viewMode === 'user' ? '用户' : '快递员'}视图`,
        icon: 'none'
      });
    },
    
    // 刷新数据
    refreshData() {
      this.page = 1;
      if (this.viewMode === 'courier') {
        this.loadCourierData();
      } else {
        this.loadOrderData();
      }
    },
    
    // 获取用户资料
    loadUserProfile() {
      this.userLoading = true;
      
      uni.showLoading({
        title: '加载中...',
        mask: true // 添加遮罩防止重复点击
      });
      
      getUserProfile()
        .then(res => {
          if (res.code === 200 && res.data) {
            this.userInfo = res.data;
            
            // 检查是否为管理员
            this.checkIsAdmin();
            
            // 判断是否为快递员
            this.checkIsCourier();
          } else {
            throw new Error(res.message || '获取用户信息失败');
          }
        })
        .catch(err => {
          console.error('获取用户信息失败', err);
          uni.showToast({
            title: '获取用户信息失败',
            icon: 'none',
            duration: 2000
          });
          // 确保在出错时也会加载订单数据
          this.loadOrderData();
        })
        .finally(() => {
          // 确保在任何情况下都会隐藏加载提示
          uni.hideLoading();
          this.userLoading = false;
        });
    },
    
    // 检查是否为管理员
    checkIsAdmin() {
      // 默认都是管理员，方便测试
      this.isAdmin = true;
      
      // 实际项目中取消下面注释并使用正确的逻辑
      /*
      if (this.userInfo && this.userInfo.username) {
        this.isAdmin = this.userInfo.username.includes('admin') || this.userInfo.role === 'admin';
      }
      */
    },
    
    // 检查是否为快递员
    checkIsCourier() {
      if (!this.userInfo || !this.userInfo.id) return;
      
      getCourierByUserId(this.userInfo.id)
        .then(res => {
          if (res.code === 200 && res.data) {
            this.courierInfo = res.data;
            this.isCourier = true;
            console.log('当前用户是快递员:', this.courierInfo);
            
            // 将快递员信息保存到本地存储
            uni.setStorageSync('courierInfo', this.courierInfo);
            
            // 如果是快递员，默认设置视图模式为快递员
            if (!this.isAdmin) {
              this.viewMode = 'courier';
            }
            
            // 加载订单数据
            this.refreshData();
          } else {
            console.log('当前用户不是快递员');
            // 清理本地可能存在的快递员信息
            uni.removeStorageSync('courierInfo');
            
            // 如果不是快递员，默认设置视图模式为用户
            if (!this.isAdmin) {
              this.viewMode = 'user';
            }
            
            // 加载普通用户订单数据
            this.loadOrderData();
          }
        })
        .catch(err => {
          console.error('检查快递员身份失败', err);
          // 清理本地可能存在的快递员信息
          uni.removeStorageSync('courierInfo');
          // 默认加载普通用户订单数据
          this.loadOrderData();
        });
    },
    
    // 获取位置信息
    getLocation() {
      uni.getLocation({
        type: 'gcj02',
        success: (res) => {
          this.location.longitude = res.longitude;
          this.location.latitude = res.latitude;
          this.showLocationTips = false;
          
          console.log('获取位置成功:', this.location);
          
          // 重新加载待接单数据
          if (this.viewMode === 'courier' && this.currentTab === 0) {
            this.loadPendingOrders();
          }
        },
        fail: (err) => {
          console.error('获取位置失败:', err);
          this.showLocationTips = true;
          
          uni.showModal({
            title: '提示',
            content: '获取位置信息失败，请前往设置开启位置权限',
            success: (res) => {
              if (res.confirm) {
                uni.openSetting();
              }
            }
          });
        }
        });
    },
    
    // 切换标签页
    switchTab(index) {
      if (this.currentTab === index) return;
      this.currentTab = index;
      this.page = 1;
      this.orderList = [];
      
      if (this.viewMode === 'courier') {
        this.loadCourierData();
      } else {
      this.loadOrderData();
      }
    },
    
    // 加载快递员数据
    loadCourierData() {
      // 管理员在切换视图模式时可能没有courierInfo
      if (this.isAdmin && (!this.courierInfo || !this.courierInfo.id)) {
        if (this.currentTab === 0) {
          // 加载附近待接单
          this.loadPendingOrders();
        } else {
          // 模拟一个默认的快递员ID用于查看数据
          uni.showToast({
            title: '管理员正在查看快递员视图',
            icon: 'none',
            duration: 2000
          });
          
          // 获取第一个可用的快递员ID进行数据查看
          this.getDefaultCourierId().then(id => {
            if (id) {
              // 临时使用这个ID查询数据
              this.loadCourierOrdersWithId(id);
            } else {
              uni.showToast({
                title: '未找到可用快递员数据',
                icon: 'none'
              });
              this.orderList = [];
            }
          });
        }
        return;
      }
      
      // 正常快递员逻辑
      if (!this.courierInfo || !this.courierInfo.id) {
        uni.showToast({
          title: '快递员信息不完整',
          icon: 'none'
        });
        return;
      }
      
      switch (this.currentTab) {
        case 0: // 附近订单
          this.loadPendingOrders();
          break;
        case 1: // 我的订单 - 确保只看到自己接单的
          this.loadCourierOrders(1, this.courierInfo.id); // 已接单状态
          break;
        case 2: // 配送中 - 确保只看到自己接单的
          this.loadCourierProcessingOrders(this.courierInfo.id);
          break;
        case 3: // 已完成 - 确保只看到自己接单的
          this.loadCourierOrders(6, this.courierInfo.id); // 已完成状态
          break;
      }
    },
    
    // 获取默认的快递员ID（用于管理员查看）
    getDefaultCourierId() {
      // 这里可以添加API调用来获取系统中的一个快递员ID
      // 为了演示，我们先返回一个Promise
      return new Promise((resolve) => {
        // 示例：从本地存储中尝试获取之前保存的courierInfo
        const savedCourierInfo = uni.getStorageSync('adminLastViewedCourier');
        if (savedCourierInfo && savedCourierInfo.id) {
          resolve(savedCourierInfo.id);
          return;
        }
        
        // 如果本地没有，可以调用API获取系统中的一个快递员
        // 这里是一个示例，实际实现应该调用您的后端API
        setTimeout(() => {
          // 模拟API返回的快递员ID，实际应从API获取
          const mockCourierId = '1'; // 默认使用ID为1的快递员
          resolve(mockCourierId);
        }, 300);
      });
    },
    
    // 使用指定ID加载快递员订单
    loadCourierOrdersWithId(courierId) {
      if (!courierId) {
        uni.showToast({
          title: '缺少快递员ID，无法查询',
          icon: 'none'
        });
        return;
      }
      
      // 根据标签页过滤不同状态
      switch (this.currentTab) {
        case 1: // 我的订单
          this.loadCourierOrders(1, courierId);
          break;
        case 2: // 配送中
          this.loadCourierProcessingOrders(courierId);
          break;
        case 3: // 已完成
          this.loadCourierOrders(6, courierId);
          break;
        default:
          // 对于其他标签页，使用默认状态
          this.loadCourierOrders(undefined, courierId);
      }
    },
    
    // 加载待接单数据
    loadPendingOrders() {
      this.loading = true;
      
      // 检查位置信息
      if (!this.location.longitude || !this.location.latitude) {
        this.getLocation();
        return;
      }
      
      // 查询参数
      const params = {
        longitude: this.location.longitude,
        latitude: this.location.latitude,
        distance: 10, // 查询10公里内的订单
        page: this.page,
        size: this.size
      };
      
      // 调用API获取待接单列表
      getPendingOrders(params)
        .then(res => {
          console.log('获取待接单响应:', res);
          if (res.code === 200 && res.data) {
            // 合并数据
            if (this.page === 1) {
              this.orderList = res.data.records || [];
            } else {
              this.orderList = [...this.orderList, ...(res.data.records || [])];
            }
            this.total = res.data.total || 0;
          } else {
            uni.showToast({
              title: res.message || '获取待接单失败',
              icon: 'none'
            });
          }
        })
        .catch(err => {
          console.error('获取待接单失败', err);
          uni.showToast({
            title: '获取待接单失败',
            icon: 'none'
          });
        })
        .finally(() => {
          this.loading = false;
        });
    },
    
    // 加载快递员订单，现在明确要求传入courierId参数
    loadCourierOrders(status, courierId) {
      if (!courierId) {
        console.error('缺少快递员ID，无法查询订单');
        return;
      }
      
      this.loading = true;
      
      // 查询参数
      const params = {
        page: this.page,
        size: this.size
      };
      
      // 如果指定了状态，添加状态过滤
      if (status !== undefined) {
        params.status = status;
        console.log('快递员订单查询 - 状态码:', status);
      }
      
      // 调用API获取快递员订单列表
      console.log('发送快递员订单查询请求，参数:', params);
      
      // 对于已完成标签页，也加载已送达(状态5)的订单
      if (this.currentTab === 3) {
        // 先加载状态为6的订单
        getCourierOrders(courierId, params)
          .then(res => {
            console.log('获取快递员状态6订单响应:', res);
            if (res.code === 200 && res.data) {
              // 保存状态6的订单
              if (this.page === 1) {
                this.orderList = res.data.records || [];
              } else {
                this.orderList = [...this.orderList, ...(res.data.records || [])];
              }
              
              // 再加载状态为5的订单
              const params5 = {
                page: this.page,
                size: this.size,
                status: 5
              };
              
              // 获取状态5(已送达)的订单
              console.log('加载快递员状态5(已送达)的订单');
              getCourierOrders(courierId, params5)
                .then(res5 => {
                  console.log('获取快递员状态5订单响应:', res5);
                  if (res5.code === 200 && res5.data && res5.data.records) {
                    // 合并数据
                    this.orderList = [...this.orderList, ...(res5.data.records || [])];
                    
                    // 按时间排序
                    this.orderList.sort((a, b) => {
                      return new Date(b.createdAt) - new Date(a.createdAt);
                    });
                    
                    // 更新数据
                    this.total = this.orderList.length;
                  }
                })
                .catch(err => {
                  console.error('获取快递员状态5订单失败', err);
                })
                .finally(() => {
                  this.loading = false;
                });
            } else {
              this.loading = false;
              uni.showToast({
                title: res.message || '获取订单失败',
                icon: 'none'
              });
            }
          })
          .catch(err => {
            console.error('获取快递员状态6订单失败', err);
            this.loading = false;
            uni.showToast({
              title: '获取订单失败',
              icon: 'none'
            });
          });
      } else {
        // 对于其他标签页，使用常规的查询
        getCourierOrders(courierId, params)
          .then(res => {
            console.log('获取快递员订单响应:', res);
            if (res.code === 200 && res.data) {
              // 合并数据
              if (this.page === 1) {
                this.orderList = res.data.records || [];
              } else {
                this.orderList = [...this.orderList, ...(res.data.records || [])];
              }
              this.total = res.data.total || 0;
              
              // 调试信息
              if (this.orderList.length > 0) {
                console.log('快递员订单示例:', this.orderList[0]);
                console.log('快递员订单状态:', this.orderList[0].status);
              } else {
                console.log('快递员订单列表为空，没有数据返回');
              }
            } else {
              uni.showToast({
                title: res.message || '获取订单失败',
                icon: 'none'
              });
            }
          })
          .catch(err => {
            console.error('获取快递员订单失败', err);
            uni.showToast({
              title: '获取订单失败',
              icon: 'none'
            });
          })
          .finally(() => {
            this.loading = false;
          });
      }
    },
    
    // 加载快递员处理中的订单，现在明确要求传入courierId参数
    loadCourierProcessingOrders(courierId) {
      if (!courierId) {
        console.error('缺少快递员ID，无法查询订单');
        return;
      }
      
      this.loading = true;
      
      const promises = [];
      
      // 依次加载状态为2-4的订单
      for (let status = 2; status <= 4; status++) {
        const params = {
          page: this.page,
          size: this.size,
          status
        };
        
        promises.push(getCourierOrders(courierId, params));
      }
      
      // 合并结果
      Promise.all(promises)
        .then(results => {
          let processingOrders = [];
          
          results.forEach(res => {
            if (res.code === 200 && res.data && res.data.records) {
              processingOrders = [...processingOrders, ...res.data.records];
            }
          });
          
          // 按创建时间排序
          processingOrders.sort((a, b) => {
            return new Date(b.createdAt) - new Date(a.createdAt);
          });
          
          // 更新数据
          if (this.page === 1) {
            this.orderList = processingOrders;
          } else {
            this.orderList = [...this.orderList, ...processingOrders];
          }
        })
        .catch(err => {
          console.error('获取处理中订单失败', err);
          uni.showToast({
            title: '获取订单失败',
            icon: 'none'
          });
        })
        .finally(() => {
          this.loading = false;
        });
    },
    
    // 加载用户订单数据
    loadOrderData() {
      if (!this.userInfo || !this.userInfo.id) {
        uni.showToast({
          title: '请先登录',
          icon: 'none'
        });
        return;
      }
      
      this.loading = true;
      
      // 查询参数
      const params = {
        page: this.page,
        size: this.size
      };
      
      // 根据标签页设置状态参数
      switch (this.currentTab) {
        case 1: // 待接单
          params.status = 0;
          break;
        case 2: // 处理中
          // 前端多次调用API，合并结果
          this.loadProcessingOrders();
          return;
        case 3: // 已完成
          params.status = 6;
          console.log('加载已完成订单，状态码:', params.status);
          break;
      }
      
      // 调用API获取订单列表
      console.log('发送订单查询请求，参数:', params);
      
      // 对于已完成标签页，也加载已送达(状态5)的订单
      if (this.currentTab === 3) {
        // 先加载状态为6的订单
        getUserOrders(this.userInfo.id, params)
          .then(res => {
            console.log('获取状态6订单响应:', res);
            if (res.code === 200 && res.data) {
              // 保存状态6的订单
              if (this.page === 1) {
                this.orderList = res.data.records || [];
              } else {
                this.orderList = [...this.orderList, ...(res.data.records || [])];
              }
              
              // 再加载状态为5的订单
              const params5 = {
                page: this.page,
                size: this.size,
                status: 5
              };
              
              // 获取状态5(已送达)的订单
              console.log('加载状态5(已送达)的订单');
              getUserOrders(this.userInfo.id, params5)
                .then(res5 => {
                  console.log('获取状态5订单响应:', res5);
                  if (res5.code === 200 && res5.data && res5.data.records) {
                    // 合并数据
                    this.orderList = [...this.orderList, ...(res5.data.records || [])];
                    
                    // 按时间排序
                    this.orderList.sort((a, b) => {
                      return new Date(b.createdAt) - new Date(a.createdAt);
                    });
                    
                    // 更新数据
                    this.total = this.orderList.length;
                  }
                })
                .catch(err => {
                  console.error('获取状态5订单失败', err);
                })
                .finally(() => {
                  this.loading = false;
                });
            } else {
              this.loading = false;
              uni.showToast({
                title: res.message || '获取订单失败',
                icon: 'none'
              });
            }
          })
          .catch(err => {
            console.error('获取状态6订单失败', err);
            this.loading = false;
            uni.showToast({
              title: '获取订单失败',
              icon: 'none'
            });
          });
      } else {
        // 对于其他标签页，使用常规的查询
      getUserOrders(this.userInfo.id, params)
        .then(res => {
          console.log('获取订单响应:', res);
          if (res.code === 200 && res.data) {
            // 合并数据
            if (this.page === 1) {
              this.orderList = res.data.records || [];
            } else {
              this.orderList = [...this.orderList, ...(res.data.records || [])];
            }
            this.total = res.data.total || 0;
            
            // 打印订单列表的第一个元素，检查字段
            if (this.orderList.length > 0) {
              console.log('订单示例:', this.orderList[0]);
              console.log('订单状态字段:', this.orderList[0].status);
              } else {
                console.log('订单列表为空，没有数据返回');
            }
          } else {
            uni.showToast({
              title: res.message || '获取订单失败',
              icon: 'none'
            });
          }
        })
        .catch(err => {
          console.error('获取订单失败', err);
          uni.showToast({
            title: '获取订单失败',
            icon: 'none'
          });
        })
        .finally(() => {
          this.loading = false;
        });
      }
    },
    
    // 加载处理中的订单（包含多种状态）
    loadProcessingOrders() {
      if (!this.userInfo || !this.userInfo.id) return;
      
      const promises = [];
      
      // 依次加载状态为1-4的订单
      for (let status = 1; status <= 4; status++) {
        const params = {
          page: this.page,
          size: this.size,
          status
        };
        
        promises.push(getUserOrders(this.userInfo.id, params));
      }
      
      // 合并结果
      Promise.all(promises)
        .then(results => {
          let processingOrders = [];
          
          results.forEach(res => {
            if (res.code === 200 && res.data && res.data.records) {
              processingOrders = [...processingOrders, ...res.data.records];
            }
          });
          
          // 按创建时间排序
          processingOrders.sort((a, b) => {
            return new Date(b.createdAt) - new Date(a.createdAt);
          });
          
          // 更新数据
          if (this.page === 1) {
            this.orderList = processingOrders;
          } else {
            this.orderList = [...this.orderList, ...processingOrders];
          }
        })
        .catch(err => {
          console.error('获取处理中订单失败', err);
          uni.showToast({
            title: '获取订单失败',
            icon: 'none'
          });
        })
        .finally(() => {
          this.loading = false;
        });
    },
    
    // 处理接单操作
    handleAcceptOrder(orderId) {
      if (!this.courierInfo || !this.courierInfo.id) {
        uni.showToast({
          title: '快递员信息不完整',
          icon: 'none'
        });
        return;
      }
      
      uni.showModal({
        title: '接单确认',
        content: '确定要接此订单吗？',
        success: (res) => {
          if (res.confirm) {
            uni.showLoading({
              title: '处理中...',
              mask: true
            });
            
            acceptOrder(orderId, this.courierInfo.id)
              .then(res => {
                uni.hideLoading();
                if (res.code === 200) {
                  uni.showToast({
                    title: '接单成功',
                    icon: 'success'
                  });
                  
                  // 刷新数据
                  setTimeout(() => {
                    this.refreshData();
                  }, 1000);
                } else {
                  uni.showToast({
                    title: res.message || '接单失败',
                    icon: 'none'
                  });
                }
              })
              .catch(err => {
                uni.hideLoading();
                console.error('接单失败', err);
                uni.showToast({
                  title: '接单失败，请重试',
                  icon: 'none'
                });
              });
          }
        }
      });
    },
    
    // 更新订单状态
    updateStatus(orderId, newStatus) {
      if (newStatus < 1 || newStatus > 6) {
        uni.showToast({
          title: '状态更新无效',
          icon: 'none'
        });
        return;
      }
      
      const statusText = this.getNextStatusText(newStatus - 1);
      
      uni.showModal({
        title: '状态更新',
        content: `确定将订单状态更新为"${statusText}"吗？`,
        success: (res) => {
          if (res.confirm) {
            uni.showLoading({
              title: '更新中...',
              mask: true
            });
            
            updateOrderStatus(orderId, newStatus)
              .then(res => {
                uni.hideLoading();
                if (res.code === 200) {
                  uni.showToast({
                    title: '状态更新成功',
                    icon: 'success'
                  });
                  
                  // 刷新数据
                  setTimeout(() => {
                    this.refreshData();
                  }, 1000);
                } else {
                  uni.showToast({
                    title: res.message || '状态更新失败',
                    icon: 'none'
                  });
                }
              })
              .catch(err => {
                uni.hideLoading();
                console.error('状态更新失败', err);
                uni.showToast({
                  title: '状态更新失败，请重试',
                  icon: 'none'
                });
              });
          }
        }
      });
    },
    
    // 获取下一个状态文本
    getNextStatusText(currentStatus) {
      const nextStatusMap = {
        1: '开始取件',   // 已接单 -> 取件中
        2: '已取到件',   // 取件中 -> 已取件
        3: '开始配送',   // 已取件 -> 配送中
        4: '已送达'      // 配送中 -> 已送达
      };
      return nextStatusMap[currentStatus] || '更新状态';
    },
    
    // 刷新待接单
    refreshPendingOrders() {
      if (this.viewMode === 'courier') {
        this.page = 1;
        this.loadPendingOrders();
      }
    },
    
    // 获取状态文本
    getStatusText(status) {
      console.log('订单状态码:', status, '状态文本:', getOrderStatusText(status));
      return getOrderStatusText(status);
    },
    
    // 格式化手机号，中间4位显示*
    formatPhone(phone) {
      if (!phone) return '';
      return phone.substring(0, 3) + '****' + phone.substring(7);
    },
    
    // 格式化日期
    formatDate(dateStr) {
      return dateStr;
    },
    
    // 跳转到详情页
    navigateToDetail(id) {
      // 根据当前视图模式跳转不同的详情页
      if (this.viewMode === 'courier') {
        uni.navigateTo({
          url: `/pages/order/courier-detail?id=${id}`
        });
      } else {
      uni.navigateTo({
        url: `/pages/order/detail?id=${id}`
      });
      }
    },
    
    // 取消订单
    cancelOrder(id) {
      uni.showModal({
        title: '取消订单',
        content: '确定要取消该订单吗？取消后不可恢复',
        confirmColor: '#FF6B00',
        cancelColor: '#999999',
        success: (res) => {
          if (res.confirm) {
            // 显示加载动画，使用遮罩
            uni.showLoading({
              title: '处理中...',
              mask: true
            });
            
            // 添加短暂延迟，增强用户体验
            setTimeout(() => {
              // 调用取消订单API
              cancelOrder(id, '用户主动取消')
                .then(res => {
                  uni.hideLoading();
                  if (res.code === 200) {
                    // 使用自定义动画效果
                    const animation = uni.createAnimation({
                      duration: 300,
                      timingFunction: 'ease',
                    });
                    
                    // 成功提示
                    uni.showToast({
                      title: '订单已取消',
                      icon: 'success',
                      duration: 1500,
                      mask: true
                    });
                    
                    // 动画完成后刷新数据
                    setTimeout(() => {
                      // 重新加载数据
                      this.loadOrderData();
                    }, 1000);
                    
                  } else {
                    // 错误提示，使用更友好的信息
                    uni.showToast({
                      title: res.message || '操作失败，请重试',
                      icon: 'none',
                      duration: 2000
                    });
                  }
                })
                .catch(err => {
                  uni.hideLoading();
                  console.error('取消订单失败', err);
                  // 网络错误提示
                  uni.showToast({
                    title: '网络异常，请检查连接',
                    icon: 'error',
                    duration: 2000
                  });
                });
            }, 300);
          }
        }
      });
    },
    
    // 评价订单
    evaluateOrder(id) {
      uni.navigateTo({
        url: `/pages/order/evaluate?id=${id}`
      });
    },
    
    // 导航到指定页面
    navigateTo(url) {
      uni.switchTab({
        url
      });
    }
  }
};
</script>

<style>
/* 列表项淡入动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 旋转动画 */
@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.order-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 40rpx;
}

.tabs {
  display: flex;
  background-color: #fff;
  padding: 20rpx 0;
  margin-bottom: 20rpx;
}

.tab-item {
  flex: 1;
  text-align: center;
  font-size: 30rpx;
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
  bottom: -10rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 40rpx;
  height: 4rpx;
  background-color: #3cc51f;
  border-radius: 2rpx;
}

.order-list {
  padding: 0 20rpx;
}

.order-item {
  background-color: #fff;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  animation: fadeIn 0.3s ease-out;
  transition: all 0.3s ease;
}

.order-item:active {
  transform: scale(0.98);
  background-color: #f9f9f9;
}

/* 订单头部样式 */
.order-header {
  display: flex;
  justify-content: space-between;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #f5f5f5;
}

.order-no {
  font-size: 28rpx;
  color: #666;
}

.order-status {
  font-size: 28rpx;
  font-weight: bold;
}

/* 订单内容样式 */
.order-info {
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.address-item {
  display: flex;
  margin-bottom: 20rpx;
}

.address-item:last-child {
  margin-bottom: 0;
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

/* 价格信息样式 */
.price-info {
  margin-top: 10rpx;
  padding: 10rpx 0;
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.price-label {
  font-size: 28rpx;
  color: #666;
}

.price-value {
  font-size: 32rpx;
  color: #ff6b00;
  font-weight: bold;
}

/* 订单底部样式 */
.order-footer {
  padding-top: 20rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-time {
  font-size: 26rpx;
  color: #999;
}

.order-actions {
  display: flex;
}

.action-btn {
  font-size: 26rpx;
  color: #666;
  padding: 10rpx 20rpx;
  border: 1rpx solid #ddd;
  border-radius: 30rpx;
  margin-left: 20rpx;
  transition: all 0.2s ease;
}

.primary-btn {
  color: #3cc51f;
  border-color: #3cc51f;
}

.action-btn:active {
  transform: scale(0.95);
  opacity: 0.8;
}

/* 空状态样式 */
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
  margin-bottom: 40rpx;
}

.empty-btn {
  width: 300rpx;
  height: 80rpx;
  line-height: 80rpx;
  border-radius: 40rpx;
  font-size: 30rpx;
  background-color: #3cc51f;
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

/* 位置提示样式 */
.location-tips {
  position: fixed;
  bottom: 40rpx;
  left: 50%;
  transform: translateX(-50%);
  background-color: rgba(0, 0, 0, 0.6);
  color: #fff;
  padding: 20rpx 30rpx;
  border-radius: 50rpx;
  display: flex;
  align-items: center;
  z-index: 100;
}

.location-btn {
  margin-left: 20rpx;
  font-size: 24rpx;
  height: 60rpx;
  line-height: 60rpx;
  margin-top: 0;
  margin-bottom: 0;
}

/* 订单状态颜色 */
.status-0 {
  color: #ff9900;
}
.status-1 {
  color: #3cc51f;
}
.status-2, .status-3, .status-4 {
  color: #007aff;
}
.status-5 {
  color: #ff6b00;
}
.status-6 {
  color: #8f8f94;
}
.status-7 {
  color: #dd524d;
}

/* 管理员视图切换悬浮按钮样式 */
.admin-switch-btn {
  position: fixed;
  bottom: 120rpx;
  right: 40rpx;
  background-color: #3cc51f;
  border-radius: 100rpx;
  padding: 20rpx 30rpx;
  display: flex;
  align-items: center;
  z-index: 9999;
  box-shadow: 0 6rpx 20rpx rgba(60, 197, 31, 0.3);
  transition: all 0.3s ease;
  animation: fadeIn 0.5s ease-out;
}

.admin-switch-btn:active {
  transform: scale(0.95);
  box-shadow: 0 3rpx 10rpx rgba(60, 197, 31, 0.2);
}

.switch-icon {
  width: 60rpx;
  height: 60rpx;
  background-color: rgba(255, 255, 255, 0.2);
  border-radius: 30rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 20rpx;
}

.switch-icon text {
  font-size: 36rpx;
  color: #fff;
  font-weight: bold;
}

.switch-text {
  font-size: 28rpx;
  color: #fff;
  font-weight: bold;
}
</style> 