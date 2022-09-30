package com.jsolution.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import reactor.core.publisher.Mono;

//Clase S7
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebSecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SecurityContextRepository securityContextRepository;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .exceptionHandling()
                .authenticationEntryPoint((swe, e) -> Mono.fromRunnable(() -> {
                    swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                })).accessDeniedHandler((swe, e) -> Mono.fromRunnable(() -> {
                    swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                }))
                .and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange()
                //SWAGGER PARA SPRING SECURITY
                .pathMatchers("/swagger-resources/**").permitAll()
                .pathMatchers("/swagger-ui.html").permitAll()
                .pathMatchers("/webjars/**").permitAll()
                //SWAGGER PARA SPRING SECURITY
                .pathMatchers("/auth/**").permitAll()
                .pathMatchers("/v2/login").permitAll()
                .pathMatchers("/v2/**").permitAll()
                //.pathMatchers("/v2/**").hasAnyAuthority("ADMIN")
                /*.pathMatchers("/v2/**")
                    .access((mono, context) -> mono
                            .map(auth -> auth.getAuthorities()
                                    .stream()
                                    .filter(e -> e.getAuthority().equals("ADMIN"))
                                    .count() > 0)
                            .map(AuthorizationDecision::new)
                    )*/
                .pathMatchers("/dishes/**").authenticated()
                .pathMatchers("/customers/**").authenticated()
                .pathMatchers("/invoices/**").authenticated()
                .pathMatchers("/backpressure/**").permitAll()
                .pathMatchers("/users/**").authenticated()
                .pathMatchers("/menus/**").authenticated()
                .anyExchange().authenticated()
                .and().build();
    }
}
