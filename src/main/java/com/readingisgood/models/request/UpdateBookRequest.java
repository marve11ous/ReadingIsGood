package com.readingisgood.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Update book info")
public class UpdateBookRequest {

    @NotNull
    @PositiveOrZero
    @Schema(required = true, description = "Stock")
    private Long stock;
}
