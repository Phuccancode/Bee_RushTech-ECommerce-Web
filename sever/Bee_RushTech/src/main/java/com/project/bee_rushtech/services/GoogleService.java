package com.project.bee_rushtech.services;

import java.security.SignatureException;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public record GoogleService() {
    private static final String GOOGLE_USERINFO_ENDPOINT = "https://www.googleapis.com/oauth2/v3/userinfo";

    public Map getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        // Tạo header với Bearer token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Gửi request để lấy thông tin người dùng
        ResponseEntity<Map> response = restTemplate.exchange(
                GOOGLE_USERINFO_ENDPOINT,
                HttpMethod.GET,
                entity,
                Map.class);

        return response.getBody();
    }

    private static final String GOOGLE_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7lABjEZt4cQ98G1xnw" +
            // Còn lại của public key ở đây
            "-----END PUBLIC KEY-----";

    public static Map<String, String> decodeIdToken(String idToken) {
        DecodedJWT jwt = JWT.decode(idToken);
        String email = jwt.getClaim("email").asString();
        String sub = jwt.getClaim("sub").asString();
        String name = jwt.getClaim("name").asString();
        return Map.of("email", email, "name", name, "sub", sub);
    }

}
