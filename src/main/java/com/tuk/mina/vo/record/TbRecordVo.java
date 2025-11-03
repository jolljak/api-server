package com.tuk.mina.vo.record;
import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("TbRecordVo")
public class TbRecordVo {

    private int recordId;               //녹음 아이디
    private int recordFileId;           //녹음 파일 아이디
    private int projectId;              //프로젝트 아이디
    private int textFileId;             //요약 파일 아이디
    private String recordlanguage;
    private String recordMemo;
    private String createUserId;
    private String createDttm;
    private String updatedDttm;
    
}
