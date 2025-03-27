<template>
  <view class="search-container">
    <!-- 搜索头部 -->
    <view class="search-header">
      <view class="search-box">
        <uni-icons type="search" size="18" color="#666"></uni-icons>
        <input 
          type="text" 
          v-model="searchQuery" 
          placeholder="搜索包裹、快递员、服务区域等" 
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
      
      <!-- 快递员结果 -->
      <view class="result-list courier-list" v-if="activeTab === 1">
        <view class="empty-result" v-if="courierResults.length === 0">
          <image src="/static/images/empty-search.png" mode="aspectFit" class="empty-image"></image>
          <text class="empty-text">未找到相关快递员</text>
        </view>
        
        <view class="courier-item" v-for="(item, index) in courierResults" :key="index" @click="navigateToDetail('courier', item.id)">
          <view class="courier-avatar">
            <image :src="item.avatar || '/static/images/user.png'" mode="aspectFill" class="avatar-image"></image>
          </view>
          <view class="courier-info">
            <view class="courier-main">
              <text class="courier-name">{{ item.name }}</text>
              <view class="courier-tags">
                <text class="tag" v-for="(tag, tagIndex) in item.tags" :key="tagIndex">{{ tag }}</text>
              </view>
            </view>
            <view class="courier-detail">
              <text class="courier-company">{{ item.company }}</text>
              <text class="courier-area">服务区域: {{ item.serviceArea }}</text>
            </view>
          </view>
          <view class="courier-right">
            <view class="rating">
              <uni-icons type="star-filled" size="14" color="#ff9900"></uni-icons>
              <text class="rating-text">{{ item.rating }}</text>
            </view>
            <uni-icons type="right" size="16" color="#ccc"></uni-icons>
          </view>
        </view>
      </view>
      
      <!-- 服务点结果 -->
      <view class="result-list station-list" v-if="activeTab === 2">
        <view class="empty-result" v-if="stationResults.length === 0">
          <image src="/static/images/empty-search.png" mode="aspectFit" class="empty-image"></image>
          <text class="empty-text">未找到相关服务点</text>
        </view>
        
        <view class="station-item" v-for="(item, index) in stationResults" :key="index" @click="navigateToDetail('station', item.id)">
          <view class="station-logo">
            <image :src="item.logo || '/static/images/station.png'" mode="aspectFit" class="logo-image"></image>
          </view>
          <view class="station-info">
            <text class="station-name">{{ item.name }}</text>
            <text class="station-address">{{ item.address }}</text>
            <view class="station-tags">
              <text class="tag" v-for="(tag, tagIndex) in item.services" :key="tagIndex">{{ tag }}</text>
            </view>
          </view>
          <view class="station-distance">
            <text class="distance-text">{{ item.distance }}</text>
            <uni-icons type="right" size="16" color="#ccc"></uni-icons>
          </view>
        </view>
      </view>
      
      <!-- 加载更多 -->
      <view class="load-more" v-if="hasMore" @click="loadMore">
        <text>加载更多</text>
      </view>
      <view class="no-more" v-if="!hasMore && getActiveResults().length > 0">
        <text>没有更多结果了</text>
      </view>
    </view>
  </view>
</template>

<script>
import uniIcons from '@/uni_modules/uni-icons/components/uni-icons/uni-icons.vue'

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
      hasMore: false,
      searchHistory: [],
      hotSearches: ['顺丰快递', '申通快递', '南昌市青山湖区', '张师傅', '取件点', '艾溪湖'],
      tabs: [
        { name: '包裹', count: 0 },
        { name: '快递员', count: 0 },
        { name: '服务点', count: 0 }
      ],
      packageResults: [],
      courierResults: [],
      stationResults: []
    };
  },
  
  onLoad() {
    // 获取搜索历史
    this.loadSearchHistory();
  },
  
  methods: {
    // 加载搜索历史
    loadSearchHistory() {
      const history = uni.getStorageSync('searchHistory');
      if (history) {
        this.searchHistory = JSON.parse(history);
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
      } else {
        uni.navigateBack();
      }
    },
    
    // 切换标签
    switchTab(index) {
      if (this.activeTab === index) return;
      this.activeTab = index;
      this.page = 1;
      this.fetchSearchResults();
    },
    
    // 获取当前标签的结果数组
    getActiveResults() {
      switch (this.activeTab) {
        case 0:
          return this.packageResults;
        case 1:
          return this.courierResults;
        case 2:
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
        5: 'status-signed'
      };
      return statusMap[status] || '';
    },
    
    // 获取搜索结果
    fetchSearchResults() {
      uni.showLoading({
        title: '搜索中...'
      });
      
      // 这里是模拟数据，实际应该调用API获取搜索结果
      setTimeout(() => {
        uni.hideLoading();
        
        // 根据当前标签和搜索内容生成模拟结果
        switch (this.activeTab) {
          case 0: // 包裹
            if (this.page === 1) {
              // 模拟包裹搜索结果
              this.packageResults = this.generatePackageResults();
              this.tabs[0].count = this.packageResults.length;
              
              // 模拟其他标签的数量
              this.tabs[1].count = Math.floor(Math.random() * 5);
              this.tabs[2].count = Math.floor(Math.random() * 8);
            } else {
              // 加载更多时追加数据
              const morePackages = this.generatePackageResults(true);
              this.packageResults = [...this.packageResults, ...morePackages];
            }
            break;
          case 1: // 快递员
            if (this.page === 1) {
              // 模拟快递员搜索结果
              this.courierResults = this.generateCourierResults();
              this.tabs[1].count = this.courierResults.length;
            } else {
              // 加载更多时追加数据
              const moreCouriers = this.generateCourierResults(true);
              this.courierResults = [...this.courierResults, ...moreCouriers];
            }
            break;
          case 2: // 服务点
            if (this.page === 1) {
              // 模拟服务点搜索结果
              this.stationResults = this.generateStationResults();
              this.tabs[2].count = this.stationResults.length;
            } else {
              // 加载更多时追加数据
              const moreStations = this.generateStationResults(true);
              this.stationResults = [...this.stationResults, ...moreStations];
            }
            break;
        }
        
        // 随机设置是否还有更多结果
        this.hasMore = this.page < 3;
      }, 1000);
    },
    
    // 加载更多结果
    loadMore() {
      if (!this.hasMore) return;
      this.page++;
      this.fetchSearchResults();
    },
    
    // 跳转到详情页
    navigateToDetail(type, id) {
      const urlMap = {
        package: `/pages/order/track?trackingNo=`,
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
    
    // 生成模拟包裹结果
    generatePackageResults(isMore = false) {
      const companies = ['顺丰速运', '中通快递', '圆通速递', '韵达快递', '申通快递', '京东物流'];
      const statusList = [
        { status: 2, text: '运输中' },
        { status: 3, text: '已到达' },
        { status: 4, text: '派送中' },
        { status: 5, text: '已签收' }
      ];
      
      // 根据页码控制结果数量
      const count = isMore ? Math.floor(Math.random() * 3) + 1 : Math.floor(Math.random() * 5) + 2;
      
      // 生成模拟数据
      const results = [];
      for (let i = 0; i < count; i++) {
        const companyIndex = Math.floor(Math.random() * companies.length);
        const statusIndex = Math.floor(Math.random() * statusList.length);
        
        // 生成随机运单号
        const trackingNo = this.generateTrackingNo(companies[companyIndex]);
        
        results.push({
          id: trackingNo,
          company: companies[companyIndex],
          logo: `/static/images/${companies[companyIndex].substring(0, 2)}-logo.png`,
          trackingNo: trackingNo,
          status: statusList[statusIndex].status,
          statusText: statusList[statusIndex].text,
          updateTime: this.generateRandomTime()
        });
      }
      
      return results;
    },
    
    // 生成模拟快递员结果
    generateCourierResults(isMore = false) {
      const names = ['张师傅', '李师傅', '王师傅', '刘师傅', '陈师傅', '赵师傅'];
      const companies = ['顺丰速运', '中通快递', '圆通速递', '韵达快递', '申通快递', '京东物流'];
      const areas = ['青山湖区', '西湖区', '东湖区', '南昌县', '新建区', '青云谱区'];
      const tags = ['准时送达', '服务好评', '专业可靠', '礼貌热情', '防疫规范', '配送迅速'];
      
      // 根据页码控制结果数量
      const count = isMore ? Math.floor(Math.random() * 2) + 1 : Math.floor(Math.random() * 3) + 2;
      
      // 生成模拟数据
      const results = [];
      for (let i = 0; i < count; i++) {
        const nameIndex = Math.floor(Math.random() * names.length);
        const companyIndex = Math.floor(Math.random() * companies.length);
        const areaIndex = Math.floor(Math.random() * areas.length);
        
        // 随机选择2-3个标签
        const tagCount = Math.floor(Math.random() * 2) + 2;
        const selectedTags = [];
        while (selectedTags.length < tagCount) {
          const tag = tags[Math.floor(Math.random() * tags.length)];
          if (!selectedTags.includes(tag)) {
            selectedTags.push(tag);
          }
        }
        
        results.push({
          id: 1000 + i + (isMore ? this.page * 10 : 0),
          name: names[nameIndex],
          avatar: `/static/images/courier-avatar-${Math.floor(Math.random() * 3) + 1}.png`,
          company: companies[companyIndex],
          serviceArea: `${areas[areaIndex]}`,
          tags: selectedTags,
          rating: (Math.random() * 1 + 4).toFixed(1)
        });
      }
      
      return results;
    },
    
    // 生成模拟服务点结果
    generateStationResults(isMore = false) {
      const names = ['顺丰快递服务点', '中通快递营业点', '圆通速递服务站', '韵达快递网点', '京东快递驿站', '邮政快递网点'];
      const addresses = [
        '南昌市青山湖区艾溪湖北路77号',
        '南昌市西湖区抚河北路128号',
        '南昌市东湖区八一大道266号',
        '南昌市青云谱区井冈山大道399号',
        '南昌市新建区长堎大道88号',
        '南昌市南昌县莲塘镇振兴大道55号'
      ];
      const services = ['寄件', '取件', '打包', '称重', '存放', '快递柜'];
      const distances = ['500m', '800m', '1.2km', '1.5km', '2.3km', '3.1km'];
      
      // 根据页码控制结果数量
      const count = isMore ? Math.floor(Math.random() * 2) + 1 : Math.floor(Math.random() * 4) + 2;
      
      // 生成模拟数据
      const results = [];
      for (let i = 0; i < count; i++) {
        const nameIndex = Math.floor(Math.random() * names.length);
        const addressIndex = Math.floor(Math.random() * addresses.length);
        const distanceIndex = Math.floor(Math.random() * distances.length);
        
        // 随机选择3-4个服务
        const serviceCount = Math.floor(Math.random() * 2) + 3;
        const selectedServices = [];
        while (selectedServices.length < serviceCount) {
          const service = services[Math.floor(Math.random() * services.length)];
          if (!selectedServices.includes(service)) {
            selectedServices.push(service);
          }
        }
        
        results.push({
          id: 2000 + i + (isMore ? this.page * 10 : 0),
          name: names[nameIndex],
          logo: `/static/images/${names[nameIndex].substring(0, 2)}-logo.png`,
          address: addresses[addressIndex],
          services: selectedServices,
          distance: distances[distanceIndex]
        });
      }
      
      return results;
    },
    
    // 生成随机运单号
    generateTrackingNo(company) {
      let prefix = '';
      if (company.includes('顺丰')) {
        prefix = 'SF';
      } else if (company.includes('中通')) {
        prefix = 'ZT';
      } else if (company.includes('圆通')) {
        prefix = 'YT';
      } else if (company.includes('韵达')) {
        prefix = 'YD';
      } else if (company.includes('申通')) {
        prefix = 'ST';
      } else if (company.includes('京东')) {
        prefix = 'JD';
      } else {
        prefix = 'EX';
      }
      
      // 生成随机数字
      let number = '';
      for (let i = 0; i < 10; i++) {
        number += Math.floor(Math.random() * 10);
      }
      
      return prefix + number;
    },
    
    // 生成随机时间（最近7天内）
    generateRandomTime() {
      const now = new Date();
      const daysAgo = Math.floor(Math.random() * 7);
      const hoursAgo = Math.floor(Math.random() * 24);
      const minutesAgo = Math.floor(Math.random() * 60);
      
      const date = new Date(now.getTime() - (daysAgo * 24 * 60 * 60 * 1000) - (hoursAgo * 60 * 60 * 1000) - (minutesAgo * 60 * 1000));
      
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      const hours = date.getHours().toString().padStart(2, '0');
      const minutes = date.getMinutes().toString().padStart(2, '0');
      
      return `${year}-${month}-${day} ${hours}:${minutes}`;
    }
  }
};
</script>

<style>
.search-container {
  min-height: 100vh;
  background-color: #f8f8f8;
}

/* 搜索头部样式 */
.search-header {
  background-color: #fff;
  padding: 20rpx 30rpx;
  display: flex;
  align-items: center;
  border-bottom: 1rpx solid #f0f0f0;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
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

/* 结果列表样式 */
.result-list {
  padding-top: 80rpx;
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
  margin-bottom: 20rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

/* 包裹结果样式 */
.package-item {
  background-color: #fff;
  margin-bottom: 20rpx;
  padding: 30rpx;
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
  width: 50rpx;
  height: 50rpx;
  margin-right: 15rpx;
}

.company-name {
  font-size: 28rpx;
  color: #333;
  font-weight: bold;
}

.package-status {
  font-size: 26rpx;
  font-weight: bold;
}

.status-waiting {
  color: #999;
}

.status-collected, .status-transporting {
  color: #3cc51f;
}

.status-arrived {
  color: #ff9900;
}

.status-delivering {
  color: #ff5500;
}

.status-signed {
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

/* 快递员结果样式 */
.courier-item {
  background-color: #fff;
  margin-bottom: 20rpx;
  padding: 30rpx;
  display: flex;
  align-items: center;
}

.courier-avatar {
  margin-right: 20rpx;
}

.avatar-image {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50rpx;
}

.courier-info {
  flex: 1;
  margin-right: 20rpx;
}

.courier-main {
  display: flex;
  align-items: center;
  margin-bottom: 10rpx;
}

.courier-name {
  font-size: 30rpx;
  color: #333;
  font-weight: bold;
  margin-right: 15rpx;
}

.courier-tags {
  display: flex;
}

.tag {
  font-size: 22rpx;
  color: #3cc51f;
  background-color: #e6f7ea;
  padding: 4rpx 10rpx;
  border-radius: 4rpx;
  margin-right: 10rpx;
}

.courier-detail {
  display: flex;
  flex-direction: column;
}

.courier-company {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 6rpx;
}

.courier-area {
  font-size: 24rpx;
  color: #999;
}

.courier-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.rating {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.rating-text {
  font-size: 26rpx;
  color: #ff9900;
  margin-left: 6rpx;
}

/* 服务点结果样式 */
.station-item {
  background-color: #fff;
  margin-bottom: 20rpx;
  padding: 30rpx;
  display: flex;
  align-items: center;
}

.station-logo {
  margin-right: 20rpx;
}

.logo-image {
  width: 80rpx;
  height: 80rpx;
  border-radius: 8rpx;
}

.station-info {
  flex: 1;
  margin-right: 20rpx;
}

.station-name {
  font-size: 30rpx;
  color: #333;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.station-address {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 10rpx;
}

.station-tags {
  display: flex;
  flex-wrap: wrap;
}

.station-distance {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.distance-text {
  font-size: 26rpx;
  color: #3cc51f;
  margin-bottom: 20rpx;
}

/* 加载更多样式 */
.load-more, .no-more {
  text-align: center;
  padding: 30rpx;
  color: #999;
  font-size: 26rpx;
}

.load-more {
  color: #3cc51f;
}
</style> 