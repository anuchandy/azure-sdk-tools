package com.azure.longrunning.test.servicebus;

import com.azure.core.amqp.AmqpRetryMode;
import com.azure.core.amqp.AmqpRetryOptions;
import com.azure.core.util.logging.ClientLogger;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderAsyncClient;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.azure.longrunning.test.servicebus.Constants.SERVICEBUS_CONNECTION_STRING;
import static com.azure.longrunning.test.servicebus.Constants.SERVICEBUS_QUEUE_NAME;
import static com.azure.longrunning.test.servicebus.Constants.SERVICEBUS_TOPIC_NAME;

@Service
public class MessageSenderSync extends LongRunningRunner {
    private final ClientLogger LOGGER = new ClientLogger(MessageProcessor.class);

    @Override
    public void run() {
        String connectionString = options.get(SERVICEBUS_CONNECTION_STRING);
        String queueName = options.get(SERVICEBUS_QUEUE_NAME);
        String topicName = queueName == null ? options.get(SERVICEBUS_TOPIC_NAME) : null;

        ServiceBusSenderClient client = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .retryOptions(new AmqpRetryOptions().setMaxRetries(10).setMode(AmqpRetryMode.FIXED))
                .sender()
                .queueName(queueName)
                .topicName(topicName)
                .buildClient();

        try (client) {
            for (long i = 0; i < Long.MAX_VALUE; i++) {
                List<ServiceBusMessage> eventDataList = new ArrayList<>();
                IntStream.range(0, 500).forEach(j -> {
                    eventDataList.add(new ServiceBusMessage("A"));
                });
                try {
                    client.sendMessages(eventDataList);
                } catch (Exception exp) {
                    LOGGER.error(exp.getMessage());
                }
            }
        }
    }
}
