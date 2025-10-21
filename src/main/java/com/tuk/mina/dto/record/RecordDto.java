package com.tuk.mina.dto.record;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class RecordDto {
    private String recordId;         // PK
    private String recordFileId;     // 녹음 파일 ID
    private String projectId;        // 프로젝트 ID
    private String textFileId;       // 텍스트 파일 ID
    private String recordLanguage;   // 언어 (예: "ko", "en", "auto")
    private String recordMemo;       // 메모 내용
    private String createUserId;     // 생성자 ID
    private String updatedUserId;    // 수정자 ID
    private LocalDateTime createDttm; // 생성일시
    private LocalDateTime updatedDttm; // 수정일시
}