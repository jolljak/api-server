package com.tuk.mina.vo.project;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("TbProjectServiceMapVo")
public class TbProjectServiceMapVo {

    private String serviceId;
    private String projectId;
    private String createUserId;
    private String createDttm;
    private String updatedDttm;
    
}
