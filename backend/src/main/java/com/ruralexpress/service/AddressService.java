package com.ruralexpress.service;

import com.ruralexpress.entity.Address;

import java.util.List;

/**
 * 地址服务接口
 */
public interface AddressService {
    
    /**
     * 获取用户的所有地址
     * @param userId 用户ID
     * @return 地址列表
     */
    List<Address> getUserAddresses(Long userId);
    
    /**
     * 获取地址详情
     * @param id 地址ID
     * @param userId 用户ID（用于权限验证）
     * @return 地址信息
     */
    Address getAddressById(Long id, Long userId);
    
    /**
     * 获取用户的默认地址
     * @param userId 用户ID
     * @return 默认地址，如果没有则返回null
     */
    Address getDefaultAddress(Long userId);
    
    /**
     * 添加新地址
     * @param address 地址信息
     * @param userId 用户ID
     * @return 添加后的地址（包含ID）
     */
    Address addAddress(Address address, Long userId);
    
    /**
     * 更新地址
     * @param address 地址信息
     * @param userId 用户ID（用于权限验证）
     * @return 是否成功
     */
    boolean updateAddress(Address address, Long userId);
    
    /**
     * 删除地址
     * @param id 地址ID
     * @param userId 用户ID（用于权限验证）
     * @return 是否成功
     */
    boolean deleteAddress(Long id, Long userId);
    
    /**
     * 设置默认地址
     * @param id 地址ID
     * @param userId 用户ID（用于权限验证）
     * @return 是否成功
     */
    boolean setDefaultAddress(Long id, Long userId);
} 