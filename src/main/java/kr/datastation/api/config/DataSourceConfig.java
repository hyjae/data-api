package kr.datastation.api.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties
public class DataSourceConfig {

    @Primary
    @Bean(name = "datasource1")
    @ConfigurationProperties(prefix="database1.datasource")
    @Qualifier("datasetDataSource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "datasource2")
    @Qualifier("dataStationDataSource")
    @ConfigurationProperties(prefix="database2.datasource")
    public DataSource dataSource2() {
        return DataSourceBuilder.create().build();
    }
}
