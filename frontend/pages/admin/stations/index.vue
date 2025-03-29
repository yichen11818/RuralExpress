<template>
  <view class="container">
    <view class="header">
      <view class="title">服务点管理</view>
      <view class="header-actions">
        <button class="action-btn diagnose-btn" @click="runDiagnostics">诊断</button>
        <button class="add-btn" @click="openStationForm('add')">添加服务点</button>
      </view>
    </view>
    
    <view class="search-bar">
      <view class="search-input">
        <uni-icons type="search" size="18" color="#999"></uni-icons>
        <input 
          type="text" 
          v-model="searchKeyword" 
          placeholder="搜索服务点名称/地址" 
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
        <picker @change="handleAreaChange" :value="areaIndex" :range="areaOptions">
          <view class="picker-item">
            <text>区域: {{ areaOptions[areaIndex] }}</text>
            <uni-icons type="down" size="14" color="#666"></uni-icons>
          </view>
        </picker>
      </view>
    </view>
    
    <view class="station-list" v-if="stations.length > 0">
      <view class="station-item" v-for="(station, index) in stations" :key="index">
        <view class="station-header">
          <view class="station-info">
            <image class="station-logo" :src="station.logo || '/static/images/default-logo.png'" mode="aspectFill"></image>
            <view class="station-basic">
              <view class="station-name">{{ station.name }}</view>
              <view class="station-address">{{ station.province + station.city + station.district + station.address }}</view>
              <view class="station-meta">
                <view class="meta-item">
                  <uni-icons type="star-filled" size="14" color="#ff9900"></uni-icons>
                  <text>{{ (station.rating || 0).toFixed(1) }}</text>
                </view>
                <view class="meta-item">
                  <uni-icons type="chatbubble" size="14" color="#007aff"></uni-icons>
                  <text>{{ station.reviewCount || 0 }}条评价</text>
                </view>
              </view>
            </view>
          </view>
          <view class="station-status" :class="getStatusClass(station.status)">
            {{ getStatusText(station.status) }}
          </view>
        </view>
        
        <view class="station-details">
          <view class="detail-row">
            <view class="detail-item">
              <text class="detail-label">联系电话:</text>
              <text class="detail-value">{{ station.phone }}</text>
            </view>
            <view class="detail-item">
              <text class="detail-label">营业时间:</text>
              <text class="detail-value">{{ station.businessHours }}</text>
            </view>
          </view>
          <view class="detail-row">
            <view class="detail-item">
              <text class="detail-label">负责人:</text>
              <text class="detail-value">{{ station.managerName }}</text>
            </view>
            <view class="detail-item">
              <text class="detail-label">负责人电话:</text>
              <text class="detail-value">{{ station.managerPhone }}</text>
            </view>
          </view>
          <view class="detail-row">
            <view class="detail-item">
              <text class="detail-label">支持公司:</text>
              <view class="company-tags">
                <text class="company-tag" v-for="(company, idx) in (station.supportedCompanies || [])" :key="idx">
                  {{ company }}
                </text>
              </view>
            </view>
          </view>
        </view>
        
        <view class="station-photos" v-if="station.photos && station.photos.length > 0">
          <text class="photos-title">服务点照片</text>
          <scroll-view scroll-x class="photos-scroll">
            <view class="photos-container">
              <image 
                v-for="(photo, pidx) in station.photos" 
                :key="pidx" 
                :src="photo" 
                mode="aspectFill" 
                class="station-photo"
                @click="previewImage(station.photos, pidx)"
              ></image>
            </view>
          </scroll-view>
        </view>
        
        <view class="station-actions">
          <button class="action-btn edit-btn" @click="openStationForm('edit', station)">编辑</button>
          <button 
            class="action-btn" 
            :class="station.status === 0 ? 'disable-btn' : 'enable-btn'" 
            @click="toggleStatus(station)"
          >
            {{ station.status === 0 ? '禁用' : '启用' }}
          </button>
          <button class="action-btn delete-btn" @click="handleDelete(station)">删除</button>
        </view>
      </view>
    </view>
    
    <view class="empty-list" v-else>
      <image src="/static/images/empty.png" mode="aspectFit"></image>
      <text>暂无服务点数据</text>
    </view>
    
    <uni-pagination
      :total="total"
      :pageSize="pageSize"
      :current="currentPage"
      @change="handlePageChange"
    ></uni-pagination>
    
    <!-- 服务点表单弹窗 -->
    <uni-popup ref="stationFormPopup" type="center">
      <view class="form-popup">
        <view class="form-header">
          <text class="form-title">{{ formType === 'add' ? '添加服务点' : '编辑服务点' }}</text>
          <uni-icons type="close" size="20" color="#999" @click="closeStationForm"></uni-icons>
        </view>
        <view class="form-body">
          <view class="form-item">
            <text class="form-label">服务点名称</text>
            <input 
              class="form-input" 
              type="text" 
              v-model="stationForm.name" 
              placeholder="请输入服务点名称"
            />
          </view>
          <view class="form-item">
            <text class="form-label">联系电话</text>
            <input 
              class="form-input" 
              type="text" 
              v-model="stationForm.phone" 
              placeholder="请输入联系电话"
            />
          </view>
          <view class="form-item">
            <text class="form-label">营业时间</text>
            <input 
              class="form-input" 
              type="text" 
              v-model="stationForm.businessHours" 
              placeholder="如: 09:00-18:00"
            />
          </view>
          <view class="form-item">
            <text class="form-label">省份</text>
            <input 
              class="form-input" 
              type="text" 
              v-model="stationForm.province" 
              placeholder="请输入省份"
            />
          </view>
          <view class="form-item">
            <text class="form-label">城市</text>
            <input 
              class="form-input" 
              type="text" 
              v-model="stationForm.city" 
              placeholder="请输入城市"
            />
          </view>
          <view class="form-item">
            <text class="form-label">区/县</text>
            <input 
              class="form-input" 
              type="text" 
              v-model="stationForm.district" 
              placeholder="请输入区/县"
            />
          </view>
          <view class="form-item">
            <text class="form-label">详细地址</text>
            <input 
              class="form-input" 
              type="text" 
              v-model="stationForm.address" 
              placeholder="请输入详细地址"
            />
          </view>
          <view class="form-item">
            <text class="form-label">负责人姓名</text>
            <input 
              class="form-input" 
              type="text" 
              v-model="stationForm.managerName" 
              placeholder="请输入负责人姓名"
            />
          </view>
          <view class="form-item">
            <text class="form-label">负责人电话</text>
            <input 
              class="form-input" 
              type="text" 
              v-model="stationForm.managerPhone" 
              placeholder="请输入负责人电话"
            />
          </view>
          <view class="form-item">
            <text class="form-label">支持公司</text>
            <view class="form-companies">
              <view 
                v-for="(company, idx) in availableCompanies" 
                :key="idx"
                class="company-tag-select"
                :class="{ 'selected': stationForm.supportedCompanies.includes(company) }"
                @click="toggleCompany(company)"
              >
                {{ company }}
              </view>
            </view>
          </view>
          <view class="form-item">
            <text class="form-label">状态</text>
            <switch 
              :checked="stationForm.status === 0" 
              @change="(e) => stationForm.status = e.detail.value ? 0 : 1"
            />
          </view>
        </view>
        <view class="form-footer">
          <button class="cancel-btn" @click="closeStationForm">取消</button>
          <button class="submit-btn" @click="submitStationForm">确认</button>
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
      areaIndex: 0,
      areaOptions: ['全部', '北京市', '上海市', '广州市', '深圳市'],
      stations: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      formType: 'add', // add 或 edit
      stationForm: {
        id: null,
        name: '',
        logo: '',
        phone: '',
        businessHours: '',
        province: '',
        city: '',
        district: '',
        address: '',
        managerName: '',
        managerPhone: '',
        supportedCompanies: [],
        status: 0,
        photos: []
      },
      originalStation: null,
      availableCompanies: ['顺丰速运', '中通快递', '圆通速递', '申通快递', '韵达快递', '百世快递', '京东物流', '邮政EMS']
    };
  },
  
  // 添加request到组件
  beforeCreate() {
    // 将request挂载到组件实例
    this.$request = request;
  },
  
  onLoad() {
    // 先测试服务器连接
    console.log('[服务点页面] 页面加载, 测试服务器连接');
    this.$request.testConnection()
      .then(result => {
        if (result.success) {
          console.log('[服务点页面] 服务器连接正常, 开始加载数据');
          // 检查是否有警告信息
          if (result.warning) {
            console.warn('[服务点页面] 连接测试警告:', result.warning);
          }
          
          // 特殊处理403状态码
          if (result.statusCode === 403) {
            console.warn('[服务点页面] 健康检查返回403, 但仍继续数据加载');
          }
          
          this.loadStations();
        } else {
          console.error('[服务点页面] 服务器连接失败:', result.error);
          uni.showToast({
            title: '服务器连接失败，请检查网络设置或联系管理员',
            icon: 'none',
            duration: 3000
          });
        }
      });
  },
  
  methods: {
    // 加载服务点列表
    loadStations() {
      console.log('[服务点页面] 开始加载服务点数据');
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
      
      if (this.areaIndex > 0) {
        params.area = this.areaOptions[this.areaIndex];
      }
      
      console.log('[服务点页面] 请求参数:', params);
      
      // 发起请求
      this.$request.get('/api/admin/stations', params)
        .then(res => {
          console.log('[服务点页面] 请求成功:', res);
          uni.hideLoading();
          if (res.code === 200) {
            const data = res.data || {};
            // 适配后端返回的数据结构
            // 后端返回的是records而不是list
            this.stations = Array.isArray(data.records) ? data.records : [];
            this.total = data.total || (this.stations.length > 0 ? this.stations.length : 0);
            console.log('[服务点页面] 数据加载成功, 总数:', this.total, '记录数:', this.stations.length);
            
            // 如果返回的数据有问题，输出更多日志
            if (!data.records && !data.list) {
              console.warn('[服务点页面] 注意: 返回数据中缺少records/list字段，返回的数据结构为:', data);
            }
          } else {
            console.error('[服务点页面] 业务错误:', res);
            // 检查是否是特定的数据库错误（business_hours字段缺失问题）
            if (res.message && res.message.includes("Unknown column 'business_hours'")) {
              console.error('[服务点页面] 检测到business_hours字段缺失错误');
              uni.showModal({
                title: '数据库错误',
                content: '数据库中缺少business_hours字段，请联系管理员修复后端问题。',
                confirmText: '我知道了',
                showCancel: false
              });
            } else {
              uni.showToast({
                title: res.message || '获取服务点列表失败',
                icon: 'none'
              });
            }
          }
        })
        .catch(error => {
          console.error('[服务点页面] 请求异常:', error);
          // 尝试获取更多错误信息
          try {
            if (typeof error === 'object') {
              console.error('[服务点页面] 错误详情:', JSON.stringify(error));
              // 检查是否是特定的数据库错误（business_hours字段缺失问题）
              if (error.message && error.message.includes("Unknown column 'business_hours'")) {
                console.error('[服务点页面] 检测到business_hours字段缺失错误');
                uni.showModal({
                  title: '数据库错误',
                  content: '数据库中缺少business_hours字段，请联系管理员修复后端问题。\n\n技术细节：数据库查询中使用了business_hours字段，但数据库表中不存在此字段。',
                  confirmText: '我知道了',
                  showCancel: false
                });
                return;
              }
            } else {
              console.error('[服务点页面] 错误信息:', error);
            }
          } catch (e) {
            console.error('[服务点页面] 无法序列化错误对象');
          }
          
          uni.hideLoading();
          uni.showToast({
            title: '网络错误，请稍后再试',
            icon: 'none'
          });
        });
    },
    
    // 搜索服务点
    handleSearch() {
      this.currentPage = 1;
      this.loadStations();
    },
    
    // 切换状态筛选
    handleStatusChange(e) {
      this.statusIndex = e.detail.value;
      this.currentPage = 1;
      this.loadStations();
    },
    
    // 切换区域筛选
    handleAreaChange(e) {
      this.areaIndex = e.detail.value;
      this.currentPage = 1;
      this.loadStations();
    },
    
    // 分页切换
    handlePageChange(e) {
      this.currentPage = e.current;
      this.loadStations();
    },
    
    // 获取状态文本
    getStatusText(status) {
      return status === 0 ? '正常营业' : '已禁用';
    },
    
    // 获取状态样式
    getStatusClass(status) {
      return status === 0 ? 'status-normal' : 'status-disabled';
    },
    
    // 预览图片
    previewImage(images, current) {
      uni.previewImage({
        urls: images,
        current: images[current]
      });
    },
    
    // 打开服务点表单
    openStationForm(type, station = null) {
      this.formType = type;
      
      if (type === 'add') {
        this.stationForm = {
          id: null,
          name: '',
          logo: '',
          phone: '',
          businessHours: '',
          province: '',
          city: '',
          district: '',
          address: '',
          managerName: '',
          managerPhone: '',
          supportedCompanies: [],
          status: 0,
          photos: []
        };
      } else {
        this.stationForm = { ...station };
        // 确保supportedCompanies总是数组
        if (!Array.isArray(this.stationForm.supportedCompanies)) {
          this.stationForm.supportedCompanies = [];
        }
        this.originalStation = { ...station };
      }
      
      this.$refs.stationFormPopup.open();
    },
    
    // 关闭服务点表单
    closeStationForm() {
      this.$refs.stationFormPopup.close();
    },
    
    // 切换支持的快递公司
    toggleCompany(company) {
      const index = this.stationForm.supportedCompanies.indexOf(company);
      if (index === -1) {
        this.stationForm.supportedCompanies.push(company);
      } else {
        this.stationForm.supportedCompanies.splice(index, 1);
      }
    },
    
    // 提交服务点表单
    submitStationForm() {
      // 表单验证
      if (!this.stationForm.name) {
        uni.showToast({
          title: '请输入服务点名称',
          icon: 'none'
        });
        return;
      }
      
      if (!this.stationForm.phone) {
        uni.showToast({
          title: '请输入联系电话',
          icon: 'none'
        });
        return;
      }
      
      if (!this.stationForm.address || !this.stationForm.province || !this.stationForm.city || !this.stationForm.district) {
        uni.showToast({
          title: '请完善地址信息',
          icon: 'none'
        });
        return;
      }
      
      if (this.stationForm.supportedCompanies.length === 0) {
        uni.showToast({
          title: '请选择至少一个支持的快递公司',
          icon: 'none'
        });
        return;
      }
      
      // 实际API调用
      const url = this.formType === 'add' ? '/api/admin/stations' : `/api/admin/stations/${this.stationForm.id}`;
      const apiMethod = this.formType === 'add' ? this.$request.post : this.$request.put;
      
      uni.showLoading({
        title: '提交中...'
      });
      
      apiMethod(url, this.stationForm)
        .then(res => {
          uni.hideLoading();
          if (res.code === 200) {
            uni.showToast({
              title: this.formType === 'add' ? '添加成功' : '更新成功'
            });
            this.closeStationForm();
            this.loadStations();
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
    
    // 切换服务点状态
    toggleStatus(station) {
      uni.showModal({
        title: '提示',
        content: station.status === 0 ? '确定要禁用该服务点吗？' : '确定要启用该服务点吗？',
        success: (res) => {
          if (res.confirm) {
            // 实际API调用
            uni.showLoading({
              title: '处理中...'
            });
            
            this.$request.put(`/api/admin/stations/${station.id}/status`, {
              status: station.status === 0 ? 1 : 0
            })
              .then(res => {
                uni.hideLoading();
                if (res.code === 200) {
                  uni.showToast({
                    title: station.status === 0 ? '禁用成功' : '启用成功'
                  });
                  this.loadStations();
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
    
    // 删除服务点
    handleDelete(station) {
      uni.showModal({
        title: '提示',
        content: '确定要删除该服务点吗？此操作不可恢复！',
        success: (res) => {
          if (res.confirm) {
            // 实际API调用
            uni.showLoading({
              title: '删除中...'
            });
            
            this.$request.delete(`/api/admin/stations/${station.id}`)
              .then(res => {
                uni.hideLoading();
                if (res.code === 200) {
                  uni.showToast({
                    title: '删除成功'
                  });
                  this.loadStations();
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
    },
    
    // 运行网络诊断
    runDiagnostics() {
      console.log('[服务点页面] 开始运行诊断工具');
      uni.showLoading({
        title: '正在诊断...'
      });
      
      this.$request.testConnection()
        .then(result => {
          uni.hideLoading();
          let diagnosisContent = '';
          let diagnosisTitle = '诊断结果';
          
          if (result.success) {
            if (result.statusCode === 200) {
              diagnosisTitle = '连接正常';
              diagnosisContent = `服务器连接正常\n状态码: ${result.statusCode}\n响应数据: ${JSON.stringify(result.data)}`;
            } else if (result.statusCode === 403) {
              diagnosisTitle = '连接正常但需要权限';
              diagnosisContent = `服务器连接有效，但健康检查端点返回403禁止访问\n这可能意味着健康检查需要授权，但不影响基本连接\n\n将继续尝试加载数据`;
            } else {
              diagnosisTitle = '连接异常';
              diagnosisContent = `服务器连接有效，但返回异常状态码: ${result.statusCode}\n响应数据: ${JSON.stringify(result.data)}`;
            }
            
            console.log('[服务点页面] 诊断信息', result);
            
            // 测试实际数据API
            this.testDataAPI(diagnosisTitle, diagnosisContent);
          } else {
            diagnosisTitle = '连接失败';
            diagnosisContent = `服务器连接失败\n错误: ${JSON.stringify(result.error)}\n\n请检查:\n1. 后端服务是否运行\n2. 网络连接是否正常\n3. API基础URL是否正确 (当前: ${this.$request.getBaseUrl ? this.$request.getBaseUrl() : 'http://localhost:8080'})`;
            
            console.error('[服务点页面] 诊断失败', result.error);
            
            uni.showModal({
              title: diagnosisTitle,
              content: diagnosisContent,
              showCancel: false
            });
          }
        });
    },
    
    // 测试数据API连接
    testDataAPI(prevTitle, prevContent) {
      uni.showLoading({
        title: '测试数据API...'
      });
      
      // 只请求第一页的一条数据用于测试
      const testParams = {
        page: 1,
        pageSize: 1
      };
      
      this.$request.get('/api/admin/stations', testParams)
        .then(res => {
          uni.hideLoading();
          let content = prevContent + '\n\n--- 数据API测试结果 ---\n';
          
          if (res.code === 200) {
            content += `数据API连接正常\n成功获取数据 (${res.data.total} 条记录可用)`;
          } else {
            content += `数据API返回错误\n错误码: ${res.code}\n错误信息: ${res.message}`;
            
            // 检查是否是已知的business_hours错误
            if (res.message && res.message.includes("Unknown column 'business_hours'")) {
              content += '\n\n检测到数据库结构错误: business_hours字段缺失\n需要联系后端开发人员修复Mapper文件和数据库结构不匹配的问题';
            }
          }
          
          uni.showModal({
            title: prevTitle,
            content: content,
            showCancel: false
          });
        })
        .catch(error => {
          uni.hideLoading();
          let content = prevContent + '\n\n--- 数据API测试结果 ---\n数据API请求失败\n';
          
          try {
            if (typeof error === 'object') {
              content += `错误详情: ${JSON.stringify(error)}`;
              
              // 检查是否是已知的business_hours错误
              if (error.message && error.message.includes("Unknown column 'business_hours'")) {
                content += '\n\n检测到数据库结构错误: business_hours字段缺失\n需要联系后端开发人员修复Mapper文件和数据库结构不匹配的问题';
              }
            } else {
              content += `错误信息: ${error}`;
            }
          } catch (e) {
            content += '无法解析错误详情';
          }
          
          uni.showModal({
            title: prevTitle,
            content: content,
            showCancel: false
          });
        });
    },
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

.header-actions {
  display: flex;
  align-items: center;
}

.diagnose-btn {
  background-color: #007aff;
  color: #fff;
  font-size: 24rpx;
  padding: 8rpx 20rpx;
  border-radius: 30rpx;
  margin-right: 20rpx;
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

.station-list {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 10rpx;
  margin-bottom: 30rpx;
}

.station-item {
  border-bottom: 1rpx solid #f5f5f5;
  padding: 20rpx;
}

.station-item:last-child {
  border-bottom: none;
}

.station-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20rpx;
}

.station-info {
  display: flex;
  flex: 1;
}

.station-logo {
  width: 100rpx;
  height: 100rpx;
  border-radius: 12rpx;
  margin-right: 20rpx;
}

.station-basic {
  flex: 1;
}

.station-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 8rpx;
}

.station-address {
  font-size: 28rpx;
  color: #666;
  margin-bottom: 10rpx;
}

.station-meta {
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

.station-status {
  font-size: 26rpx;
  padding: 6rpx 16rpx;
  border-radius: 30rpx;
  height: fit-content;
}

.status-normal {
  color: #3cc51f;
  background-color: rgba(60, 197, 31, 0.1);
}

.status-disabled {
  color: #ff5722;
  background-color: rgba(255, 87, 34, 0.1);
}

.station-details {
  background-color: #f9f9f9;
  border-radius: 8rpx;
  padding: 16rpx;
  margin-bottom: 20rpx;
}

.detail-row {
  display: flex;
  margin-bottom: 10rpx;
}

.detail-row:last-child {
  margin-bottom: 0;
}

.detail-item {
  flex: 1;
  display: flex;
  font-size: 26rpx;
}

.detail-label {
  width: 140rpx;
  color: #999;
}

.detail-value {
  flex: 1;
  color: #333;
}

.company-tags {
  display: flex;
  flex-wrap: wrap;
}

.company-tag {
  font-size: 24rpx;
  color: #007aff;
  background-color: rgba(0, 122, 255, 0.1);
  padding: 4rpx 12rpx;
  border-radius: 20rpx;
  margin-right: 10rpx;
  margin-bottom: 10rpx;
}

.station-photos {
  margin-bottom: 20rpx;
}

.photos-title {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 10rpx;
  display: block;
}

.photos-scroll {
  width: 100%;
  white-space: nowrap;
}

.photos-container {
  display: inline-flex;
}

.station-photo {
  width: 200rpx;
  height: 150rpx;
  border-radius: 8rpx;
  margin-right: 10rpx;
}

.station-actions {
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

.form-companies {
  flex: 1;
  display: flex;
  flex-wrap: wrap;
}

.company-tag-select {
  font-size: 24rpx;
  color: #666;
  background-color: #f5f5f5;
  padding: 8rpx 16rpx;
  border-radius: 20rpx;
  margin-right: 10rpx;
  margin-bottom: 10rpx;
  transition: all 0.3s;
}

.company-tag-select.selected {
  color: #007aff;
  background-color: rgba(0, 122, 255, 0.1);
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