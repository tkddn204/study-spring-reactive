package net.rightpair.movies.controller;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public class SinksTest {

    @Test
    void sink() {
        //given
        Sinks.Many<Integer> replaySink = Sinks.many().replay().all();

        //when
        replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        //then
        Flux<Integer> resultFlux = replaySink.asFlux();
        resultFlux.subscribe(i -> System.out.println("Subscriber 1 : " + i));

        Flux<Integer> result2Flux = replaySink.asFlux();
        result2Flux.subscribe(i -> System.out.println("Subscriber 2 : " + i));

        replaySink.tryEmitNext(3);
    }

    @Test
    void sinks_multicast() {
        //given
        Sinks.Many<Integer> sinkMulticast = Sinks.many().multicast().onBackpressureBuffer();

        //when
        sinkMulticast.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        sinkMulticast.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        //then
        Flux<Integer> resultFlux = sinkMulticast.asFlux();
        resultFlux.subscribe(i -> System.out.println("Subscriber 1 : " + i));

        Flux<Integer> result2Flux = sinkMulticast.asFlux();
        result2Flux.subscribe(i -> System.out.println("Subscriber 2 : " + i));
        sinkMulticast.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);
    }

    @Test
    void sinks_unicast() {
        //given
        Sinks.Many<Integer> sinkUnicast = Sinks.many().unicast().onBackpressureBuffer();

        //when
        sinkUnicast.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        sinkUnicast.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        //then
        Flux<Integer> resultFlux = sinkUnicast.asFlux();
        resultFlux.subscribe(i -> System.out.println("Subscriber 1 : " + i));

        Flux<Integer> result2Flux = sinkUnicast.asFlux();
        result2Flux.subscribe(i -> System.out.println("Subscriber 2 : " + i));
        sinkUnicast.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);
    }
}
