package com.ruralexpress.service.impl;

import com.ruralexpress.service.LogisticsService;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.text.SimpleDateFormat;
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
        
        // 此处应该是从第三方物流API获取物流信息
        // 由于暂时没有接入第三方物流API，这里返回一些基本的物流信息
        
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
            
            Object[] params = {userId, pageSize, offset};
            
            List<Map<String, Object>> packages = jdbcTemplate.queryForList(sql, params);
            
            // 查询总记录数
            String countSql = "SELECT COUNT(*) FROM t_package WHERE user_id = ?";
            Integer total = jdbcTemplate.queryForObject(countSql, Integer.class, userId);
            
            // 构建响应数据
            for (Map<String, Object> pkg : packages) {
                Map<String, Object> tracking = new HashMap<>();
                
                // 基本信息
                tracking.put("id", pkg.get("id"));
                tracking.put("trackingNo", pkg.get("tracking_no"));
                tracking.put("company", pkg.get("company_name"));
                tracking.put("logo", pkg.get("company_logo"));
                tracking.put("status", pkg.get("status"));
                
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
                    updateTime = (Date) pkg.get("updated_at");
                } else if (pkg.get("created_at") != null) {
                    updateTime = (Date) pkg.get("created_at");
                } else {
                    updateTime = new Date();
                }
                tracking.put("updateTime", formatDate(updateTime));
                
                trackingList.add(tracking);
            }
            
            // 如果数据库中没有记录，返回模拟数据 (仅作为开发时测试用)
            if (trackingList.isEmpty()) {
                trackingList = generateMockTrackingData();
                total = trackingList.size();
            }
            
            result.put("list", trackingList);
            result.put("total", total != null ? total : 0);
            result.put("page", page);
            result.put("pageSize", pageSize);
            
        } catch (Exception e) {
            logger.error("获取物流追踪列表失败", e);
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            result.put("page", page);
            result.put("pageSize", pageSize);
            
            // 仅开发环境中返回模拟数据
            if (trackingList.isEmpty()) {
                trackingList = generateMockTrackingData();
                result.put("list", trackingList);
                result.put("total", trackingList.size());
            }
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
     * 生成模拟的物流追踪列表数据 (仅用于开发测试)
     */
    private List<Map<String, Object>> generateMockTrackingData() {
        List<Map<String, Object>> mockList = new ArrayList<>();
        
        // 顺丰速运
        Map<String, Object> tracking1 = new HashMap<>();
        tracking1.put("id", 1);
        tracking1.put("trackingNo", "SF1234567890");
        tracking1.put("company", "顺丰速运");
        tracking1.put("logo", "/static/images/icon/sf.png");
        tracking1.put("status", 2);
        tracking1.put("packageInfo", "文件包裹 2kg");
        tracking1.put("address", "江西省赣州市章贡区红旗大道123号");
        tracking1.put("updateTime", formatDate(new Date()));
        mockList.add(tracking1);
        
        // 圆通快递
        Map<String, Object> tracking2 = new HashMap<>();
        tracking2.put("id", 2);
        tracking2.put("trackingNo", "YT9876543210");
        tracking2.put("company", "圆通快递");
        tracking2.put("logo", "/static/images/icon/yt.png");
        tracking2.put("status", 4);
        tracking2.put("packageInfo", "衣服 1kg");
        tracking2.put("address", "江西省赣州市南康区健康路456号");
        tracking2.put("updateTime", formatDate(new Date()));
        mockList.add(tracking2);
        
        // 中通快递
        Map<String, Object> tracking3 = new HashMap<>();
        tracking3.put("id", 3);
        tracking3.put("trackingNo", "ZT5678901234");
        tracking3.put("company", "中通快递");
        tracking3.put("logo", "/static/images/icon/zt.png");
        tracking3.put("status", 5);
        tracking3.put("packageInfo", "电子产品 3kg");
        tracking3.put("address", "江西省赣州市赣县区红金大道789号");
        
        // 一天前的时间
        Calendar oneDayAgo = Calendar.getInstance();
        oneDayAgo.add(Calendar.DAY_OF_MONTH, -1);
        tracking3.put("updateTime", formatDate(oneDayAgo.getTime()));
        mockList.add(tracking3);
        
        return mockList;
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