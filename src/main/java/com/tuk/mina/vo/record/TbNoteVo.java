package com.tuk.mina.vo.record;
import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("TbNoteVo")
public class TbNoteVo {

    private int noteId;
    private int recordId;
    private String noteSummary;
    private String noteFullText;
    private String createdUserId;
    private String createdDttm;
    private String updatedDttm;
    
}