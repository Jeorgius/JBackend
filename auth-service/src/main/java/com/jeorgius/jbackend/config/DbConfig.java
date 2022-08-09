package com.jeorgius.jbackend.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = {"com.jeorgius.jbackend.repository"})
public class DbConfig {

    @Primary
    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix = "spring.datasource.writable")
    @LiquibaseDataSource
    public HikariDataSource writableDataSource() {
        return (HikariDataSource) DataSourceBuilder.create().build();
    }

    @Bean(name = "readonlyDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.readonly")
    public HikariDataSource readonlyDataSource() {
        return (HikariDataSource) DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean writableEntityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                               @Qualifier("dataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.jeorgius.jbackend.entities")
                .persistenceUnit("core-db-rw-pu")
                .build();
    }

    @Bean(name = "readonlyEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean readonlyEntityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                               @Qualifier("readonlyDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("om.jeorgius.jbackend.entities")
                .persistenceUnit("core-db-ro-pu")
                .build();
    }

    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager writableTransactionManager(@Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean(name = "readonlyTransactionManager")
    public PlatformTransactionManager readonlyTransactionManager(@Qualifier("readonlyEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Primary
    @Bean(name = "jdbcTemplate")
    public JdbcTemplate writableJdbcTemplate(@Qualifier("dataSource") DataSource datasource) {
        return new JdbcTemplate(datasource);
    }

    @Bean(name = "readonlyJdbcTemplate")
    public JdbcTemplate readonlyJdbcTemplate(@Qualifier("readonlyDataSource") DataSource datasource) {
        return new JdbcTemplate(datasource);
    }
}
