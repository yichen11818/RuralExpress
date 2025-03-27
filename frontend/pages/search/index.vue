<template>
  <view class="search-container">
    <!-- 搜索头部 -->
    <view class="search-header">
      <view class="search-box">
        <uni-icons type="search" size="18" color="#666"></uni-icons>
        <input 
          type="text" 
          v-model="searchQuery" 
          placeholder="搜索包裹、订单、快递员、服务区域等" 
          class="search-input" 
          confirm-type="search" 
          @confirm="handleSearch"
          focus 
        />
        <text class="cancel-btn" @click="handleCancel">取消</text>
      </view>
    </view>
    
    <!-- 搜索前的历史记录和热门 -->
    <view class="search-content" v-if="!hasSearched">
      <!-- 搜索历史 -->
      <view class="search-history" v-if="searchHistory.length > 0">
        <view class="section-header">
          <text class="section-title">搜索历史</text>
          <uni-icons type="trash" size="20" color="#999" @click="clearHistory"></uni-icons>
        </view>
        <view class="history-list">
          <view 
            class="history-item" 
            v-for="(item, index) in searchHistory" 
            :key="index"
            @click="useHistoryItem(item)"
          >
            <uni-icons type="search" size="16" color="#999"></uni-icons>
            <text class="history-text">{{ item }}</text>
          </view>
        </view>
      </view>
      
      <!-- 热门搜索 -->
      <view class="hot-search">
        <view class="section-header">
          <text class="section-title">热门搜索</text>
        </view>
        <view class="tag-list">
          <view 
            class="tag-item" 
            v-for="(item, index) in hotSearches" 
            :key="index"
            @click="useHistoryItem(item)"
          >
            {{ item }}
          </view>
        </view>
      </view>
    </view>
    
    <!-- 搜索后的结果显示 -->
    <view class="search-result" v-if="hasSearched">
      <!-- 分类Tab -->
      <view class="result-tabs">
        <view 
          class="tab-item" 
          v-for="(tab, index) in tabs" 
          :key="index"
          :class="{ active: activeTab === index }"
          @click="switchTab(index)"
        >
          {{ tab.name }}
          <text class="tab-count" v-if="tab.count > 0">({{ tab.count }})</text>
        </view>
      </view>
      
      <!-- 包裹结果 -->
      <view class="result-list package-list" v-if="activeTab === 0">
        <view class="empty-result" v-if="packageResults.length === 0">
          <image src="/static/images/empty-search.png" mode="aspectFit" class="empty-image"></image>
          <text class="empty-text">未找到相关包裹</text>
        </view>
        
        <view class="package-item" v-for="(item, index) in packageResults" :key="index" @click="navigateToDetail('package', item.id)">
          <view class="package-header">
            <view class="company-info">
              <image :src="item.logo || '/static/images/package.png'" mode="aspectFit" class="company-logo"></image>
              <text class="company-name">{{ item.company }}</text>
            </view>
            <text class="package-status" :class="getStatusClass(item.status)">{{ item.statusText }}</text>
          </view>
          <view class="package-content">
            <view class="package-info">
              <text class="tracking-no">{{ item.trackingNo }}</text>
              <text class="package-time">{{ item.updateTime }}</text>
            </view>
            <uni-icons type="right" size="16" color="#ccc"></uni-icons>
          </view>
        </view>
      </view>
      
      <!-- 订单结果 -->
      <view class="result-list order-list" v-if="activeTab === 1">
        <view class="empty-result" v-if="orderResults.length === 0">
          <image src="/static/images/empty-search.png" mode="aspectFit" class="empty-image"></image>
          <text class="empty-text">未找到相关订单</text>
        </view>
        
        <view class="order-item" v-for="(item, index) in orderResults" :key="index" @click="navigateToDetail('order', item.id)">
          <view class="order-header">
            <text class="order-no">订单号：{{ item.orderNo }}</text>
            <text class="order-status" :class="getStatusClass(item.status)">{{ item.statusText }}</text>
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
            
            <view class="order-detail-info">
              <view class="detail-item">
                <text class="detail-label">下单时间</text>
                <text class="detail-value">{{ item.createTime }}</text>
              </view>
              <view class="detail-item">
                <text class="detail-label">包裹类型</text>
                <text class="detail-value">{{ item.packageType }}</text>
              </view>
              <view class="detail-item">
                <text class="detail-label">重量</text>
                <text class="detail-value">{{ item.weight }}kg</text>
              </view>
              <view class="detail-item">
                <text class="detail-label">配送费</text>
                <text class="detail-value price">¥{{ item.price }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 快递员结果 -->
      <view class="result-list courier-list" v-if="activeTab === 2">
        <view class="empty-result" v-if="courierResults.length === 0">
          <image src="/static/images/empty-search.png" mode="aspectFit" class="empty-image"></image>
          <text class="empty-text">未找到相关快递员</text>
        </view>
        
        <view class="courier-item" v-for="(item, index) in courierResults" :key="index" @click="navigateToDetail('courier', item.id)">
          <view class="courier-avatar">
            <image :src="item.avatar || '/static/images/avatar-default.png'" mode="aspectFill" class="avatar-image"></image>
          </view>
          <view class="courier-info">
            <view class="courier-name-row">
              <text class="courier-name">{{ item.name }}</text>
              <view class="courier-rating">
                <uni-icons type="star-filled" size="14" color="#ff9900"></uni-icons>
                <text class="rating-value">{{ item.rating }}</text>
              </view>
            </view>
            <view class="courier-detail">
              <text class="courier-company">{{ item.company }}</text>
              <text class="courier-area">{{ item.serviceArea }}</text>
            </view>
            <view class="courier-stats">
              <text class="stat-item">配送{{ item.deliveryCount || 0 }}单</text>
              <text class="stat-divider">|</text>
              <text class="contact-btn" @click.stop="contactCourier(item.phone)">联系快递员</text>
            </view>
          </view>
          <uni-icons type="right" size="16" color="#ccc"></uni-icons>
        </view>
      </view>
      
      <!-- 服务点结果 -->
      <view class="result-list station-list" v-if="activeTab === 3">
        <view class="empty-result" v-if="stationResults.length === 0">
          <image src="/static/images/empty-search.png" mode="aspectFit" class="empty-image"></image>
          <text class="empty-text">未找到相关服务点</text>
        </view>
        
        <view class="station-item" v-for="(item, index) in stationResults" :key="index" @click="navigateToDetail('station', item.id)">
          <view class="station-info">
            <text class="station-name">{{ item.name }}</text>
            <view class="station-address">
              <uni-icons type="location" size="14" color="#666"></uni-icons>
              <text class="address-text">{{ item.address }}</text>
              <text class="distance" v-if="item.distance">{{ item.distance }}km</text>
            </view>
            <view class="station-business">
              <uni-icons type="calendar" size="14" color="#666"></uni-icons>
              <text class="business-text">{{ item.businessHours || '全天候营业' }}</text>
            </view>
          </view>
          <view class="station-actions">
            <view class="action-btn phone-btn" @click.stop="callPhone(item.phone)">
              <uni-icons type="phone" size="16" color="#3cc51f"></uni-icons>
            </view>
            <view class="action-btn navi-btn" @click.stop="openNavigation(item.address)">
              <uni-icons type="map" size="16" color="#2979ff"></uni-icons>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 加载更多 -->
      <view class="loading-more" v-if="hasSearched && getActiveResults().length > 0">
        <text v-if="loading" class="loading-text">加载中...</text>
        <text v-else-if="hasMore" class="more-text" @click="loadMore">点击加载更多</text>
        <text v-else class="no-more-text">已经到底了</text>
      </view>
    </view>
  </view>
</template>

<script>
import uniIcons from '@/uni_modules/uni-icons/components/uni-icons/uni-icons.vue'
import { searchPackages, searchOrders, searchCouriers, searchStations } from '@/api/search'

export default {
  components: {
    uniIcons
  },
  data() {
    return {
      searchQuery: '',
      hasSearched: false,
      activeTab: 0,
      page: 1,
      pageSize: 10,
      loading: false,
      refreshing: false,
      hasMore: false,
      searchHistory: [],
      hotSearches: ['顺丰快递', '申通快递', '南昌市青山湖区', '张师傅', '取件点', '艾溪湖'],
      tabs: [
        { name: '包裹', count: 0 },
        { name: '订单', count: 0 },
        { name: '快递员', count: 0 },
        { name: '服务点', count: 0 }
      ],
      packageResults: [],
      orderResults: [],
      courierResults: [],
      stationResults: []
    };
  },
  
  onLoad(options) {
    // 获取搜索历史
    this.loadSearchHistory();
    
    // 如果有搜索参数，直接执行搜索
    if (options && options.keyword) {
      this.searchQuery = options.keyword;
      this.handleSearch();
    }
    
    // 如果有指定类型，切换到相应标签
    if (options && options.type) {
      const typeMap = {
        'package': 0,
        'order': 1,
        'courier': 2,
        'station': 3
      };
      if (typeMap[options.type] !== undefined) {
        this.activeTab = typeMap[options.type];
      }
    }
  },
  
  // 上拉加载更多
  onReachBottom() {
    if (this.hasMore && !this.loading) {
      this.loadMore();
    }
  },
  
  // 下拉刷新
  onPullDownRefresh() {
    if (this.hasSearched) {
      this.refreshResults();
    } else {
      uni.stopPullDownRefresh();
    }
  },
  
  methods: {
    // 加载搜索历史
    loadSearchHistory() {
      const history = uni.getStorageSync('searchHistory');
      if (history) {
        try {
          this.searchHistory = JSON.parse(history);
        } catch (e) {
          this.searchHistory = [];
        }
      }
    },
    
    // 保存搜索历史
    saveSearchHistory(query) {
      if (!query) return;
      
      // 如果已存在相同的搜索词，先删除
      const index = this.searchHistory.indexOf(query);
      if (index !== -1) {
        this.searchHistory.splice(index, 1);
      }
      
      // 将新搜索词添加到最前面
      this.searchHistory.unshift(query);
      
      // 只保留最近10条记录
      if (this.searchHistory.length > 10) {
        this.searchHistory = this.searchHistory.slice(0, 10);
      }
      
      // 保存到本地存储
      uni.setStorageSync('searchHistory', JSON.stringify(this.searchHistory));
    },
    
    // 清空搜索历史
    clearHistory() {
      uni.showModal({
        title: '提示',
        content: '确定要清空搜索历史吗？',
        success: (res) => {
          if (res.confirm) {
            this.searchHistory = [];
            uni.removeStorageSync('searchHistory');
          }
        }
      });
    },
    
    // 使用历史记录或热门搜索项
    useHistoryItem(item) {
      this.searchQuery = item;
      this.handleSearch();
    },
    
    // 处理搜索
    handleSearch() {
      if (!this.searchQuery.trim()) {
        return uni.showToast({
          title: '请输入搜索内容',
          icon: 'none'
        });
      }
      
      // 保存搜索历史
      this.saveSearchHistory(this.searchQuery.trim());
      
      // 重置页码和结果
      this.page = 1;
      this.hasSearched = true;
      
      // 执行搜索
      this.fetchSearchResults();
    },
    
    // 取消搜索
    handleCancel() {
      if (this.hasSearched) {
        this.hasSearched = false;
        this.searchQuery = '';
        this.resetResults();
      } else {
        uni.navigateBack();
      }
    },
    
    // 重置搜索结果
    resetResults() {
      this.packageResults = [];
      this.orderResults = [];
      this.courierResults = [];
      this.stationResults = [];
      this.tabs.forEach(tab => tab.count = 0);
    },
    
    // 切换标签
    switchTab(index) {
      if (this.activeTab === index) return;
      this.activeTab = index;
      
      // 如果该标签下还没有数据，则加载数据
      if (this.getActiveResults().length === 0) {
        this.page = 1;
        this.fetchSearchResults();
      }
    },
    
    // 获取当前标签的结果数组引用
    getActiveResults() {
      switch (this.activeTab) {
        case 0:
          return this.packageResults;
        case 1:
          return this.orderResults;
        case 2:
          return this.courierResults;
        case 3:
          return this.stationResults;
        default:
          return [];
      }
    },
    
    // 获取状态样式类名
    getStatusClass(status) {
      const statusMap = {
        0: 'status-waiting',
        1: 'status-collected',
        2: 'status-transporting',
        3: 'status-arrived',
        4: 'status-delivering',
        5: 'status-signed',
        6: 'status-cancelled'
      };
      return statusMap[status] || '';
    },
    
    // 刷新搜索结果
    refreshResults() {
      this.refreshing = true;
      this.page = 1;
      this.fetchSearchResults();
    },
    
    // 获取搜索结果
    fetchSearchResults() {
      if (this.loading) return;
      
      this.loading = true;
      if (!this.refreshing) {
        uni.showLoading({
          title: '搜索中...'
        });
      }
      
      let searchPromise;
      
      // 根据当前标签选择调用对应的搜索API
      switch (this.activeTab) {
        case 0: // 包裹
          searchPromise = searchPackages(this.searchQuery, this.page, this.pageSize);
          break;
        case 1: // 订单
          searchPromise = searchOrders(this.searchQuery, this.page, this.pageSize);
          break;
        case 2: // 快递员
          searchPromise = searchCouriers(this.searchQuery, this.page, this.pageSize);
          break;
        case 3: // 服务点
          searchPromise = searchStations(this.searchQuery, this.page, this.pageSize);
          break;
        default:
          searchPromise = Promise.resolve({ data: [], total: 0, totalPage: 0 });
      }
      
      searchPromise
        .then(res => {
          // 处理返回的数据
          if (res.success) {
            const results = res.data.list || [];
            const total = res.data.total || 0;
            const totalPage = res.data.totalPage || 1;
            
            // 更新对应标签的结果数据
            this.updateSearchResults(results, total);
            
            // 判断是否还有更多数据
            this.hasMore = this.page < totalPage;
          } else {
            uni.showToast({
              title: res.message || '搜索失败',
              icon: 'none'
            });
          }
        })
        .catch(err => {
          console.error('搜索失败', err);
          uni.showToast({
            title: '搜索出错，请稍后重试',
            icon: 'none'
          });
          
          // 如果是服务端错误，使用模拟数据进行展示
          if (this.page === 1) {
            this.useSimulatedData();
          }
        })
        .finally(() => {
          this.loading = false;
          this.refreshing = false;
          uni.hideLoading();
          uni.stopPullDownRefresh();
        });
    },
    
    // 更新搜索结果
    updateSearchResults(results, total) {
      // 根据当前标签更新相应的结果数组
      switch (this.activeTab) {
        case 0: // 包裹
          if (this.page === 1) {
            this.packageResults = results;
          } else {
            this.packageResults = [...this.packageResults, ...results];
          }
          this.tabs[0].count = total;
          break;
        case 1: // 订单
          if (this.page === 1) {
            this.orderResults = results;
          } else {
            this.orderResults = [...this.orderResults, ...results];
          }
          this.tabs[1].count = total;
          break;
        case 2: // 快递员
          if (this.page === 1) {
            this.courierResults = results;
          } else {
            this.courierResults = [...this.courierResults, ...results];
          }
          this.tabs[2].count = total;
          break;
        case 3: // 服务点
          if (this.page === 1) {
            this.stationResults = results;
          } else {
            this.stationResults = [...this.stationResults, ...results];
          }
          this.tabs[3].count = total;
          break;
      }
    },
    
    // 加载更多结果
    loadMore() {
      if (!this.hasMore || this.loading) return;
      this.page++;
      this.fetchSearchResults();
    },
    
    // 跳转到详情页
    navigateToDetail(type, id) {
      const urlMap = {
        package: `/pages/order/track?trackingNo=`,
        order: `/pages/order/detail?id=`,
        courier: `/pages/courier/detail?id=`,
        station: `/pages/station/detail?id=`
      };
      
      const baseUrl = urlMap[type] || '';
      if (baseUrl) {
        uni.navigateTo({
          url: `${baseUrl}${id}`
        });
      }
    },
    
    // 使用模拟数据（仅在API调用失败时使用）
    useSimulatedData() {
      // 根据当前标签生成不同的模拟数据
      switch (this.activeTab) {
        case 0: // 包裹
          this.packageResults = this.generatePackageResults();
          this.tabs[0].count = this.packageResults.length;
          break;
        case 1: // 订单
          this.orderResults = this.generateOrderResults();
          this.tabs[1].count = this.orderResults.length;
          break;
        case 2: // 快递员
          this.courierResults = this.generateCourierResults();
          this.tabs[2].count = this.courierResults.length;
          break;
        case 3: // 服务点
          this.stationResults = this.generateStationResults();
          this.tabs[3].count = this.stationResults.length;
          break;
      }
      
      // 模拟可加载更多
      this.hasMore = true;
    },
    
    // 以下为生成模拟数据的方法，API正常时不会使用
    generatePackageResults() {
      const companies = ['顺丰速运', '中通快递', '圆通速递', '韵达快递', '申通快递', '京东物流'];
      const statusList = [
        { status: 2, text: '运输中' },
        { status: 3, text: '已到达' },
        { status: 4, text: '派送中' },
        { status: 5, text: '已签收' }
      ];
      
      const count = Math.floor(Math.random() * 5) + 2;
      const results = [];
      
      for (let i = 0; i < count; i++) {
        const companyIndex = Math.floor(Math.random() * companies.length);
        const statusIndex = Math.floor(Math.random() * statusList.length);
        
        results.push({
          id: `PKG${Math.floor(Math.random() * 1000000)}`,
          company: companies[companyIndex],
          trackingNo: `${companies[companyIndex].substring(0, 1)}${Math.floor(Math.random() * 10000000000)}`,
          status: statusList[statusIndex].status,
          statusText: statusList[statusIndex].text,
          updateTime: this.generateRandomTime()
        });
      }
      
      return results;
    },
    
    generateOrderResults() {
      const statusList = [
        { status: 0, text: '待接单' },
        { status: 1, text: '已接单' },
        { status: 2, text: '取件中' },
        { status: 3, text: '已取件' },
        { status: 4, text: '配送中' },
        { status: 5, text: '已完成' },
        { status: 6, text: '已取消' }
      ];
      
      const count = Math.floor(Math.random() * 5) + 2;
      const results = [];
      
      for (let i = 0; i < count; i++) {
        const statusIndex = Math.floor(Math.random() * statusList.length);
        
        results.push({
          id: Math.floor(Math.random() * 10000),
          orderNo: `OD${Math.floor(Math.random() * 1000000)}`,
          status: statusList[statusIndex].status,
          statusText: statusList[statusIndex].text,
          createTime: this.generateRandomTime(30),
          senderName: '张三',
          senderPhone: '13800138000',
          senderAddress: '江西省南昌市青山湖区艾溪湖北路77号',
          receiverName: '李四',
          receiverPhone: '13900139000',
          receiverAddress: '江西省南昌市青云谱区解放西路88号',
          packageType: '普通快递',
          weight: (Math.random() * 10).toFixed(1),
          price: (Math.random() * 50 + 10).toFixed(2)
        });
      }
      
      return results;
    },
    
    generateCourierResults() {
      const areas = ['青山湖区', '东湖区', '西湖区', '青云谱区', '湾里区', '新建区'];
      const count = Math.floor(Math.random() * 5) + 2;
      const results = [];
      
      for (let i = 0; i < count; i++) {
        const areaIndex = Math.floor(Math.random() * areas.length);
        
        results.push({
          id: Math.floor(Math.random() * 10000),
          name: `张${Math.floor(Math.random() * 100)}`,
          avatar: '/static/images/avatar-default.png',
          company: '农村快递',
          serviceArea: `南昌市${areas[areaIndex]}`,
          rating: (Math.random() * 2 + 3).toFixed(1),
          deliveryCount: Math.floor(Math.random() * 1000),
          phone: `138${Math.floor(Math.random() * 100000000).toString().padStart(8, '0')}`
        });
      }
      
      return results;
    },
    
    generateStationResults() {
      const areas = ['青山湖区', '东湖区', '西湖区', '青云谱区', '湾里区', '新建区'];
      const count = Math.floor(Math.random() * 5) + 2;
      const results = [];
      
      for (let i = 0; i < count; i++) {
        const areaIndex = Math.floor(Math.random() * areas.length);
        
        results.push({
          id: Math.floor(Math.random() * 10000),
          name: `农村快递${areas[areaIndex]}${Math.floor(Math.random() * 10)}号站点`,
          address: `南昌市${areas[areaIndex]}艾溪湖北路${Math.floor(Math.random() * 100)}号`,
          businessHours: '08:00-20:00',
          distance: (Math.random() * 5).toFixed(1),
          phone: `0791-8${Math.floor(Math.random() * 1000000).toString().padStart(6, '0')}`
        });
      }
      
      return results;
    },
    
    // 生成随机时间
    generateRandomTime(daysAgo = 7) {
      const date = new Date();
      date.setDate(date.getDate() - Math.floor(Math.random() * daysAgo));
      date.setHours(Math.floor(Math.random() * 24), Math.floor(Math.random() * 60));
      
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      const hours = date.getHours().toString().padStart(2, '0');
      const minutes = date.getMinutes().toString().padStart(2, '0');
      
      return `${year}-${month}-${day} ${hours}:${minutes}`;
    },
    
    // 格式化手机号
    formatPhone(phone) {
      if (!phone || phone.length !== 11) return phone;
      return phone.substr(0, 3) + '****' + phone.substr(7);
    },
    
    // 联系快递员
    contactCourier(phone) {
      if (!phone) {
        return uni.showToast({
          title: '暂无联系方式',
          icon: 'none'
        });
      }
      
      uni.makePhoneCall({
        phoneNumber: phone,
        fail: () => {
          uni.showToast({
            title: '拨打失败，请手动联系',
            icon: 'none'
          });
        }
      });
    },
    
    // 拨打电话
    callPhone(phone) {
      if (!phone) {
        return uni.showToast({
          title: '暂无联系方式',
          icon: 'none'
        });
      }
      
      uni.makePhoneCall({
        phoneNumber: phone,
        fail: () => {
          uni.showToast({
            title: '拨打失败，请手动联系',
            icon: 'none'
          });
        }
      });
    },
    
    // 打开导航
    openNavigation(address) {
      if (!address) {
        return uni.showToast({
          title: '地址信息不完整',
          icon: 'none'
        });
      }
      
      // 尝试使用微信内置地图导航
      uni.openLocation({
        latitude: 0, // 这里应该从后端获取准确经纬度
        longitude: 0,
        name: address,
        address: address,
        fail: () => {
          // 如果微信内置地图无法打开，提示用户手动导航
          uni.showModal({
            title: '提示',
            content: '无法打开导航，请手动复制地址导航',
            confirmText: '复制地址',
            success: (res) => {
              if (res.confirm) {
                uni.setClipboardData({
                  data: address,
                  success: () => {
                    uni.showToast({
                      title: '地址已复制',
                      icon: 'success'
                    });
                  }
                });
              }
            }
          });
        }
      });
    }
  }
};
</script>

<style>
.search-container {
  min-height: 100vh;
  background-color: #f0f0f0;
  padding-bottom: 30rpx;
}

.search-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 112rpx;
  padding: 20rpx;
  background-color: #fff;
  display: flex;
  align-items: center;
  z-index: 100;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.search-box {
  flex: 1;
  display: flex;
  align-items: center;
  background-color: #f5f5f5;
  height: 72rpx;
  border-radius: 36rpx;
  padding: 0 30rpx;
}

.search-input {
  flex: 1;
  height: 72rpx;
  line-height: 72rpx;
  font-size: 28rpx;
  margin: 0 20rpx;
}

.cancel-btn {
  color: #3cc51f;
  font-size: 28rpx;
  margin-left: 20rpx;
}

/* 搜索内容样式 */
.search-content, .search-result {
  padding-top: 112rpx;
}

/* 历史记录样式 */
.search-history, .hot-search {
  background-color: #fff;
  margin-bottom: 20rpx;
  padding: 30rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
}

.history-list {
  display: flex;
  flex-direction: column;
}

.history-item {
  display: flex;
  align-items: center;
  padding: 15rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.history-item:last-child {
  border-bottom: none;
}

.history-text {
  font-size: 28rpx;
  color: #333;
  margin-left: 15rpx;
}

/* 热门搜索标签样式 */
.tag-list {
  display: flex;
  flex-wrap: wrap;
}

.tag-item {
  background-color: #f5f5f5;
  border-radius: 30rpx;
  padding: 10rpx 20rpx;
  font-size: 26rpx;
  color: #333;
  margin-right: 20rpx;
  margin-bottom: 20rpx;
}

/* 结果标签页样式 */
.result-tabs {
  display: flex;
  background-color: #fff;
  border-bottom: 1rpx solid #f0f0f0;
  position: fixed;
  top: 112rpx;
  left: 0;
  right: 0;
  z-index: 99;
}

.tab-item {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  text-align: center;
  font-size: 28rpx;
  color: #333;
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
  width: 40rpx;
  height: 4rpx;
  background-color: #3cc51f;
  border-radius: 2rpx;
}

.tab-count {
  font-size: 24rpx;
  color: #999;
  font-weight: normal;
}

/* 搜索结果列表样式 */
.result-list {
  margin-top: 80rpx;
  padding: 0 20rpx;
}

/* 空结果样式 */
.empty-result {
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
  font-size: 28rpx;
  color: #999;
}

/* 包裹列表样式 */
.package-item {
  background-color: #fff;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.package-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.company-info {
  display: flex;
  align-items: center;
}

.company-logo {
  width: 48rpx;
  height: 48rpx;
  margin-right: 15rpx;
}

.company-name {
  font-size: 28rpx;
  color: #333;
  font-weight: bold;
}

.package-status {
  font-size: 26rpx;
  padding: 6rpx 16rpx;
  border-radius: 20rpx;
}

.status-waiting {
  background-color: #f0f0f0;
  color: #999;
}

.status-collected {
  background-color: #e1f3d8;
  color: #3cc51f;
}

.status-transporting {
  background-color: #d7f0ff;
  color: #2979ff;
}

.status-arrived {
  background-color: #e6f7ff;
  color: #1890ff;
}

.status-delivering {
  background-color: #fff2e6;
  color: #ff6600;
}

.status-signed {
  background-color: #f0f9eb;
  color: #3cc51f;
}

.status-cancelled {
  background-color: #f5f5f5;
  color: #999;
}

.package-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.package-info {
  display: flex;
  flex-direction: column;
}

.tracking-no {
  font-size: 30rpx;
  color: #333;
  margin-bottom: 10rpx;
}

.package-time {
  font-size: 24rpx;
  color: #999;
}

/* 订单列表样式 */
.order-item {
  background-color: #fff;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.order-no {
  font-size: 28rpx;
  color: #333;
}

.order-status {
  font-size: 26rpx;
  padding: 6rpx 16rpx;
  border-radius: 20rpx;
}

.order-info {
  background-color: #f8f8f8;
  border-radius: 8rpx;
  padding: 20rpx;
}

.address-item {
  display: flex;
  margin-bottom: 20rpx;
}

.address-icon {
  width: 40rpx;
  height: 40rpx;
  line-height: 40rpx;
  text-align: center;
  border-radius: 50%;
  font-size: 24rpx;
  margin-right: 15rpx;
  flex-shrink: 0;
}

.sender {
  background-color: #ff6600;
  color: #fff;
}

.receiver {
  background-color: #2979ff;
  color: #fff;
}

.address-content {
  flex: 1;
}

.address-user {
  display: flex;
  align-items: center;
  margin-bottom: 10rpx;
}

.user-name {
  font-size: 28rpx;
  color: #333;
  font-weight: bold;
  margin-right: 15rpx;
}

.user-phone {
  font-size: 26rpx;
  color: #666;
}

.address-text {
  font-size: 26rpx;
  color: #666;
  line-height: 1.4;
}

.order-detail-info {
  display: flex;
  flex-wrap: wrap;
  margin-top: 20rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid #eee;
}

.detail-item {
  width: 50%;
  margin-bottom: 16rpx;
}

.detail-label {
  font-size: 24rpx;
  color: #999;
  margin-right: 8rpx;
}

.detail-value {
  font-size: 26rpx;
  color: #333;
}

.detail-value.price {
  color: #ff6600;
  font-weight: bold;
}

/* 快递员列表样式 */
.courier-item {
  display: flex;
  align-items: center;
  background-color: #fff;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.courier-avatar {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 25rpx;
}

.avatar-image {
  width: 100%;
  height: 100%;
}

.courier-info {
  flex: 1;
}

.courier-name-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10rpx;
}

.courier-name {
  font-size: 32rpx;
  color: #333;
  font-weight: bold;
}

.courier-rating {
  display: flex;
  align-items: center;
}

.rating-value {
  font-size: 26rpx;
  color: #ff9900;
  margin-left: 5rpx;
}

.courier-detail {
  margin-bottom: 10rpx;
}

.courier-company, .courier-area {
  font-size: 26rpx;
  color: #666;
  margin-right: 15rpx;
}

.courier-stats {
  display: flex;
  align-items: center;
  font-size: 24rpx;
  color: #999;
}

.stat-divider {
  margin: 0 15rpx;
}

.contact-btn {
  color: #3cc51f;
}

/* 服务点列表样式 */
.station-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.station-info {
  flex: 1;
}

.station-name {
  font-size: 30rpx;
  color: #333;
  font-weight: bold;
  margin-bottom: 10rpx;
  display: block;
}

.station-address, .station-business {
  display: flex;
  align-items: center;
  margin-bottom: 10rpx;
}

.address-text, .business-text {
  font-size: 26rpx;
  color: #666;
  margin-left: 10rpx;
  flex: 1;
}

.distance {
  font-size: 24rpx;
  color: #999;
  margin-left: 10rpx;
}

.station-actions {
  display: flex;
  flex-direction: column;
}

.action-btn {
  width: 60rpx;
  height: 60rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 15rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.phone-btn {
  background-color: #f0f9eb;
}

.navi-btn {
  background-color: #ecf5ff;
}

/* 加载更多样式 */
.loading-more {
  text-align: center;
  padding: 30rpx 0;
}

.loading-text, .more-text, .no-more-text {
  font-size: 26rpx;
  color: #999;
}

.more-text {
  color: #3cc51f;
}
</style> 