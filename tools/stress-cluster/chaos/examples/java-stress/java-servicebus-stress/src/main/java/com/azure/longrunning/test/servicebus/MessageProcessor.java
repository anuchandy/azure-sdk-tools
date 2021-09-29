package com.azure.longrunning.test.servicebus;

import com.azure.core.amqp.AmqpRetryOptions;
import com.azure.core.util.logging.ClientLogger;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.models.ServiceBusReceiveMode;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class MessageProcessor extends LongRunningRunner {
    private final ClientLogger LOGGER = new ClientLogger(MessageProcessor.class);

    @Override
    public void run() {
        String connectionString = cmdlineArgs.get(Constants.SERVICEBUS_CONN_STR);
        String queueName = cmdlineArgs.get(Constants.SERVICEBUS_QUEUE_NAME);
        String topicName = queueName == null ? cmdlineArgs.get(Constants.SERVICEBUS_TOPIC_NAME, null) : null;
        String subscriptionName = queueName == null ? cmdlineArgs.get(Constants.SERVICEBUS_SUBSCRIPTION_NAME, null) : null;
        var processorClientBuilder = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .retryOptions(new AmqpRetryOptions()
                    .setTryTimeout(Duration.ofSeconds(5)))
                .processor();
        processorClientBuilder.queueName(queueName);
        processorClientBuilder.topicName(topicName);
        processorClientBuilder.subscriptionName(subscriptionName);
        String metricKey = queueName != null? queueName : topicName + "/" + subscriptionName;

        var processorClient = processorClientBuilder
                .maxConcurrentCalls(2)
                //.maxAutoLockRenewDuration(Duration.ofMinutes(5))
                .receiveMode(ServiceBusReceiveMode.PEEK_LOCK)
                .disableAutoComplete()
                .prefetchCount(0)
                .processMessage(messageContext -> {
                    System.out.printf("messageProcessor %s%n", messageContext.getMessage().getLockToken());
                    LOGGER.info("Before complete. messageId: {}, lockToken: {}",
                            messageContext.getMessage().getMessageId(),
                            messageContext.getMessage().getLockToken());
                    messageContext.complete();
                    rateMeter.add(metricKey, 1);
                    LOGGER.info("After complete. messageId: {}, lockToken: {}",
                            messageContext.getMessage().getMessageId(),
                            messageContext.getMessage().getLockToken());
                })
                .processError(err -> {
                    throw LOGGER.logExceptionAsError(new RuntimeException(err.getException()));
                })
                .buildProcessorClient();
        processorClient.start();
    }
}

