package com.zhmenko.web.controllers.containers;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresDbContainer extends PostgreSQLContainer<PostgresDbContainer> {
    private static final String IMAGE_VERSION = "postgres:11.1";
    private static PostgresDbContainer container;

    private PostgresDbContainer() {
        super(IMAGE_VERSION);
    }

    public static PostgresDbContainer getInstance() {
        if (container == null) {
            container = new PostgresDbContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}