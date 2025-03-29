package com.ruralexpress.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruralexpress.entity.Company;
import org.apache.ibatis.annotations.Mapper;

/**
 * 快递公司Mapper接口
 */
@Mapper
public interface CompanyMapper extends BaseMapper<Company> {
} 