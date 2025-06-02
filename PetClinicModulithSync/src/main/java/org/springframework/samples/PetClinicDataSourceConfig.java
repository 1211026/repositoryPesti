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
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.Statement;
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
        settings.setContinueOnError(true);  // Continuar mesmo com erros
        return new DataSourceScriptDatabaseInitializer(dataSource, settings);
    }
    
 // Adicione este método para criar o JdbcTemplate para o owner
    @Bean(name = "ownerJdbcTemplate")
    public JdbcTemplate ownerJdbcTemplate(@Qualifier("ownerDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    //PET ------------------------------------------------------------------------------------

    @Bean
    @ConfigurationProperties("app.datasource.pet")
    public DataSourceProperties petDataSourceProperties(){
        return new DataSourceProperties();
    }

    @Bean
    public DataSource petDataSource(@Qualifier("petDataSourceProperties") DataSourceProperties petDataSourceProperties) {
        return petDataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    DataSourceScriptDatabaseInitializer petDataSourceScriptDatabaseInitializer(@Qualifier("petDataSource") DataSource dataSource) {
        var settings = new DatabaseInitializationSettings();
        settings.setSchemaLocations(List.of("classpath*:db/Pet/schema.sql"));
        settings.setDataLocations(List.of("classpath*:db/Pet/data.sql"));
        settings.setMode(DatabaseInitializationMode.EMBEDDED);
        settings.setContinueOnError(true);  // Continuar mesmo com erros
        
        // Executar um script para limpar o banco antes
        try {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            // Desativar temporariamente as restrições de chave estrangeira
            stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
            // Remover tabelas na ordem correta
            stmt.execute("DROP TABLE IF EXISTS pet_visit");
            stmt.execute("DROP TABLE IF EXISTS pets");
            stmt.execute("DROP TABLE IF EXISTS types");
            // Reativar as restrições
            stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
            stmt.close();
            conn.close();
        } catch (Exception e) {
            // Apenas log do erro, mas continua
            System.err.println("Erro ao limpar tabelas: " + e.getMessage());
        }
        
        return new DataSourceScriptDatabaseInitializer(dataSource, settings);
    }
    
 // Adicione este método para criar o JdbcTemplate para o pet
    @Bean(name = "petJdbcTemplate")
    public JdbcTemplate petJdbcTemplate(@Qualifier("petDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    //VET ------------------------------------------------------------------------------------

    @Bean
    @ConfigurationProperties("app.datasource.vet")
    public DataSourceProperties vetDataSourceProperties(){
        return new DataSourceProperties();
    }

    @Bean
    public DataSource vetDataSource(@Qualifier("vetDataSourceProperties") DataSourceProperties vetDataSourceProperties) {
        return vetDataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    

    @Bean
    DataSourceScriptDatabaseInitializer vetDataSourceScriptDatabaseInitializer(@Qualifier("vetDataSource") DataSource dataSource) {
        var settings = new DatabaseInitializationSettings();
        settings.setSchemaLocations(List.of("classpath*:db/Vet/schema.sql"));
        settings.setDataLocations(List.of("classpath*:db/Vet/data.sql"));
        settings.setMode(DatabaseInitializationMode.EMBEDDED);
        settings.setContinueOnError(true);  // Continuar mesmo com erros
        return new DataSourceScriptDatabaseInitializer(dataSource, settings);
    }
    
 // Adicione este método para criar o JdbcTemplate para o vet
    @Bean(name = "vetJdbcTemplate")
    public JdbcTemplate vetJdbcTemplate(@Qualifier("vetDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    //VISIT ------------------------------------------------------------------------------------

    @Bean
    @ConfigurationProperties("app.datasource.visit")
    public DataSourceProperties visitDataSourceProperties(){
        return new DataSourceProperties();
    }

    @Bean
    public DataSource visitDataSource(@Qualifier("visitDataSourceProperties") DataSourceProperties visitDataSourceProperties) {
        return visitDataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    DataSourceScriptDatabaseInitializer visitDataSourceScriptDatabaseInitializer(@Qualifier("visitDataSource") DataSource dataSource) {
        var settings = new DatabaseInitializationSettings();
        settings.setSchemaLocations(List.of("classpath*:db/Visit/schema.sql"));
        settings.setDataLocations(List.of("classpath*:db/Visit/data.sql"));
        settings.setMode(DatabaseInitializationMode.EMBEDDED);
        settings.setContinueOnError(true);  // Continuar mesmo com erros
        return new DataSourceScriptDatabaseInitializer(dataSource, settings);
    }
    
 // Adicione este método para criar o JdbcTemplate para o visit
    @Bean(name = "visitJdbcTemplate")
    public JdbcTemplate visitJdbcTemplate(@Qualifier("visitDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
