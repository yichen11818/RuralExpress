package com.ruralexpress.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruralexpress.entity.CourierTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 快递员标签 Mapper 接口
 */
@Mapper
public interface CourierTagMapper extends BaseMapper<CourierTag> {
    
    /**
     * 查询快递员的所有标签
     * @param courierId 快递员ID
     * @return 标签列表
     */
    @Select("SELECT * FROM t_courier_tag WHERE courier_id = #{courierId} ORDER BY count DESC, tag_type ASC")
    List<CourierTag> findByCourierId(@Param("courierId") Long courierId);
    
    /**
     * 查询快递员的系统标签
     * @param courierId 快递员ID
     * @return 标签列表
     */
    @Select("SELECT * FROM t_courier_tag WHERE courier_id = #{courierId} AND tag_type = 0 ORDER BY count DESC")
    List<CourierTag> findSystemTagsByCourierId(@Param("courierId") Long courierId);
    
    /**
     * 查询快递员的用户评价标签
     * @param courierId 快递员ID
     * @return 标签列表
     */
    @Select("SELECT * FROM t_courier_tag WHERE courier_id = #{courierId} AND tag_type = 1 ORDER BY count DESC")
    List<CourierTag> findUserTagsByCourierId(@Param("courierId") Long courierId);
    
    /**
     * 增加标签计数
     * @param courierId 快递员ID
     * @param tagName 标签内容
     * @return 影响行数
     */
    @Update("UPDATE t_courier_tag SET count = count + 1 WHERE courier_id = #{courierId} AND tag_name = #{tagName}")
    int incrementTagCount(@Param("courierId") Long courierId, @Param("tagName") String tagName);
    
    /**
     * 批量插入标签
     * @param tags 标签列表
     * @return 影响行数
     */
    @Select("<script>" +
            "INSERT INTO t_courier_tag (courier_id, tag_name, tag_type, count, created_at, updated_at) VALUES " +
            "<foreach collection='tags' item='tag' separator=','>" +
            "(#{tag.courierId}, #{tag.tagName}, #{tag.tagType}, #{tag.count}, NOW(), NOW())" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("tags") List<CourierTag> tags);
} 