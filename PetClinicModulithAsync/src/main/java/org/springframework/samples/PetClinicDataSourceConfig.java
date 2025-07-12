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

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

@Configuration(proxyBeanMethods = false)
public class PetClinicDataSourceConfig {
	
	private void cleanupTables(DataSource dataSource, String... tables) {
	    try {
	        Connection conn = dataSource.getConnection();
	        Statement stmt = conn.createStatement();
	        // Desativar temporariamente as restrições de chave estrangeira
	        stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
	        // Remover tabelas na ordem especificada
	        for (String table : tables) {
	            stmt.execute("DROP TABLE IF EXISTS " + table);
	        }
	        // Reativar as restrições
	        stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
	        stmt.close();
	        conn.close();
	    } catch (Exception e) {
	        System.err.println("Erro ao limpar tabelas: " + e.getMessage());
	    }
	}

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
    	cleanupTables(dataSource, "owners");
    	
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
        return petDataSourceProperties
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }


    @Bean
    DataSourceScriptDatabaseInitializer petDataSourceScriptDatabaseInitializer(@Qualifier("petDataSource") DataSource dataSource) {
    	cleanupTables(dataSource, "pet_visit", "pets", "types");
    	
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
    public DataSource vetDataSource(@Qualifier("vetDataSourceProperties") DataSourceProperties props) {
        return props.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean
    DataSourceScriptDatabaseInitializer vetDataSourceScriptDatabaseInitializer(@Qualifier("vetDataSource") DataSource dataSource) {
    	cleanupTables(dataSource, "vet_specialties", "vets", "specialties");
    	
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
    public DataSource visitDataSource(@Qualifier("visitDataSourceProperties") DataSourceProperties props) {
        return props.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean
    DataSourceScriptDatabaseInitializer visitDataSourceScriptDatabaseInitializer(@Qualifier("visitDataSource") DataSource dataSource) {
    	cleanupTables(dataSource, "visits");
    	
    	var settings = new DatabaseInitializationSettings();
        settings.setSchemaLocations(List.of("classpath*:db/Visit/schema.sql"));
        settings.setDataLocations(List.of("classpath*:db/Visit/data.sql"));
        settings.setMode(DatabaseInitializationMode.EMBEDDED);
        return new DataSourceScriptDatabaseInitializer(dataSource,settings);
    }
}
