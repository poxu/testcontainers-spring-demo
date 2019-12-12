package com.evilcorp.demo;

import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.OracleContainer;

public class TestcontainersInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    static Logger log = LoggerFactory.getLogger(TestcontainersInitializer.class);

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        final String testcontainersEnabled = applicationContext.getEnvironment().getProperty("evilcorp.testcontainers.enabled");
        if (!"true".equals(testcontainersEnabled)) {
            return;
        }
        OracleContainer oracleContainer = StaticOracleContainer.getContainer();
        oracleContainer.followOutput(s -> log.debug(() -> s.getUtf8String()));

        // Container ip address may differ depending on where it is launched
        // (linux, MacOs, Windows or docker instance) , so use
        // oracleContainer.getContainerIpAddress() to find out which ip to use
        //
        // Testcontainers is going to map standard docker port to a
        // random external port, so use oracleContainer.getOraclePort() to
        // find out which port to use
        final String jdbcUrl = "jdbc:oracle:thin:@//" + oracleContainer.getContainerIpAddress() + ":" + oracleContainer.getOraclePort() + "/XE";
        // Username and password which are defined in init_db.sql
        // Those should be used by application
        final String user = "TEST_USER";
        final String password = "passwordnoquotes";
        TestPropertyValues.of(
                "spring.jpa.properties.hibernate.default_schema=" + user,
                "spring.datasource.driver-class-name=oracle.jdbc.OracleDriver",
                "spring.jpa.database-platform=org.hibernate.dialect.Oracle10gDialect",
                //hardcoded because login/password are passed to container in init-db.sql
                "spring.datasource.username=" + user,
                "spring.datasource.password=" + password,
                "spring.datasource.url=" + jdbcUrl,
                "spring.liquibase.url=" + jdbcUrl,
                "spring.liquibase.user=" + user,
                "spring.liquibase.password=" + password
        //).applyTo(applicationContext.getEnvironment(), TestPropertyValues.Type.MAP, "test");
        ).applyTo(applicationContext.getEnvironment(), TestPropertyValues.Type.MAP, "test");
    }
}
