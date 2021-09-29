package com.azure.longrunning.test.servicebus;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderAsyncClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.azure.longrunning.test.servicebus.Constants.*;

@Service
public class MessageSender extends LongRunningRunner {

    @Override
    public void run() {
        String connectionString = options.get(SERVICEBUS_CONN_STR);
        String queueName = options.get(SERVICEBUS_QUEUE_NAME);
        String topicName = queueName == null ? options.get(SERVICEBUS_TOPIC_NAME) : null;

        ServiceBusSenderAsyncClient client = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .sender()
                .queueName(queueName)
                .topicName(topicName)
                .buildAsyncClient();
        Flux.range(0, Integer.MAX_VALUE).concatMap(i -> {
            List<ServiceBusMessage> eventDataList = new ArrayList<>();
            IntStream.range(0, 500).forEach(j -> {
                eventDataList.add(new ServiceBusMessage("A"));
            });
            return client.sendMessages(eventDataList);
        }).subscribe();
    }
}
