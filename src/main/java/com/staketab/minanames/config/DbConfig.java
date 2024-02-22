package com.staketab.minanames.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@PropertySource({"classpath:application.yaml"})
@EnableJpaRepositories(
        basePackages = "com.staketab.minanames.repository",
        entityManagerFactoryRef = "minaNamesEntityManager",
        transactionManagerRef = "minaNamesTransactionManager"
)
@EnableTransactionManagement
public class DbConfig {
    private static final String MINA_NAMES_ENTITIES_PACKAGE = "com.staketab.minanames.entity";
    private static final String MINA_NAMES_PERSISTENCE_UNIT_NAME = "minaNames";

    @Value(value = "${hibernate.mina-names.hbm2ddl.auto}")
    private String dllAuto;
    @Value(value = "${hibernate.mina-names.show_sql:false}")
    private String showSql;
    @Value(value = "${hibernate.mina-names.batch_size}")
    private int batchSize;
    @Value(value = "${hibernate.mina-names.generate_statistics:false}")
    private boolean generateStatistics;

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.mina-names-datasource")
    public DataSource minaNamesDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public Map<String, Object> minaNamesJpaProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", dllAuto);
        properties.put("hibernate.show_sql", showSql);
        properties.put("hibernate.jdbc.batch_size", batchSize);
        properties.put("hibernate.generate_statistics", generateStatistics);
        return properties;
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean minaNamesEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPersistenceUnitName(MINA_NAMES_PERSISTENCE_UNIT_NAME);
        em.setDataSource(minaNamesDataSource());
        em.setPackagesToScan(MINA_NAMES_ENTITIES_PACKAGE);
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaPropertyMap(minaNamesJpaProperties());
        return em;
    }

    @Bean
    @Primary
    public PlatformTransactionManager minaNamesTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(minaNamesEntityManager().getObject());
        return transactionManager;
    }
}
