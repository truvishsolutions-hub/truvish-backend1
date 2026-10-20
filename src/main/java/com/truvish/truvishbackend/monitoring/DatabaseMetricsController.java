package com.truvish.truvishbackend.monitoring;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics/database")
@RequiredArgsConstructor
public class DatabaseMetricsController {

    private final DatabaseMetrics databaseMetrics;


    @GetMapping
    public Map<String, Long> getDatabaseMetrics() {

        Map<String, Long> response = new LinkedHashMap<>();

        long read = databaseMetrics.getReadCount();
        long write = databaseMetrics.getWriteCount();
        long total = databaseMetrics.getTotalCount();

        response.put("read", read);
        response.put("write", write);
        response.put("total", total);

        return response;
    }


    @GetMapping("/details")
    public Map<String, Long> getDetailedDatabaseMetrics() {

        Map<String, Long> response = new LinkedHashMap<>();

        response.put(
                "read",
                databaseMetrics.getReadCount()
        );

        response.put(
                "write",
                databaseMetrics.getWriteCount()
        );

        response.put(
                "select",
                databaseMetrics.getSelectCount()
        );

        response.put(
                "insert",
                databaseMetrics.getInsertCount()
        );

        response.put(
                "update",
                databaseMetrics.getUpdateCount()
        );

        response.put(
                "delete",
                databaseMetrics.getDeleteCount()
        );

        response.put(
                "total",
                databaseMetrics.getTotalCount()
        );

        return response;
    }


    @PostMapping("/reset")
    public Map<String, String> resetDatabaseMetrics() {

        databaseMetrics.reset();

        Map<String, String> response = new LinkedHashMap<>();

        response.put(
                "message",
                "Database metrics reset successfully"
        );

        return response;
    }
}