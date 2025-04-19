package com.ruralexpress.controller;

import com.ruralexpress.dto.CourierClusterDTO;
import com.ruralexpress.dto.DemandPredictionDTO;
import com.ruralexpress.dto.HistoricalDemandDTO;
import com.ruralexpress.dto.SkillTagDTO;
import com.ruralexpress.entity.Courier;
import com.ruralexpress.service.CourierAnalysisService;
import com.ruralexpress.service.DemandPredictionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 数据分析控制器
 * 提供劳动力画像分析和区域用工需求预测API
 */
@Slf4j
@RestController
@RequestMapping("/api/analysis")
@Api(tags = "数据分析接口")
public class AnalysisController {
    
    @Autowired
    private CourierAnalysisService courierAnalysisService;
    
    @Autowired
    private DemandPredictionService demandPredictionService;
    
    /**
     * 劳动力画像分析相关接口
     */
    
    @GetMapping("/courier/skills/all")
    @ApiOperation("获取所有技能标签")
    public ResponseEntity<List<SkillTagDTO>> getAllSkillTags() {
        return ResponseEntity.ok(courierAnalysisService.getAllSkillTags());
    }
    
    @GetMapping("/courier/skills/area")
    @ApiOperation("获取指定区域的技能标签")
    public ResponseEntity<List<SkillTagDTO>> getSkillTagsByArea(
            @ApiParam(value = "区域名称", required = true) @RequestParam String area) {
        return ResponseEntity.ok(courierAnalysisService.getSkillTagsByArea(area));
    }
    
    @GetMapping("/courier/clusters")
    @ApiOperation("获取快递员技能聚类分析")
    public ResponseEntity<List<CourierClusterDTO>> getCourierClusters() {
        return ResponseEntity.ok(courierAnalysisService.clusterCouriersBySkills());
    }
    
    @GetMapping("/courier/similar/{courierId}")
    @ApiOperation("获取相似快递员推荐")
    public ResponseEntity<List<Courier>> getSimilarCouriers(
            @ApiParam(value = "快递员ID", required = true) @PathVariable Long courierId,
            @ApiParam(value = "限制数量") @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(courierAnalysisService.getSimilarCouriers(courierId, limit));
    }
    
    @GetMapping("/courier/tag/distribution")
    @ApiOperation("获取标签的区域分布")
    public ResponseEntity<Map<String, Integer>> getTagDistribution(
            @ApiParam(value = "标签名称", required = true) @RequestParam String tagName) {
        return ResponseEntity.ok(courierAnalysisService.getTagDistributionByArea(tagName));
    }
    
    /**
     * 区域用工需求预测相关接口
     */
    
    @GetMapping("/demand/predict")
    @ApiOperation("预测指定区域未来需求")
    public ResponseEntity<List<DemandPredictionDTO>> predictAreaDemand(
            @ApiParam(value = "区域名称", required = true) @RequestParam String area,
            @ApiParam(value = "开始日期") @RequestParam(required = false) 
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @ApiParam(value = "预测天数") @RequestParam(required = false, defaultValue = "7") Integer days) {
        return ResponseEntity.ok(demandPredictionService.predictDemandForArea(area, startDate, days));
    }
    
    @GetMapping("/demand/predict/multi")
    @ApiOperation("预测多个区域未来需求")
    public ResponseEntity<Map<String, List<DemandPredictionDTO>>> predictMultiAreaDemand(
            @ApiParam(value = "区域列表，用逗号分隔") @RequestParam(required = false) List<String> areas,
            @ApiParam(value = "开始日期") @RequestParam(required = false) 
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @ApiParam(value = "预测天数") @RequestParam(required = false, defaultValue = "7") Integer days) {
        return ResponseEntity.ok(demandPredictionService.predictDemandForAreas(areas, startDate, days));
    }
    
    @GetMapping("/demand/historical")
    @ApiOperation("获取历史需求数据")
    public ResponseEntity<List<HistoricalDemandDTO>> getHistoricalDemand(
            @ApiParam(value = "区域名称", required = true) @RequestParam String area,
            @ApiParam(value = "开始日期", required = true) 
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @ApiParam(value = "结束日期", required = true) 
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(demandPredictionService.getHistoricalDemand(area, startDate, endDate));
    }
    
    @GetMapping("/demand/accuracy")
    @ApiOperation("获取预测准确度数据")
    public ResponseEntity<List<DemandPredictionDTO>> getPredictionAccuracy(
            @ApiParam(value = "区域名称", required = true) @RequestParam String area,
            @ApiParam(value = "过去天数", defaultValue = "30") @RequestParam(required = false, defaultValue = "30") Integer days) {
        return ResponseEntity.ok(demandPredictionService.getPredictionAccuracy(area, days));
    }
    
    @PostMapping("/demand/update-model")
    @ApiOperation("更新预测模型")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> updatePredictionModel(
            @ApiParam(value = "区域名称", required = true) @RequestParam String area) {
        return ResponseEntity.ok(demandPredictionService.updatePredictionModel(area));
    }
} 