package com.ruralexpress.service.impl;

import com.ruralexpress.entity.Notice;
import com.ruralexpress.mapper.NoticeMapper;
import com.ruralexpress.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公告服务实现类
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    public Notice getNoticeDetail(Long id) {
        // 增加浏览次数
        noticeMapper.incrementViewCount(id);
        // 返回公告详情
        return noticeMapper.selectById(id);
    }

    @Override
    public Map<String, Object> getNoticeList(Map<String, Object> params) {
        String keyword = params.get("keyword") != null ? params.get("keyword").toString() : null;
        String category = params.get("category") != null ? params.get("category").toString() : null;
        
        // 获取分页参数
        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        int size = params.get("size") != null ? Integer.parseInt(params.get("size").toString()) : 10;
        int offset = (page - 1) * size;
        
        // 查询数据
        List<Notice> list = noticeMapper.selectList(keyword, category, offset, size);
        int total = noticeMapper.selectCount(keyword, category);
        
        // 封装结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        
        return result;
    }

    @Override
    public List<Notice> getRelatedNotices(Long id, Integer limit) {
        return noticeMapper.selectRelated(id, limit);
    }

    @Override
    public List<Notice> getLatestNotices(Integer limit) {
        return noticeMapper.selectLatest(limit);
    }

    @Override
    public boolean addNotice(Notice notice) {
        LocalDateTime now = LocalDateTime.now();
        notice.setCreateTime(now);
        notice.setUpdateTime(now);
        notice.setViewCount(0);
        
        return noticeMapper.insert(notice) > 0;
    }

    @Override
    public boolean updateNotice(Notice notice) {
        notice.setUpdateTime(LocalDateTime.now());
        return noticeMapper.update(notice) > 0;
    }

    @Override
    public boolean deleteNotice(Long id) {
        return noticeMapper.deleteById(id) > 0;
    }
} 