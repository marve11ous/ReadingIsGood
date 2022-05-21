package com.readingisgood.models.request;

import com.readingisgood.models.constraints.UniqueEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "New customer info")
public class CreateUserRequest {

    @NotBlank
    @Schema(required = true, description = "Name")
    private String name;

    @NotBlank
    @Schema(required = true, description = "Lastname")
    private String lastName;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(required = true, description = "Birthdate")
    private LocalDate birthDate;

    @Email
    @NotBlank
    @UniqueEmail
    @Schema(required = true, description = "Email (must be unique)")
    private String email;
}
