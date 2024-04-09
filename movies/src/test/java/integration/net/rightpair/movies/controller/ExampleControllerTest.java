package net.rightpair.movies.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

@WebFluxTest(controllers = ExampleController.class)
@AutoConfigureWebTestClient
class ExampleControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @Nested
    class FluxTest {
        @Test
        void flux_success() {
            webTestClient.
                    get()
                    .uri("/example/flux")
                    .exchange()
                    .expectStatus()
                    .is2xxSuccessful()
                    .expectBodyList(Integer.class)
                    .hasSize(3);
        }

        @Test
        void flux_step_verifier_approach_success() {
            var body = webTestClient.
                    get()
                    .uri("/example/flux")
                    .exchange()
                    .expectStatus()
                    .is2xxSuccessful()
                    .returnResult(Integer.class)
                    .getResponseBody();
            StepVerifier.create(body)
                    .expectNext(1, 2, 3)
                    .verifyComplete();
        }

        @Test
        void flux_consume_with_approach_success() {
            webTestClient.
                    get()
                    .uri("/example/flux")
                    .exchange()
                    .expectStatus()
                    .is2xxSuccessful()
                    .expectBodyList(Integer.class)
                    .consumeWith(result -> {
                        var responseBody = result.getResponseBody();
                        Assertions.assertNotNull(responseBody);
                        Assertions.assertEquals(3, responseBody.size());
                    });
        }
    }

    @Nested
    class MonoTest {
        @Test
        void mono_success() {
            webTestClient.
                    get()
                    .uri("/example/mono")
                    .exchange()
                    .expectStatus()
                    .is2xxSuccessful()
                    .expectBodyList(String.class)
                    .consumeWith(result -> {
                        var responseBody = result.getResponseBody();
                        Assertions.assertNotNull(responseBody);
                        Assertions.assertEquals("Hello, Mono!", responseBody.get(0));
                    });
        }
    }

    @Nested
    class EventStreamTest {
        @Test
        void event_stream_success() {
            var body = webTestClient.
                    get()
                    .uri("/example/stream")
                    .exchange()
                    .expectStatus()
                    .is2xxSuccessful()
                    .returnResult(Long.class)
                    .getResponseBody();
            StepVerifier.create(body)
                    .expectNext(0L, 1L, 2L)
                    .thenCancel()
                    .verify();
        }
    }
}