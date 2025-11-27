package com.tuk.mina.vo.project;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("TbProjServiceInfoVo")
public class TbProjServiceInfoVo {

    private Integer serviceId;
    private Integer projectId;
    private String serviceField;
    private String fieldValue;
    private String createdUserId;
    private String createdDttm;
    private String updatedDttm;
    
}