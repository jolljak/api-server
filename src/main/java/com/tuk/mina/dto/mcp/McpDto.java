package com.tuk.mina.dto.mcp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;
import java.util.List;

public class McpDto {

    // 1. AI 가이드 요청 (프론트 -> 백엔드 -> 파이썬)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GuideRequest {
        private String request;
    }

    // 2. 프로젝트 연결 요청 (프론트 -> 백엔드 저장)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LinkRequest {
        private Integer serviceId;
        private Map<String, Object> config; // 토큰, repo, owner 등
    }

    // 3. 연결 해제 요청
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UnlinkRequest {
        private Integer serviceId;
    }

    // 4. 도구 실행 요청 (프론트 -> 백 -> 파이썬)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExecuteRequest {
        @JsonProperty("projectId")
        private Integer projectId;

        @JsonProperty("serviceType")
        private String serviceType;

        @JsonProperty("items")
        private List<Map<String, Object>> items;

        @JsonProperty("config")
        private Map<String, Object> config; // [NEW] Added for unified registration/execution

        @JsonProperty("context")
        private Map<String, Object> context; // [NEW] Added for environment context (e.g., projectName)
    }
}