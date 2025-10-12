package com.tuk.mina.api.ctl.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import  com.tuk.mina.api.svc.user.userSvc;
import com.tuk.mina.dto.user.UserDto;
import com.tuk.mina.vo.user.TbUserVo;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/user")
public class userCtl {

    @Autowired
    private userSvc userSvc;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody TbUserVo user) {
        userSvc.signup(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto user) {
        return ResponseEntity.ok(userSvc.login(user));
    }
    
}
