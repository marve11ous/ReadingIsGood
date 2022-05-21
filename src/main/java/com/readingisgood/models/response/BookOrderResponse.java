package com.readingisgood.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Book order info")
public class BookOrderResponse {

    @Schema(description = "Book identifier")
    private Long id;

    @Schema(description = "Book amount")
    private Long amount;

    @Schema(description = "Status")
    private Status status;

    public enum Status {
        OK, OUT_OF_STOCK
    }
}
