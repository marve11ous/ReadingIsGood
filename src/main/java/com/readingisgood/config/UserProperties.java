package com.readingisgood.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Validated
@ConfigurationProperties("security")
public class UserProperties {

    @Valid
    @NotEmpty
    private List<UserInfo> users;

    public enum Role {
        USER, ADMIN
    }

    @Data
    public static class UserInfo {
        @NotBlank
        private String username;
        @NotBlank
        private String password;
        @NotNull
        private List<Role> roles;
    }
}
