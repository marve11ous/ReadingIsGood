package com.readingisgood.models.response;

import com.readingisgood.models.common.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order info")
public class OrderResponse {

    @Schema(description = "Identifier")
    private Long id;

    @Schema(description = "Status")
    private OrderStatus status;

    @Schema(description = "Date and time")
    private LocalDateTime date;

    @Schema(description = "Books in order")
    private List<BookOrderResponse> books;

}
