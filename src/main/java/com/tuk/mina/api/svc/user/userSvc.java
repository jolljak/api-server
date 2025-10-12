package com.tuk.mina.api.svc.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tuk.mina.dao.user.TbUserDao;
import com.tuk.mina.dto.jwt.TokenDto;
import com.tuk.mina.dto.user.UserDto;
import com.tuk.mina.util.jwt.JwtTokenProvider;
import com.tuk.mina.vo.user.TbUserVo;

@Service
@Transactional(readOnly = true)
public class userSvc {
    
    @Autowired
    private AuthenticationManagerBuilder authManagerBuilder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private PasswordEncoder pwEncoder;

    @Autowired
    private TbUserDao userDao;

    @Transactional
    public TokenDto login(UserDto loginInfo) {
        // 1. login id, pw 를 기반으로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = 
            new UsernamePasswordAuthenticationToken(loginInfo.getUserId(), loginInfo.getUserPw());

        // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        //    authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만들었던 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        String jwt = tokenProvider.generateToken(authentication);
        return new TokenDto("Bearer", jwt);
    }

    @Transactional
    public void signup(TbUserVo user) {
        TbUserVo userParam = new TbUserVo();
        userParam.setUserId(user.getUserId());
        if(!userDao.getUser(userParam).isEmpty()) throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        
        user.setUserPw(pwEncoder.encode(user.getUserPw()));
        userDao.newUser(user);
    }
}
