package com.tuk.mina.dto.user;

import com.tuk.mina.vo.team.TbTeamVo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class UserResponseDto {

    private String userId;
    private String userName;
    private String email;
    private String userPhone;
    private String userBirth;
    private String createdDttm;
    private String updatedDttm;
    
    private TbTeamVo team;
}
