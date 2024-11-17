package com.project.bee_rushtech.services;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

}
