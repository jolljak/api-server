package com.tuk.mina.vo.task;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("TbTaskHistVo")
public class TbTaskHistVo {

    private Integer taskId;
    private Integer projectId;
    private String taskStatusId;
    private String userId;
    private String taskContent;
    private boolean isDone;
    private String createUserId;
    private String createDttm;
    private String updatedDttm;

}
