package com.tuk.mina.dto.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class TokenDto {
    
    private String grantType;
    private String accessToken;

}
