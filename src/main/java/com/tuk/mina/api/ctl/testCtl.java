package com.tuk.mina.api.ctl;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class testCtl {
    
    @GetMapping("/test")
    public String test() {
        return "Hello, World!";
    }

    /**
     * 현재 로그인된 사용자의 ID를 반환하는 예제 엔드포인트입니다.
     * @param userDetails Spring Security가 주입해주는 사용자 정보
     * @return 현재 사용자 ID
     */
    @GetMapping("/my-userid")
    public String getMyUserId(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            String userId = userDetails.getUsername();
            return "Current User ID: " + userId;
        }
        return "User not authenticated.";
    }
}
