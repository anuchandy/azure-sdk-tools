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
    protected CmdlineArgs cmdlineArgs;

    @Autowired
    protected EnvArgs envArgs;

    public static void main(String[] args) {
        SpringApplication.run(LongRunningApp.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        final String testClass = cmdlineArgs.get(Constants.TEST_NAME);
        LongRunningRunner runner = (LongRunningRunner) applicationContext.getBean(Class.forName(testClass));
        runner.run();
    }
}
