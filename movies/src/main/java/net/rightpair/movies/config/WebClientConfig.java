package net.rightpair.movies.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }
}
