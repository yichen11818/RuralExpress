<template>
  <view class="rating-component">
    <view class="rating-title">{{ title }}</view>
    
    <!-- 星级评分 -->
    <view class="rating-stars">
      <view class="stars-label">{{ starsLabel }}</view>
      <uni-rate 
        v-model="ratingData.score" 
        :size="30" 
        :color="'#bbb'" 
        :active-color="'#ff6600'" 
        :is-fill="true" 
        :value="ratingData.score" 
        @change="handleRateChange"
      />
      <text class="score-text">{{ ratingScoreText }}</text>
    </view>
    
    <!-- 快递员评价时显示的标签选择 -->
    <view class="rating-tags" v-if="type === 'courier'">
      <view 
        v-for="(tag, index) in tags" 
        :key="index" 
        class="tag-item" 
        :class="{ active: ratingData.tags.includes(tag.value) }"
        @click="toggleTag(tag.value)"
      >
        {{ tag.label }}
      </view>
    </view>
    
    <!-- 评价内容 -->
    <view class="rating-content">
      <uni-easyinput
        type="textarea"
        v-model="ratingData.content"
        placeholder="请输入您的评价内容，分享您的体验或建议..."
        :maxlength="200"
        :autoHeight="true"
      />
      <text class="content-count">{{ ratingData.content.length }}/200</text>
    </view>
    
    <!-- 图片上传 -->
    <view class="rating-images">
      <uni-file-picker
        v-model="ratingData.images"
        file-mediatype="image"
        mode="grid"
        limit="3"
        @select="selectImages"
        @delete="deleteImage"
      ></uni-file-picker>
    </view>
    
    <!-- 匿名评价选项 -->
    <view class="rating-anonymous">
      <text>匿名评价</text>
      <switch 
        :checked="ratingData.isAnonymous" 
        @change="(e) => ratingData.isAnonymous = e.detail.value"
        color="#ff6600"
      />
    </view>
    
    <!-- 提交按钮 -->
    <view class="rating-actions">
      <button 
        class="submit-btn" 
        :disabled="ratingData.score === 0" 
        :class="{ disabled: ratingData.score === 0 }"
        @click="submitRating"
      >
        提交评价
      </button>
    </view>
  </view>
</template>

<script>
export default {
  name: 'RatingComponent',
  props: {
    // 评价类型：order（订单）或 courier（快递员）
    type: {
      type: String,
      default: 'order'
    },
    // 评价对象ID（订单ID或快递员ID）
    targetId: {
      type: String,
      required: true
    },
    // 自定义标题
    title: {
      type: String,
      default: '评价'
    },
    // 星级评分标签
    starsLabel: {
      type: String,
      default: '服务评分'
    }
  },
  data() {
    return {
      ratingData: {
        score: 5, // 默认5星
        content: '',
        tags: [],
        images: [],
        isAnonymous: false,
        targetId: this.targetId,
        targetType: this.type
      },
      // 快递员评价标签
      tags: [
        { label: '服务态度好', value: 'attitude' },
        { label: '按时送达', value: 'onTime' },
        { label: '包裹完好', value: 'intact' },
        { label: '配送速度快', value: 'fast' },
        { label: '着装整洁', value: 'neat' },
        { label: '很有礼貌', value: 'polite' }
      ]
    }
  },
  computed: {
    ratingScoreText() {
      const scoreTexts = [
        '',
        '非常差',
        '差',
        '一般',
        '好',
        '非常好'
      ];
      return scoreTexts[this.ratingData.score] || '';
    }
  },
  methods: {
    handleRateChange(score) {
      this.ratingData.score = score;
    },
    
    toggleTag(tagValue) {
      const index = this.ratingData.tags.indexOf(tagValue);
      if (index > -1) {
        // 如果标签已存在，则移除
        this.ratingData.tags.splice(index, 1);
      } else {
        // 如果标签不存在，则添加
        this.ratingData.tags.push(tagValue);
      }
    },
    
    selectImages(e) {
      // uni-file-picker 组件选择图片后的回调
      this.ratingData.images = e.tempFilePaths || [];
    },
    
    deleteImage(e) {
      // 删除图片
      const index = e.index;
      if (index !== undefined && this.ratingData.images.length > index) {
        this.ratingData.images.splice(index, 1);
      }
    },
    
    submitRating() {
      if (this.ratingData.score === 0) {
        uni.showToast({
          title: '请至少选择一颗星',
          icon: 'none'
        });
        return;
      }
      
      // 发射评价提交事件
      this.$emit('submit', {
        ...this.ratingData,
        // 处理标签，将标签数组转换为标签文本数组
        tagTexts: this.ratingData.tags.map(tagValue => {
          const tag = this.tags.find(t => t.value === tagValue);
          return tag ? tag.label : '';
        })
      });
      
      // 清空评价数据，准备下一次评价
      this.resetRatingData();
    },
    
    resetRatingData() {
      this.ratingData = {
        score: 5,
        content: '',
        tags: [],
        images: [],
        isAnonymous: false,
        targetId: this.targetId,
        targetType: this.type
      };
    }
  }
}
</script>

<style lang="scss">
.rating-component {
  background-color: #fff;
  padding: 30rpx;
  border-radius: 12rpx;
  
  .rating-title {
    font-size: 32rpx;
    font-weight: bold;
    margin-bottom: 30rpx;
    color: #333;
  }
  
  .rating-stars {
    display: flex;
    align-items: center;
    margin-bottom: 30rpx;
    
    .stars-label {
      font-size: 28rpx;
      color: #666;
      margin-right: 20rpx;
      min-width: 120rpx;
    }
    
    .score-text {
      font-size: 26rpx;
      color: #ff6600;
      margin-left: 20rpx;
    }
  }
  
  .rating-tags {
    display: flex;
    flex-wrap: wrap;
    margin-bottom: 30rpx;
    
    .tag-item {
      padding: 10rpx 20rpx;
      background-color: #f5f5f5;
      border-radius: 30rpx;
      margin-right: 20rpx;
      margin-bottom: 20rpx;
      font-size: 24rpx;
      color: #666;
      
      &.active {
        background-color: #fff5e6;
        color: #ff6600;
        border: 1rpx solid #ff6600;
      }
    }
  }
  
  .rating-content {
    margin-bottom: 30rpx;
    position: relative;
    
    .content-count {
      position: absolute;
      right: 10rpx;
      bottom: 10rpx;
      font-size: 24rpx;
      color: #999;
    }
  }
  
  .rating-images {
    margin-bottom: 30rpx;
  }
  
  .rating-anonymous {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 80rpx;
    margin-bottom: 30rpx;
    font-size: 28rpx;
    color: #666;
  }
  
  .rating-actions {
    .submit-btn {
      width: 100%;
      height: 80rpx;
      line-height: 80rpx;
      background-color: #ff6600;
      color: #fff;
      border-radius: 40rpx;
      font-size: 30rpx;
      
      &.disabled {
        background-color: #ccc;
        color: #fff;
      }
    }
  }
}
</style> 