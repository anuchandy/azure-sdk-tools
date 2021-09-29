package com.azure.longrunning.test.servicebus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class LongRunningApp implements ApplicationRunner {

    @Autowired
    protected ApplicationContext applicationContext;

    @Autowired
    protected LongRunningOptions longRunningOptions;

    public static void main(String[] args) {
        System.out.println("arg.len == " + args.length);
        for (String arg : args) {
            System.out.println("Y -> " + arg);
        }
        SpringApplication.run(LongRunningApp.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String testClass = longRunningOptions.get("testname");
        LongRunningRunner runner = (LongRunningRunner) applicationContext.getBean(Class.forName(testClass));
        runner.run();
    }
}
