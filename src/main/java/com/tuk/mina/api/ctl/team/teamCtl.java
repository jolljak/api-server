package com.tuk.mina.api.ctl.team;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tuk.mina.dao.team.TbTeamDao;
import com.tuk.mina.util.SecurityUtil;
import com.tuk.mina.vo.team.TbTeamVo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RequestMapping("/api/team")
@CrossOrigin("*")
@Slf4j
@RestController
@Tag(name = "Team Controller", description = "팀 관련 API")
public class teamCtl {

    @Autowired
    private TbTeamDao teamDao;

    @Autowired
    private SecurityUtil securityUtil;

    @GetMapping
    @Operation(summary = "Get Team List", description = "팀 목록 조회 API")
    public ResponseEntity<List<TbTeamVo>> getTeam(TbTeamVo param) {
        return ResponseEntity.ok(teamDao.getTeam(param));
    }

    @PostMapping
    @Operation(summary = "Create New Team", description = "새로운 팀 등록 API")
    public ResponseEntity<String> newTeam(@RequestBody TbTeamVo param) {
        param.setCreatedUserId(securityUtil.getAuthUserId().get());
        param.setUpdatedUserId(securityUtil.getAuthUserId().get()); 
        teamDao.newTeam(param);
        return ResponseEntity.ok("팀이 등록되었습니다.");
    }

    @PutMapping
    @Operation(summary = "Update Team", description = "팀 수정 API")
    public ResponseEntity<String> putTeam(@RequestBody TbTeamVo param) {
        param.setUpdatedUserId(securityUtil.getAuthUserId().get());
        teamDao.putTeam(param);
        return ResponseEntity.ok("팀이 수정되었습니다.");
    }

    @DeleteMapping
    @Operation(summary = "Delete Team", description = "팀 삭제 API")
    public ResponseEntity<String> delTeam(@RequestParam("teamId") Integer teamId) {
        teamDao.delTeam(teamId);
        return ResponseEntity.ok("팀이 삭제되었습니다.");
    }
}
