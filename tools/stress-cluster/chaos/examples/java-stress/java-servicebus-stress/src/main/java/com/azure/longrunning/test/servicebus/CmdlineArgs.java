package com.azure.longrunning.test.servicebus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Configuration;

// Type exposing the command line arguments (e.g. provided via job yaml files in template dir)
//
@Configuration
public class CmdlineArgs {
    private final ApplicationArguments innerArgs;

    @Autowired
    public CmdlineArgs(ApplicationArguments innerArgs) {
        this.innerArgs = innerArgs;
    }

    public String get(String name) {
        if (innerArgs.containsOption(name)) {
            return innerArgs.getOptionValues(name).get(0);
        } else {
            return null;
        }
    }

    public String get(String name, String defaultValue) {
        if (innerArgs.containsOption(name)) {
            return innerArgs.getOptionValues(name).get(0);
        } else {
            return defaultValue;
        }
    }
}
