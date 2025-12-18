package com.tuk.mina.api.svc.mcp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuk.mina.dto.mcp.McpDto;
import com.tuk.mina.api.svc.project.ProjServiceInfoSvc;
import com.tuk.mina.vo.project.TbProjServiceInfoVo;
import com.tuk.mina.dao.task.TbTaskDao;
import com.tuk.mina.dao.project.TbProjectDao;
import com.tuk.mina.dao.project.TbProjServiceInfoDao;
import com.tuk.mina.dao.team.TbTeamUserMapDao;
import com.tuk.mina.vo.task.TbTaskVo;
import com.tuk.mina.vo.project.TbProjectVo;
import com.tuk.mina.vo.team.TbTeamUserMapVo;
import com.tuk.mina.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class McpService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProjServiceInfoSvc projServiceInfoSvc;
    
    @Autowired
    private TbProjServiceInfoDao projServiceInfoDao;

    @Autowired
    private TbTaskDao taskDao;

    @Autowired
    private TbProjectDao projectDao;

    @Autowired
    private TbTeamUserMapDao teamUserMapDao;
    
    @Autowired
    private SecurityUtil securityUtil;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    // --- 1. Service Master Methods (DB Based) ---

    public List<Map<String, Object>> getAvailableServices() {
        List<TbProjServiceInfoVo> serviceMasters = projServiceInfoSvc.getServiceMasterList();
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (TbProjServiceInfoVo vo : serviceMasters) {
            try {
                // fieldValue에 저장된 JSON을 Map으로 파싱
                Map<String, Object> serviceData = objectMapper.readValue(
                    vo.getFieldValue(), 
                    new TypeReference<Map<String, Object>>() {}
                );
                result.add(serviceData);
            } catch (Exception e) {
                log.error("Failed to parse service master data: {}", vo.getFieldValue(), e);
            }
        }
        
        return result;
    }

    public Object getServiceGuide(McpDto.GuideRequest request) {
        String url = aiServerUrl + "/services/guide";
        log.info("Proxying POST to: {}", url);
        return restTemplate.postForObject(url, request, Object.class);
    }

    public Map<String, Object> registerService(Map<String, Object> serviceData) {
        log.info("Starting service registration. payload: {}", serviceData);
        try {
            // serviceId 추출 (타입 안전하게: JSON 숫자는 Long이나 Double로 올 수 있음)
            Object sidObj = serviceData.get("serviceId");
            Integer serviceId = null;
            
            if (sidObj != null) {
                if (sidObj instanceof Number) {
                    serviceId = ((Number) sidObj).intValue();
                } else if (sidObj instanceof String) {
                    try {
                        serviceId = Integer.parseInt((String) sidObj);
                    } catch (NumberFormatException ignored) {}
                }
            }

            if (serviceId == null) {
                log.info("serviceId is missing, generating a new one from master list.");
                // serviceId가 없으면 현재 마스터 목록에서 최대값 + 1을 찾음
                List<TbProjServiceInfoVo> masters = projServiceInfoSvc.getServiceMasterList();
                serviceId = masters.stream()
                        .filter(m -> m.getServiceId() != null)
                        .mapToInt(TbProjServiceInfoVo::getServiceId)
                        .max()
                        .orElse(0) + 1;
                
                // 가급적 원본 맵을 수정하기 위해 상위 레벨에서 HashMap 보장 권장
                try {
                    serviceData.put("serviceId", serviceId);
                } catch (UnsupportedOperationException e) {
                    log.warn("Payload map is unmodifiable, creating a copy.");
                    serviceData = new HashMap<>(serviceData);
                    serviceData.put("serviceId", serviceId);
                }
            }

            log.info("Final serviceId to be registered: {}", serviceId);

            // serviceData를 JSON 문자열로 변환
            String jsonData = objectMapper.writeValueAsString(serviceData);
            
            // 이미 존재하는지 확인 (projectId=0, serviceId=?)
            TbProjServiceInfoVo param = new TbProjServiceInfoVo();
            param.setProjectId(0);
            param.setServiceId(serviceId);
            List<TbProjServiceInfoVo> existing = projServiceInfoSvc.getProjServiceInfo(param);

            TbProjServiceInfoVo vo = new TbProjServiceInfoVo();
            vo.setServiceId(serviceId);
            vo.setProjectId(0);  
            vo.setServiceField("service_master");
            vo.setFieldValue(jsonData);

            if (existing != null && !existing.isEmpty()) {
                // 업데이트
                log.info("Existing service found for ID {}. Performing UPDATE.", serviceId);
                projServiceInfoSvc.putProjServiceInfo(vo);
            } else {
                // 신규 등록
                log.info("New service for ID {}. Performing INSERT.", serviceId);
                projServiceInfoSvc.newProjServiceInfo(List.of(vo));
            }
            
            return Map.of(
                "status", "success", 
                "message", "Service registered successfully",
                "serviceId", serviceId
            );
        } catch (Exception e) {
            log.error("Failed in registerService: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to register service: " + e.getMessage());
        }
    }

    public Map<String, Object> registerPreset(Map<String, Object> payload) {
        log.info("Registering preset service to DB: {}", payload.get("serviceType"));
        
        // serviceType을 기반으로 serviceId 자동 매핑
        String serviceType = (String) payload.get("serviceType");
        Integer serviceId = getServiceIdByType(serviceType);
        
        if (serviceId == null) {
            throw new RuntimeException("Unknown service type: " + serviceType);
        }
        
        // payload에 serviceId 추가
        payload.put("serviceId", serviceId);
        
        // 필요한 필드들을 serviceName, description, requiredFields와 함께 구성
        if (!payload.containsKey("serviceName")) {
            payload.put("serviceName", getServiceNameByType(serviceType));
        }
        if (!payload.containsKey("description")) {
            payload.put("description", getServiceDescriptionByType(serviceType));
        }
        if (!payload.containsKey("requiredFields")) {
            payload.put("requiredFields", getRequiredFieldsByType(serviceType));
        }
        if (!payload.containsKey("icon")) {
            payload.put("icon", serviceType + "-icon.png");
        }
        
        return registerService(payload);
    }
    
    private Integer getServiceIdByType(String serviceType) {
        return switch (serviceType.toLowerCase()) {
            case "notion" -> 1;
            case "github" -> 2;
            case "jira" -> 3;
            case "google-calendar", "googlecalendar", "google_calendar" -> 4;
            default -> null;
        };
    }
    
    private String getServiceNameByType(String serviceType) {
        return switch (serviceType.toLowerCase()) {
            case "notion" -> "Notion";
            case "github" -> "GitHub";
            case "jira" -> "Jira";
            case "google-calendar", "googlecalendar", "google_calendar" -> "Google Calendar";
            default -> serviceType;
        };
    }
    
    private String getServiceDescriptionByType(String serviceType) {
        return switch (serviceType.toLowerCase()) {
            case "notion" -> "Notion 페이지 생성 및 관리";
            case "github" -> "GitHub Issue 생성 및 관리";
            case "jira" -> "Jira 티켓 생성 및 관리";
            case "google-calendar", "googlecalendar", "google_calendar" -> "Google Calendar 일정 조회";
            default -> "";
        };
    }
    
    private List<String> getRequiredFieldsByType(String serviceType) {
        return switch (serviceType.toLowerCase()) {
            case "notion" -> List.of("token", "databaseId");
            case "github" -> List.of("token", "owner", "repo");
            case "jira" -> List.of("url", "email", "apiToken", "projectKey");
            case "google-calendar", "googlecalendar", "google_calendar" -> List.of("clientId", "clientSecret", "refreshToken");
            default -> List.of();
        };
    }

    public Object executeActions(McpDto.ExecuteRequest payload) {
        Integer projectId = payload.getProjectId();
        String serviceType = payload.getServiceType();
        
        log.info("Executing MCP actions for project: {}, service: {}", projectId, serviceType);

        // [NEW] 환경 컨텍스트 수집 (프로젝트 이름 등)
        Map<String, Object> contextMap = new HashMap<>();
        if (projectId != null && projectId != 0) {
            TbProjectVo projectParam = new TbProjectVo();
            projectParam.setProjectId(projectId);
            List<TbProjectVo> projects = projectDao.getProject(projectParam);
            if (!projects.isEmpty()) {
                contextMap.put("projectName", projects.get(0).getProjectName());
                log.info("Included project name '{}' in context", projects.get(0).getProjectName());
            }
        }
        payload.setContext(contextMap);
        
        // 1. 서비스 정보가 있는 경우 백엔드에서 설정을 조회하여 payload에 포함
        if (serviceType != null) {
            try {
                // projectId가 없으면 Master(0) 설정을 사용하도록 fallback
                Integer targetProjectId = (projectId != null) ? projectId : 0;
                Integer serviceId = getServiceIdByType(serviceType);
                
                if (serviceId != null) {
                    TbProjServiceInfoVo param = new TbProjServiceInfoVo();
                    param.setProjectId(targetProjectId);
                    param.setServiceId(serviceId);
                    List<TbProjServiceInfoVo> configs = projServiceInfoSvc.getProjServiceInfo(param);

                    // [Removed] rawServiceConfig injection to avoid un-normalized data leaking.
                    // Now using payload.setConfig(finalConfig) for unified configuration.
                    
                    // 마스터 설정 조회 (Fallback용으로 항상 준비)
                    Map<String, Object> masterMap = new HashMap<>();
                    TbProjServiceInfoVo masterParam = new TbProjServiceInfoVo();
                    masterParam.setProjectId(0);
                    masterParam.setServiceId(serviceId);
                    List<TbProjServiceInfoVo> masterConfigs = projServiceInfoSvc.getProjServiceInfo(masterParam);
                    if (!masterConfigs.isEmpty()) {
                        Optional<TbProjServiceInfoVo> masterVo = masterConfigs.stream()
                            .filter(vo -> "service_master".equals(vo.getServiceField()))
                            .findFirst();
                        if (masterVo.isPresent()) {
                            masterMap = objectMapper.readValue(masterVo.get().getFieldValue(), new TypeReference<Map<String, Object>>() {});
                        }
                    }

                    Map<String, Object> finalConfig = new HashMap<>();
                    if (!masterMap.isEmpty() || !configs.isEmpty()) {
                        // 1. 모든 키 정규화 (dots 제거)하여 임시 맵에 저장
                        Map<String, Object> allConfigs = new HashMap<>();
                        
                        // (1) Master JSON 기반 (가장 낮은 우선순위)
                        masterMap.forEach((k, v) -> {
                            String shortK = k.contains(".") ? k.substring(k.lastIndexOf(".") + 1) : k;
                            allConfigs.put(shortK, v);
                        });

                        // (2) Master 개별 행 기반 (그 다음)
                        if (!masterConfigs.isEmpty()) {
                            for (TbProjServiceInfoVo vo : masterConfigs) {
                                if (!"service_master".equals(vo.getServiceField())) {
                                    String f = vo.getServiceField();
                                    String shortK = f.contains(".") ? f.substring(f.lastIndexOf(".") + 1) : f;
                                    allConfigs.put(shortK, vo.getFieldValue());
                                }
                            }
                        }

                        // (3) 프로젝트 개별 행 기반 (가장 높은 우선순위)
                        for (TbProjServiceInfoVo vo : configs) {
                            if (!"service_master".equals(vo.getServiceField())) {
                                String f = vo.getServiceField();
                                String shortK = f.contains(".") ? f.substring(f.lastIndexOf(".") + 1) : f;
                                allConfigs.put(shortK, vo.getFieldValue());
                            }
                        }

                        // 2. 토큰 필드 특별 처리 및 정리
                        String tokenCandidate = null;
                        if (allConfigs.containsKey("token")) tokenCandidate = String.valueOf(allConfigs.get("token"));
                        else if (allConfigs.containsKey("runtimeAuthKey")) tokenCandidate = String.valueOf(allConfigs.get("runtimeAuthKey"));
                        else if (allConfigs.containsKey("apiToken")) tokenCandidate = String.valueOf(allConfigs.get("apiToken"));

                        // 3. 최종 cleanedConfig 생성
                        Map<String, Object> cleanedConfig = new HashMap<>();
                        allConfigs.forEach((k, v) -> {
                            // 점(.)이 포함된 키와 구형 키(runtimeAuthKey, apiToken)는 제외
                            if (!k.contains(".") && !"runtimeAuthKey".equals(k) && !"apiToken".equals(k)) {
                                cleanedConfig.put(k, v);
                            }
                        });
                        
                        if (tokenCandidate != null && !tokenCandidate.trim().isEmpty() && !"null".equals(tokenCandidate)) {
                            cleanedConfig.put("token", tokenCandidate.trim());
                        }
                        
                        finalConfig = cleanedConfig;
                    }
                    
                    if (!finalConfig.isEmpty()) {
                        payload.setConfig(finalConfig);
                        log.info("[MCP] Injected Final Config for {}: {}", serviceType, finalConfig.keySet());
                    } else {
                        log.warn("[MCP] No configuration found for service {}", serviceType);
                    }
                }
            } catch (Exception e) {
                log.error("Failed to prepare service context for executeActions", e);
            }
        }

        // 2. AI 서버로 통합된 요청 전송 (하나의 POST로 등록+실행 처리)
        String executeUrl = aiServerUrl + "/execute-actions";
        log.info("Proxying unified execute-actions request to: {}", executeUrl);
        
        return restTemplate.postForObject(executeUrl, payload, Object.class);
    }

    public Object getGoogleCalendarEvents() {
        // 1. DB에서 Google Calendar 마스터 정보 조회 (serviceId=4, projectId=0)
        TbProjServiceInfoVo param = new TbProjServiceInfoVo();
        param.setServiceId(4);
        param.setProjectId(0);
        param.setServiceField("service_master");
        
        List<TbProjServiceInfoVo> masters = projServiceInfoDao.getProjServiceInfo(param);
        if (masters.isEmpty()) {
            log.warn("Google Calendar service not registered.");
            return Map.of("status", "error", "message", "Service not registered");
        }
        
        try {
            // 2. JSON에서 토큰 추출
            Map<String, Object> serviceData = objectMapper.readValue(
                masters.get(0).getFieldValue(), 
                new TypeReference<Map<String, Object>>() {}
            );
            
            String token = (String) serviceData.get("token");
            if (token == null || token.isEmpty()) {
                return Map.of("status", "error", "message", "Token missing in configuration");
            }
            
            // 3. AI 서버에 헤더와 함께 요청
            String url = aiServerUrl + "/services/google-calendar/events";
            log.info("Proxying GET (Calendar) to: {}", url);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            return restTemplate.exchange(url, HttpMethod.GET, entity, Object.class).getBody();
            
        } catch (Exception e) {
            log.error("Failed to fetch Google Calendar events", e);
            return Map.of("status", "error", "message", e.getMessage());
        }
    }

    public void linkServiceToProject(int projectId, McpDto.LinkRequest request) {
        log.info("Linking Service to Project [DB]");
        log.info("Project: {}, Service: {}, Config: {}", projectId, request.getServiceId(), request.getConfig());

        // [Fix] 기존 연결 정보가 있으면 삭제 후 재등록 (중복 키 방지)
        TbProjServiceInfoVo delParam = new TbProjServiceInfoVo();
        delParam.setProjectId(projectId);
        delParam.setServiceId(request.getServiceId());
        projServiceInfoSvc.delProjServiceInfo(delParam);
        log.info("Cleared existing config for project {} and service {}", projectId, request.getServiceId());

        List<TbProjServiceInfoVo> configList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : request.getConfig().entrySet()) {
            TbProjServiceInfoVo vo = new TbProjServiceInfoVo();
            vo.setServiceId(request.getServiceId());
            vo.setProjectId(projectId);
            vo.setServiceField(entry.getKey());
            vo.setFieldValue(String.valueOf(entry.getValue()));
            configList.add(vo);
        }
        projServiceInfoSvc.newProjServiceInfo(configList);
    }

    public void unlinkServiceFromProject(int projectId, int serviceId) {
        TbProjServiceInfoVo param = new TbProjServiceInfoVo();
        param.setProjectId(projectId);
        param.setServiceId(serviceId);
        projServiceInfoSvc.delProjServiceInfo(param);
        log.info("Unlinked Service {} from Project {}", serviceId, projectId);
    }

    public List<TbProjServiceInfoVo> getProjectServiceConfig(int projectId) {
        TbProjServiceInfoVo param = new TbProjServiceInfoVo();
        param.setProjectId(projectId);
        return projServiceInfoSvc.getProjServiceInfo(param);
    }

    // --- 3. 대시보드 API Methods ---

    // 📅 주간 캘린더 (TbTaskVo에 taskDueDate가 없어 전체 업무 반환)
    public Map<String, Object> getWeeklyCalendar(String userId, String startDate) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate end = start.plusDays(6);

        TbTaskVo param = new TbTaskVo();
        param.setUserId(userId);
        List<TbTaskVo> allTasks = taskDao.getTask(param);

        Map<String, Object> result = new HashMap<>();
        result.put("startDate", start.toString());
        result.put("endDate", end.toString());
        result.put("tasks", allTasks);
        return result;
    }

    // 🔔 오늘 마감 업무 (현재 TbTaskVo에 taskDueDate가 없어 전체 업무 반환)
    public List<TbTaskVo> getTodayDueTasks(String userId) {
        TbTaskVo param = new TbTaskVo();
        param.setUserId(userId);
        return taskDao.getTask(param);
    }

    // 👨‍💼 팀장 대시보드
    public Map<String, Object> getLeaderDashboard(int teamId) {
        TbTeamUserMapVo teamParam = new TbTeamUserMapVo();
        teamParam.setTeamId(teamId);
        List<TbTeamUserMapVo> members = teamUserMapDao.getTeamUserMap(teamParam);

        List<TbProjectVo> projects = projectDao.getProject(null);

        int totalTasks = 0, completedTasks = 0, delayedTasks = 0;
        for (TbTeamUserMapVo m : members) {
            TbTaskVo taskParam = new TbTaskVo();
            taskParam.setUserId(m.getUserId());
            List<TbTaskVo> tasks = taskDao.getTask(taskParam);
            totalTasks += tasks.size();
            completedTasks += (int) tasks.stream().filter(t -> "DONE".equals(t.getTaskStatusId())).count();
            delayedTasks += (int) tasks.stream().filter(t -> "DELAYED".equals(t.getTaskStatusId())).count();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("teamMembers", members.size());
        result.put("totalProjects", projects.size());
        result.put("totalTasks", totalTasks);
        result.put("completedTasks", completedTasks);
        result.put("delayedTasks", delayedTasks);
        result.put("avgCompletion", totalTasks > 0 ? (completedTasks * 100 / totalTasks) : 0);
        return result;
    }

    // 📊 팀원 업무 부하
    public List<Map<String, Object>> getTeamWorkload(int teamId) {
        TbTeamUserMapVo teamParam = new TbTeamUserMapVo();
        teamParam.setTeamId(teamId);
        List<TbTeamUserMapVo> members = teamUserMapDao.getTeamUserMap(teamParam);

        return members.stream().map(m -> {
            TbTaskVo taskParam = new TbTaskVo();
            taskParam.setUserId(m.getUserId());
            List<TbTaskVo> tasks = taskDao.getTask(taskParam);

            long todo = tasks.stream().filter(t -> "TODO".equals(t.getTaskStatusId())).count();
            long inProgress = tasks.stream().filter(t -> "IN_PROGRESS".equals(t.getTaskStatusId())).count();
            long done = tasks.stream().filter(t -> "DONE".equals(t.getTaskStatusId())).count();

            Map<String, Object> workload = new HashMap<>();
            workload.put("userId", m.getUserId());
            workload.put("role", m.getMemberRole() != null ? m.getMemberRole() : "팀원");
            workload.put("totalTasks", tasks.size());
            workload.put("todo", todo);
            workload.put("inProgress", inProgress);
            workload.put("done", done);
            return workload;
        }).collect(Collectors.toList());
    }

    // 📈 프로젝트 진행률
    public List<Map<String, Object>> getProjectProgress(Integer projectId) {
        TbProjectVo param = null;
        if (projectId != null) {
            param = new TbProjectVo();
            param.setProjectId(projectId);
        }
        List<TbProjectVo> projects = projectDao.getProject(param);

        return projects.stream().map(p -> {
            TbTaskVo taskParam = new TbTaskVo();
            taskParam.setProjectId(p.getProjectId());
            List<TbTaskVo> tasks = taskDao.getTask(taskParam);

            long done = tasks.stream().filter(t -> "DONE".equals(t.getTaskStatusId())).count();
            int progress = tasks.size() > 0 ? (int)(done * 100 / tasks.size()) : 0;

            Map<String, Object> progressMap = new HashMap<>();
            progressMap.put("projectId", p.getProjectId());
            progressMap.put("projectName", p.getProjectName());
            progressMap.put("totalTasks", tasks.size());
            progressMap.put("completedTasks", done);
            progressMap.put("progress", progress);
            return progressMap;
        }).collect(Collectors.toList());
    }
    
    // --- 4. 초기 서비스 등록 ---
    
    public void initDefaultServices() {
        log.info("Initializing default services...");
        
        // 이미 등록된 서비스가 있는지 확인
        List<TbProjServiceInfoVo> existing = projServiceInfoSvc.getServiceMasterList();
        if (!existing.isEmpty()) {
            log.info("Services already initialized. Skipping.");
            return;
        }
        
        // 4개 기본 서비스 등록
        registerDefaultService(1, "Notion", "notion", 
            "Notion 페이지 생성 및 관리", 
            List.of("token", "databaseId"), 
            "notion-icon.png");
            
        registerDefaultService(2, "GitHub", "github", 
            "GitHub Issue 생성 및 관리", 
            List.of("token", "owner", "repo"), 
            "github-icon.png");
            
        registerDefaultService(3, "Jira", "jira", 
            "Jira 티켓 생성 및 관리", 
            List.of("url", "email", "apiToken", "projectKey"), 
            "jira-icon.png");
            
        registerDefaultService(4, "Google Calendar", "google-calendar", 
            "Google Calendar 일정 조회", 
            List.of("clientId", "clientSecret", "refreshToken"), 
            "calendar-icon.png");
        
        log.info("Default services initialized successfully.");
    }
    
    private void registerDefaultService(int serviceId, String serviceName, 
                                          String serviceType, String description, 
                                          List<String> requiredFields, String icon) {
        Map<String, Object> serviceData = new HashMap<>();
        serviceData.put("serviceId", serviceId);
        serviceData.put("serviceName", serviceName);
        serviceData.put("serviceType", serviceType);
        serviceData.put("description", description);
        serviceData.put("requiredFields", requiredFields);
        serviceData.put("icon", icon);
        
        registerService(serviceData);
    }
}
