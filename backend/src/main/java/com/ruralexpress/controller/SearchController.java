package com.ruralexpress.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * 搜索控制器
 */
@RestController
@RequestMapping("/search")
public class SearchController {

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
            // 模拟搜索结果
            List<Map<String, Object>> packages = generateMockPackages(keyword);
            
            // 分页处理
            int total = packages.size();
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, total);
            
            List<Map<String, Object>> pagedList = packages.subList(start, end);
            
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
            // 模拟搜索结果
            List<Map<String, Object>> orders = generateMockOrders(keyword);
            
            // 分页处理
            int total = orders.size();
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, total);
            
            List<Map<String, Object>> pagedList = orders.subList(start, end);
            
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
            // 模拟搜索结果
            List<Map<String, Object>> couriers = generateMockCouriers(keyword);
            
            // 分页处理
            int total = couriers.size();
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, total);
            
            List<Map<String, Object>> pagedList = couriers.subList(start, end);
            
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
            // 模拟搜索结果
            List<Map<String, Object>> stations = generateMockStations(keyword);
            
            // 分页处理
            int total = stations.size();
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, total);
            
            List<Map<String, Object>> pagedList = stations.subList(start, end);
            
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
     * 生成模拟的包裹数据
     */
    private List<Map<String, Object>> generateMockPackages(String keyword) {
        List<Map<String, Object>> packages = new ArrayList<>();
        
        // 模拟不同公司的包裹
        String[] companies = {"顺丰速运", "中通快递", "圆通速递", "韵达快递", "申通快递", "京东物流"};
        String[] statuses = {"待收件", "已揽收", "运输中", "已到达", "派送中", "已签收"};
        
        // 关键词匹配的结果数量
        int count = new Random().nextInt(5) + 3; // 3-7个结果
        
        for (int i = 0; i < count; i++) {
            Map<String, Object> pkg = new HashMap<>();
            pkg.put("id", String.valueOf(10000 + i));
            pkg.put("trackingNo", "RR" + (100000000 + new Random().nextInt(900000000)));
            pkg.put("company", companies[new Random().nextInt(companies.length)]);
            
            int statusIndex = new Random().nextInt(statuses.length);
            pkg.put("status", statusIndex);
            pkg.put("statusText", statuses[statusIndex]);
            
            // 如果包含南昌市青山湖区的关键词，添加相关地址
            if (keyword.contains("南昌") || keyword.contains("青山湖")) {
                pkg.put("senderAddress", "江西省南昌市青山湖区高新大道" + (100 + i) + "号");
                pkg.put("receiverAddress", "江西省南昌市青山湖区艾溪湖" + (1 + i) + "号");
            } else {
                pkg.put("senderAddress", "江西省南昌市" + (100 + i) + "号");
                pkg.put("receiverAddress", "浙江省杭州市" + (200 + i) + "号");
            }
            
            // 随机生成更新时间
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -new Random().nextInt(7));
            pkg.put("updateTime", calendar.getTime());
            
            packages.add(pkg);
        }
        
        return packages;
    }
    
    /**
     * 生成模拟的订单数据
     */
    private List<Map<String, Object>> generateMockOrders(String keyword) {
        List<Map<String, Object>> orders = new ArrayList<>();
        
        String[] statuses = {"pending", "shipped", "delivered", "cancelled"};
        
        // 关键词匹配的结果数量
        int count = new Random().nextInt(5) + 2; // 2-6个结果
        
        for (int i = 0; i < count; i++) {
            Map<String, Object> order = new HashMap<>();
            order.put("id", String.valueOf(20000 + i));
            order.put("orderNo", "DO" + (200000000 + new Random().nextInt(900000000)));
            
            int statusIndex = new Random().nextInt(statuses.length);
            order.put("status", statuses[statusIndex]);
            
            // 如果包含南昌市青山湖区的关键词，添加相关地址
            if (keyword.contains("南昌") || keyword.contains("青山湖")) {
                order.put("senderAddress", "江西省南昌市青山湖区高新大道" + (100 + i) + "号");
                order.put("receiverAddress", "江西省南昌市青山湖区艾溪湖" + (1 + i) + "号");
            } else {
                order.put("senderAddress", "江西省南昌市" + (100 + i) + "号");
                order.put("receiverAddress", "浙江省杭州市" + (200 + i) + "号");
            }
            
            order.put("price", 15.0 + new Random().nextInt(50));
            
            // 随机生成创建时间
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -new Random().nextInt(30));
            order.put("createdAt", calendar.getTime());
            
            orders.add(order);
        }
        
        return orders;
    }
    
    /**
     * 生成模拟的快递员数据
     */
    private List<Map<String, Object>> generateMockCouriers(String keyword) {
        List<Map<String, Object>> couriers = new ArrayList<>();
        
        String[] names = {"张师傅", "李师傅", "王师傅", "刘师傅", "赵师傅"};
        
        // 关键词匹配的结果数量
        int count = new Random().nextInt(3) + 2; // 2-4个结果
        
        for (int i = 0; i < count; i++) {
            Map<String, Object> courier = new HashMap<>();
            courier.put("id", String.valueOf(30000 + i));
            
            // 如果关键词包含特定名字，优先匹配
            String name = names[new Random().nextInt(names.length)];
            for (String n : names) {
                if (keyword.contains(n)) {
                    name = n;
                    break;
                }
            }
            courier.put("name", name);
            
            courier.put("avatar", "/static/images/courier-" + (1 + new Random().nextInt(5)) + ".png");
            courier.put("rating", 4.0 + new Random().nextDouble() * 1.0);
            courier.put("completedOrders", 50 + new Random().nextInt(200));
            
            // 如果包含南昌市青山湖区的关键词，添加相关区域
            if (keyword.contains("南昌") || keyword.contains("青山湖")) {
                courier.put("serviceArea", "江西省南昌市青山湖区");
            } else {
                courier.put("serviceArea", "江西省南昌市");
            }
            
            couriers.add(courier);
        }
        
        return couriers;
    }
    
    /**
     * 生成模拟的服务点数据
     */
    private List<Map<String, Object>> generateMockStations(String keyword) {
        List<Map<String, Object>> stations = new ArrayList<>();
        
        // 关键词匹配的结果数量
        int count = new Random().nextInt(4) + 2; // 2-5个结果
        
        for (int i = 0; i < count; i++) {
            Map<String, Object> station = new HashMap<>();
            station.put("id", String.valueOf(40000 + i));
            
            // 如果包含南昌市青山湖区的关键词，添加相关区域
            if (keyword.contains("南昌") || keyword.contains("青山湖")) {
                station.put("name", "乡递通青山湖第" + (i + 1) + "服务点");
                station.put("address", "江西省南昌市青山湖区高新大道" + (1000 + i) + "号");
            } else if (keyword.contains("艾溪湖")) {
                station.put("name", "乡递通艾溪湖第" + (i + 1) + "服务点");
                station.put("address", "江西省南昌市青山湖区艾溪湖" + (100 + i) + "号");
            } else {
                station.put("name", "乡递通第" + (i + 1) + "服务点");
                station.put("address", "江西省南昌市" + (300 + i) + "号");
            }
            
            station.put("phone", "1388888" + (1000 + i));
            station.put("businessHours", "09:00-21:00");
            station.put("distance", 0.5 + new Random().nextDouble() * 5.0);
            
            stations.add(station);
        }
        
        return stations;
    }
} 