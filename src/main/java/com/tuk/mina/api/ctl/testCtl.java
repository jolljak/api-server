package com.tuk.mina.api.ctl;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testCtl {
    
    @GetMapping
    public String test() {
        return "Hello, World!";
    }
    
}
