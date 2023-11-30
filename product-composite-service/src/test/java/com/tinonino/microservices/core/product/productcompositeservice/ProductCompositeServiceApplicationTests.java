package com.tinonino.microservices.core.product.productcompositeservice;

import com.tinonino.microservices.core.product.productcompositeservice.domain.entities.Product;
import com.tinonino.microservices.core.product.productcompositeservice.domain.entities.Recommendation;
import com.tinonino.microservices.core.product.productcompositeservice.domain.entities.Review;
import com.tinonino.microservices.core.product.productcompositeservice.domain.exception.InvalidInputException;
import com.tinonino.microservices.core.product.productcompositeservice.domain.exception.ProductNotFoundException;
import com.tinonino.microservices.core.product.productcompositeservice.infra.rest.output.facades.ProductFacade;
import com.tinonino.microservices.core.product.productcompositeservice.infra.rest.output.facades.RecommendationFacade;
import com.tinonino.microservices.core.product.productcompositeservice.infra.rest.output.facades.ReviewFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;

@SpringBootTest(webEnvironment = RANDOM_PORT,
		classes = {TestSecurityConfig.class},
		properties = {
				"spring.security.oauth2.resourceserver.jwt.issuer-uri=",
				"spring.main.allow-bean-definition-overriding=true",
				"eureka.client.enabled=false"})
class ProductCompositeServiceApplicationTests {
	private static final int PRODUCT_ID_OK = 1;
	private static final int PRODUCT_ID_NOT_FOUND = 2;
	private static final int PRODUCT_ID_INVALID = 3;

	@Autowired
	private WebTestClient client;

	@MockBean
	private ProductFacade productFacade;
	@MockBean
	private RecommendationFacade recommendationFacade;
	@MockBean
	private ReviewFacade reviewFacade;

	@BeforeEach
	void setUp() {
		when(productFacade.getProduct(PRODUCT_ID_OK))
				.thenReturn(Mono.just(new Product(PRODUCT_ID_OK, "name", 1, "mock-address")));

		when(recommendationFacade.getRecommendations(PRODUCT_ID_OK))
				.thenReturn(Flux.fromIterable(singletonList(new Recommendation(PRODUCT_ID_OK, 1, "author", 1, "content", "mock address"))));

		when(reviewFacade.getReviews(PRODUCT_ID_OK))
				.thenReturn(Flux.fromIterable(singletonList(new Review(PRODUCT_ID_OK, 1, "author", "subject", "content", "mock address"))));

		when(productFacade.getProduct(PRODUCT_ID_NOT_FOUND)).thenThrow(new ProductNotFoundException("NOT FOUND: " + PRODUCT_ID_NOT_FOUND));

		when(productFacade.getProduct(PRODUCT_ID_INVALID)).thenThrow(new InvalidInputException("INVALID: " + PRODUCT_ID_INVALID));
	}

	@Test
	void contextLoads() {}

	@Test
	void getProductById() {

		getAndVerifyProduct(PRODUCT_ID_OK, OK)
				.jsonPath("$.productId").isEqualTo(PRODUCT_ID_OK)
				.jsonPath("$.recommendations.length()").isEqualTo(1)
				.jsonPath("$.reviews.length()").isEqualTo(1);
	}

	@Test
	void getProductNotFound() {

		getAndVerifyProduct(PRODUCT_ID_NOT_FOUND, NOT_FOUND)
				.jsonPath("$.path").isEqualTo("/product-composite/" + PRODUCT_ID_NOT_FOUND)
				.jsonPath("$.message").isEqualTo("NOT FOUND: " + PRODUCT_ID_NOT_FOUND);
	}

	@Test
	void getProductInvalidInput() {

		getAndVerifyProduct(PRODUCT_ID_INVALID, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/product-composite/" + PRODUCT_ID_INVALID)
				.jsonPath("$.message").isEqualTo("INVALID: " + PRODUCT_ID_INVALID);
	}

	private WebTestClient.BodyContentSpec getAndVerifyProduct(int productId, HttpStatus expectedStatus) {
		return client.get()
				.uri("/product-composite/" + productId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}


	@Test
	void testStepVerifier() {
		StepVerifier.create(Flux.just(1,2,3,4)
				.filter(n -> n % 2 == 0)
				.map(n -> n * 2)
				.log())
				.expectNext(4, 8)
				.verifyComplete();
	}
}
