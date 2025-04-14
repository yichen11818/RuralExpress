package com.ruralexpress.service.impl;

import com.ruralexpress.service.LogisticsService;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * 物流信息服务实现类
 */
@Service
public class LogisticsServiceImpl implements LogisticsService {

    private static final Logger logger = LoggerFactory.getLogger(LogisticsServiceImpl.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 获取物流信息
     *
     * @param trackingNo 运单号
     * @return 物流信息
     */
    @Override
    public Map<String, Object> getLogisticsInfo(String trackingNo) {
        logger.info("获取物流信息: trackingNo={}", trackingNo);
        
        try {
            // 查询数据库获取物流信息
            String sql = "SELECT p.id, p.tracking_no, p.status, p.receiver_name, p.receiver_address, " +
                    "p.created_at, p.updated_at, p.remark, p.estimated_delivery_time, p.signed_time, " +
                    "p.user_id, c.name as company_name, c.logo as company_logo " +
                    "FROM t_package p " +
                    "JOIN t_express_company c ON p.company_id = c.id " +
                    "WHERE p.tracking_no = ?";
            
            Map<String, Object> packageInfo = null;
            
            try {
                packageInfo = jdbcTemplate.queryForMap(sql, trackingNo);
                logger.info("查询物流信息成功: {}", packageInfo);
            } catch (Exception e) {
                logger.error("查询物流信息失败: {}", e.getMessage(), e);
            }
            
            // 如果数据库中没有记录，返回模拟数据
            if (packageInfo == null || packageInfo.isEmpty()) {
                logger.warn("未找到物流信息，使用模拟数据: trackingNo={}", trackingNo);
                return generateMockLogisticsInfo(trackingNo);
            }
            
            // 构建物流信息
            Map<String, Object> data = new HashMap<>();
            
            // 运单基本信息
            data.put("trackingNo", trackingNo);
            data.put("companyName", packageInfo.get("company_name") != null ? 
                    packageInfo.get("company_name").toString() : "乡递通快递");
            data.put("companyLogo", packageInfo.get("company_logo") != null ? 
                    packageInfo.get("company_logo").toString() : "/static/images/company-logo.png");
            
            // 状态信息
            int status = 0;
            if (packageInfo.get("status") != null) {
                try {
                    status = Integer.parseInt(packageInfo.get("status").toString());
                } catch (NumberFormatException e) {
                    logger.warn("物流状态解析失败: {}", e.getMessage());
                }
            }
            data.put("status", status);
            
            // 状态文本
            String statusText;
            switch (status) {
                case 0: statusText = "待揽收"; break;
                case 1: statusText = "已揽收"; break;
                case 2: statusText = "运输中"; break;
                case 3: statusText = "派送中"; break;
                case 4: statusText = "已签收"; break;
                case 5: statusText = "异常"; break;
                default: statusText = "未知";
            }
            data.put("statusText", statusText);
            
            // 收件人信息
            data.put("receiverName", packageInfo.get("receiver_name") != null ? 
                    packageInfo.get("receiver_name").toString() : "收件人");
            data.put("receiverPhone", maskPhoneNumber("13900139000")); // 模拟手机号
            data.put("receiverAddress", packageInfo.get("receiver_address") != null ? 
                    packageInfo.get("receiver_address").toString() : "");
            
            // 发件人信息 - 从数据库获取不到的使用默认值
            data.put("senderName", "发件人");
            data.put("senderPhone", maskPhoneNumber("13800138000"));
            data.put("senderAddress", "江西省南昌市青山湖区高新大道1888号");
            
            // 包裹信息
            String packageDesc = "包裹";
            if (packageInfo.get("remark") != null && !packageInfo.get("remark").toString().isEmpty()) {
                packageDesc = packageInfo.get("remark").toString();
            }
            data.put("packageInfo", packageDesc);
            
            // 时间信息
            Date estimatedDelivery = null;
            
            if (packageInfo.get("estimated_delivery_time") != null) {
                if (packageInfo.get("estimated_delivery_time") instanceof LocalDateTime) {
                    LocalDateTime ldt = (LocalDateTime) packageInfo.get("estimated_delivery_time");
                    estimatedDelivery = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
                } else {
                    estimatedDelivery = (Date) packageInfo.get("estimated_delivery_time");
                }
            } else {
                // 如果没有预计送达时间，默认为当前时间+2天
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, 2);
                estimatedDelivery = cal.getTime();
            }
            
            data.put("estimatedDelivery", formatDate(estimatedDelivery));
            
            // 物流轨迹
            List<Map<String, Object>> traces = generateTraces(trackingNo, status);
            data.put("traces", traces);
            
            return data;
            
        } catch (Exception e) {
            logger.error("获取物流信息失败: {}", e.getMessage(), e);
            return generateMockLogisticsInfo(trackingNo);
        }
    }
    
    /**
     * 生成模拟物流信息（当数据库中没有记录时使用）
     */
    private Map<String, Object> generateMockLogisticsInfo(String trackingNo) {
        Map<String, Object> data = new HashMap<>();
        
        // 运单基本信息
        data.put("trackingNo", trackingNo);
        data.put("companyName", "乡递通快递");
        data.put("companyLogo", "/static/images/company-logo.png");
        
        // 根据运单号最后一位确定物流状态（仅用于演示）
        int lastChar = trackingNo.charAt(trackingNo.length() - 1) - '0';
        int status = (lastChar % 6); // 0-5之间的状态
        String statusText;
        
        switch (status) {
            case 0:
                statusText = "待揽收";
                break;
            case 1:
                statusText = "已揽收";
                break;
            case 2:
                statusText = "运输中";
                break;
            case 3:
                statusText = "派送中";
                break;
            case 4:
                statusText = "已签收";
                break;
            case 5:
                statusText = "异常";
                break;
            default:
                statusText = "未知";
        }
        
        data.put("status", status);
        data.put("statusText", statusText);
        
        // 发件人信息
        data.put("senderName", "发件人");
        data.put("senderPhone", maskPhoneNumber("13800138000"));
        data.put("senderAddress", "江西省南昌市青山湖区高新大道1888号");
        
        // 收件人信息
        data.put("receiverName", "收件人");
        data.put("receiverPhone", maskPhoneNumber("13900139000"));
        data.put("receiverAddress", "江西省南昌市青云谱区井冈山大道2000号");
        
        // 添加预计送达时间
        Calendar estimatedDelivery = Calendar.getInstance();
        estimatedDelivery.add(Calendar.DAY_OF_MONTH, 2); // 预计两天后送达
        data.put("estimatedDelivery", formatDate(estimatedDelivery.getTime()));
        
        // 物流轨迹
        List<Map<String, Object>> traces = generateTraces(trackingNo, status);
        data.put("traces", traces);
        
        return data;
    }
    
    /**
     * 通过订单ID获取物流信息
     *
     * @param orderId 订单ID
     * @return 物流信息
     */
    @Override
    public Map<String, Object> getLogisticsInfoByOrderId(Long orderId) {
        logger.info("通过订单ID获取物流信息: orderId={}", orderId);
        
        try {
            // 查询订单关联的物流信息
            String sql = "SELECT o.tracking_no, o.status, o.receiver_name, o.receiver_address, " +
                    "o.sender_name, o.sender_address, o.created_at, o.updated_at, " +
                    "o.estimated_delivery_time, o.package_description, o.courier_id, " +
                    "c.name as company_name, c.logo as company_logo " +
                    "FROM t_order o " +
                    "LEFT JOIN t_express_company c ON o.company_id = c.id " +
                    "WHERE o.id = ?";
            
            Map<String, Object> orderInfo = null;
            
            try {
                orderInfo = jdbcTemplate.queryForMap(sql, orderId);
                logger.info("查询订单物流信息: {}", orderInfo);
            } catch (Exception e) {
                logger.error("查询订单物流信息失败: {}", e.getMessage(), e);
            }
            
            if (orderInfo == null || orderInfo.isEmpty()) {
                logger.warn("未找到订单物流信息: orderId={}", orderId);
                // 如果数据库中没有记录，返回一个基本信息
                Map<String, Object> emptyData = new HashMap<>();
                emptyData.put("trackingNo", "未分配");
                emptyData.put("companyName", "乡递通快递");
                emptyData.put("companyLogo", "/static/images/company-logo.png");
                emptyData.put("status", 0);
                emptyData.put("statusText", "待揽收");
                emptyData.put("traces", new ArrayList<>());
                return emptyData;
            }
            
            // 构建物流信息
            Map<String, Object> data = new HashMap<>();
            
            // 运单基本信息
            String trackingNo = orderInfo.get("tracking_no") != null ? 
                    orderInfo.get("tracking_no").toString() : "未分配";
            data.put("trackingNo", trackingNo);
            data.put("orderId", orderId);
            data.put("companyName", orderInfo.get("company_name") != null ? 
                    orderInfo.get("company_name").toString() : "乡递通快递");
            data.put("companyLogo", orderInfo.get("company_logo") != null ? 
                    orderInfo.get("company_logo").toString() : "/static/images/company-logo.png");
            
            // 状态信息
            int status = 0;
            if (orderInfo.get("status") != null) {
                try {
                    status = Integer.parseInt(orderInfo.get("status").toString());
                } catch (NumberFormatException e) {
                    logger.warn("订单状态解析失败: {}", e.getMessage());
                }
            }
            data.put("status", status);
            
            // 状态文本
            String statusText;
            switch (status) {
                case 0: statusText = "待揽收"; break;
                case 1: statusText = "已揽收"; break;
                case 2: statusText = "运输中"; break;
                case 3: statusText = "派送中"; break;
                case 4: statusText = "已签收"; break;
                case 5: statusText = "异常"; break;
                default: statusText = "未知";
            }
            data.put("statusText", statusText);
            
            // 发件人信息
            data.put("senderName", orderInfo.get("sender_name") != null ? 
                    orderInfo.get("sender_name").toString() : "发件人");
            data.put("senderAddress", orderInfo.get("sender_address") != null ? 
                    orderInfo.get("sender_address").toString() : "");
            
            // 收件人信息
            data.put("receiverName", orderInfo.get("receiver_name") != null ? 
                    orderInfo.get("receiver_name").toString() : "收件人");
            data.put("receiverAddress", orderInfo.get("receiver_address") != null ? 
                    orderInfo.get("receiver_address").toString() : "");
            
            // 包裹信息
            data.put("packageInfo", orderInfo.get("package_description") != null ? 
                    orderInfo.get("package_description").toString() : "包裹");
            
            // 时间信息
            Date createdAt = null;
            Date updatedAt = null;
            Date estimatedDelivery = null;
            
            if (orderInfo.get("created_at") != null) {
                if (orderInfo.get("created_at") instanceof LocalDateTime) {
                    LocalDateTime ldt = (LocalDateTime) orderInfo.get("created_at");
                    createdAt = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
                } else {
                    createdAt = (Date) orderInfo.get("created_at");
                }
            }
            
            if (orderInfo.get("updated_at") != null) {
                if (orderInfo.get("updated_at") instanceof LocalDateTime) {
                    LocalDateTime ldt = (LocalDateTime) orderInfo.get("updated_at");
                    updatedAt = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
                } else {
                    updatedAt = (Date) orderInfo.get("updated_at");
                }
            }
            
            if (orderInfo.get("estimated_delivery_time") != null) {
                if (orderInfo.get("estimated_delivery_time") instanceof LocalDateTime) {
                    LocalDateTime ldt = (LocalDateTime) orderInfo.get("estimated_delivery_time");
                    estimatedDelivery = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
                } else {
                    estimatedDelivery = (Date) orderInfo.get("estimated_delivery_time");
                }
            } else {
                // 如果没有预计送达时间，默认为创建时间+2天
                if (createdAt != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(createdAt);
                    cal.add(Calendar.DAY_OF_MONTH, 2);
                    estimatedDelivery = cal.getTime();
                } else {
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DAY_OF_MONTH, 2);
                    estimatedDelivery = cal.getTime();
                }
            }
            
            data.put("createdTime", createdAt != null ? formatDate(createdAt) : "");
            data.put("updateTime", updatedAt != null ? formatDate(updatedAt) : "");
            data.put("estimatedDelivery", estimatedDelivery != null ? formatDate(estimatedDelivery) : "");
            
            // 快递员信息
            if (orderInfo.get("courier_id") != null) {
                Long courierId = Long.parseLong(orderInfo.get("courier_id").toString());
                if (courierId > 0) {
                    try {
                        String courierSql = "SELECT id, name, phone, avatar FROM t_user WHERE id = ? AND role = 'courier'";
                        Map<String, Object> courierInfo = jdbcTemplate.queryForMap(courierSql, courierId);
                        
                        Map<String, Object> courier = new HashMap<>();
                        courier.put("id", courierInfo.get("id"));
                        courier.put("name", courierInfo.get("name"));
                        courier.put("phone", maskPhoneNumber(courierInfo.get("phone").toString()));
                        courier.put("avatar", courierInfo.get("avatar"));
                        
                        data.put("courier", courier);
                    } catch (Exception e) {
                        logger.warn("获取快递员信息失败: {}", e.getMessage());
                    }
                }
            }
            
            // 物流轨迹
            List<Map<String, Object>> traces = generateTraces(trackingNo, status);
            data.put("traces", traces);
            
            return data;
            
        } catch (Exception e) {
            logger.error("通过订单ID获取物流信息失败: {}", e.getMessage(), e);
            // 如果发生异常，返回一个基本信息
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("trackingNo", "未知");
            errorData.put("companyName", "乡递通快递");
            errorData.put("companyLogo", "/static/images/company-logo.png");
            errorData.put("status", 0);
            errorData.put("statusText", "待揽收");
            errorData.put("traces", new ArrayList<>());
            return errorData;
        }
    }
    
    /**
     * 获取用户的物流追踪列表
     *
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 物流追踪列表
     */
    @Override
    public Map<String, Object> getTrackingList(Long userId, Integer page, Integer pageSize) {
        logger.info("获取用户物流追踪列表: userId={}, page={}, pageSize={}", userId, page, pageSize);
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> trackingList = new ArrayList<>();
        
        try {
            // 计算分页参数
            int offset = (page - 1) * pageSize;
            
            // 查询用户的包裹列表
            String sql = "SELECT p.id, p.tracking_no, p.status, p.receiver_name, p.receiver_address, " +
                    "p.created_at, p.updated_at, p.remark, p.estimated_delivery_time, p.signed_time, " +
                    "c.name as company_name, c.logo as company_logo " +
                    "FROM t_package p " +
                    "JOIN t_express_company c ON p.company_id = c.id " +
                    "WHERE p.user_id = ? " +
                    "ORDER BY p.updated_at DESC " +
                    "LIMIT ? OFFSET ?";
            
            logger.info("执行SQL: {}, 参数: userId={}, limit={}, offset={}", sql, userId, pageSize, offset);
            
            Object[] params = {userId, pageSize, offset};
            
            try {
                List<Map<String, Object>> packages = jdbcTemplate.queryForList(sql, params);
                logger.info("查询结果: 找到{}条记录", packages.size());
                
                // 查询总记录数
                String countSql = "SELECT COUNT(*) FROM t_package WHERE user_id = ?";
                logger.info("执行计数SQL: {}, 参数: userId={}", countSql, userId);
                
                Integer total = jdbcTemplate.queryForObject(countSql, Integer.class, userId);
                logger.info("总记录数: {}", total);
                
                // 构建响应数据
                for (Map<String, Object> pkg : packages) {
                    Map<String, Object> tracking = new HashMap<>();
                    
                    // 基本信息
                    tracking.put("id", pkg.get("id"));
                    tracking.put("trackingNo", pkg.get("tracking_no"));
                    tracking.put("company", pkg.get("company_name"));
                    tracking.put("logo", pkg.get("company_logo"));
                    
                    // 记录状态值及其类型
                    Object statusObj = pkg.get("status");
                    logger.info("包裹[{}]的状态值: {}, 类型: {}", pkg.get("id"), statusObj, 
                        statusObj != null ? statusObj.getClass().getName() : "null");
                    
                    tracking.put("status", statusObj);
                    
                    // 包裹信息
                    String packageInfo = "包裹";
                    if (pkg.get("remark") != null && !pkg.get("remark").toString().isEmpty()) {
                        packageInfo = pkg.get("remark").toString();
                    }
                    tracking.put("packageInfo", packageInfo);
                    
                    // 地址信息
                    String address = "";
                    if (pkg.get("receiver_address") != null) {
                        address = pkg.get("receiver_address").toString();
                    }
                    tracking.put("address", address);
                    
                    // 更新时间
                    Date updateTime = null;
                    if (pkg.get("updated_at") != null) {
                        // 转换LocalDateTime为Date
                        if (pkg.get("updated_at") instanceof java.time.LocalDateTime) {
                            java.time.LocalDateTime localDateTime = (java.time.LocalDateTime) pkg.get("updated_at");
                            updateTime = java.util.Date.from(localDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant());
                        } else {
                            updateTime = (Date) pkg.get("updated_at");
                        }
                    } else if (pkg.get("created_at") != null) {
                        // 转换LocalDateTime为Date
                        if (pkg.get("created_at") instanceof java.time.LocalDateTime) {
                            java.time.LocalDateTime localDateTime = (java.time.LocalDateTime) pkg.get("created_at");
                            updateTime = java.util.Date.from(localDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant());
                        } else {
                            updateTime = (Date) pkg.get("created_at");
                        }
                    } else {
                        updateTime = new Date();
                    }
                    tracking.put("updateTime", formatDate(updateTime));
                    
                    trackingList.add(tracking);
                }
            } catch (Exception e) {
                logger.error("SQL执行异常: {}", e.getMessage(), e);
            }
            
            // 移除模拟数据的使用，直接使用数据库查询结果
            if (trackingList.isEmpty()) {
                logger.warn("未从数据库获取到物流数据，返回空列表");
            }
            
            result.put("list", trackingList);
            result.put("total", trackingList.size());
            result.put("page", page);
            result.put("pageSize", pageSize);
            
            logger.info("返回结果: totalRecords={}, page={}, pageSize={}", 
                    result.get("total"), result.get("page"), result.get("pageSize"));
            
        } catch (Exception e) {
            logger.error("获取物流追踪列表失败", e);
            // 处理异常情况，但不再使用模拟数据
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            result.put("page", page);
            result.put("pageSize", pageSize);
            logger.warn("由于异常，返回空列表");
        }
        
        return result;
    }
    
    /**
     * 生成物流轨迹
     */
    private List<Map<String, Object>> generateTraces(String trackingNo, int status) {
        List<Map<String, Object>> traces = new ArrayList<>();
        
        // 当前时间
        Calendar calendar = Calendar.getInstance();
        
        // 已签收
        if (status == 4) {
            // 第5条 - 签收
            Map<String, Object> trace5 = new HashMap<>();
            trace5.put("time", formatDate(calendar.getTime()));
            trace5.put("status", "已签收");
            trace5.put("content", "您的快递已签收，签收人：本人，感谢使用乡递通快递");
            trace5.put("location", "江西省南昌市青云谱区井冈山大道网点");
            traces.add(trace5);
            
            // 时间往前推4小时
            calendar.add(Calendar.HOUR, -4);
        }
        
        // 派送中
        if (status >= 3) {
            // 第4条 - 派送中
            Map<String, Object> trace4 = new HashMap<>();
            trace4.put("time", formatDate(calendar.getTime()));
            trace4.put("status", "派送中");
            trace4.put("content", "您的快递正在派送，快递员：张师傅，联系电话：" + maskPhoneNumber("13812345678"));
            trace4.put("location", "江西省南昌市青云谱区井冈山大道网点");
            traces.add(trace4);
            
            // 时间往前推8小时
            calendar.add(Calendar.HOUR, -8);
        }
        
        // 到达目的地
        if (status >= 2) {
            // 第3条 - 到达目的地
            Map<String, Object> trace3 = new HashMap<>();
            trace3.put("time", formatDate(calendar.getTime()));
            trace3.put("status", "已到达");
            trace3.put("content", "您的快递已到达【南昌市青云谱区井冈山大道网点】");
            trace3.put("location", "江西省南昌市青云谱区井冈山大道网点");
            traces.add(trace3);
            
            // 时间往前推1天
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        
        // 运输中
        if (status >= 1) {
            // 第2条 - 运输中
            Map<String, Object> trace2 = new HashMap<>();
            trace2.put("time", formatDate(calendar.getTime()));
            trace2.put("status", "运输中");
            trace2.put("content", "您的快递已从【南昌市青山湖区高新大道网点】发出，正在运往【南昌市青云谱区井冈山大道网点】");
            trace2.put("location", "江西省南昌市青山湖区高新大道网点");
            traces.add(trace2);
            
            // 时间往前推8小时
            calendar.add(Calendar.HOUR, -8);
            
            // 第1条 - 已揽收
            Map<String, Object> trace1 = new HashMap<>();
            trace1.put("time", formatDate(calendar.getTime()));
            trace1.put("status", "已揽收");
            trace1.put("content", "您的快递已被揽收，快递员：李师傅，联系电话：" + maskPhoneNumber("13987654321"));
            trace1.put("location", "江西省南昌市青山湖区高新大道网点");
            traces.add(trace1);
        }
        
        return traces;
    }
    
    /**
     * 格式化日期为字符串
     */
    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
    
    /**
     * 隐藏手机号中间4位
     */
    private String maskPhoneNumber(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
} 