/*
package com.zhmenko.config.database;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Configuration
@PropertySource(value = "classpath:application-data.yml")
public class FlywayConfiguration {
    @Autowired
    //@Qualifier("postgres")
    private DataSource postgresDatasource;

    @PostConstruct
    public void migrateFlyway() {
        Flyway flyway;
        // postgres migration
        flyway = new Flyway(Flyway.configure()
                .dataSource(postgresDatasource)
                .locations("db/migration"));
        flyway.migrate();
    }
}
*/
