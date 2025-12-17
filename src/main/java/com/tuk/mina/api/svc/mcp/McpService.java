package com.tuk.mina.api.svc.mcp;

import com.tuk.mina.dto.mcp.McpDto;
import com.tuk.mina.api.svc.project.ProjServiceInfoSvc;
import com.tuk.mina.vo.project.TbProjServiceInfoVo;
import com.tuk.mina.dao.task.TbTaskDao;
import com.tuk.mina.dao.project.TbProjectDao;
import com.tuk.mina.dao.team.TbTeamUserMapDao;
import com.tuk.mina.vo.task.TbTaskVo;
import com.tuk.mina.vo.project.TbProjectVo;
import com.tuk.mina.vo.team.TbTeamUserMapVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private TbTaskDao taskDao;

    @Autowired
    private TbProjectDao projectDao;

    @Autowired
    private TbTeamUserMapDao teamUserMapDao;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    // --- 1. Proxy Methods (To Python AI Server) ---

    public Object getAvailableServices() {
        String url = aiServerUrl + "/services/list";
        log.info("Proxying GET to: {}", url);
        return restTemplate.getForObject(url, Object.class);
    }

    public Object getServiceGuide(McpDto.GuideRequest request) {
        String url = aiServerUrl + "/services/guide";
        log.info("Proxying POST to: {}", url);
        return restTemplate.postForObject(url, request, Object.class);
    }

    public Object registerService(Map<String, Object> serviceData) {
        String url = aiServerUrl + "/services/register";
        log.info("Proxying POST to: {}", url);
        return restTemplate.postForObject(url, serviceData, Object.class);
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

    public Object getGoogleCalendarEvents() {
        String url = aiServerUrl + "/services/google-calendar/events";
        log.info("Proxying GET (Calendar) to: {}", url);
        return restTemplate.getForObject(url, Object.class);
    }

    // --- 2. 실제 DB 연동 Methods ---

    public void linkServiceToProject(int projectId, McpDto.LinkRequest request) {
        log.info("Linking Service to Project [DB]");
        log.info("Project: {}, Service: {}, Config: {}", projectId, request.getServiceId(), request.getConfig());

        List<TbProjServiceInfoVo> configList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : request.getConfig().entrySet()) {
            TbProjServiceInfoVo vo = new TbProjServiceInfoVo();
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
}
