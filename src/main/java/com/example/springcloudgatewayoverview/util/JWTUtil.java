package com.example.springcloudgatewayoverview.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secret;
    public boolean hasRole(ServerHttpRequest request, String role) {

        if(!request.getHeaders().containsKey("Authorization")) {
            return false;
        }

        String token = request.getHeaders().get("Authorization").toString().split(" ")[1];
        String roles = (String) this.getALlClaims(token).get("roles");
        roles = roles.replace("[", "").replace("]", "");
        String[] roleNames = roles.split(",");

        for (String aRoleName : roleNames) {
            if(role.equals(aRoleName)) {
                return true;
            }
        }

        return  false;
    }

    public Claims getALlClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token ) {
        return this.getALlClaims(token).getExpiration().before(new Date());
    }

    public boolean isInvalid(String token) {

        String roles = (String) this.getALlClaims(token).get("roles");
        System.out.println("Authentication roles: " + roles);

        return this.isTokenExpired(token);
    }

}
