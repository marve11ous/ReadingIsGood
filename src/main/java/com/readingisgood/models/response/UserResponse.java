package com.readingisgood.models.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Customer info")
public class UserResponse {

    @Schema(description = "Identifier")
    private Long id;

    @Schema(description = "Name")
    private String name;

    @Schema(description = "Lastname")
    private String lastName;

    @Schema(description = "Birthdate")
    private LocalDate birthDate;

    @Schema(description = "Email")
    private String email;
}
