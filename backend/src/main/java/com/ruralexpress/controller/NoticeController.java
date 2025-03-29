package com.ruralexpress.controller;

import com.ruralexpress.entity.Notice;
import com.ruralexpress.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公告控制器
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 获取公告详情
     *
     * @param id 公告ID
     * @return 响应结果
     */
    @GetMapping("/detail")
    public ResponseEntity<Map<String, Object>> getNoticeDetail(@RequestParam Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Notice notice = noticeService.getNoticeDetail(id);
            if (notice != null) {
                result.put("code", 200);
                result.put("message", "获取成功");
                result.put("data", notice);
                return ResponseEntity.ok(result);
            } else {
                result.put("code", 404);
                result.put("message", "公告不存在");
                return ResponseEntity.ok(result);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "服务器错误: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 获取公告列表
     *
     * @param page     页码
     * @param size     每页数量
     * @param keyword  关键词
     * @param category 分类
     * @return 响应结果
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getNoticeList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("page", page);
            params.put("size", size);
            params.put("keyword", keyword);
            params.put("category", category);
            
            Map<String, Object> data = noticeService.getNoticeList(params);
            
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", data);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "服务器错误: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 获取相关公告
     *
     * @param id    当前公告ID
     * @param limit 数量限制
     * @return 响应结果
     */
    @GetMapping("/related")
    public ResponseEntity<Map<String, Object>> getRelatedNotices(
            @RequestParam Long id,
            @RequestParam(defaultValue = "5") Integer limit) {
        
        Map<String, Object> result = new HashMap<>();
        try {
            List<Notice> relatedNotices = noticeService.getRelatedNotices(id, limit);
            
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", relatedNotices);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "服务器错误: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 获取最新公告
     *
     * @param limit 数量限制
     * @return 响应结果
     */
    @GetMapping("/latest")
    public ResponseEntity<Map<String, Object>> getLatestNotices(
            @RequestParam(defaultValue = "5") Integer limit) {
        
        Map<String, Object> result = new HashMap<>();
        try {
            List<Notice> latestNotices = noticeService.getLatestNotices(limit);
            
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", latestNotices);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "服务器错误: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 添加公告
     *
     * @param notice 公告信息
     * @return 响应结果
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addNotice(@RequestBody Notice notice) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = noticeService.addNotice(notice);
            
            if (success) {
                result.put("code", 200);
                result.put("message", "添加成功");
            } else {
                result.put("code", 400);
                result.put("message", "添加失败");
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "服务器错误: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 更新公告
     *
     * @param notice 公告信息
     * @return 响应结果
     */
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateNotice(@RequestBody Notice notice) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (notice.getId() == null) {
                result.put("code", 400);
                result.put("message", "缺少公告ID");
                return ResponseEntity.ok(result);
            }
            
            boolean success = noticeService.updateNotice(notice);
            
            if (success) {
                result.put("code", 200);
                result.put("message", "更新成功");
            } else {
                result.put("code", 400);
                result.put("message", "更新失败，公告可能不存在");
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "服务器错误: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 删除公告
     *
     * @param id 公告ID
     * @return 响应结果
     */
    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteNotice(@RequestParam Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = noticeService.deleteNotice(id);
            
            if (success) {
                result.put("code", 200);
                result.put("message", "删除成功");
            } else {
                result.put("code", 400);
                result.put("message", "删除失败，公告可能不存在");
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "服务器错误: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }
} 