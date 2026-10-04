package com.deng.auth_center.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.deng.auth_center.common.filter.RequestHeaderAuthFilter;
import com.deng.auth_center.common.handler.AccessDeniedHandlerImpl;
import com.deng.auth_center.common.handler.AuthenticationEntryPointImpl;

@Configuration 
@EnableMethodSecurity 
public class SecurityConfig {
    private final AuthenticationEntryPointImpl authenticationEntryPointImpl;
    private final AccessDeniedHandlerImpl accessDeniedHandlerImpl;
    private final RequestHeaderAuthFilter requestHeaderAuthFilter;

    public SecurityConfig(AuthenticationEntryPointImpl authenticationEntryPointImpl,
            AccessDeniedHandlerImpl accessDeniedHandlerImpl, RequestHeaderAuthFilter requestHeaderAuthFilter) {
        this.authenticationEntryPointImpl = authenticationEntryPointImpl;
        this.accessDeniedHandlerImpl = accessDeniedHandlerImpl;
        this.requestHeaderAuthFilter = requestHeaderAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(CsrfConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPointImpl)
                .accessDeniedHandler(accessDeniedHandlerImpl)
            )
            .httpBasic(httpBasis -> httpBasis.disable())
            .formLogin(form -> form.disable());

        http.addFilterBefore(requestHeaderAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    
}
