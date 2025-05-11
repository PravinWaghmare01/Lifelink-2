package org.organdomation.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Database configuration for MySQL
 */
@Configuration
@EnableTransactionManagement
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    @Autowired
    private Environment env;

    /**
     * Creates and configures a DataSource for MySQL
     * 
     * @return Configured DataSource for MySQL
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        logger.info("Configuring MySQL DataSource...");
        
        // Get database connection details from application.properties with environment variable override
        String jdbcUrl = env.getProperty("spring.datasource.url");
        String dbUsername = env.getProperty("spring.datasource.username");
        String dbPassword = env.getProperty("spring.datasource.password");
        
        // Override with environment variables if available
        if (env.getProperty("MYSQL_URL") != null) {
            jdbcUrl = env.getProperty("MYSQL_URL");
        }
        
        if (env.getProperty("MYSQL_USER") != null) {
            dbUsername = env.getProperty("MYSQL_USER");
        }
        
        if (env.getProperty("MYSQL_PASSWORD") != null) {
            dbPassword = env.getProperty("MYSQL_PASSWORD");
        }
        
        logger.info("Using JDBC URL: {}", jdbcUrl);
        
        // Create and configure data source
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);

        return dataSource;
    }
    
    /**
     * Creates the JdbcTemplate bean
     * 
     * @return JdbcTemplate for database operations
     */
    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }
    
    /**
     * Creates the EntityManagerFactory bean
     * 
     * @return EntityManagerFactory bean
     */
    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("org.organdomation.model");
        
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        em.setJpaVendorAdapter(vendorAdapter);
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.format_sql", "true");
        em.setJpaPropertyMap(properties);
        
        return em;
    }
    
    /**
     * Creates the transaction manager
     * 
     * @return JPA transaction manager
     */
    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }
}