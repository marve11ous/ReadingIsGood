package com.readingisgood.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Book order info")
public class BookOrderRequest {

    @NotNull
    @Positive
    @Schema(required = true, description = "Book identifier")
    private Long id;

    @NotNull
    @Positive
    @Schema(required = true, description = "Book amount")
    private Long amount;
}
