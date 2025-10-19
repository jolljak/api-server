package com.tuk.mina.api.ctl.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tuk.mina.dao.service.TbServiceDao;
import com.tuk.mina.util.SecurityUtil;
import com.tuk.mina.vo.service.TbServiceVo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RequestMapping("/api/service")
@CrossOrigin("*")
@Slf4j
@RestController
@Tag(name = "Service Controller", description = "서비스 관련 API")
public class serviceCtl {

    @Autowired
    private TbServiceDao serviceDao;

    @Autowired
    private SecurityUtil securityUtil;

    @GetMapping
    @Operation(summary = "Get Service List", description = "서비스 목록 조회 API")
    public ResponseEntity<List<TbServiceVo>> getService(TbServiceVo param) {
        return ResponseEntity.ok(serviceDao.getService(param));
    }

    @PostMapping
    @Operation(summary = "Create New Service", description = "새로운 서비스 등록 API")
    public ResponseEntity<String> newService(@RequestBody TbServiceVo param) {
        param.setCreateUserId(securityUtil.getAuthUserId().get());
        serviceDao.newService(param);
        return ResponseEntity.ok("서비스가 등록되었습니다.");
    }
    
    @PutMapping
    @Operation(summary = "Update Service", description = "서비스 수정 API")
    public ResponseEntity<String> putService(@RequestBody TbServiceVo param) {
        serviceDao.putService(param);
        return ResponseEntity.ok("서비스가 수정되었습니다.");
    }

    @DeleteMapping
    @Operation(summary = "Delete Service", description = "서비스 삭제 API")
    public ResponseEntity<String> delService(@RequestParam("serviceId") String serviceId) {
        serviceDao.delService(serviceId);
        return ResponseEntity.ok("서비스가 삭제되었습니다.");
    }
    
}
