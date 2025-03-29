<template>
	<view class="container">
		<!-- 错误提示 -->
		<view v-if="loadError" class="error-container">
			<uni-icons type="info" size="64" color="#FF6B35"></uni-icons>
			<text class="error-text">{{ errorMessage }}</text>
			<button class="back-button" @click="goBack">返回上一页</button>
		</view>
		
		<!-- 公告详情 -->
		<view v-else class="notice-detail">
			<view class="notice-header">
				<text class="notice-title">{{noticeData.title}}</text>
				<view class="notice-info">
					<text class="notice-time">发布时间：{{formatDate(noticeData.createTime)}}</text>
					<text class="notice-source">来源：{{noticeData.source || '乡递通'}}</text>
				</view>
			</view>
			<view class="notice-content">
				<rich-text :nodes="noticeData.content"></rich-text>
			</view>
			
			<view class="related-notices" v-if="relatedNotices.length > 0">
				<view class="section-title">相关公告</view>
				<view class="notice-list">
					<view 
						class="notice-item" 
						v-for="(item, index) in relatedNotices" 
						:key="index"
						@click="goToNoticeDetail(item.id)"
					>
						<text class="notice-item-title">{{item.title}}</text>
						<text class="notice-item-time">{{formatDate(item.createTime)}}</text>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
// 直接导入notice API
import noticeApi from '@/api/notice';

export default {
	data() {
		return {
			id: '',
			noticeData: {
				id: '',
				title: '',
				content: '',
				createTime: '',
				source: '乡递通',
				viewCount: 0
			},
			relatedNotices: [],
			loadError: false,
			errorMessage: ''
		}
	},
	onLoad(options) {
		// 添加调试信息
		console.log('API对象结构:', this.$api);
		console.log('Notice API结构:', this.$api.notice);
		console.log('直接导入的noticeApi:', noticeApi);
		
		if (options.id) {
			this.id = options.id;
			this.getNoticeDetail();
			this.getRelatedNotices();
		} else {
			this.setError('公告ID不存在');
		}
	},
	methods: {
		// 设置错误状态
		setError(message) {
			this.loadError = true;
			this.errorMessage = message || '加载公告失败';
		},
		
		// 返回上一页
		goBack() {
			uni.navigateBack();
		},
		
		// 获取公告详情
		getNoticeDetail() {
			uni.showLoading({
				title: '加载中'
			});
			
			// 使用直接导入的API模块
			noticeApi.detail(this.id).then(res => {
				if (res.code === 200) {
					this.noticeData = res.data;
				} else {
					// 处理错误
					this.setError(res.message || '获取公告详情失败');
					uni.showToast({
						title: res.message || '获取公告详情失败',
						icon: 'none'
					});
				}
			}).catch(err => {
				console.error('获取公告详情失败:', err);
				this.setError('网络错误，请稍后重试');
				uni.showToast({
					title: '网络错误，请稍后重试',
					icon: 'none'
				});
			}).finally(() => {
				uni.hideLoading();
			});
		},
		
		// 获取相关公告
		getRelatedNotices() {
			// 使用直接导入的API模块
			noticeApi.related({
				id: this.id,
				limit: 5
			}).then(res => {
				if (res.code === 200) {
					this.relatedNotices = res.data;
				}
			}).catch(err => {
				console.error('获取相关公告失败:', err);
			});
		},
		
		// 格式化日期
		formatDate(dateString) {
			if (!dateString) return '';
			try {
				// 尝试直接创建日期对象
				const date = new Date(dateString);
				
				// 检查日期是否有效
				if (isNaN(date.getTime())) {
					// 如果无效，尝试替换字符
					const convertedDate = new Date(dateString.replace(/-/g, '/'));
					
					// 如果仍然无效，返回原始字符串
					if (isNaN(convertedDate.getTime())) {
						console.warn('无法解析日期:', dateString);
						return dateString;
					}
					
					return this.formatDateObject(convertedDate);
				}
				
				return this.formatDateObject(date);
			} catch (error) {
				console.error('日期格式化错误:', error);
				return dateString;
			}
		},
		
		// 格式化日期对象
		formatDateObject(date) {
			const year = date.getFullYear();
			const month = String(date.getMonth() + 1).padStart(2, '0');
			const day = String(date.getDate()).padStart(2, '0');
			const hour = String(date.getHours()).padStart(2, '0');
			const minute = String(date.getMinutes()).padStart(2, '0');
			
			return `${year}-${month}-${day} ${hour}:${minute}`;
		},
		
		// 跳转到另一个公告详情
		goToNoticeDetail(id) {
			uni.navigateTo({
				url: `/pages/notice/detail?id=${id}`
			});
		}
	}
}
</script>

<style lang="scss">
.container {
	padding: 30rpx;
	background-color: #f8f8f8;
	min-height: 100vh;
}

.error-container {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 100rpx 50rpx;
	background-color: #fff;
	border-radius: 12rpx;
	box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
	min-height: 60vh;
}

.error-text {
	font-size: 32rpx;
	color: #666;
	margin: 40rpx 0;
	text-align: center;
}

.back-button {
	background: linear-gradient(to right, #FF6B35, #FF9A5A);
	color: #fff;
	font-size: 30rpx;
	padding: 20rpx 60rpx;
	border-radius: 50rpx;
	margin-top: 40rpx;
	box-shadow: 0 4rpx 16rpx rgba(255, 107, 53, 0.2);
}

.notice-detail {
	background-color: #fff;
	border-radius: 12rpx;
	padding: 30rpx;
	box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
}

.notice-header {
	border-bottom: 1px solid #eee;
	padding-bottom: 30rpx;
	margin-bottom: 30rpx;
}

.notice-title {
	font-size: 40rpx;
	font-weight: bold;
	color: #333;
	line-height: 1.4;
	margin-bottom: 20rpx;
	display: block;
}

.notice-info {
	display: flex;
	flex-direction: column;
	gap: 10rpx;
}

.notice-time, .notice-source {
	font-size: 24rpx;
	color: #999;
}

.notice-content {
	font-size: 30rpx;
	color: #333;
	line-height: 1.8;
	padding: 20rpx 0;
}

.related-notices {
	margin-top: 50rpx;
}

.section-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 30rpx;
	position: relative;
	padding-left: 20rpx;
	
	&::before {
		content: '';
		position: absolute;
		left: 0;
		top: 10rpx;
		height: 30rpx;
		width: 6rpx;
		background-color: #3cc51f;
		border-radius: 3rpx;
	}
}

.notice-list {
	background-color: #fff;
}

.notice-item {
	padding: 30rpx 0;
	border-bottom: 1px solid #eee;
	display: flex;
	flex-direction: column;
	gap: 15rpx;
	
	&:last-child {
		border-bottom: none;
	}
}

.notice-item-title {
	font-size: 30rpx;
	color: #333;
	line-height: 1.4;
}

.notice-item-time {
	font-size: 24rpx;
	color: #999;
}
</style> 