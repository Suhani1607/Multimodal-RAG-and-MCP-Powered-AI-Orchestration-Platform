package com.multimodalrag.api.health;

import java.time.Instant;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
            "status", "UP",
            "service", "spring-boot-orchestrator",
            "timestamp", Instant.now().toString()
        );
    }

    @GetMapping("/api/v1/system/status")
    public Map<String, Object> systemStatus() {
        return Map.of(
            "orchestrator", Map.of("status", "UP"),
            "ragService", Map.of("status", "NOT_CHECKED", "url", "http://localhost:8000/health"),
            "mcp", Map.of("status", "NOT_CONFIGURED")
        );
    }
}
