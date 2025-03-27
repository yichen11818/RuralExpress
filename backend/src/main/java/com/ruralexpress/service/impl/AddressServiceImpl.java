package com.ruralexpress.service.impl;

import com.ruralexpress.entity.Address;
import com.ruralexpress.exception.BusinessException;
import com.ruralexpress.mapper.AddressMapper;
import com.ruralexpress.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 地址服务实现类
 */
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public List<Address> getUserAddresses(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        return addressMapper.findByUserId(userId);
    }

    @Override
    public Address getAddressById(Long id, Long userId) {
        if (id == null) {
            throw new BusinessException("地址ID不能为空");
        }
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        Address address = addressMapper.findById(id);
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        
        // 验证地址所有权
        if (!address.getUserId().equals(userId)) {
            throw new BusinessException("无权访问此地址");
        }
        
        return address;
    }

    @Override
    public Address getDefaultAddress(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        return addressMapper.findDefaultByUserId(userId);
    }

    @Override
    @Transactional
    public Address addAddress(Address address, Long userId) {
        if (address == null) {
            throw new BusinessException("地址信息不能为空");
        }
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        // 验证必填字段
        validateAddress(address);
        
        // 设置用户ID和时间
        address.setUserId(userId);
        address.setCreateTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());
        address.setIsDeleted(0);
        
        // 如果是默认地址，需要先清除其他默认地址
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            addressMapper.clearDefault(userId);
        }
        
        // 添加地址
        addressMapper.insert(address);
        
        return address;
    }

    @Override
    @Transactional
    public boolean updateAddress(Address address, Long userId) {
        if (address == null || address.getId() == null) {
            throw new BusinessException("地址信息不完整");
        }
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        // 验证地址所有权
        Address existingAddress = getAddressById(address.getId(), userId);
        if (existingAddress == null) {
            throw new BusinessException("地址不存在或无权修改");
        }
        
        // 验证必填字段
        validateAddress(address);
        
        // 设置不变的字段
        address.setUserId(userId);
        address.setUpdateTime(LocalDateTime.now());
        address.setIsDeleted(0);
        
        // 如果是默认地址，需要先清除其他默认地址
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            addressMapper.clearDefault(userId);
        }
        
        // 更新地址
        return addressMapper.update(address) > 0;
    }

    @Override
    @Transactional
    public boolean deleteAddress(Long id, Long userId) {
        if (id == null) {
            throw new BusinessException("地址ID不能为空");
        }
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        // 验证地址是否存在
        Address existingAddress = getAddressById(id, userId);
        if (existingAddress == null) {
            throw new BusinessException("地址不存在或无权删除");
        }
        
        // 删除地址
        return addressMapper.deleteById(id, userId) > 0;
    }

    @Override
    @Transactional
    public boolean setDefaultAddress(Long id, Long userId) {
        if (id == null) {
            throw new BusinessException("地址ID不能为空");
        }
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        // 验证地址是否存在
        Address existingAddress = getAddressById(id, userId);
        if (existingAddress == null) {
            throw new BusinessException("地址不存在或无权操作");
        }
        
        // 清除其他默认地址
        addressMapper.clearDefault(userId);
        
        // 设置默认地址
        return addressMapper.setDefault(id, userId) > 0;
    }
    
    /**
     * 验证地址必填字段
     * @param address 地址信息
     */
    private void validateAddress(Address address) {
        if (!StringUtils.hasText(address.getName())) {
            throw new BusinessException("联系人姓名不能为空");
        }
        if (!StringUtils.hasText(address.getPhone())) {
            throw new BusinessException("联系人电话不能为空");
        }
        if (!StringUtils.hasText(address.getProvince()) || 
            !StringUtils.hasText(address.getCity()) || 
            !StringUtils.hasText(address.getDistrict())) {
            throw new BusinessException("所在地区不能为空");
        }
        if (!StringUtils.hasText(address.getDetailAddress())) {
            throw new BusinessException("详细地址不能为空");
        }
        
        // 如果未设置地址类型，默认为"其他"
        if (!StringUtils.hasText(address.getAddressType())) {
            address.setAddressType("其他");
        }
        
        // 如果未设置是否默认，默认为非默认
        if (address.getIsDefault() == null) {
            address.setIsDefault(0);
        }
    }
} 