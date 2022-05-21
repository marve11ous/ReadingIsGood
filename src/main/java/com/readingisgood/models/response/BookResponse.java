package com.readingisgood.models.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Book info")
public class BookResponse {

    @Schema(description = "Identifier")
    private Long id;

    @Schema(description = "Name")
    private String name;

    @Schema(description = "Author")
    private String author;

    @Schema(description = "Price")
    private Double price;

    @Schema(description = "Stock")
    private Long stock;
}
