package com.zhmenko.web.controllers;

import io.hypersistence.optimizer.HypersistenceOptimizer;
import io.hypersistence.optimizer.core.config.JpaConfig;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

public class HibernateOptimizerTest {
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @PostConstruct
    void initHibernateOptimizer() {
        new HypersistenceOptimizer(
            new JpaConfig(entityManagerFactory)
        );
    }
}
