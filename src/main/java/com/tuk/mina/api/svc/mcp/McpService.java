package com.tuk.mina.api.svc.mcp;

import com.tuk.mina.dto.mcp.McpDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class McpService {

    private final RestTemplate restTemplate;

    // application.properties에 ai.server.url=http://localhost:8000 설정 필요
    @Value("${ai.server.url}")
    private String aiServerUrl;

    // [Mock DB] 프로젝트별 연결 정보 저장소
    // Key: ProjectId, Value: List of Configs
    private static final Map<Integer, List<Map<String, Object>>> mockDb = new ConcurrentHashMap<>();

    // --- 1. Proxy Methods (To Python AI Server) ---

    // 서비스 목록 조회
    public Object getAvailableServices() {
        String url = aiServerUrl + "/services/list";
        log.info("Proxying GET to: {}", url);
        return restTemplate.getForObject(url, Object.class);
    }

    // AI 가이드 요청
    public Object getServiceGuide(McpDto.GuideRequest request) {
        String url = aiServerUrl + "/services/guide";
        log.info("Proxying POST to: {}", url);
        return restTemplate.postForObject(url, request, Object.class);
    }

    // 서비스 등록
    public Object registerService(Map<String, Object> serviceData) {
        String url = aiServerUrl + "/services/register";
        log.info("Proxying POST to: {}", url);
        return restTemplate.postForObject(url, serviceData, Object.class);
    }

    // --- 2. Mock DB Methods (Local Storage) ---

    // 프로젝트에 서비스 연결 (설정값 저장)
    public void linkServiceToProject(int projectId, McpDto.LinkRequest request) {
        log.info("Linking Service to Project [Mock DB]");
        log.info("Project: {}, Service: {}, Config: {}", projectId, request.getServiceId(), request.getConfig());

        mockDb.computeIfAbsent(projectId, k -> new ArrayList<>());
        List<Map<String, Object>> links = mockDb.get(projectId);

        // 중복 제거 (기존 연결 있으면 삭제 후 갱신)
        links.removeIf(link -> {
            Integer sid = (Integer) link.get("serviceId");
            return sid != null && sid.equals(request.getServiceId());
        });

        // 저장
        Map<String, Object> newLink = new HashMap<>();
        newLink.put("serviceId", request.getServiceId());
        newLink.put("config", request.getConfig());
        newLink.put("linkedAt", new Date());

        links.add(newLink);
    }

    // 연결 해제
    public void unlinkServiceFromProject(int projectId, int serviceId) {
        if (mockDb.containsKey(projectId)) {
            mockDb.get(projectId).removeIf(link -> {
                Integer sid = (Integer) link.get("serviceId");
                return sid != null && sid.equals(serviceId);
            });
            log.info("Unlinked Service {} from Project {}", serviceId, projectId);
        }
    }

    public Object registerPreset(Map<String, Object> payload) {
        String url = aiServerUrl + "/services/register-preset";
        log.info("Proxying POST (Preset) to: {}", url);
        return restTemplate.postForObject(url, payload, Object.class);
    }

    public Object executeActions(Map<String, Object> payload) {
        String url = aiServerUrl + "/execute-actions";
        log.info("Proxying POST (Execute) to: {}", url);
        return restTemplate.postForObject(url, payload, Object.class);
    }
}