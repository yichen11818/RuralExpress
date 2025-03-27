package com.ruralexpress.controller;

import com.ruralexpress.dto.AddressDTO;
import com.ruralexpress.entity.Address;
import com.ruralexpress.service.AddressService;
import com.ruralexpress.utils.AuthUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 地址管理控制器
 */
@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;
    
    @Autowired
    private AuthUtil authUtil;
    
    /**
     * 获取当前用户的所有地址
     * @return 地址列表
     */
    @GetMapping
    public ResponseEntity<List<AddressDTO>> getUserAddresses() {
        // 获取当前登录用户的ID
        Long userId = authUtil.getCurrentUserId();
        
        // 查询用户的所有地址
        List<Address> addresses = addressService.getUserAddresses(userId);
        
        // 转换为DTO
        List<AddressDTO> addressDTOs = addresses.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(addressDTOs);
    }
    
    /**
     * 获取地址详情
     * @param id 地址ID
     * @return 地址详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<AddressDTO> getAddress(@PathVariable Long id) {
        // 获取当前登录用户的ID
        Long userId = authUtil.getCurrentUserId();
        
        // 查询地址
        Address address = addressService.getAddressById(id, userId);
        
        // 转换为DTO
        AddressDTO addressDTO = convertToDTO(address);
        
        return ResponseEntity.ok(addressDTO);
    }
    
    /**
     * 获取当前用户的默认地址
     * @return 默认地址
     */
    @GetMapping("/default")
    public ResponseEntity<AddressDTO> getDefaultAddress() {
        // 获取当前登录用户的ID
        Long userId = authUtil.getCurrentUserId();
        
        // 查询默认地址
        Address address = addressService.getDefaultAddress(userId);
        
        if (address == null) {
            return ResponseEntity.noContent().build();
        }
        
        // 转换为DTO
        AddressDTO addressDTO = convertToDTO(address);
        
        return ResponseEntity.ok(addressDTO);
    }
    
    /**
     * 添加新地址
     * @param addressDTO 地址信息
     * @return 添加后的地址
     */
    @PostMapping
    public ResponseEntity<AddressDTO> addAddress(@RequestBody AddressDTO addressDTO) {
        // 获取当前登录用户的ID
        Long userId = authUtil.getCurrentUserId();
        
        // 转换为实体
        Address address = convertToEntity(addressDTO);
        
        // 添加地址
        Address savedAddress = addressService.addAddress(address, userId);
        
        // 转换为DTO
        AddressDTO savedAddressDTO = convertToDTO(savedAddress);
        
        return ResponseEntity.ok(savedAddressDTO);
    }
    
    /**
     * 更新地址
     * @param id 地址ID
     * @param addressDTO 地址信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateAddress(@PathVariable Long id, @RequestBody AddressDTO addressDTO) {
        // 获取当前登录用户的ID
        Long userId = authUtil.getCurrentUserId();
        
        // 转换为实体
        Address address = convertToEntity(addressDTO);
        address.setId(id);
        
        // 更新地址
        boolean success = addressService.updateAddress(address, userId);
        
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 删除地址
     * @param id 地址ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        // 获取当前登录用户的ID
        Long userId = authUtil.getCurrentUserId();
        
        // 删除地址
        boolean success = addressService.deleteAddress(id, userId);
        
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 设置默认地址
     * @param id 地址ID
     * @return 设置结果
     */
    @PutMapping("/{id}/default")
    public ResponseEntity<Void> setDefaultAddress(@PathVariable Long id) {
        // 获取当前登录用户的ID
        Long userId = authUtil.getCurrentUserId();
        
        // 设置默认地址
        boolean success = addressService.setDefaultAddress(id, userId);
        
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 将实体转换为DTO
     * @param address 地址实体
     * @return 地址DTO
     */
    private AddressDTO convertToDTO(Address address) {
        if (address == null) {
            return null;
        }
        
        AddressDTO addressDTO = new AddressDTO();
        BeanUtils.copyProperties(address, addressDTO);
        
        // 转换Boolean类型
        addressDTO.setIsDefault(address.getIsDefault() != null && address.getIsDefault() == 1);
        
        return addressDTO;
    }
    
    /**
     * 将DTO转换为实体
     * @param addressDTO 地址DTO
     * @return 地址实体
     */
    private Address convertToEntity(AddressDTO addressDTO) {
        if (addressDTO == null) {
            return null;
        }
        
        Address address = new Address();
        BeanUtils.copyProperties(addressDTO, address);
        
        // 转换Boolean类型
        address.setIsDefault(addressDTO.getIsDefault() != null && addressDTO.getIsDefault() ? 1 : 0);
        
        return address;
    }
} 