package com.tuk.mina.api.ctl.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tuk.mina.dao.task.TbTaskDao;
import com.tuk.mina.util.SecurityUtil;
import com.tuk.mina.vo.task.TbTaskVo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RequestMapping("/api/task")
@CrossOrigin("*")
@Slf4j
@RestController
@Tag(name = "Task Controller", description = "업무 관련 API")
public class taskCtl {

    @Autowired
    private TbTaskDao taskDao;

    @Autowired
    private SecurityUtil securityUtil;

    @GetMapping
    @Operation(summary = "Get Task List", description = "업무 목록 조회 API")
    public ResponseEntity<List<TbTaskVo>> getTask(TbTaskVo param) {
        return ResponseEntity.ok(taskDao.getTask(param));
    }

    @PostMapping
    @Operation(summary = "Create New Task", description = "새로운 업무 등록 API")
    public ResponseEntity<String> newTask(@RequestBody TbTaskVo param) {
        param.setCreateUserId(securityUtil.getAuthUserId().get());
        taskDao.newTask(param);
        return ResponseEntity.ok("업무가 등록되었습니다.");
    }

    @PutMapping
    @Operation(summary = "Update Task", description = "업무 수정 API")
    public ResponseEntity<String> putTask(@RequestBody TbTaskVo param) {
        taskDao.putTask(param);
        return ResponseEntity.ok("업무가 수정되었습니다.");
    }

    @DeleteMapping
    @Operation(summary = "Delete Task", description = "업무 삭제 API")
    public ResponseEntity<String> delTask(@RequestParam("taskId") String taskId) {
        taskDao.delTask(taskId);
        return ResponseEntity.ok("업무가 삭제되었습니다.");
    }

}
