package com.tuk.mina.api.svc.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tuk.mina.dao.team.TbTeamDao;
import com.tuk.mina.dao.team.TbTeamUserMapDao;
import com.tuk.mina.dao.user.TbUserDao;
import com.tuk.mina.dto.jwt.TokenDto;
import com.tuk.mina.dto.user.UserDto;
import com.tuk.mina.dto.user.UserResponseDto;
import com.tuk.mina.util.SecurityUtil;
import com.tuk.mina.util.jwt.JwtTokenProvider;
import com.tuk.mina.vo.team.TbTeamUserMapVo;
import com.tuk.mina.vo.team.TbTeamVo;
import com.tuk.mina.vo.user.TbUserVo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

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

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private TbTeamUserMapDao teamUserMapDao;

    @Autowired
    private TbTeamDao teamDao;

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

    public UserResponseDto getCurrentUser() {
        java.util.Optional<String> userIdOpt = SecurityUtil.getAuthUserId();
        if (userIdOpt.isEmpty()) {
            return null;
        }
        
        TbUserVo userParam = new TbUserVo();
        userParam.setUserId(userIdOpt.get());

        List<TbUserVo> users = userDao.getUser(userParam);
        if (users.isEmpty()) {
            return null;
        }
        TbUserVo currentUserVo = users.get(0);

        UserResponseDto userRes = new UserResponseDto();
        userRes.setUserId(currentUserVo.getUserId());
        userRes.setUserName(currentUserVo.getUserStringName());
        userRes.setEmail(currentUserVo.getEmail());
        userRes.setUserPhone(currentUserVo.getUserPhone());
        userRes.setUserBirth(currentUserVo.getUserBirth());
        userRes.setCreatedDttm(currentUserVo.getCreateDttm());
        userRes.setUpdatedDttm(currentUserVo.getUpdatedDttm());
        userRes.setTeams(getUserTeams(currentUserVo.getUserId()));

        return userRes;
    }

    public List<TbTeamVo> getUserTeams(String userId) {
        TbTeamUserMapVo param = new TbTeamUserMapVo();
        param.setUserId(userId);
        List<TbTeamUserMapVo> teamUserMap = teamUserMapDao.getTeamUserMap(param);    

        java.util.ArrayList<TbTeamVo> teams = new java.util.ArrayList<>();
        for (TbTeamUserMapVo map : teamUserMap) {
            TbTeamVo teamParam = new TbTeamVo();
            teamParam.setTeamId(map.getTeamId());
            List<TbTeamVo> foundTeams = teamDao.getTeam(teamParam);
            if (!foundTeams.isEmpty()) {
                teams.add(foundTeams.get(0));
            }
        }
        return teams;
    }
}
