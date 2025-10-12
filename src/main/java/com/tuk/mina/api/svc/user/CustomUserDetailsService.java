package com.tuk.mina.api.svc.user;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tuk.mina.dao.user.TbUserDao;
import com.tuk.mina.vo.user.TbUserVo;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private TbUserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TbUserVo userParam = new TbUserVo();
        userParam.setUserId(username);
        List<TbUserVo> user = userDao.getUser(userParam);

        if(user.isEmpty()) throw new UsernameNotFoundException("User not found with id: " + username);
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        
        TbUserVo loadUser = user.get(0);
        return new User(
            loadUser.getUserId(),
            loadUser.getUserPw(),
            authorities
        );
    }
    
}
