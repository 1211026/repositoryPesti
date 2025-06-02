package org.springframework.samples;


import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.init.DataSourceScriptDatabaseInitializer;
import org.springframework.boot.sql.init.DatabaseInitializationMode;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.List;

@Configuration(proxyBeanMethods = false)
public class PetClinicDataSourceConfig {

    //OWNER ------------------------------------------------------------------------------------

    @Bean
    @Primary
    @ConfigurationProperties("app.datasource.owner")
    public DataSourceProperties ownerDataSourceProperties(){
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource ownerDataSource(DataSourceProperties ownerDataSourceProperties) {
        return ownerDataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    DataSourceScriptDatabaseInitializer ownerDataSourceScriptDatabaseInitializer(@Qualifier("ownerDataSource") DataSource dataSource) {
        var settings = new DatabaseInitializationSettings();
        settings.setSchemaLocations(List.of("classpath*:db/Owner/schema.sql"));
        settings.setDataLocations(List.of("classpath*:db/Owner/data.sql"));
        settings.setMode(DatabaseInitializationMode.EMBEDDED);
        return new DataSourceScriptDatabaseInitializer(dataSource,settings);
    }

    //PET ------------------------------------------------------------------------------------

    @Bean
    @ConfigurationProperties("app.datasource.pet")
    public DataSourceProperties petDataSourceProperties(){
        return new DataSourceProperties();
    }

    @Bean
    public DataSource petDataSource(@Qualifier("petDataSourceProperties") DataSourceProperties petDataSourceProperties) {
        return DataSourceBuilder
                .create()
                .url(petDataSourceProperties.getUrl())
                .username(petDataSourceProperties.getUsername())
                .password(petDataSourceProperties.getPassword())
                .build();
    }

    @Bean
    DataSourceScriptDatabaseInitializer petDataSourceScriptDatabaseInitializer(@Qualifier("petDataSource") DataSource dataSource) {
        var settings = new DatabaseInitializationSettings();
        settings.setSchemaLocations(List.of("classpath*:db/Pet/schema.sql"));
        settings.setDataLocations(List.of("classpath*:db/Pet/data.sql"));
        settings.setMode(DatabaseInitializationMode.EMBEDDED);
        return new DataSourceScriptDatabaseInitializer(dataSource,settings);
    }

    //VET ------------------------------------------------------------------------------------

    @Bean
    @ConfigurationProperties("app.datasource.vet")
    public DataSourceProperties vetDataSourceProperties(){
        return new DataSourceProperties();
    }

    @Bean
    public DataSource vetDataSource(@Qualifier("vetDataSourceProperties") DataSourceProperties vetDataSourceProperties) {
        return DataSourceBuilder
                .create()
                .url(vetDataSourceProperties.getUrl())
                .username(vetDataSourceProperties.getUsername())
                .password(vetDataSourceProperties.getPassword())
                .build();
    }

    @Bean
    DataSourceScriptDatabaseInitializer vetDataSourceScriptDatabaseInitializer(@Qualifier("vetDataSource") DataSource dataSource) {
        var settings = new DatabaseInitializationSettings();
        settings.setSchemaLocations(List.of("classpath*:db/Vet/schema.sql"));
        settings.setDataLocations(List.of("classpath*:db/Vet/data.sql"));
        settings.setMode(DatabaseInitializationMode.EMBEDDED);
        return new DataSourceScriptDatabaseInitializer(dataSource,settings);
    }

    //VISIT ------------------------------------------------------------------------------------

    @Bean
    @ConfigurationProperties("app.datasource.visit")
    public DataSourceProperties visitDataSourceProperties(){
        return new DataSourceProperties();
    }

    @Bean
    public DataSource visitDataSource(@Qualifier("visitDataSourceProperties") DataSourceProperties visitDataSourceProperties) {
        return DataSourceBuilder
                .create()
                .url(visitDataSourceProperties.getUrl())
                .username(visitDataSourceProperties.getUsername())
                .password(visitDataSourceProperties.getPassword())
                .build();
    }

    @Bean
    DataSourceScriptDatabaseInitializer visitDataSourceScriptDatabaseInitializer(@Qualifier("visitDataSource") DataSource dataSource) {
        var settings = new DatabaseInitializationSettings();
        settings.setSchemaLocations(List.of("classpath*:db/Visit/schema.sql"));
        settings.setDataLocations(List.of("classpath*:db/Visit/data.sql"));
        settings.setMode(DatabaseInitializationMode.EMBEDDED);
        return new DataSourceScriptDatabaseInitializer(dataSource,settings);
    }
}
