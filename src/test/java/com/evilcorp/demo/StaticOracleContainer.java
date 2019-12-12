package com.evilcorp.demo;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.OracleContainer;

public class StaticOracleContainer {
    public static OracleContainer getContainer() {
        return LazyOracleContainer.ORACLE_CONTAINER;
    }

    private static class LazyOracleContainer {
        private static final OracleContainer ORACLE_CONTAINER = makeContainer();

        private static OracleContainer makeContainer() {
            //Hardcoded, devops put this image to artifactory
            final String dockerImageName = "my/oracle-for-habr";
            final OracleContainer container = new OracleContainer(dockerImageName)
                    // Username which testcontainers is going to use
                    // to find out if container is up and running
                    .withUsername("SYSTEM")
                    // Password which testcontainers is going to use
                    // to find out if container is up and running
                    .withPassword("123")
                    // Tell testcontainers, that those ports should
                    // be mapped to external ports
                    .withExposedPorts(1521, 5500)
                    // Oracle database is not going to start if less
                    // than 1gb of shared memory is available, so this is necessary
                    .withSharedMemorySize(2147483648L)
                    // This the same as giving the container
                    // -v /path/to/init_db.sql:/u01/app/oracle/scripts/startup/init_db.sql
                    // Oracle will execute init_db.sql, after container is started
                    .withClasspathResourceMapping("init_db.sql"
                            , "/u01/app/oracle/scripts/startup/init_db.sql"
                            , BindMode.READ_ONLY);

            container.start();
            return container;
        }
    }
}
