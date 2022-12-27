package com.zhmenko.web.controllers.config.database.postgres;

import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@EnableJpaRepositories(basePackages = {"com.zhmenko.data.**.repository"},
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager")
@Configuration
@EnableTransactionManagement
@Profile("test")
public class PostgresTestConfiguration {
    @Bean(name = "postgres")
    @FlywayDataSource
    @Primary
    public DataSource postgresDataSource() {
        PostgreSQLContainer<?> postgreSQLContainer = postgreSQLContainer();
        return DataSourceBuilder.create()
                .url(postgreSQLContainer.getJdbcUrl())
                .username(postgreSQLContainer.getUsername())
                .password(postgreSQLContainer.getPassword())
                .driverClassName(postgreSQLContainer.getDriverClassName())
                .build();
    }
    @Bean
    public PostgreSQLContainer<?> postgreSQLContainer() {
        PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");
        postgreSQLContainer.start();
        return postgreSQLContainer;
    }

    @Bean
    public TestEntityManager testEntityManager(EntityManagerFactory entityManagerFactory) {
        return new TestEntityManager(entityManagerFactory);
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
