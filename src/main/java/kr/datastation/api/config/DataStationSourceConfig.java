package kr.datastation.api.config;

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

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "kr.datastation.api.repository.datastation",
        entityManagerFactoryRef = "dataStationEntityManagerFactory",
        transactionManagerRef= "dataStationTransactionManager")
public class DataStationSourceConfig {

    @Bean
    @ConfigurationProperties("database2.datasource")
    public DataSourceProperties dataStationDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("database2.datasource.configuration")
    public DataSource dataStationDataSource() {
        return dataStationDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "dataStationEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean dataStationEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataStationDataSource())
                .packages("kr.datastation.api.model.datastation")
                .build();
    }

    @Bean
    public PlatformTransactionManager dataStationTransactionManager(
            final @Qualifier("dataStationEntityManagerFactory") LocalContainerEntityManagerFactoryBean dataStationEntityManagerFactory) {
        return new JpaTransactionManager(dataStationEntityManagerFactory.getObject());
    }
}
