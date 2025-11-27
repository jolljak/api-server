package com.tuk.mina.api.ctl.project;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tuk.mina.api.svc.project.ProjServiceInfoSvc;
import com.tuk.mina.vo.project.TbProjServiceInfoVo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/api/project/service/info")
@CrossOrigin("*")
@Slf4j
@RestController
@Tag(name = "Project Service Info Controller", description = "프로젝트 서비스 정보 관련 API")
public class projServiceInfoCtl {

    @Autowired
    private ProjServiceInfoSvc projServiceInfoSvc;

    @GetMapping
    @Operation(summary = "Get Project Service Info List", description = "프로젝트 서비스 정보 목록 조회 API")
    public ResponseEntity<List<TbProjServiceInfoVo>> getProjServiceInfo(TbProjServiceInfoVo param) {
        return ResponseEntity.ok(projServiceInfoSvc.getProjServiceInfo(param));
    }

    @PostMapping
    @Operation(summary = "Create New Project Service Info", description = "새로운 프로젝트 서비스 정보 등록 API")
    public ResponseEntity<String> newProjServiceInfo(@RequestBody List<TbProjServiceInfoVo> param) {
        projServiceInfoSvc.newProjServiceInfo(param);
        return ResponseEntity.ok("프로젝트 서비스 정보가 등록되었습니다.");
    }
    
    @PutMapping
    @Operation(summary = "Update Project Service Info", description = "프로젝트 서비스 정보 수정 API")
    public ResponseEntity<String> putProjServiceInfo(@RequestBody TbProjServiceInfoVo param) {
        projServiceInfoSvc.putProjServiceInfo(param);
        return ResponseEntity.ok("프로젝트 서비스 정보가 수정되었습니다.");
    }

    @DeleteMapping
    @Operation(summary = "Delete Project Service Info", description = "프로젝트 서비스 정보 삭제 API")
    public ResponseEntity<String> delProjServiceInfo(TbProjServiceInfoVo param) {
        projServiceInfoSvc.delProjServiceInfo(param);
        return ResponseEntity.ok("프로젝트 서비스 정보가 삭제되었습니다.");
    }
}
