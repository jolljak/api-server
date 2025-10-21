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

    private String recordId;
    private String recordFileId;
    private String projectId;
    private String textFileId;
    private String recordlanguage;
    private String recordMemo;
    private String createUserId;
    private String updatedUserId;
    private String createDttm;
    private String updatedDttm;
    
}
