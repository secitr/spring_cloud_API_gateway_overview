package com.example.springcloudgatewayoverview.config;

import com.example.springcloudgatewayoverview.filter.AuthFilter;
import com.example.springcloudgatewayoverview.filter.PostGlobalFilter;
import com.example.springcloudgatewayoverview.filter.RequestFilter;
import com.example.springcloudgatewayoverview.filter.RoleAuthGatewayFilterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.WebFilter;

@Configuration
public class GatewayConfig {

    @Autowired
    RequestFilter requestFilter;

    @Autowired
    AuthFilter authFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, RoleAuthGatewayFilterFactory roleAuthGatewayFilterFactory) {
                // adding 2 rotes to first microservice as we need to log request body if method is POST
        return builder.routes()
                .route("first-microservice",r -> r.path("/first/**")
                        .and().method("GET", "POST")
                        .filters(f-> f.filters(authFilter)
                                .filter(roleAuthGatewayFilterFactory.apply(new RoleAuthGatewayFilterFactory.Config("ROLE_MANAGER"))))
                        .uri("lb://FIRST-MICROSERVICE"))
                        //.uri("http://localhost:8081"))

                .route("second-microservice",r -> r.path("/second/**")
                        .and().method("GET", "POST")
                        .filters(f-> f.filters(authFilter)
                                .filter(roleAuthGatewayFilterFactory.apply(new RoleAuthGatewayFilterFactory.Config("ROLE_ADMIN"))))
                        .uri("lb://SECOND-MICROSERVICE"))
                        //.uri("http://localhost:8082"))


                .route("auth-server",r -> r.path("/login/**")
                        .uri("lb://AUTH-SERVER"))
                        //.uri("http://localhost:8088"))
                .build();
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public WebFilter responseFilter(){
        return new PostGlobalFilter();
    }

}
