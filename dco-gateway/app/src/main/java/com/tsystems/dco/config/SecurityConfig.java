package com.tsystems.dco.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  @Value("${app.username}")
  private String username;
  @Value("${app.password}")
  private String password;

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http
      .cors(cors -> {}) // Enable CORS with configuration from WebConfig
      .csrf(ServerHttpSecurity.CsrfSpec::disable)
      .authorizeExchange(exchanges -> exchanges
        .pathMatchers("/actuator/**", "/actuator/health/**", "/actuator/prometheus/**").permitAll()
        .pathMatchers("/graphql/**", "/graphql").permitAll() // Allow GraphQL endpoint without authentication
        .anyExchange().authenticated()
      )
      .httpBasic(httpBasic -> {})
      .build();
  }

  @Bean
  public MapReactiveUserDetailsService userDetailsService() {

    UserDetails user1 = User.withDefaultPasswordEncoder()
      .username(username)
      .password(password)
      .roles("USER")
      .build();

    UserDetails user2 = User.withDefaultPasswordEncoder()
      .username("dco")
      .password("dco")
      .roles("USER")
      .build();

    UserDetails admin = User.withDefaultPasswordEncoder()
      .username("admin")
      .password("password")
      .roles("USER","ADMIN")
      .build();

    return new MapReactiveUserDetailsService(user1, user2, admin);
  }

}
