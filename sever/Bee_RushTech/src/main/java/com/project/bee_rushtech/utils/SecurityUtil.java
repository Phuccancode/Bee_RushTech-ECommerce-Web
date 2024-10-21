package com.project.bee_rushtech.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.project.bee_rushtech.dtos.ResLoginDTO;

@Service
public class SecurityUtil {
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;
    private final JwtEncoder jwtEncoder;

    public SecurityUtil(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    @Value("${project.jwt.base64-secret}")
    private String jwtKey;

    @Value("${project.jwt.access-token-validity-in-seconds}")
    private long jwtAccessExpiration;

    public String createAccessToken(Authentication authentication) {
        Instant now = Instant.now();
        Instant validity = now.plus(this.jwtAccessExpiration, ChronoUnit.SECONDS);

        // @formatter:off 
        JwtClaimsSet claims = JwtClaimsSet.builder() 
            .issuedAt(now) 
            .expiresAt(validity) 
            .subject(authentication.getName()) 
            .claim("bee_rushtech", authentication) 
            .build(); 
 
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build(); 
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader,claims)).getTokenValue();
    }
    @Value("${project.jwt.refresh-token-validity-in-seconds}")
    private long jwtRefreshExpiration;

    public String createRefreshToken(String email, ResLoginDTO resLoginDTO) {
        Instant now = Instant.now();
        Instant validity = now.plus(this.jwtRefreshExpiration, ChronoUnit.SECONDS);

        // @formatter:off 
        JwtClaimsSet claims = JwtClaimsSet.builder() 
            .issuedAt(now) 
            .expiresAt(validity) 
            .subject(email) 
            .claim("user", resLoginDTO.getUser()) 
            .build(); 
 
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build(); 
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader,claims)).getTokenValue();
    }


}