package com.tuk.mina.vo.user;

import java.time.LocalDateTime;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("TbUserVo")
public class TbUserVo {

    private String userId;
    private String userPw;
    private String userName;
    private String email;
    private String userPhone;
    private String userBirth;
    private String createDttm;
    private String updatedDttm;

}
