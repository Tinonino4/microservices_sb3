package com.tinonino.microservices.core.product.productcompositeservice.infra.rest.input;

import com.tinonino.microservices.core.product.productcompositeservice.domain.ProductAggregate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "ProductCompositeController", description = "RestAPI for composite product info.")
public interface ProductCompositeController {
    /**
     * Sample usage: "curl $HOST:$PORT/product-composite/1".
     *
     * @param productId Id of the product
     * @return the composite product info, if found, else null
     */
    @Operation(summary = "${api.product-composite.get-composite-product.description}",
    description = "${api.product-composite.get-composite-product.notes}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description =
        "${api.responseCodes.ok.description}"),
        @ApiResponse(responseCode = "404", description =
        "${api.responseCodes.notFound.description}"),
        @ApiResponse(responseCode = "422", description =
        "${api.responseCodes.unprocessableEntity.description}")
    })
    @GetMapping(
            value = "/product-composite/{productId}",
            produces = "application/json")
    ProductAggregate getProduct(@PathVariable int productId);
}
