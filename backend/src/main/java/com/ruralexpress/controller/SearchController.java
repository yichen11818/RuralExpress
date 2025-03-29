package com.ruralexpress.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruralexpress.entity.Package;
import com.ruralexpress.entity.Order;
import com.ruralexpress.service.PackageService;
import com.ruralexpress.service.OrderService;
import com.ruralexpress.service.CourierService;
import com.ruralexpress.service.StationService;

import java.util.*;

/**
 * 搜索控制器
 */
@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private PackageService packageService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private CourierService courierService;
    
    @Autowired
    private StationService stationService;

    /**
     * 搜索包裹
     *
     * @param keyword  关键词
     * @param page     页码
     * @param pageSize 每页数量
     * @return 响应结果
     */
    @GetMapping("/packages")
    public ResponseEntity<Map<String, Object>> searchPackages(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 使用真实数据搜索包裹
            List<Map<String, Object>> packages = packageService.searchPackages(keyword, 50);
            
            // 分页处理
            int total = packages.size();
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, total);
            
            List<Map<String, Object>> pagedList = total > 0 ? packages.subList(start, end) : new ArrayList<>();
            
            Map<String, Object> data = new HashMap<>();
            data.put("list", pagedList);
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);
            data.put("totalPage", (int) Math.ceil((double) total / pageSize));
            
            result.put("success", true);
            result.put("code", 200);
            result.put("message", "搜索成功");
            result.put("data", data);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("code", 500);
            result.put("message", "搜索失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }
    
    /**
     * 搜索订单
     */
    @GetMapping("/orders")
    public ResponseEntity<Map<String, Object>> searchOrders(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 使用真实数据搜索订单
            List<Map<String, Object>> orders = orderService.searchOrders(keyword, 50);
            
            // 分页处理
            int total = orders.size();
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, total);
            
            List<Map<String, Object>> pagedList = total > 0 ? orders.subList(start, end) : new ArrayList<>();
            
            Map<String, Object> data = new HashMap<>();
            data.put("list", pagedList);
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);
            data.put("totalPage", (int) Math.ceil((double) total / pageSize));
            
            result.put("success", true);
            result.put("code", 200);
            result.put("message", "搜索成功");
            result.put("data", data);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("code", 500);
            result.put("message", "搜索失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }
    
    /**
     * 搜索快递员
     */
    @GetMapping("/couriers")
    public ResponseEntity<Map<String, Object>> searchCouriers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 使用真实数据搜索快递员
            List<Map<String, Object>> couriers = courierService.searchCouriers(keyword, 50);
            
            // 分页处理
            int total = couriers.size();
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, total);
            
            List<Map<String, Object>> pagedList = total > 0 ? couriers.subList(start, end) : new ArrayList<>();
            
            Map<String, Object> data = new HashMap<>();
            data.put("list", pagedList);
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);
            data.put("totalPage", (int) Math.ceil((double) total / pageSize));
            
            result.put("success", true);
            result.put("code", 200);
            result.put("message", "搜索成功");
            result.put("data", data);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("code", 500);
            result.put("message", "搜索失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }
    
    /**
     * 搜索服务点
     */
    @GetMapping("/stations")
    public ResponseEntity<Map<String, Object>> searchStations(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 使用真实数据搜索服务点
            List<Map<String, Object>> stations = stationService.searchStations(keyword, 50);
            
            // 分页处理
            int total = stations.size();
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, total);
            
            List<Map<String, Object>> pagedList = total > 0 ? stations.subList(start, end) : new ArrayList<>();
            
            Map<String, Object> data = new HashMap<>();
            data.put("list", pagedList);
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);
            data.put("totalPage", (int) Math.ceil((double) total / pageSize));
            
            result.put("success", true);
            result.put("code", 200);
            result.put("message", "搜索成功");
            result.put("data", data);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("code", 500);
            result.put("message", "搜索失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }
} 