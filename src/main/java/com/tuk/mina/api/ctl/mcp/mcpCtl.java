package com.tuk.mina.api.ctl.mcp;

import com.tuk.mina.dto.mcp.McpDto;
import com.tuk.mina.api.svc.mcp.McpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/api/mcp")
@CrossOrigin("*")
@Slf4j
@RestController
@Tag(name = "Mcp Controller", description = "MCP 관련 API")
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

    // ==========================================
    // 2. 프로젝트 연결 API
    // ==========================================

    @PostMapping("/projects/{projectId}/link")
    @Operation(summary = "프로젝트 연결", description = "서비스 설정값(토큰 등)을 프로젝트에 저장합니다.")
    public ResponseEntity<?> linkService(
            @PathVariable int projectId,
            @RequestBody McpDto.LinkRequest request) {
        mcpService.linkServiceToProject(projectId, request);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Service linked successfully"));
    }

    @PostMapping("/projects/{projectId}/unlink")
    @Operation(summary = "프로젝트 연결 해제")
    public ResponseEntity<?> unlinkService(
            @PathVariable int projectId,
            @RequestBody McpDto.UnlinkRequest request) {
        mcpService.unlinkServiceFromProject(projectId, request.getServiceId());
        return ResponseEntity.ok(Map.of("status", "success", "message", "Service unlinked successfully"));
    }

    @GetMapping("/projects/{projectId}/config")
    @Operation(summary = "프로젝트 서비스 설정 조회", description = "Python AI 서버에서 호출하여 저장된 서비스 설정을 조회합니다.")
    public ResponseEntity<?> getProjectServiceConfig(@PathVariable int projectId) {
        return ResponseEntity.ok(mcpService.getProjectServiceConfig(projectId));
    }

    // ==========================================
    // 3. 대시보드 API (URL: /api/mcp/dashboard/...)
    // ==========================================

    @GetMapping("/dashboard/weekly-calendar")
    @Operation(summary = "주간 캘린더", description = "해당 주의 업무 일정을 조회합니다.")
    public ResponseEntity<?> getWeeklyCalendar(
            @RequestParam String userId,
            @RequestParam(required = false) String startDate) {
        return ResponseEntity.ok(mcpService.getWeeklyCalendar(userId, startDate));
    }

    @GetMapping("/dashboard/today-tasks")
    @Operation(summary = "오늘 마감 업무", description = "오늘 마감인 업무 목록을 조회합니다.")
    public ResponseEntity<?> getTodayDueTasks(@RequestParam String userId) {
        return ResponseEntity.ok(mcpService.getTodayDueTasks(userId));
    }

    @GetMapping("/dashboard/leader")
    @Operation(summary = "팀장 대시보드", description = "팀장용 프로젝트 현황 및 업무 부하 분석 데이터를 조회합니다.")
    public ResponseEntity<?> getLeaderDashboard(@RequestParam int teamId) {
        return ResponseEntity.ok(mcpService.getLeaderDashboard(teamId));
    }

    @GetMapping("/dashboard/workload")
    @Operation(summary = "팀원 업무 부하", description = "팀원별 업무 현황을 조회합니다.")
    public ResponseEntity<?> getTeamWorkload(@RequestParam int teamId) {
        return ResponseEntity.ok(mcpService.getTeamWorkload(teamId));
    }

    @GetMapping("/dashboard/project-progress")
    @Operation(summary = "프로젝트 진행률", description = "프로젝트별 진행률을 조회합니다.")
    public ResponseEntity<?> getProjectProgress(@RequestParam(required = false) Integer projectId) {
        return ResponseEntity.ok(mcpService.getProjectProgress(projectId));
    }

    @GetMapping("/dashboard/google-calendar")
    @Operation(summary = "구글 캘린더", description = "최근 구글 캘린더 일정 5개를 조회합니다.")
    public ResponseEntity<?> getGoogleCalendarEvents() {
        return ResponseEntity.ok(mcpService.getGoogleCalendarEvents());
    }
}
