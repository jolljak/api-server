package com.tuk.mina.vo.team;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("TbTeamUserMapVo")
public class TbTeamUserMapVo {

    private String teamId;
    private String userId;
    private String memberRole;
    private String memberStatus;
    private String memberNick;
    private String createDttm;
    private String updatedDttm;
    
}
