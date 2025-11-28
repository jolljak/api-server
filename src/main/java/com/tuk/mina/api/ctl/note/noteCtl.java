package com.tuk.mina.api.ctl.note;

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

import com.tuk.mina.api.svc.note.NoteSvc;
import com.tuk.mina.vo.record.TbNoteVo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/api/note")
@CrossOrigin("*")
@Slf4j
@RestController
@Tag(name = "Note Controller", description = "노트 관련 API")
public class noteCtl {

    @Autowired
    private NoteSvc noteSvc;

    @GetMapping
    @Operation(summary = "Get Note List", description = "노트 목록 조회 API")
    public ResponseEntity<List<TbNoteVo>> getNote(TbNoteVo param) {
        return ResponseEntity.ok(noteSvc.getNote(param));
    }

    @PostMapping
    @Operation(summary = "Create New Note", description = "새로운 노트 등록 API")
    public ResponseEntity<String> newNote(@RequestBody TbNoteVo param) {
        noteSvc.newNote(param);
        return ResponseEntity.ok("노트가 등록되었습니다.");
    }
    
    @PutMapping
    @Operation(summary = "Update Note", description = "노트 수정 API")
    public ResponseEntity<String> putNote(@RequestBody TbNoteVo param) {
        noteSvc.putNote(param);
        return ResponseEntity.ok("노트가 수정되었습니다.");
    }

    @DeleteMapping
    @Operation(summary = "Delete Note", description = "노트 삭제 API")
    public ResponseEntity<String> delNote(@RequestParam("noteId") Integer noteId) {
        noteSvc.delNote(noteId);
        return ResponseEntity.ok("노트가 삭제되었습니다.");
    }
}
