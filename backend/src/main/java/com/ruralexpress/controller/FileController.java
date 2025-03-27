package com.ruralexpress.controller;

import com.ruralexpress.utils.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    @Value("${file.upload.domain:http://localhost:8080}")
    private String domain;

    /**
     * 通用文件上传
     * @param file 上传的文件
     * @return 文件URL
     */
    @PostMapping("/upload")
    public ApiResult<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResult.error(400, "文件不能为空");
        }

        // 获取文件名
        String originalFilename = file.getOriginalFilename();
        
        // 获取文件后缀
        String extension = getFileExtension(originalFilename);
        
        // 检查文件类型
        if (!isAllowedFileType(extension)) {
            return ApiResult.error(400, "不支持的文件类型: " + extension);
        }
        
        // 生成新的文件名，防止重复
        String newFilename = generateUniqueFilename(extension);
        
        // 构建保存路径
        String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        String relativePath = datePath + "/" + newFilename;
        
        try {
            // 确保目录存在
            Path uploadDir = Paths.get(uploadPath, datePath);
            Files.createDirectories(uploadDir);
            
            // 保存文件
            Path targetPath = uploadDir.resolve(newFilename);
            file.transferTo(targetPath.toFile());
            
            // 构建返回URL
            String fileUrl = domain + "/api/file/preview?path=" + relativePath;
            
            // 返回结果
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("name", originalFilename);
            
            log.info("文件上传成功: {}", fileUrl);
            return ApiResult.success(result);
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            return ApiResult.serverError("文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 图片上传
     * @param file 上传的图片
     * @return 图片URL
     */
    @PostMapping("/upload/image")
    public ApiResult<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResult.error(400, "图片不能为空");
        }

        // 获取文件名
        String originalFilename = file.getOriginalFilename();
        
        // 获取文件后缀
        String extension = getFileExtension(originalFilename);
        
        // 检查文件类型是否为图片
        if (!isImageFile(extension)) {
            return ApiResult.error(400, "不支持的图片格式: " + extension);
        }
        
        // 调用通用上传方法
        return uploadFile(file);
    }
    
    /**
     * 文件预览
     * @param path 文件路径
     * @return 文件内容
     */
    @GetMapping("/preview")
    public ResponseEntity<Resource> preview(@RequestParam("path") String path) {
        try {
            // 构建完整的文件路径
            Path filePath = Paths.get(uploadPath, path);
            Resource resource = new FileSystemResource(filePath.toFile());
            
            // 检查文件是否存在
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            // 获取文件MIME类型
            String contentType = determineContentType(path);
            
            // 返回文件
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            log.error("文件预览失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 删除文件
     * @param path 文件路径
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public ApiResult<String> deleteFile(@RequestParam("path") String path) {
        try {
            // 构建完整的文件路径
            Path filePath = Paths.get(uploadPath, path);
            File file = filePath.toFile();
            
            // 检查文件是否存在
            if (!file.exists()) {
                return ApiResult.error(404, "文件不存在");
            }
            
            // 删除文件
            boolean deleted = file.delete();
            if (deleted) {
                log.info("文件删除成功: {}", path);
                return ApiResult.success("文件删除成功");
            } else {
                log.error("文件删除失败: {}", path);
                return ApiResult.serverError("文件删除失败");
            }
        } catch (Exception e) {
            log.error("文件删除失败: {}", e.getMessage(), e);
            return ApiResult.serverError("文件删除失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取文件扩展名
     * @param filename 文件名
     * @return 扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty() || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
    
    /**
     * 生成唯一文件名
     * @param extension 文件扩展名
     * @return 唯一文件名
     */
    private String generateUniqueFilename(String extension) {
        return UUID.randomUUID().toString().replace("-", "") + "." + extension;
    }
    
    /**
     * 检查文件类型是否允许上传
     * @param extension 文件扩展名
     * @return 是否允许
     */
    private boolean isAllowedFileType(String extension) {
        // 允许上传的文件类型
        String[] allowedTypes = {
            "jpg", "jpeg", "png", "gif", "bmp", "webp", // 图片
            "doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf", "txt" // 文档
        };
        
        for (String type : allowedTypes) {
            if (type.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 检查是否是图片文件
     * @param extension 文件扩展名
     * @return 是否为图片
     */
    private boolean isImageFile(String extension) {
        String[] imageTypes = {"jpg", "jpeg", "png", "gif", "bmp", "webp"};
        
        for (String type : imageTypes) {
            if (type.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 根据文件扩展名确定内容类型
     * @param filename 文件名
     * @return 内容类型
     */
    private String determineContentType(String filename) {
        String extension = getFileExtension(filename);
        
        switch (extension.toLowerCase()) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "bmp":
                return "image/bmp";
            case "webp":
                return "image/webp";
            case "pdf":
                return "application/pdf";
            case "doc":
            case "docx":
                return "application/msword";
            case "xls":
            case "xlsx":
                return "application/vnd.ms-excel";
            case "ppt":
            case "pptx":
                return "application/vnd.ms-powerpoint";
            case "txt":
                return "text/plain";
            default:
                return "application/octet-stream";
        }
    }
} 