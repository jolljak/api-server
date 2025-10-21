package com.tuk.mina.api.ctl.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import  com.tuk.mina.api.svc.user.userSvc;
import com.tuk.mina.dto.user.UserDto;
import com.tuk.mina.dto.user.UserResponseDto;
import com.tuk.mina.util.SecurityUtil;
import com.tuk.mina.vo.user.TbUserVo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@CrossOrigin("*")
@RequestMapping("/api/user")
@Tag(name = "User Controller", description = "유저 관련 API")
public class userCtl {

    @Autowired
    private userSvc userSvc;

    @Autowired
    private SecurityUtil securityUtil;

    @PostMapping("/signup")
    @Operation(summary = "User Registration", description = "회원가입 API")
    public ResponseEntity<String> signUp(@RequestBody TbUserVo user) {
        userSvc.signup(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "로그인 API")
    public ResponseEntity<?> login(@RequestBody UserDto user) {
        return ResponseEntity.ok(userSvc.login(user));
    }

    @GetMapping("/current-user")
    @Operation(summary = "Get Current User", description = "현재 로그인된 유저 정보 조회 API")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        return ResponseEntity.ok(userSvc.getCurrentUser());
    }
    
    
}
