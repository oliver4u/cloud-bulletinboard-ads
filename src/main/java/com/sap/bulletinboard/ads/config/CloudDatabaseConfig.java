package com.sap.bulletinboard.ads.config;

import com.sap.bulletinboard.ads.models.Advertisement;
import com.sap.bulletinboard.ads.models.AdvertisementRepository;
import com.sap.bulletinboard.ads.util.EntityManagerFactoryProvider;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.cloud.service.relational.DataSourceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

/**
 * This class uses the database connection information provided in the environment variable VCAP_SERVICES to connect to
 * the database, initializes JPA, and creates a CRUD repository instance. This is done in three steps, as explained
 * below.
 * <p>
 * As this class is registered in the Spring application context ({link com.sap.bulletinboard.ads.ContextListener)),
 * the three methods annotated with {@literal @}Bean also are registered and used to provide bean instances of
 * {@link DataSource}, {@link EntityManagerFactory}, and {@link JpaTransactionManager}, respectively.
 * <p>
 * (Step 3) The @EnableJpaRepositories annotation (of Spring Data JPA) is used to provide a convenient repository, based
 * on JPA (EntityManager, TransactionManager).
 */
@Configuration
@EnableJpaRepositories(basePackageClasses = AdvertisementRepository.class)
@Profile("cloud")
public class CloudDatabaseConfig extends AbstractCloudConfig {
    /**
     * Parses VCAP_SERVICES from Cloud configuration and provides a DataSource.
     */
    @Bean
    public DataSource dataSource() {
        /*
         * Load BasicDbcpPooledDataSourceCreator before TomcatJdbcPooledDataSourceCreator. Also see the following link
         * for a detailed discussion of this issue:
         * https://stackoverflow.com/questions/36885891/jpa-eclipselink-understanding-classloader-issues
         */
        List<String> dataSourceNames = Arrays.asList("BasicDbcpPooledDataSourceCreator",
                "TomcatJdbcPooledDataSourceCreator", "HikariCpPooledDataSourceCreator",
                "TomcatDbcpPooledDataSourceCreator");
        DataSourceConfig dataSourceConfig = new DataSourceConfig(dataSourceNames);
        return connectionFactory().dataSource(dataSourceConfig);
    }

    /**
     * Based on a DataSource, provides EntityManager (JPA)
     */
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        return EntityManagerFactoryProvider.get(dataSource, Advertisement.class.getPackage().getName());
    }

    /**
     * Based on a EntityManager, provides TransactionManager (JPA)
     */
    @Bean(name = "transactionManager")
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
