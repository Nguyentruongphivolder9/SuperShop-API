package com.project.supershop.auth.controller;


import com.project.supershop.auth.JwtTokenService.JwtTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private JwtTokenService jwtTokenService;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService){
            this.authenticationManager = authenticationManager;
            this.jwtTokenService = jwtTokenService;
    }

}
