package com.azure.longrunning.test.servicebus;

import com.azure.core.amqp.AmqpRetryMode;
import com.azure.core.amqp.AmqpRetryOptions;
import com.azure.core.util.logging.ClientLogger;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class MessageSenderSync extends LongRunningRunner {
    private final ClientLogger LOGGER = new ClientLogger(MessageProcessor.class);

    @Override
    public void run() {
        String connectionString = envArgs.get(Constants.SERVICEBUS_CONN_STR);
        String queueName = envArgs.get(Constants.SERVICEBUS_QUEUE_NAME);
        String topicName = queueName == null ? envArgs.get(Constants.SERVICEBUS_TOPIC_NAME, null) : null;

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
