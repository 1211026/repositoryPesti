package org.springframework.samples;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration(proxyBeanMethods = false)
public class PetClinicJdbcConfig {

    // — Owner JdbcTemplate —————————————————————
    @Bean
    @Qualifier("ownerJdbcTemplate")
    public JdbcTemplate ownerJdbcTemplate(
            @Qualifier("ownerDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    // — Pet JdbcTemplate ——————————————————————
    @Bean
    @Qualifier("petJdbcTemplate")
    public JdbcTemplate petJdbcTemplate(
            @Qualifier("petDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    // — Vet JdbcTemplate ——————————————————————
    @Bean
    @Qualifier("vetJdbcTemplate")
    public JdbcTemplate vetJdbcTemplate(
            @Qualifier("vetDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    // — Visit JdbcTemplate —————————————————————
    @Bean
    @Qualifier("visitJdbcTemplate")
    public JdbcTemplate visitJdbcTemplate(
            @Qualifier("visitDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }
}
