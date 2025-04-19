<script>
export default {
  globalData: {
    isLoggedIn: false,
    userInfo: null
  },
  
  onLaunch: function() {
    console.log('App Launch');
    this.checkLoginStatus();
    
    // 添加页面跳转拦截器
    this.addInterceptor();
  },
  
  onShow: function() {
    console.log('App Show');
  },
  
  onHide: function() {
    console.log('App Hide');
  },
  
  methods: {
    // 检查登录状态
    checkLoginStatus() {
      try {
        const token = uni.getStorageSync('token');
        const userInfo = uni.getStorageSync('userInfo');
        
        if (token && userInfo) {
          this.globalData.isLoggedIn = true;
          this.globalData.userInfo = typeof userInfo === 'string' ? JSON.parse(userInfo) : userInfo;
          console.log('已登录', this.globalData.userInfo);
        } else {
          this.globalData.isLoggedIn = false;
          this.globalData.userInfo = null;
          console.log('未登录');
        }
      } catch (e) {
        console.error('检查登录状态失败', e);
        this.globalData.isLoggedIn = false;
        this.globalData.userInfo = null;
      }
    },
    
    // 添加页面跳转拦截器
    addInterceptor() {
      console.log('添加页面跳转拦截器');
      
      // 拦截页面跳转
      uni.addInterceptor('navigateTo', {
        invoke(args) {
          // 为所有导航添加超时设置
          if (!args.timeout) {
            args.timeout = 15000; // 15秒超时
          }
          
          // 记录跳转信息
          //console.log('navigateTo 请求:', args);
          
          // 如果是前往登录页，建议使用重定向
          if (args.url && args.url.includes('/pages/login/login')) {
            //console.log('检测到导航到登录页，建议使用redirectTo');
          }
          
          return args;
        },
        success(args) {
          // 记录跳转信息
          //console.log('navigateTo 成功:', args);
          return args;
        },
        fail(err) {
          console.error('navigateTo 失败:', err);
          
          // 如果是跳转到登录页失败，尝试使用reLaunch
          const pages = getCurrentPages();
          const currentPage = pages[pages.length - 1];
          
          if (err.errMsg && err.errMsg.includes('timeout') && 
              currentPage && currentPage.route === 'pages/index/index') {
            console.log('尝试使用备选方案打开登录页');
            
            // 延迟执行，避免冲突
            setTimeout(() => {
              uni.reLaunch({
                url: '/pages/login/login'
              });
            }, 500);
          }
          
          return err;
        }
      });
      
      // 拦截页面重定向
      uni.addInterceptor('redirectTo', {
        invoke(args) {
          // 为所有导航添加超时设置
          if (!args.timeout) {
            args.timeout = 15000; // 15秒超时
          }
          
          // 记录跳转信息
          console.log('redirectTo 请求:', args);
          return args;
        },
        success(args) {
          // 记录跳转信息
          console.log('redirectTo 成功:', args);
          return args;
        },
        fail(err) {
          console.error('redirectTo 失败:', err);
          
          // 如果重定向到登录页面失败，尝试使用reLaunch
          if (err.errMsg && err.errMsg.includes('timeout') && 
              err.url && err.url.includes('/pages/login/login')) {
            console.log('尝试使用备选方案打开登录页');
            
            // 延迟执行，避免冲突
            setTimeout(() => {
              uni.reLaunch({
                url: '/pages/login/login'
              });
            }, 500);
          }
          
          return err;
        }
      });
      
      // 拦截reLaunch
      uni.addInterceptor('reLaunch', {
        invoke(args) {
          // 为所有导航添加超时设置
          if (!args.timeout) {
            args.timeout = 15000; // 15秒超时
          }
          
          console.log('reLaunch 请求:', args);
          return args;
        },
        success(args) {
          console.log('reLaunch 成功:', args);
          return args;
        },
        fail(err) {
          console.error('reLaunch 失败:', err);
          return err;
        }
      });
    }
  }
};
</script>

<style>
/* 全局样式 */
page {
  font-family: 'PingFang SC', 'Helvetica Neue', Helvetica, 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
  min-height: 100%;
  display: flex;
  flex-direction: column;
}

.container {
  display: flex;
  flex-direction: column;
  padding: 30rpx;
}

.primary-button {
  background-color: #ff6600;
  color: white;
  border-radius: 10rpx;
  padding: 20rpx 0;
  text-align: center;
  margin: 20rpx 0;
}

.primary-button:active {
  opacity: 0.8;
}

.secondary-button {
  background-color: #f8f8f8;
  color: #333;
  border: 1rpx solid #ddd;
  border-radius: 10rpx;
  padding: 20rpx 0;
  text-align: center;
  margin: 20rpx 0;
}

.secondary-button:active {
  background-color: #eee;
}

.card {
  background-color: white;
  border-radius: 10rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
  padding: 30rpx;
  margin-bottom: 30rpx;
}

.form-item {
  margin-bottom: 30rpx;
}

.form-label {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 15rpx;
}

.form-input {
  background-color: #f8f8f8;
  padding: 20rpx;
  border-radius: 8rpx;
  font-size: 28rpx;
}
</style> 