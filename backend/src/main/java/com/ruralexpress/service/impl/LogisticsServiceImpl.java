package com.ruralexpress.service.impl;

import com.ruralexpress.service.LogisticsService;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 物流信息服务实现类
 */
@Service
public class LogisticsServiceImpl implements LogisticsService {

    private static final Logger logger = LoggerFactory.getLogger(LogisticsServiceImpl.class);
    
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
     * 手机号码脱敏
     */
    private String maskPhoneNumber(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
} 