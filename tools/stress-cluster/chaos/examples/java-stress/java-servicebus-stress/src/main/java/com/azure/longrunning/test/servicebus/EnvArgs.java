package com.azure.longrunning.test.servicebus;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

// Type exposing the environment arguments (e.g. output by the bicep file)
//
@Configuration
public class EnvArgs {
    private final Dotenv innerArgs;

    @Autowired
    public EnvArgs() {
        this.innerArgs = Dotenv.configure()
                .directory(System.getenv("ENV_FILE"))
                .load();
    }

    public String get(String name) {
        return innerArgs.get(name);
    }

    public String get(String name, String defaultValue) {
        return innerArgs.get(name, defaultValue);
    }
}
