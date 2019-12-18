package kr.datastation.api.config;


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

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "kr.datastation.api.repository.dataset",
        entityManagerFactoryRef = "datasetEntityManagerFactory",
        transactionManagerRef= "datasetTransactionManager")
public class DatasetSourceConfig {

    @Primary
    @Bean
    @ConfigurationProperties("database1.datasource")
    public DataSourceProperties datasetDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean
    @ConfigurationProperties("database1.datasource.configuration")
    public DataSource datasetDataSource() {
        return datasetDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Primary
    @Bean(name = "datasetEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean datasetEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(datasetDataSource())
                .packages("kr.datastation.api.model.dataset")
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager datasetTransactionManager(
            final @Qualifier("datasetEntityManagerFactory") LocalContainerEntityManagerFactoryBean datasetEntityManagerFactory) {
        return new JpaTransactionManager(datasetEntityManagerFactory.getObject());
    }
}
