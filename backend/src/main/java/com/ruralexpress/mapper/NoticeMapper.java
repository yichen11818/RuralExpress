package com.ruralexpress.mapper;

import com.ruralexpress.entity.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 公告Mapper接口
 */
@Mapper
public interface NoticeMapper {

    /**
     * 根据ID查询公告
     *
     * @param id 公告ID
     * @return 公告信息
     */
    Notice selectById(Long id);

    /**
     * 获取公告列表
     *
     * @param keyword  关键词
     * @param category 分类
     * @param offset   偏移量
     * @param limit    数量限制
     * @return 公告列表
     */
    List<Notice> selectList(@Param("keyword") String keyword,
                           @Param("category") String category,
                           @Param("offset") Integer offset,
                           @Param("limit") Integer limit);

    /**
     * 获取公告总数
     *
     * @param keyword  关键词
     * @param category 分类
     * @return 公告总数
     */
    int selectCount(@Param("keyword") String keyword, @Param("category") String category);

    /**
     * 获取相关公告
     *
     * @param id    当前公告ID
     * @param limit 数量限制
     * @return 相关公告列表
     */
    List<Notice> selectRelated(@Param("id") Long id, @Param("limit") Integer limit);

    /**
     * 获取最新公告
     *
     * @param limit 数量限制
     * @return 最新公告列表
     */
    List<Notice> selectLatest(@Param("limit") Integer limit);

    /**
     * 增加浏览次数
     *
     * @param id 公告ID
     * @return 影响行数
     */
    int incrementViewCount(Long id);

    /**
     * 新增公告
     *
     * @param notice 公告信息
     * @return 影响行数
     */
    int insert(Notice notice);

    /**
     * 更新公告
     *
     * @param notice 公告信息
     * @return 影响行数
     */
    int update(Notice notice);

    /**
     * 删除公告
     *
     * @param id 公告ID
     * @return 影响行数
     */
    int deleteById(Long id);
} 