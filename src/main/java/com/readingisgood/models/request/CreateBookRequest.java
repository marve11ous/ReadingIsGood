package com.readingisgood.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "New book info")
public class CreateBookRequest {

    @NotBlank
    @Schema(required = true, description = "Name")
    private String name;

    @NotBlank
    @Schema(required = true, description = "Auhtor")
    private String author;

    @NotNull
    @Positive
    @Schema(required = true, description = "Price")
    private Double price;

    @NotNull
    @PositiveOrZero
    @Schema(required = true, description = "Stock")
    private Long stock;
}
