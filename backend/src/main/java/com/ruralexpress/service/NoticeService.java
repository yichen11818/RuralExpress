package com.ruralexpress.service;

import com.ruralexpress.entity.Notice;

import java.util.List;
import java.util.Map;

/**
 * 公告服务接口
 */
public interface NoticeService {

    /**
     * 获取公告详情
     *
     * @param id 公告ID
     * @return 公告信息
     */
    Notice getNoticeDetail(Long id);

    /**
     * 分页获取公告列表
     *
     * @param params 查询参数
     * @return 包含公告列表和总数的Map
     */
    Map<String, Object> getNoticeList(Map<String, Object> params);

    /**
     * 获取相关公告
     *
     * @param id    当前公告ID
     * @param limit 数量限制
     * @return 相关公告列表
     */
    List<Notice> getRelatedNotices(Long id, Integer limit);

    /**
     * 获取最新公告
     *
     * @param limit 数量限制
     * @return 最新公告列表
     */
    List<Notice> getLatestNotices(Integer limit);

    /**
     * 添加公告
     *
     * @param notice 公告信息
     * @return 是否成功
     */
    boolean addNotice(Notice notice);

    /**
     * 更新公告
     *
     * @param notice 公告信息
     * @return 是否成功
     */
    boolean updateNotice(Notice notice);

    /**
     * 删除公告
     *
     * @param id 公告ID
     * @return 是否成功
     */
    boolean deleteNotice(Long id);
} 