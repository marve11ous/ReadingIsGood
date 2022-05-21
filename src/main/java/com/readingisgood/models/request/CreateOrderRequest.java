package com.readingisgood.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "New order info")
public class CreateOrderRequest {

    @NotNull
    @Positive
    @Schema(required = true, description = "Customer identifier")
    private Long userId;

    @Valid
    @NotEmpty
    @Schema(required = true, description = "Books to order")
    private List<BookOrderRequest> books;
}
