package com.azure.longrunning.test.servicebus;


import com.microsoft.applicationinsights.TelemetryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;

@Service
public abstract class LongRunningRunner {
    @Autowired
    protected LongRunningOptions options;

    @Autowired
    private ApplicationContext applicationContext;

    protected TelemetryClient telemetryClient;

    protected RateMeter rateMeter;

    @Bean
    private TelemetryClient createTelemetryClient() {
        return new TelemetryClient();
    }

    @Bean
    private RateMeter createRateMeter() {
        return new RateMeter(applicationContext.getBean(TelemetryClient.class), Duration.ofSeconds(
                Integer.parseInt(options.get(Constants.METRIC_INTERVAL_SEC, "60")))
        );
    }

    @PostConstruct
    private void postConstruct() {
        this.rateMeter = applicationContext.getBean(RateMeter.class);
        this.telemetryClient = applicationContext.getBean(TelemetryClient.class);
    }

    public abstract void run();
}
