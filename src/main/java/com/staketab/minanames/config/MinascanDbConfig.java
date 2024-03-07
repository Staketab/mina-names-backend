package com.staketab.minanames.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(value = "spring.minascan-datasource.enabled", havingValue = "true")
@PropertySource({"classpath:application.yaml"})
@EnableJpaRepositories(
        basePackages = "com.staketab.minanames.minascan",
        entityManagerFactoryRef = "minascanEntityManager",
        transactionManagerRef = "minascanTransactionManager"
)
public class MinascanDbConfig {

    private static final String MINASCAN_ENTITIES_PACKAGE = "com.staketab.minanames.minascan";
    private static final String MINASCAN_PERSISTENCE_UNIT_NAME = "minascan";

    @Value(value = "${hibernate.minascan.hbm2ddl.auto}")
    private String dllAuto;
    @Value(value = "${hibernate.minascan.show_sql}")
    private String showSql;
    @Value(value = "${hibernate.minascan.dialect}")
    private String dialect;

    @Bean
    @ConfigurationProperties(prefix = "spring.minascan-datasource")
    public DataSource minascanDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public Map<String, Object> minascanJpaProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", dllAuto);
        properties.put("hibernate.show_sql", showSql);
        properties.put("hibernate.dialect", dialect);
        return properties;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean minascanEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPersistenceUnitName(MINASCAN_PERSISTENCE_UNIT_NAME);
        em.setDataSource(minascanDataSource());
        em.setPackagesToScan(MINASCAN_ENTITIES_PACKAGE);
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaPropertyMap(minascanJpaProperties());
        return em;
    }

    @Bean
    public PlatformTransactionManager minascanTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(minascanEntityManager().getObject());
        return transactionManager;
    }
}
