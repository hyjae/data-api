package kr.datastation.api.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "datasetEntityManagerFactory",
        transactionManagerRef = "datasetTransactionManager",
        basePackages = {"kr.datastation.api.repository.dataset"} // repository ref
)
public class DatasetSourceConfig {

    private final DataSource datasetDataSource;

    @Autowired
    public DatasetSourceConfig(@Qualifier("datasetDataSource") DataSource datasetDataSource) {
        this.datasetDataSource = datasetDataSource;
    }

    @Primary
    @Bean(name = "datasetEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean datasetEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(this.datasetDataSource)
                .packages("kr.datastation.api.model.dataset") // domain ref
                .persistenceUnit("dataset")
                .build();
    }

    @Primary
    @Bean(name = "datasetTransactionManager")
    public PlatformTransactionManager datasetTransactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(datasetEntityManagerFactory(builder).getObject());
    }
}
