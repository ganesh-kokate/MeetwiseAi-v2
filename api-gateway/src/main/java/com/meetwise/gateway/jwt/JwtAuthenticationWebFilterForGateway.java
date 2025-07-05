package com.meetwise.gateway.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationWebFilterForGateway implements WebFilter {

    private final JwtUtilForGateway jwtUtilForGateway;

    public JwtAuthenticationWebFilterForGateway(JwtUtilForGateway jwtUtilForGateway) {
        this.jwtUtilForGateway = jwtUtilForGateway;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);


            if(authHeader != null && authHeader.startsWith("Bearer"))
            {
                String token = authHeader.substring(7);
                if(jwtUtilForGateway.validateJwtToken(token))
                {
                    String username = jwtUtilForGateway.getUsernameFromJwtToken(token);
                    var claims = jwtUtilForGateway.getAllClaimsFromToken(token);

                    var authorities = ((List<String>) claims.get("roles")).stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList();

                    var authentication = new UsernamePasswordAuthenticationToken(username,null,authorities);

                    var securityConntext = new SecurityContextImpl(authentication);

                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityConntext)));

                }
                else {
                    exchange.getResponse()
                            .setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                }
            }
            return chain.filter(exchange);
    }
}
