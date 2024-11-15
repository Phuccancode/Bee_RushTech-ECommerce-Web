package com.project.bee_rushtech.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.project.bee_rushtech.responses.SocailAccessTokenResponse;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class SocialLoginController {

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping("/home")
    public ResponseEntity<SocailAccessTokenResponse> getAccessToken(OAuth2AuthenticationToken authentication,
            HttpServletResponse response) {
        String registrationId = authentication.getAuthorizedClientRegistrationId();

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                registrationId, authentication.getName());

        String accessToken = client.getAccessToken().getTokenValue();

        SocailAccessTokenResponse socailAccessTokenResponse = new SocailAccessTokenResponse(accessToken);

        response.setHeader("Authorization", "Bearer " + accessToken);

        return ResponseEntity.ok(socailAccessTokenResponse);
    }
}