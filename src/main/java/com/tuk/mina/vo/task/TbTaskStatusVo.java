package com.tuk.mina.vo.task;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("TbTaskStatusVo")
public class TbTaskStatusVo {

    private String taskStatusId;
    private String taskStatus;

}
