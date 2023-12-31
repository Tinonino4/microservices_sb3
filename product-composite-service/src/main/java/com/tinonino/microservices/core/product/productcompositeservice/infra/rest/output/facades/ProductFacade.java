package com.tinonino.microservices.core.product.productcompositeservice.infra.rest.output.facades;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinonino.microservices.core.utils.http.ServiceUtil;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import com.tinonino.microservices.core.product.productcompositeservice.domain.Event;
import com.tinonino.microservices.core.product.productcompositeservice.domain.entities.Product;
import com.tinonino.microservices.core.product.productcompositeservice.domain.exception.InvalidInputException;
import com.tinonino.microservices.core.product.productcompositeservice.domain.exception.ProductNotFoundException;
import com.tinonino.microservices.core.utils.http.HttpErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;

import static com.tinonino.microservices.core.product.productcompositeservice.domain.Event.Type.CREATE;
import static com.tinonino.microservices.core.product.productcompositeservice.domain.Event.Type.DELETE;
import static java.util.logging.Level.FINE;

@Component
public class ProductFacade {

    private static final Logger LOG = LoggerFactory.getLogger(ProductFacade.class);
    private final WebClient webClient;
    private final ObjectMapper mapper;
    private final String productServiceUrl = "http://product";
    private final StreamBridge streamBridge;
    private final Scheduler publishEventScheduler;
    private final ServiceUtil serviceUtil;

    @Autowired
    public ProductFacade(
            @Qualifier("publishEventScheduler") Scheduler publishEventScheduler,
            WebClient.Builder webClient,
            ObjectMapper mapper,
            StreamBridge streamBridge, ServiceUtil serviceUtil) {
        this.publishEventScheduler = publishEventScheduler;
        this.webClient = webClient.build();
        this.mapper = mapper;
        this.streamBridge = streamBridge;
        this.serviceUtil = serviceUtil;
    }
   @Retry(name = "product")
   @TimeLimiter(name = "product")
   @CircuitBreaker(name = "product", fallbackMethod = "getProductFallbackValue")
   public Mono<Product> getProduct(int productId, int delay, int faultPercent) {

     URI url = UriComponentsBuilder.fromUriString(productServiceUrl
             + "/products/{productId}?delay={delay}&faultPercent={faultPercent}")
             .build(productId, delay, faultPercent);

     return webClient.get().uri(url)
             .retrieve().bodyToMono(Product.class)
             .log(LOG.getName(), FINE)
             .onErrorMap(WebClientResponseException.class, ex -> handleException(ex));
    }

    private Mono<Product> getProductFallbackValue(int productId, int delay, int faultPercent, CallNotPermittedException ex) {

        LOG.warn("Creating a fail-fast fallback product for productId = {}, delay = {}, faultPercent = {} and exception = {} ",
                productId, delay, faultPercent, ex.toString());

        if (productId == 13) {
            String errMsg = "Product Id: " + productId + " not found in fallback cache!";
            LOG.warn(errMsg);
            throw new ProductNotFoundException(errMsg);
        }

        return Mono.just(new Product(productId, "Fallback product" + productId, productId, serviceUtil.getServiceAddress()));
    }

    public Mono<Product> createProduct(Product body) {
        return Mono.fromCallable(() -> {
            sendMessage("products-out-0", new Event(CREATE, body.getProductId(), body));
            return body;
        }).subscribeOn(publishEventScheduler);
    }

    public Mono<Void> deleteProduct(int productId) {
        return Mono.fromRunnable(() -> sendMessage("products-out-0", new Event(DELETE, productId, null)))
                .subscribeOn(publishEventScheduler).then();
    }

    private void sendMessage(String bindingName, Event event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("partitionKey", event.getKey())
                .build();
        streamBridge.send(bindingName, message);
    }

    public Mono<Health> getProductHealth() {
        String url = productServiceUrl + "/actuator/health";
        LOG.debug("Will call the Health API on URL: {}", url);
        return webClient.get().uri(url).retrieve().bodyToMono(String.class)
                .map(s -> new Health.Builder().up().build())
                .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
                .log(LOG.getName(), FINE);
    }

    private Throwable handleException(Throwable ex) {
        if (!(ex instanceof WebClientResponseException)) {
            LOG.warn("Got a unexpected error: {}, will rethrow it", ex.toString());
            return ex;
        }
        WebClientResponseException wcre = (WebClientResponseException)ex;
        switch (Objects.requireNonNull(HttpStatus.resolve(wcre.getStatusCode().value()))) {
            case NOT_FOUND:
                return new ProductNotFoundException(getErrorMessage(wcre));

            case UNPROCESSABLE_ENTITY:
                return new InvalidInputException(getErrorMessage(wcre));

            default:
                LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", wcre.getStatusCode());
                LOG.warn("Error body: {}", wcre.getResponseBodyAsString());
                return ex;
        }
    }
    private String getErrorMessage(WebClientResponseException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).message();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }
}
