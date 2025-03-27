package com.ruralexpress.mapper;

import com.ruralexpress.entity.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 地址数据访问接口
 */
@Mapper
public interface AddressMapper {
    
    /**
     * 根据用户ID查询所有地址
     * @param userId 用户ID
     * @return 地址列表
     */
    List<Address> findByUserId(Long userId);
    
    /**
     * 根据ID查询地址
     * @param id 地址ID
     * @return 地址信息
     */
    Address findById(Long id);
    
    /**
     * 查询用户的默认地址
     * @param userId 用户ID
     * @return 默认地址
     */
    Address findDefaultByUserId(Long userId);
    
    /**
     * 新增地址
     * @param address 地址信息
     * @return 影响的行数
     */
    int insert(Address address);
    
    /**
     * 更新地址
     * @param address 地址信息
     * @return 影响的行数
     */
    int update(Address address);
    
    /**
     * 删除地址（逻辑删除）
     * @param id 地址ID
     * @param userId 用户ID（用于验证权限）
     * @return 影响的行数
     */
    int deleteById(@Param("id") Long id, @Param("userId") Long userId);
    
    /**
     * 将用户的所有地址设置为非默认
     * @param userId 用户ID
     * @return 影响的行数
     */
    int clearDefault(Long userId);
    
    /**
     * 设置默认地址
     * @param id 地址ID
     * @param userId 用户ID（用于验证权限）
     * @return 影响的行数
     */
    int setDefault(@Param("id") Long id, @Param("userId") Long userId);
} 