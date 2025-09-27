package com.tuk.mina.vo.service;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("TbServiceVo")
public class TbServiceVo {

    private String serviceId;
    private String serviceName;
    private String createUserId;
    private String createDttm;
    private String updatedDttm;

}
