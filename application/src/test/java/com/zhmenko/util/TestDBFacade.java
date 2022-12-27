package com.zhmenko.util;

import com.zhmenko.web.controllers.config.database.postgres.PostgresTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;

@Import(PostgresTestConfiguration.class)
public class TestDBFacade {

    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void cleanDatabase() {
        transactionTemplate.execute(status -> {
            JdbcTestUtils.deleteFromTables(jdbcTemplate,
                    "nac_role_network_resource",
                    "security_user_security_role",
                    "security_user_nac_role",
                    "security_role",
                    "network_resource",
                    "nac_role",
                    "user_device_block_info",
                    "user_device_alerts",
                    "user_device",
                    "security_user");
            return null;
        });
    }

    public <T> T find(Object id, Class<T> entityClass) {
        return transactionTemplate.execute(status -> testEntityManager.find(entityClass, id));
    }

    public <T> List<T> findEntities(List<Object> ids, Class<T> entityClass) {
        List<T> result = new ArrayList<>();
        transactionTemplate.execute(status -> {
            for (Object id : ids) {
                result.add(testEntityManager.find(entityClass, id));
            }
            return null;
        });
        return result;
    }

    public void saveAll(TestBuilder<?>... builders) {
        transactionTemplate.execute(status -> {
            for (TestBuilder<?> b : builders) {
                save(b);
            }
            return null;
        });
    }

    public <T> T save(TestBuilder<T> builder) {
        return transactionTemplate.execute(
                status -> testEntityManager.persistAndFlush(builder.build()));
    }

    public Object save(Object object) {
        return transactionTemplate.execute(
                status -> testEntityManager.persistAndFlush(object));
    }

    public <T> TestBuilder<T> persistedOnce(TestBuilder<T> builder) {
        return new TestBuilder<>() {
            private T entity;

            @Override
            public T build() {
                if (entity == null) {
                    entity = save(builder);
                }
                return entity;
            }
        };
    }

    @TestConfiguration
    public static class Config {

        @Bean
        public TestDBFacade testDBFacade() {
            return new TestDBFacade();
        }
    }
}