package com.tuk.mina.dto.mcp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

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
}