<template>
	<view class="container">
		<view class="notice-detail">
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
				relatedNotices: []
			}
		},
		onLoad(options) {
			if (options.id) {
				this.id = options.id;
				this.getNoticeDetail();
				this.getRelatedNotices();
			}
		},
		methods: {
			// 获取公告详情
			getNoticeDetail() {
				// 模拟数据，实际项目中应该从API获取
				uni.showLoading({
					title: '加载中'
				});
				
				// 模拟网络请求延迟
				setTimeout(() => {
					// 模拟数据
					this.noticeData = {
						id: this.id,
						title: '重要通知：乡递通服务升级公告',
						content: '<div style="line-height: 1.8;">亲爱的乡递通用户：<br/><br/>为了提供更好的服务体验，我们将于2023年12月15日凌晨2:00-6:00进行系统升级维护。在此期间，您可能无法正常使用乡递通的部分功能。<br/><br/>此次升级将带来以下改进：<br/><ol><li>优化寄件流程，提高下单效率</li><li>增强物流追踪功能，物流信息更加透明</li><li>改进用户界面，带来更直观的操作体验</li><li>提升系统安全性，更好地保护用户信息</li></ol><br/>感谢您对乡递通的支持与理解！如有疑问，请联系客服热线：400-123-4567。<br/><br/>乡递通团队<br/>2023年12月10日</div>',
						createTime: '2023-12-10 10:00:00',
						source: '乡递通官方',
						viewCount: 1243
					};
					
					uni.hideLoading();
				}, 500);
				
				// 实际项目中的API调用
				/*
				uni.request({
					url: this.$api.notice.detail,
					method: 'GET',
					data: {
						id: this.id
					},
					success: (res) => {
						if (res.data.code === 200) {
							this.noticeData = res.data.data;
						} else {
							uni.showToast({
								title: res.data.message || '获取公告详情失败',
								icon: 'none'
							});
						}
					},
					fail: () => {
						uni.showToast({
							title: '网络错误，请稍后重试',
							icon: 'none'
						});
					},
					complete: () => {
						uni.hideLoading();
					}
				});
				*/
			},
			
			// 获取相关公告
			getRelatedNotices() {
				// 模拟数据
				setTimeout(() => {
					this.relatedNotices = [
						{
							id: '2',
							title: '乡递通春节期间配送调整通知',
							createTime: '2024-01-15 14:30:00'
						},
						{
							id: '3',
							title: '乡递通App新功能上线公告',
							createTime: '2023-11-20 09:15:00'
						},
						{
							id: '4',
							title: '乡递通配送范围扩展通知',
							createTime: '2023-10-05 16:45:00'
						}
					];
				}, 800);
				
				// 实际项目中的API调用
				/*
				uni.request({
					url: this.$api.notice.related,
					method: 'GET',
					data: {
						id: this.id,
						limit: 5
					},
					success: (res) => {
						if (res.data.code === 200) {
							this.relatedNotices = res.data.data;
						}
					}
				});
				*/
			},
			
			// 格式化日期
			formatDate(dateString) {
				if (!dateString) return '';
				const date = new Date(dateString.replace(/-/g, '/'));
				return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
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