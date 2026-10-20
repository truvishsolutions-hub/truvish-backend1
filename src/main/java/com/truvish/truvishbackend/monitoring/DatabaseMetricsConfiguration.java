package com.truvish.truvishbackend.monitoring;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseMetricsConfiguration {

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(
            DatabaseMetrics databaseMetrics
    ) {

        return hibernateProperties -> {

            hibernateProperties.put(
                    "hibernate.session_factory.statement_inspector",
                    databaseMetrics
            );
        };
    }


    @Bean
    public Object databaseSqlMetrics(
            MeterRegistry meterRegistry,
            DatabaseMetrics databaseMetrics
    ) {

        Gauge.builder(
                        "db.sql.read",
                        databaseMetrics,
                        DatabaseMetrics::getReadCount
                )
                .description(
                        "Total database READ SQL statements executed"
                )
                .register(meterRegistry);


        Gauge.builder(
                        "db.sql.write",
                        databaseMetrics,
                        DatabaseMetrics::getWriteCount
                )
                .description(
                        "Total database WRITE SQL statements executed"
                )
                .register(meterRegistry);


        Gauge.builder(
                        "db.sql.select",
                        databaseMetrics,
                        DatabaseMetrics::getSelectCount
                )
                .description(
                        "Total SELECT SQL statements executed"
                )
                .register(meterRegistry);


        Gauge.builder(
                        "db.sql.insert",
                        databaseMetrics,
                        DatabaseMetrics::getInsertCount
                )
                .description(
                        "Total INSERT SQL statements executed"
                )
                .register(meterRegistry);


        Gauge.builder(
                        "db.sql.update",
                        databaseMetrics,
                        DatabaseMetrics::getUpdateCount
                )
                .description(
                        "Total UPDATE SQL statements executed"
                )
                .register(meterRegistry);


        Gauge.builder(
                        "db.sql.delete",
                        databaseMetrics,
                        DatabaseMetrics::getDeleteCount
                )
                .description(
                        "Total DELETE SQL statements executed"
                )
                .register(meterRegistry);


        Gauge.builder(
                        "db.sql.total",
                        databaseMetrics,
                        DatabaseMetrics::getTotalCount
                )
                .description(
                        "Total database SQL statements executed"
                )
                .register(meterRegistry);


        return new Object();
    }
}