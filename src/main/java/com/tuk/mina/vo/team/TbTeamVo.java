package com.tuk.mina.vo.team;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("TbTeamVo")
public class TbTeamVo {

    private String teamId;
    private String teamName;
    private String teamLeaderId;
    private String teamDesc;
    private String createUserId;
    private String updatedUserId;
    private String createDttm;
    private String updatedDttm;

}
