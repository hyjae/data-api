package kr.datastation.api.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "dataStationEntityManagerFactory",
        transactionManagerRef = "dataStationTransactionManager",
        basePackages = {"kr.datastation.api.repository.datastation"} // repository ref
)
public class DataStationSourceConfig {

    private final DataSource dataStationDataSource;

    @Autowired
    public DataStationSourceConfig(@Qualifier("dataStationDataSource") DataSource dataStationDataSource) {
        this.dataStationDataSource = dataStationDataSource;
    }

    @Bean(name = "dataStationEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean dataStationEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(this.dataStationDataSource)
                .packages("kr.datastation.api.model.datastation") // domain ref
                .persistenceUnit("datastation") // TODO: ?
                .build();
    }

    @Bean(name = "dataStationTransactionManager")
    public PlatformTransactionManager datasetTransactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(dataStationEntityManagerFactory(builder).getObject());
    }
}
