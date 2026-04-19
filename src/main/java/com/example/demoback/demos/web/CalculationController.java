package com.example.demoback.demos.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 智能测算控制器
 */
@RestController
@RequestMapping("/api/calculation")
public class CalculationController {

    /**
     * 保价采购计算
     */
    @PostMapping("/baojia")
    public Map<String, Object> calculateBaojia(@RequestBody CalculationRequest request) {
        // 模拟市场数据
        double spotPrice = 7600;
        double futuresPrice = 7550;
        double spotVol = 0.15;
        double futuresVol = 0.18;
        
        // 计算参数
        int quantity = request.getQuantity();
        int periodDays = request.getPeriodDays();
        String preference = request.getPreference();
        
        // 时间参数
        double T = periodDays / 360.0;
        
        // 偏好系数
        double a = getPreferenceCoefficient(preference);
        double m = 0.3;
        double r = 0.018;
        double lambda = 0.15;
        
        // 现货保护价
        double strikeSpot = spotPrice * (1 + a * spotVol * Math.sqrt(T) * m);
        
        // 基差
        double basis = spotPrice - futuresPrice;
        
        // 期货执行价
        double strikeFutures = strikeSpot - basis;
        
        // 模拟期权费（简化计算）
        double optionFeePerTon = spotPrice * spotVol * Math.sqrt(T) * 0.35;
        double serviceFeePerTon = optionFeePerTon * lambda;
        double totalFeePerTon = optionFeePerTon + serviceFeePerTon;
        double totalFee = totalFeePerTon * quantity;
        
        // 综合采购价
        double allInPrice = strikeSpot + totalFeePerTon;
        
        // VaR计算
        double varBefore = calculateVaR(spotPrice, quantity, spotVol, T);
        double varAfter = calculateVaR(allInPrice, quantity, spotVol, T);
        double reductionRate = Math.abs(varBefore - varAfter) / varBefore;
        
        // 构建响应
        Map<String, Object> result = new HashMap<>();
        result.put("spotPrice", spotPrice);
        result.put("futuresPrice", futuresPrice);
        result.put("strikeSpot", strikeSpot);
        result.put("feePerTon", totalFeePerTon);
        result.put("totalFee", totalFee);
        result.put("allInPrice", allInPrice);
        result.put("varBefore", varBefore);
        result.put("varAfter", varAfter);
        result.put("reductionRate", reductionRate);
        
        // 情景分析
        result.put("scenarios", generateScenarios(spotPrice, strikeSpot, totalFeePerTon, quantity));
        
        return result;
    }

    /**
     * 封顶销售计算
     */
    @PostMapping("/fengding")
    public Map<String, Object> calculateFengding(@RequestBody CalculationRequest request) {
        // 模拟市场数据
        double spotPrice = 7600;
        double futuresPrice = 7550;
        double spotVol = 0.15;
        double futuresVol = 0.18;
        
        // 计算参数
        int quantity = request.getQuantity();
        int periodDays = request.getPeriodDays();
        String preference = request.getPreference();
        
        // 时间参数
        double T = periodDays / 360.0;
        
        // 偏好系数
        double a = getPreferenceCoefficient(preference);
        double m = 0.3;
        double r = 0.018;
        double lambda = 0.15;
        
        // 现货保护价
        double strikeSpot = spotPrice * (1 - a * spotVol * Math.sqrt(T) * m);
        
        // 基差
        double basis = spotPrice - futuresPrice;
        
        // 期货执行价
        double strikeFutures = strikeSpot - basis;
        
        // 模拟期权费（简化计算）
        double optionFeePerTon = spotPrice * spotVol * Math.sqrt(T) * 0.35;
        double serviceFeePerTon = optionFeePerTon * lambda;
        double totalFeePerTon = optionFeePerTon + serviceFeePerTon;
        double totalFee = totalFeePerTon * quantity;
        
        // 收入保护下限
        double incomeFloor = strikeSpot * quantity - totalFee;
        
        // VaR计算
        double varBefore = calculateVaR(spotPrice, quantity, spotVol, T);
        double varAfter = calculateVaR(strikeSpot, quantity, spotVol, T) + totalFee;
        double reductionRate = Math.abs(varBefore - varAfter) / varBefore;
        
        // 构建响应
        Map<String, Object> result = new HashMap<>();
        result.put("spotPrice", spotPrice);
        result.put("futuresPrice", futuresPrice);
        result.put("strikeSpot", strikeSpot);
        result.put("feePerTon", totalFeePerTon);
        result.put("totalFee", totalFee);
        result.put("incomeFloor", incomeFloor);
        result.put("varBefore", varBefore);
        result.put("varAfter", varAfter);
        result.put("reductionRate", reductionRate);
        
        return result;
    }

    /**
     * 区间结算计算
     */
    @PostMapping("/qujian")
    public Map<String, Object> calculateQujian(@RequestBody CalculationRequest request) {
        // 模拟市场数据
        double spotPrice = 7600;
        double futuresPrice = 7550;
        double spotVol = 0.15;
        
        // 计算参数
        int quantity = request.getQuantity();
        int periodDays = request.getPeriodDays();
        String preference = request.getPreference();
        
        // 时间参数
        double T = periodDays / 360.0;
        
        // 偏好系数
        double beta = getPreferenceCoefficient(preference);
        double m = 0.3;
        double lambda = 0.15;
        
        // 区间上下限
        double lowerSpot = spotPrice * (1 - beta * spotVol * Math.sqrt(T) * m);
        double upperSpot = spotPrice * (1 + beta * spotVol * Math.sqrt(T) * m);
        
        // 模拟费用（简化计算）
        double optionFeePerTon = spotPrice * spotVol * Math.sqrt(T) * 0.25;
        double serviceFeePerTon = optionFeePerTon * lambda;
        double totalFeePerTon = optionFeePerTon + serviceFeePerTon;
        double totalFee = totalFeePerTon * quantity;
        
        // VaR计算
        double varBefore = calculateVaR(spotPrice, quantity, spotVol, T);
        double varAfter = calculateVaR(spotPrice, quantity, spotVol * 0.5, T) + totalFee;
        double reductionRate = Math.abs(varBefore - varAfter) / varBefore;
        
        // 构建响应
        Map<String, Object> result = new HashMap<>();
        result.put("spotPrice", spotPrice);
        result.put("futuresPrice", futuresPrice);
        result.put("lowerSpot", lowerSpot);
        result.put("upperSpot", upperSpot);
        result.put("feePerTon", totalFeePerTon);
        result.put("totalFee", totalFee);
        result.put("varBefore", varBefore);
        result.put("varAfter", varAfter);
        result.put("reductionRate", reductionRate);
        
        return result;
    }

    /**
     * 获取偏好系数
     */
    private double getPreferenceCoefficient(String preference) {
        switch (preference) {
            case "stronger":
                return 0.6;
            case "lowerFee":
                return 1.4;
            default: // balanced
                return 1.0;
        }
    }

    /**
     * 计算VaR
     */
    private double calculateVaR(double price, int quantity, double volatility, double T) {
        double Z = 1.65; // 95%置信度
        return price * quantity * Z * volatility * Math.sqrt(T);
    }

    /**
     * 生成情景分析数据
     */
    private Map<String, Object>[] generateScenarios(double spotPrice, double strikeSpot, double feePerTon, int quantity) {
        double[] priceChanges = {-0.10, -0.05, 0, 0.05, 0.10};
        Map<String, Object>[] scenarios = new Map[priceChanges.length];
        
        for (int i = 0; i < priceChanges.length; i++) {
            double delta = priceChanges[i];
            double futurePrice = spotPrice * (1 + delta);
            double costWithoutProtection = futurePrice * quantity;
            double costWithProtection = Math.min(futurePrice, strikeSpot) * quantity + feePerTon * quantity;
            double savings = costWithoutProtection - costWithProtection;
            
            Map<String, Object> scenario = new HashMap<>();
            scenario.put("priceChange", delta);
            scenario.put("futurePrice", futurePrice);
            scenario.put("costWithoutProtection", costWithoutProtection);
            scenario.put("costWithProtection", costWithProtection);
            scenario.put("savings", savings);
            
            scenarios[i] = scenario;
        }
        
        return scenarios;
    }

    /**
     * 计算请求数据模型
     */
    public static class CalculationRequest {
        private String direction;
        private String symbol;
        private int quantity;
        private int periodDays;
        private String concern;
        private String preference;
        private String region;

        // Getters and Setters
        public String getDirection() { return direction; }
        public void setDirection(String direction) { this.direction = direction; }
        public String getSymbol() { return symbol; }
        public void setSymbol(String symbol) { this.symbol = symbol; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public int getPeriodDays() { return periodDays; }
        public void setPeriodDays(int periodDays) { this.periodDays = periodDays; }
        public String getConcern() { return concern; }
        public void setConcern(String concern) { this.concern = concern; }
        public String getPreference() { return preference; }
        public void setPreference(String preference) { this.preference = preference; }
        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }
    }
}
