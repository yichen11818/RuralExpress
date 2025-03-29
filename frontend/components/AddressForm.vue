<template>
  <view class="address-form">
    <uni-forms ref="addressForm" :model="formData" :rules="rules" validate-trigger="submit" err-show-type="toast">
      <uni-forms-item label="联系人" name="name" required>
        <uni-easyinput v-model="formData.name" placeholder="请输入联系人姓名" />
      </uni-forms-item>
      
      <uni-forms-item label="手机号码" name="phone" required>
        <uni-easyinput v-model="formData.phone" placeholder="请输入联系人手机号码" />
      </uni-forms-item>
      
      <uni-forms-item label="所在地区" name="region" required>
        <picker mode="region" @change="handleRegionChange" :value="formData.region">
          <view class="picker-view">
            <text v-if="formData.province && formData.city && formData.district">
              {{ formData.province + ' ' + formData.city + ' ' + formData.district }}
            </text>
            <text v-else class="placeholder">请选择所在地区</text>
            <text class="arrow">></text>
          </view>
        </picker>
      </uni-forms-item>
      
      <uni-forms-item label="详细地址" name="detailAddress" required>
        <uni-easyinput 
          type="textarea" 
          v-model="formData.detailAddress" 
          placeholder="请输入详细地址，如街道、门牌号、小区、楼栋号、单元室等" 
          :maxlength="100"
        />
      </uni-forms-item>
      
      <uni-forms-item label="地址类型" name="addressType">
        <view class="tag-group">
          <view 
            class="tag-item" 
            :class="{ active: formData.addressType === '家' }" 
            @click="formData.addressType = '家'"
          >
            家
          </view>
          <view 
            class="tag-item" 
            :class="{ active: formData.addressType === '公司' }" 
            @click="formData.addressType = '公司'"
          >
            公司
          </view>
          <view 
            class="tag-item" 
            :class="{ active: formData.addressType === '学校' }" 
            @click="formData.addressType = '学校'"
          >
            学校
          </view>
          <view 
            class="tag-item custom" 
            :class="{ active: !['家', '公司', '学校'].includes(formData.addressType) }" 
            @click="showCustomTag"
          >
            {{ customTag || '自定义' }}
          </view>
        </view>
      </uni-forms-item>
      
      <uni-forms-item name="isDefault">
        <view class="default-switch">
          <text>设为默认地址</text>
          <switch :checked="formData.isDefault" @change="(e) => formData.isDefault = e.detail.value" color="#ff6600" />
        </view>
      </uni-forms-item>
      
      <view class="form-actions">
        <button class="submit-btn" @click="submitForm">保存</button>
      </view>
    </uni-forms>
    
    <!-- 自定义标签弹窗 -->
    <uni-popup ref="customTagPopup" type="dialog">
      <uni-popup-dialog 
        title="自定义地址标签" 
        mode="input" 
        placeholder="请输入自定义标签，如'公园'、'健身房'等" 
        :value="customTag"
        @confirm="confirmCustomTag"
      ></uni-popup-dialog>
    </uni-popup>
  </view>
</template>

<script>
export default {
  name: 'AddressForm',
  props: {
    // 用于编辑时传入的地址数据
    address: {
      type: Object,
      default: () => ({})
    },
    // 操作类型：add（添加）或 edit（编辑）
    type: {
      type: String,
      default: 'add'
    }
  },
  data() {
    return {
      formData: {
        id: '',
        name: '',
        phone: '',
        province: '',
        city: '',
        district: '',
        region: [], // 用于 picker 数据绑定
        detailAddress: '',
        addressType: '家', // 默认家
        isDefault: false
      },
      customTag: '',
      rules: {
        name: {
          rules: [
            {
              required: true,
              errorMessage: '请输入联系人姓名'
            }
          ]
        },
        phone: {
          rules: [
            {
              required: true,
              errorMessage: '请输入联系人手机号码'
            },
            {
              pattern: /^1[3-9]\d{9}$/,
              errorMessage: '手机号码格式不正确'
            }
          ]
        },
        region: {
          rules: [
            {
              required: true,
              errorMessage: '请选择所在地区'
            }
          ]
        },
        detailAddress: {
          rules: [
            {
              required: true,
              errorMessage: '请输入详细地址'
            }
          ]
        }
      }
    }
  },
  created() {
    // 编辑模式下，初始化表单数据
    if (this.type === 'edit' && this.address) {
      this.initFormData();
    }
  },
  methods: {
    initFormData() {
      const address = this.address;
      
      // 将数字类型的地址类型转换为文本显示
      const addressType = this.getAddressTypeText(address.addressType);
      
      this.formData = {
        id: address.id || '',
        name: address.name || '',
        phone: address.phone || '',
        province: address.province || '',
        city: address.city || '',
        district: address.district || '',
        region: address.province && address.city && address.district ? 
          [address.province, address.city, address.district] : [],
        detailAddress: address.detailAddress || '',
        addressType: addressType,
        isDefault: !!address.isDefault
      };
      
      // 如果是自定义标签，设置 customTag
      if (!['家', '公司', '学校'].includes(addressType)) {
        this.customTag = addressType;
      }
    },
    
    handleRegionChange(e) {
      const region = e.detail.value;
      this.formData.region = region;
      this.formData.province = region[0];
      this.formData.city = region[1];
      this.formData.district = region[2];
    },
    
    showCustomTag() {
      this.$refs.customTagPopup.open();
    },
    
    confirmCustomTag(val) {
      this.customTag = val;
      this.formData.addressType = val;
    },
    
    submitForm() {
      this.$refs.addressForm.validate().then(res => {
        // 表单验证通过
        const addressData = {
          ...this.formData,
          // 确保地址类型字段格式为数字
          addressType: this.getAddressTypeValue(this.formData.addressType)
        };
        
        // 发送事件，将表单数据传递给父组件
        this.$emit('submit', addressData);
      }).catch(err => {
        console.error('表单验证失败', err);
      });
    },
    
    // 将地址类型文本转换为数值
    getAddressTypeValue(type) {
      // 根据后端API要求转换类型
      // 假设后端需要的是：0-家庭，1-公司，2-学校，3-其他
      const typeMap = {
        '家': 0,
        '公司': 1,
        '学校': 2
      };
      return typeMap[type] !== undefined ? typeMap[type] : 3;
    },
    
    // 将数字类型转换为显示文本
    getAddressTypeText(type) {
      // 数字类型转文字
      const typeMap = {
        0: '家',
        1: '公司',
        2: '学校'
      };
      
      // 如果是数字类型
      if (typeof type === 'number') {
        return typeMap[type] || '其他';
      }
      
      // 如果已经是字符串，且是有效值，则直接返回
      if (typeof type === 'string' && ['家', '公司', '学校'].includes(type)) {
        return type;
      }
      
      // 其他情况返回默认值
      return '家';
    }
  }
}
</script>

<style lang="scss">
.address-form {
  padding: 20rpx;
  
  .picker-view {
    width: 100%;
    height: 70rpx;
    line-height: 70rpx;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1rpx solid #eee;
    
    .placeholder {
      color: #bbb;
    }
    
    .arrow {
      color: #ccc;
      font-size: 32rpx;
    }
  }
  
  .tag-group {
    display: flex;
    flex-wrap: wrap;
    
    .tag-item {
      padding: 10rpx 30rpx;
      background-color: #f5f5f5;
      border-radius: 30rpx;
      margin-right: 20rpx;
      margin-bottom: 20rpx;
      font-size: 26rpx;
      color: #666;
      
      &.active {
        background-color: #ff6600;
        color: #fff;
      }
      
      &.custom {
        border: 1rpx dashed #ddd;
        background-color: #fff;
        
        &.active {
          background-color: #ff6600;
          border-color: #ff6600;
          color: #fff;
        }
      }
    }
  }
  
  .default-switch {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 80rpx;
  }
  
  .form-actions {
    margin-top: 60rpx;
    padding: 0 20rpx;
    
    .submit-btn {
      width: 100%;
      height: 80rpx;
      line-height: 80rpx;
      background-color: #ff6600;
      color: #fff;
      border-radius: 40rpx;
      font-size: 30rpx;
    }
  }
}
</style> 