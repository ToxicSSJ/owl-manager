package com.owl;

import com.corundumstudio.socketio.Configuration;

public class Constant {

    public static Configuration getKernelConfiguration() {

        Configuration configuration = new Configuration();
        configuration.setPort(8090);
        configuration.setHostname("localhost");

        return configuration;

    }

    public static Configuration getFilesConfiguration() {

        Configuration configuration = new Configuration();
        configuration.setPort(8091);
        configuration.setHostname("127.0.0.1");

        return configuration;

    }

    public static Configuration getApplicationConfiguration() {

        Configuration configuration = new Configuration();
        configuration.setPort(8092);
        configuration.setHostname("127.0.0.1");

        return configuration;

    }

}
