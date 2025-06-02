package org.springframework.samples;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.jdbc.core.simple.JdbcClient;

import javax.sql.DataSource;

@SpringBootApplication
@ImportRuntimeHints(PetClinicRuntimeHints.class)
public class PetClinicApplicationSync {
    public static void main(String[] args) {
        SpringApplication.run(PetClinicApplicationSync.class, args);
    }


    @Bean(name = "syncOwnerJdbcClient")
    JdbcClient ownerJdbcClient(@Qualifier("ownerDataSource") DataSource dataSource){
        return JdbcClient.create(dataSource);
    }


    @Bean(name = "syncPetJdbcClient")
    JdbcClient petJdbcClient(@Qualifier("petDataSource") DataSource dataSource){
        return JdbcClient.create(dataSource);
    }

    @Bean(name = "syncVetJdbcClient")
    JdbcClient vetJdbcClient(@Qualifier("vetDataSource") DataSource dataSource){
        return JdbcClient.create(dataSource);
    }

    @Bean(name = "syncVisitJdbcClient")
    JdbcClient visitJdbcClient(@Qualifier("visitDataSource") DataSource dataSource){
        return JdbcClient.create(dataSource);
    }
}