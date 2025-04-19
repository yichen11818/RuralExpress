package com.ruralexpress.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件上传工具类
 */
@Slf4j
@Component
public class FileUploadUtil {
    
    @Value("${file.upload.base-path}")
    private String baseUploadPath;
    
    @Value("${file.upload.base-url}")
    private String baseUrl;
    
    /**
     * 上传文件
     * @param file 文件对象
     * @param dirPath 保存目录
     * @return 文件访问URL
     * @throws IOException 上传异常
     */
    public String uploadFile(MultipartFile file, String dirPath) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }
        
        // 生成文件保存路径
        String todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String savePath = baseUploadPath + File.separator + dirPath + File.separator + todayStr;
        File saveDir = new File(savePath);
        
        // 创建保存目录
        if (!saveDir.exists()) {
            if (!saveDir.mkdirs()) {
                throw new IOException("创建文件保存目录失败: " + savePath);
            }
        }
        
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String newFileName = UUID.randomUUID().toString().replace("-", "") + fileExtension;
        File saveFile = new File(saveDir, newFileName);
        
        // 保存文件
        try {
            file.transferTo(saveFile);
            log.info("文件上传成功: {}", saveFile.getAbsolutePath());
            
            // 返回文件URL
            return baseUrl + "/" + dirPath + "/" + todayStr + "/" + newFileName;
        } catch (IOException e) {
            log.error("文件保存失败: {}", e.getMessage(), e);
            throw new IOException("保存文件失败: " + e.getMessage());
        }
    }
} 