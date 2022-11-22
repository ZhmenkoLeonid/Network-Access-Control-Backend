package com.zhmenko.web.controllers.config.database.clickhouse;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.ClickHouseContainer;

import javax.sql.DataSource;

@Configuration
//@PropertySource(value = "classpath:application-test.yml")
@Profile("test")
public class ClickHouseTestConfiguration {
    @Bean(name = "clickhouse")
    //@ConfigurationProperties(prefix = "spring.datasource.clickhouse")
    public DataSource clickHouseDataSource() {
        ClickHouseContainer clickHouseContainer = clickHouseContainer();
        return DataSourceBuilder.create()
                .url(clickHouseContainer.getJdbcUrl())
                .username(clickHouseContainer.getUsername())
                .password(clickHouseContainer.getPassword())
                .driverClassName("com.clickhouse.jdbc.ClickHouseDriver")
                .build();
    }
    @Bean
    public ClickHouseContainer clickHouseContainer() {
        ClickHouseContainer clickHouseContainer = new ClickHouseContainer("clickhouse/clickhouse-server:latest")
                .withInitScript("clickhouse_db.sql");
        clickHouseContainer.start();
        return clickHouseContainer;
    }

    @Bean("clickhouseJdbcTemplate")
    public JdbcTemplate clickhouseJdbcTemplate() {
        DataSource dataSource = clickHouseDataSource();
        return new JdbcTemplate(dataSource);
    }
}

