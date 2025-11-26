package com.tuk.mina.api.ctl.mcp;

import com.tuk.mina.dto.mcp.McpDto;
import com.tuk.mina.api.svc.mcp.McpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/api/mcp")
@CrossOrigin("*")
@Slf4j
@RestController
@Tag(name = "Mcp Controller", description = "프로젝트 관련 API")
public class mcpCtl {

    private final McpService mcpService;

    public mcpCtl(McpService mcpService) {
        this.mcpService = mcpService;
    }

    // ==========================================
    // 1. MCP 관리 API (URL: /api/mcp/...)
    // ==========================================

    @GetMapping("/list")
    @Operation(summary = "서비스 목록 조회", description = "파이썬 AI 서버에서 서비스 목록을 가져옵니다.")
    public ResponseEntity<?> getServices() {
        return ResponseEntity.ok(mcpService.getAvailableServices());
    }

    @PostMapping("/guide")
    @Operation(summary = "AI 가이드 요청", description = "자연어 요청을 파이썬 AI 서버로 보내 설정값을 받습니다.")
    public ResponseEntity<?> getServiceGuide(@RequestBody McpDto.GuideRequest request) {
        return ResponseEntity.ok(mcpService.getServiceGuide(request));
    }

    @PostMapping("/register")
    @Operation(summary = "서비스 등록", description = "새로운 서비스를 파이썬 AI 서버에 등록합니다.")
    public ResponseEntity<?> registerService(@RequestBody Map<String, Object> serviceData) {
        return ResponseEntity.ok(mcpService.registerService(serviceData));
    }

    // ==========================================
    // 2. 프로젝트 연결 API (URL: /api/projects/...)
    // ==========================================

    @PostMapping("/projects/{projectId}/link")
    @Operation(summary = "프로젝트 연결", description = "서비스 설정값(토큰 등)을 프로젝트에 저장합니다 (Mock).")
    public ResponseEntity<?> linkService(
            @PathVariable int projectId,
            @RequestBody McpDto.LinkRequest request) {

        mcpService.linkServiceToProject(projectId, request);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Service linked successfully"
        ));
    }

    @PostMapping("/projects/{projectId}/unlink")
    @Operation(summary = "프로젝트 연결 해제")
    public ResponseEntity<?> unlinkService(
            @PathVariable int projectId,
            @RequestBody McpDto.UnlinkRequest request) {

        mcpService.unlinkServiceFromProject(projectId, request.getServiceId());

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Service unlinked successfully"
        ));
    }

    @PostMapping("/register-preset")
    @Operation(summary = "간편 서비스 등록", description = "사전 정의된 템플릿(Notion, GitHub 등)을 사용하여 서비스를 등록합니다.")
    public ResponseEntity<?> registerPreset(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(mcpService.registerPreset(payload));
    }

    @PostMapping("/execute-actions")
    @Operation(summary = "도구 실행", description = "AI 에이전트를 통해 실제 도구(Notion 저장, Jira 생성 등)를 실행합니다.")
    public ResponseEntity<?> executeActions(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(mcpService.executeActions(payload));
    }
}