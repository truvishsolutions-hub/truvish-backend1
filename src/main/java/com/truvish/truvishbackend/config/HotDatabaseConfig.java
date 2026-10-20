package com.truvish.truvishbackend.config;

import jakarta.persistence.EntityManagerFactory;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
        basePackages = "com.truvish.truvishbackend",

        excludeFilters = @org.springframework.context.annotation.ComponentScan.Filter(
                type = org.springframework.context.annotation.FilterType.REGEX,
                pattern = "com\\.truvish\\.truvishbackend\\.cold\\..*"
        ),

        entityManagerFactoryRef = "hotEntityManagerFactory",
        transactionManagerRef = "hotTransactionManager"
)
public class HotDatabaseConfig {

    // =========================================================
    // HOT DATASOURCE PROPERTIES
    // =========================================================

    @Primary
    @Bean(name = "hotDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties hotDataSourceProperties() {

        return new DataSourceProperties();
    }


    // =========================================================
    // HOT DATASOURCE
    // =========================================================

    @Primary
    @Bean(name = "hotDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public DataSource hotDataSource(
            @Qualifier("hotDataSourceProperties")
            DataSourceProperties properties
    ) {

        return properties
                .initializeDataSourceBuilder()
                .build();
    }


    // =========================================================
    // HOT ENTITY MANAGER FACTORY
    // =========================================================

    @Primary
    @Bean(name = "hotEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean hotEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("hotDataSource")
            DataSource hotDataSource
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
                "hibernate.generate_statistics",
                true
        );

        properties.put(
                "hibernate.dialect",
                "org.hibernate.dialect.PostgreSQLDialect"
        );


        // =====================================================
        // ONLY HOT ENTITY PACKAGES
        // =====================================================

        return builder
                .dataSource(hotDataSource)

                .packages(
                        "com.truvish.truvishbackend.admin",
                        "com.truvish.truvishbackend.client",
                        "com.truvish.truvishbackend.ClientChooseBrand",
                        "com.truvish.truvishbackend.corporateLogin",
                        "com.truvish.truvishbackend.DemoRequest.entity",
                        "com.truvish.truvishbackend.inventory",
                        "com.truvish.truvishbackend.redemption",
                        "com.truvish.truvishbackend.TruBlankCode",
                        "com.truvish.truvishbackend.TruOpeAdmin",
                        "com.truvish.truvishbackend.voucherinventory",
                        "com.truvish.truvishbackend.wallet"
                )

                .persistenceUnit("hot")
                .properties(properties)
                .build();
    }


    // =========================================================
    // HOT TRANSACTION MANAGER
    // =========================================================

    @Primary
    @Bean(name = "hotTransactionManager")
    public PlatformTransactionManager hotTransactionManager(
            @Qualifier("hotEntityManagerFactory")
            EntityManagerFactory hotEntityManagerFactory
    ) {

        return new JpaTransactionManager(
                hotEntityManagerFactory
        );
    }
}