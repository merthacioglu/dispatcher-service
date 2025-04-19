package org.mhacioglu.dispatcherservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.cloud.function.context.test.FunctionalSpringBootTest;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.function.Function;

@FunctionalSpringBootTest
public class DispatchingFunctionsIntegrationTests {

    @Autowired
    private FunctionCatalog functionCatalog;

    @Test
    void packAndLabelOrder() {
        Function<OrderAcceptedMessage, Flux<Message<?>>>
                packAndLabel = functionCatalog.lookup(Function.class, "pack|label");
        long orderId = 121;

        StepVerifier
                .create(packAndLabel.apply(new OrderAcceptedMessage(orderId)))
                .expectNextMatches(message -> {
                    // Get the payload as a byte array and convert to a string
                    byte[] payload = (byte[]) message.getPayload();
                    String json = new String(payload);
                    // Simple check that the JSON contains the expected orderId
                    return json.contains("\"orderId\":" + orderId);
                })
                .verifyComplete();
    }
}
