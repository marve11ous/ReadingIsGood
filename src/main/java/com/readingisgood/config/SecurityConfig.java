package com.readingisgood.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

@Configuration
@SecurityScheme(name = "basicAuth", type = SecuritySchemeType.HTTP, scheme = "basic")
@RequiredArgsConstructor
@EnableConfigurationProperties(UserProperties.class)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final SecurityProblemSupport problemSupport;
    private final PasswordEncoder passwordEncoder;
    private final UserProperties userProperties;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        var authentication = auth.inMemoryAuthentication();
        for (UserProperties.UserInfo userInfo : userProperties.getUsers()) {
            authentication = authentication.withUser(userInfo.getUsername())
                    .password(passwordEncoder.encode(userInfo.getPassword()))
                    .roles(userInfo.getRoles().stream().map(Enum::name).toArray(String[]::new))
                    .and();
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(HttpMethod.GET, "/v*/api-docs/**", "/swagger-ui/**").permitAll()
                .antMatchers(HttpMethod.GET, "/**").hasRole(UserProperties.Role.USER.name())
                .anyRequest().hasRole(UserProperties.Role.ADMIN.name())
                .and().csrf().disable()
                .httpBasic()
                .authenticationEntryPoint(problemSupport)
                .and().exceptionHandling().accessDeniedHandler(problemSupport);
    }

}
