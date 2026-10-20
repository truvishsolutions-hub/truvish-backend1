package com.truvish.truvishbackend.cold.config;

import jakarta.persistence.EntityManagerFactory;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.truvish.truvishbackend.cold.repository",
        entityManagerFactoryRef = "coldEntityManagerFactory",
        transactionManagerRef = "coldTransactionManager"
)
public class ColdDatabaseConfig {

    // =========================================================
    // COLD DATASOURCE PROPERTIES
    // =========================================================

    @Bean(name = "coldDataSourceProperties")
    @ConfigurationProperties(prefix = "cold.datasource")
    public DataSourceProperties coldDataSourceProperties() {
        return new DataSourceProperties();
    }


    // =========================================================
    // COLD DATASOURCE
    // =========================================================

    @Bean(name = "coldDataSource")
    @ConfigurationProperties(prefix = "cold.datasource.hikari")
    public DataSource coldDataSource(
            @Qualifier("coldDataSourceProperties")
            DataSourceProperties properties
    ) {
        return properties
                .initializeDataSourceBuilder()
                .build();
    }


    // =========================================================
    // COLD ENTITY MANAGER FACTORY
    // =========================================================

    @Bean(name = "coldEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean coldEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("coldDataSource")
            DataSource coldDataSource
    ) {

        Map<String, Object> properties = new HashMap<>();

        properties.put(
                "hibernate.hbm2ddl.auto",
                "update"
        );

        properties.put(
                "hibernate.show_sql",
                true
        );

        properties.put(
                "hibernate.format_sql",
                true
        );

        properties.put(
                "hibernate.jdbc.lob.non_contextual_creation",
                true
        );

        properties.put(
                "hibernate.dialect",
                "org.hibernate.dialect.PostgreSQLDialect"
        );


        /*
         * =====================================================
         * IMPORTANT
         * =====================================================
         *
         * COLD EntityManager scans ONLY:
         *
         * com.truvish.truvishbackend.cold.entity
         *
         * Therefore cold_* tables are created only inside:
         *
         * truvish_cold_db
         */

        return builder
                .dataSource(coldDataSource)

                .packages(
                        "com.truvish.truvishbackend.cold.entity"
                )

                .persistenceUnit("cold")
                .properties(properties)
                .build();
    }


    // =========================================================
    // COLD TRANSACTION MANAGER
    // =========================================================

    @Bean(name = "coldTransactionManager")
    public PlatformTransactionManager coldTransactionManager(
            @Qualifier("coldEntityManagerFactory")
            EntityManagerFactory coldEntityManagerFactory
    ) {
        return new JpaTransactionManager(coldEntityManagerFactory);
    }
}