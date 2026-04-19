package com.example.demoback.demos.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 市场数据控制器
 */
@RestController
@RequestMapping("/api/market")
public class MarketDataController {

    /**
     * 获取市场参考价
     */
    @GetMapping("/price")
    public Map<String, Object> getMarketPrice(@RequestParam String symbol) {
        Map<String, Object> result = new HashMap<>();
        
        // 模拟市场数据
        if ("PP".equals(symbol)) {
            result.put("spotPrice", 7600);
            result.put("futuresPrice", 7550);
            result.put("spotVol", 0.15);
            result.put("futuresVol", 0.18);
            result.put("updateTime", "2026-04-19 16:00");
        } else if ("PE".equals(symbol)) {
            result.put("spotPrice", 7800);
            result.put("futuresPrice", 7750);
            result.put("spotVol", 0.12);
            result.put("futuresVol", 0.15);
            result.put("updateTime", "2026-04-19 16:00");
        }
        
        return result;
    }

    /**
     * 获取历史价格走势
     */
    @GetMapping("/trend")
    public Map<String, Object> getPriceTrend(@RequestParam String symbol) {
        Map<String, Object> result = new HashMap<>();
        
        // 模拟历史数据
        double[] prices = new double[30];
        double basePrice = "PP".equals(symbol) ? 7500 : 7700;
        
        for (int i = 0; i < 30; i++) {
            // 模拟价格波动
            double random = (Math.random() - 0.5) * 200;
            prices[i] = basePrice + random + (i * 10);
        }
        
        result.put("prices", prices);
        result.put("dates", generateDates(30));
        
        return result;
    }

    /**
     * 生成日期数组
     */
    private String[] generateDates(int days) {
        String[] dates = new String[days];
        java.time.LocalDate today = java.time.LocalDate.now();
        
        for (int i = 0; i < days; i++) {
            java.time.LocalDate date = today.minusDays(days - i - 1);
            dates[i] = date.toString();
        }
        
        return dates;
    }
}
