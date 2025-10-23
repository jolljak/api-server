package com.tuk.mina.vo.task;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("TbTaskVo")
public class TbTaskVo {
    private Integer taskId;
    private String projectId;
    private String taskStatusId;
    private String userId;
    private String taskContent;
    private boolean isDone;
    private String createUserId;
    private String createdDttm; 
    private String updatedDttm;
}
