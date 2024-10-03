package com.example.springcloudgatewayoverview.filter;

import com.example.springcloudgatewayoverview.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class RoleAuthGatewayFilterFactory extends
        AbstractGatewayFilterFactory<RoleAuthGatewayFilterFactory.Config> {

  @Autowired
  private JWTUtil jwtUtil;
  public RoleAuthGatewayFilterFactory() {
    super(Config.class);
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      var request = exchange.getRequest();

      System.out.println("Required role: " + config.role);

      // JWTUtil can extract the token from the request, parse it and verify if the given role is available
      if(!jwtUtil.hasRole(request, config.role)){
        // seems we miss the auth token
        var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
      }
      return chain.filter(exchange);
    };
  }

  public static class Config {
    private String role;

    public Config(String role) {
      this.role = role;
    }
  }

  @Override
  public List<String> shortcutFieldOrder() {
    // we need this to use shortcuts in the application.yml
    return Arrays.asList("role");
  }
}
