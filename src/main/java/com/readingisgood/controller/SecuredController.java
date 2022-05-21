package com.readingisgood.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.zalando.problem.AbstractThrowableProblem;

@SecurityRequirement(name = "basicAuth")
@ApiResponse(responseCode = "200")
@ApiResponse(responseCode = "400",
        content = @Content(schema = @Schema(implementation = AbstractThrowableProblem.class)))
@ApiResponse(responseCode = "401",
        content = @Content(schema = @Schema(implementation = AbstractThrowableProblem.class)))
@ApiResponse(responseCode = "403",
        content = @Content(schema = @Schema(implementation = AbstractThrowableProblem.class)))
@ApiResponse(responseCode = "415",
        content = @Content(schema = @Schema(implementation = AbstractThrowableProblem.class)))
@ApiResponse(responseCode = "500",
        content = @Content(schema = @Schema(implementation = AbstractThrowableProblem.class)))
public interface SecuredController {
}
