package com.zhmenko.config.database.postgres;

import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@EnableJpaRepositories(basePackages = {"com.zhmenko.data.**.repository"},
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager")
@Configuration
@EnableTransactionManagement
@PropertySource(value = "classpath:application-data.yml")
@Profile({"dev","prod"})
public class PostgresConfiguration {
    @Bean(name = "postgres")
    @ConfigurationProperties(prefix="spring.datasource.postgres")
    @FlywayDataSource
    @Primary
    public DataSource postgresDataSource() {
        return DataSourceBuilder.create().build();
    }

/*    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean postgresEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(postgresDataSource())
                .packages("com.zhmenko.**.model", "com.zhmenko.**.model.**")
                .build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager postgresTransactionManager(
            final @Qualifier("postgresEntityManagerFactory") LocalContainerEntityManagerFactoryBean postgresEntityManagerFactory) {
        return new JpaTransactionManager(postgresEntityManagerFactory.getObject());
    }*/
}
