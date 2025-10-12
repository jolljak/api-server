package com.tuk.mina.vo.user;

import java.time.LocalDateTime;
import java.util.Collection;

import org.apache.ibatis.type.Alias;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("TbUserVo")
public class TbUserVo implements UserDetails{

    private String userId;
    private String userPw;
    private String userName;
    private String email;
    private String userPhone;
    private String userBirth;
    private String createDttm;
    private String updatedDttm;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAuthorities'");
    }
    @Override
    public String getPassword() {
        return this.userPw;
    }
    @Override
    public String getUsername() {
        return this.userId;
    }

}
