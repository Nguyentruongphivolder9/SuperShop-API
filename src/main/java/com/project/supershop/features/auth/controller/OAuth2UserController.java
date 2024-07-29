package com.project.supershop.features.auth.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.project.supershop.common.ResultResponse;
import com.project.supershop.config.GoogleOpaqueTokenIntrospector;
import com.project.supershop.features.account.domain.entities.Account;
import com.project.supershop.features.account.services.AccountService;
import com.project.supershop.features.auth.domain.dto.response.JwtResponse;
import com.project.supershop.features.auth.domain.dto.response.UrlDto;
import com.project.supershop.features.auth.domain.entities.AccessToken;
import com.project.supershop.features.auth.services.AccessTokenService;
import com.project.supershop.features.auth.services.JwtTokenService;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

@RestController
@RequestMapping("/api/v1/oauth")
public class OAuth2UserController {

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    private static final String REDIRECT_URI = "http://localhost:8080/api/v1/oauth/oauth-2-user/callback";
    private final GoogleOpaqueTokenIntrospector googleOpaqueTokenIntrospector;
    private final AccountService accountService;
    private final JwtTokenService jwtTokenService;
    private final AccessTokenService accessTokenService;
    public OAuth2UserController(GoogleOpaqueTokenIntrospector googleOpaqueTokenIntrospector, AccountService accountService, JwtTokenService jwtTokenService, AccessTokenService accessTokenService) {
        this.googleOpaqueTokenIntrospector = googleOpaqueTokenIntrospector;
        this.accountService = accountService;
        this.jwtTokenService = jwtTokenService;
        this.accessTokenService = accessTokenService;
    }

    @GetMapping("/oauth-2-user/url")
    public ResponseEntity<ResultResponse<UrlDto>> generateAuthUrl() {
        String url = new GoogleAuthorizationCodeRequestUrl(
                googleClientId,
                REDIRECT_URI,
                Arrays.asList("email", "profile", "openid")
        ).setAccessType("offline").build();

        return ResponseEntity.ok(
                ResultResponse.<UrlDto>builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .body(new UrlDto(url))
                        .message("Authorization URL generated successfully")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    @GetMapping("/oauth-2-user/callback")
    public ResponseEntity<ResultResponse<JwtResponse>> handleCallback(@RequestParam("code") String code) {
        try {
            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(),
                    new GsonFactory(),
                    googleClientId,
                    googleClientSecret,
                    code,
                    REDIRECT_URI
            ).execute();

            OAuth2AuthenticatedPrincipal principal = googleOpaqueTokenIntrospector.introspect(tokenResponse.getAccessToken());

            Account accountFromGoogle = createAccountFromPrincipal(principal);
            Account newAccount = accountService.createOrMergeGoogleAccountToLocalAccount(accountFromGoogle);
            newAccount.setIsActive(true);
            JwtResponse jwtResponse = jwtTokenService.createJwtResponse(newAccount);
            AccessToken accessToken = AccessToken.builder()
                    .token(jwtResponse.getAccessToken())
                    .refreshToken(jwtResponse.getRefreshToken())
                    .issuedAt(System.currentTimeMillis())
                    .expiresAt(jwtResponse.getExpires())
                    .build();
            accessTokenService.saveToken(accessToken);
            String redirectUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/login")
                    .queryParam("token", jwtResponse.getAccessToken())
                    .queryParam("refreshToken", jwtResponse.getRefreshToken())
                    .build().toUriString();

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(redirectUrl))
                    .build();

        } catch (IOException e) {
            return handleErrorResponse(e);
        }
    }











    private Account createAccountFromPrincipal(OAuth2AuthenticatedPrincipal principal) {
        Account account = new Account();
        account.setEmail(principal.getAttribute("email"));
        account.setUserName(principal.getAttribute("name"));
        account.setAvatarUrl(principal.getAttribute("picture"));
        account.setIsEnable(principal.getAttribute("email_verified"));
        account.setRoleName("USER");
        return account;
    }

    private ResponseEntity<ResultResponse<JwtResponse>> handleErrorResponse(IOException e) {
        System.err.println("Error exchanging code for token: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ResultResponse.<JwtResponse>builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .body(null)
                        .message("Error exchanging code for token")
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .build()
        );
    }
}
