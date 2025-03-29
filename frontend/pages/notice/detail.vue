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
				uni.showLoading({
					title: '加载中'
				});
				
				// 实际项目中的API调用
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
			},
			
			// 获取相关公告
			getRelatedNotices() {
				// 实际项目中的API调用
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