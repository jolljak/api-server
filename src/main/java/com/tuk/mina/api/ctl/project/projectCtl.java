package com.tuk.mina.api.ctl.project;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tuk.mina.dao.project.TbProjectDao;
import com.tuk.mina.util.SecurityUtil;
import com.tuk.mina.vo.project.TbProjectVo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RequestMapping("/api/project")
@CrossOrigin("*")
@Slf4j
@RestController
@Tag(name = "Project Controller", description = "프로젝트 관련 API")
public class projectCtl {

    @Autowired
    private TbProjectDao projectDao;

    @Autowired
    private SecurityUtil securityUtil;

    @GetMapping
    @Operation(summary = "Get Project List", description = "프로젝트 목록 조회 API")
    public ResponseEntity<List<TbProjectVo>> getProject(TbProjectVo param) {
        return ResponseEntity.ok(projectDao.getProject(param));
    }

    @PostMapping
    @Operation(summary = "Create New Project", description = "새로운 프로젝트 등록 API")
    public ResponseEntity<String> newProject(@RequestBody TbProjectVo param) {
        param.setCreatedUserId(securityUtil.getAuthUserId().get());
        param.setUpdatedUserId(securityUtil.getAuthUserId().get());
        projectDao.newProject(param);
        return ResponseEntity.ok("프로젝트가 등록되었습니다.");
    }

    @PutMapping
    @Operation(summary = "Update Project", description = "프로젝트 수정 API")
    public ResponseEntity<String> putProject(@RequestBody TbProjectVo param) {
        param.setUpdatedUserId(securityUtil.getAuthUserId().get());
        projectDao.putProject(param);
        return ResponseEntity.ok("프로젝트가 수정되었습니다.");
    }

    @DeleteMapping
    @Operation(summary = "Delete Project", description = "프로젝트 삭제 API")
    public ResponseEntity<String> delProject(@RequestParam("projectId") Integer projectId) {
        projectDao.delProject(projectId);
        return ResponseEntity.ok("프로젝트가 삭제되었습니다.");
    }

}
