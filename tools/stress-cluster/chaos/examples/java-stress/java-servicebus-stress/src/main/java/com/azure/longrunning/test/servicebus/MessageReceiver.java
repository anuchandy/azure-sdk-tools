package com.azure.longrunning.test.servicebus;

import com.azure.core.util.logging.ClientLogger;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusReceiverAsyncClient;
import com.azure.messaging.servicebus.models.ServiceBusReceiveMode;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static com.azure.longrunning.test.servicebus.Constants.*;

@Service
public class MessageReceiver extends LongRunningRunner {
    private final ClientLogger LOGGER = new ClientLogger(MessageReceiver.class);

    @Override
    public void run() {
        String connectionString = options.get(SERVICEBUS_CONN_STR);
        String queueName = options.get(SERVICEBUS_QUEUE_NAME);
        String topicName = queueName == null ? options.get(SERVICEBUS_TOPIC_NAME) : null;
        String subscriptionName = queueName == null ? options.get(SERVICEBUS_SUBSCRIPTION_NAME) : null;
        String metricKey = queueName != null? queueName : topicName + "/" + subscriptionName;

        ServiceBusReceiverAsyncClient client = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .receiver()
                .queueName(queueName)
                .topicName(topicName)
                .receiveMode(ServiceBusReceiveMode.PEEK_LOCK)
                .disableAutoComplete()
                .subscriptionName(subscriptionName)
                .buildAsyncClient();
        client.receiveMessages()
                .flatMap(message -> {
                    LOGGER.info("message received: {}", message.getMessageId());
                    rateMeter.add(metricKey, 1);
                    return client.complete(message)
                            .onErrorResume(error -> {
                                LOGGER.info(error.getMessage());
                                return Mono.empty();});
                })
                .subscribe(
                    message -> {
                        LOGGER.info("message subscribed: {}");
                    },
                error -> {
                    LOGGER.info("error happened: {}", error);
                    telemetryClient.trackException((Exception) error);
                });

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
