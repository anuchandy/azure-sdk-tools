package com.azure.longrunning.test.servicebus;

import com.azure.core.amqp.AmqpRetryMode;
import com.azure.core.amqp.AmqpRetryOptions;
import com.azure.core.amqp.exception.AmqpException;
import com.azure.core.util.IterableStream;
import com.azure.core.util.logging.ClientLogger;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusException;
import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.azure.messaging.servicebus.ServiceBusReceiverClient;
import com.azure.messaging.servicebus.models.ServiceBusReceiveMode;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
public class MessageReceiverSync extends LongRunningRunner {
    private final ClientLogger LOGGER = new ClientLogger(MessageReceiverSync.class);

    @Override
    public void run() {
        String connectionString = envArgs.get(Constants.SERVICEBUS_CONN_STR);
        String queueName = envArgs.get(Constants.SERVICEBUS_QUEUE_NAME);
        String topicName = queueName == null ? envArgs.get(Constants.SERVICEBUS_TOPIC_NAME, null) : null;
        String subscriptionName = queueName == null ? envArgs.get(Constants.SERVICEBUS_SUBSCRIPTION_NAME, null) : null;
        String metricKey = queueName != null? queueName : topicName + "/" + subscriptionName;

        ServiceBusReceiverClient client = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .retryOptions(new AmqpRetryOptions().setMaxRetries(20)
                        .setTryTimeout(Duration.ofMillis(5000))
                        .setDelay(Duration.ofMillis(3000))
                        .setMode(AmqpRetryMode.FIXED)
                )
                .receiver()
                .queueName(queueName)
                .topicName(topicName)
                .receiveMode(ServiceBusReceiveMode.PEEK_LOCK)
                .disableAutoComplete()
                .subscriptionName(subscriptionName)
                .buildClient();

        while(true) {
            IterableStream<ServiceBusReceivedMessage> receivedMessages = client.receiveMessages(1);
            for (ServiceBusReceivedMessage receivedMessage : receivedMessages) {
                try {
                    LOGGER.info("Before complete. messageId: {}, lockToken: {}",
                            receivedMessage.getMessageId(),
                            receivedMessage.getLockToken());
                    client.complete(receivedMessage);
                    rateMeter.add(metricKey, 1);
                    LOGGER.info("After complete. messageId: {}, lockToken: {}",
                            receivedMessage.getMessageId(),
                            receivedMessage.getLockToken());
                } catch (ServiceBusException | AmqpException err) {
                    LOGGER.warning(err.getMessage());
                }
            }
        }
    }
}
