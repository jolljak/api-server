package com.tuk.mina.vo.project;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("TbProjectVo")
public class TbProjectVo {
    
    private String projectId;
    private String teamId;
    private String projectName;
    private String description;
    private String teamName;
    private String projectRole;
    private boolean isDone;
    private String createUserId;
    private String updatedUserId;
    private String createDttm;
    private String updatedDttm;

}
