package com.ruralexpress.controller;

import com.ruralexpress.utils.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${upload.base-path:/uploads}")
    private String uploadBasePath;

    @Value("${upload.url-prefix:http://localhost:8080/uploads}")
    private String uploadUrlPrefix;

    /**
     * 上传文件
     * @param file 文件
     * @param type 文件类型（可选，如：image、file、avatar等）
     * @return 上传结果
     */
    @PostMapping
    public ApiResult<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", defaultValue = "image") String type) {
        
        try {
            log.info("开始上传文件: 文件名={}, 文件大小={}, 类型={}", 
                    file.getOriginalFilename(), file.getSize(), type);
            
            // 验证文件
            if (file.isEmpty()) {
                return ApiResult.error(400, "上传文件不能为空");
            }
            
            // 验证文件类型
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return ApiResult.error(400, "文件名不能为空");
            }
            
            String extension = getFileExtension(originalFilename);
            if (!isValidFileExtension(extension, type)) {
                return ApiResult.error(400, "不支持的文件类型: " + extension);
            }
            
            // 检查文件大小
            long maxFileSize = getMaxFileSize(type);
            if (file.getSize() > maxFileSize) {
                return ApiResult.error(400, "文件大小超过限制: " + formatFileSize(maxFileSize));
            }
            
            // 创建上传目录
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String uploadDir = uploadBasePath + "/" + type + "/" + datePath;
            Path uploadPath = Paths.get(uploadDir);
            
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // 生成新的文件名
            String newFilename = UUID.randomUUID().toString().replace("-", "") + "." + extension;
            Path filePath = uploadPath.resolve(newFilename);
            
            // 保存文件
            file.transferTo(filePath.toFile());
            
            // 生成访问URL
            String fileUrl = uploadUrlPrefix + "/" + type + "/" + datePath + "/" + newFilename;
            
            // 构建返回结果
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", newFilename);
            result.put("originalFilename", originalFilename);
            result.put("size", String.valueOf(file.getSize()));
            result.put("type", type);
            
            log.info("文件上传成功: url={}", fileUrl);
            return ApiResult.success(result);
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            return ApiResult.serverError("文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("文件上传过程中发生错误: {}", e.getMessage(), e);
            return ApiResult.serverError("文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取文件扩展名
     * @param filename 文件名
     * @return 扩展名
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }
    
    /**
     * 验证文件扩展名是否有效
     * @param extension 扩展名
     * @param type 文件类型
     * @return 是否有效
     */
    private boolean isValidFileExtension(String extension, String type) {
        switch (type) {
            case "image":
                return extension.matches("jpg|jpeg|png|gif|bmp|webp");
            case "document":
                return extension.matches("pdf|doc|docx|xls|xlsx|ppt|pptx|txt");
            case "video":
                return extension.matches("mp4|avi|mov|flv|wmv");
            case "audio":
                return extension.matches("mp3|wav|wma|ogg|aac");
            case "avatar":
                return extension.matches("jpg|jpeg|png|gif");
            default:
                return true; // 默认不限制
        }
    }
    
    /**
     * 获取最大文件大小
     * @param type 文件类型
     * @return 最大文件大小（字节）
     */
    private long getMaxFileSize(String type) {
        switch (type) {
            case "image":
                return 5 * 1024 * 1024; // 5MB
            case "avatar":
                return 2 * 1024 * 1024; // 2MB
            case "document":
                return 20 * 1024 * 1024; // 20MB
            case "video":
                return 100 * 1024 * 1024; // 100MB
            case "audio":
                return 20 * 1024 * 1024; // 20MB
            default:
                return 10 * 1024 * 1024; // 10MB
        }
    }
    
    /**
     * 格式化文件大小
     * @param size 文件大小（字节）
     * @return 格式化后的文件大小
     */
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + "B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f", size / 1024.0) + "KB";
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f", size / (1024.0 * 1024)) + "MB";
        } else {
            return String.format("%.2f", size / (1024.0 * 1024 * 1024)) + "GB";
        }
    }
} 