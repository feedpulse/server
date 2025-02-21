package de.feedpulse.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;

/**
 * This class is a configuration class that is responsible for loading
 * Git properties defined in the "git.properties" file.
 * <p>
 * It is annotated with @Configuration and @PropertySource, indicating that it is a
 * Spring configuration class and that it should load properties from the "git.properties"
 * file located in the classpath.
 */
@Configuration
@PropertySource(value = "classpath:git.properties", ignoreResourceNotFound = true)
public class GitMetaPropertiesConfig {

    private static final Logger logger = LoggerFactory.getLogger(GitMetaPropertiesConfig.class);

    @Autowired
    private ConfigurableEnvironment env;

    /**
     * This method is called after the bean has been constructed and the dependencies have been injected. It is responsible for printing the Git properties defined in the "git.properties" file.
     * <p>
     * It iterates over the property sources in the environment, looking for an EnumerablePropertySource with the name "git.properties". If found, it prints the Git properties tothe console.
     * <p>
     * Note: This method is defined in the `GitMetaPropertiesConfig` class, which is annotated with `@Configuration` and `@PropertySource`. The `env` field is autowired, providing
     * access to the environment.
     */
    @PostConstruct
    public void printGitProperties() {
        for (org.springframework.core.env.PropertySource<?> ps : env.getPropertySources()) {
            if (ps instanceof EnumerablePropertySource eps) {
                if (eps.getName().contains("git.properties")) {
//                    logger.info("Git properties:");
                    for (String propName : eps.getPropertyNames()) {
//                        logger.info("{}: {}", propName, eps.getProperty(propName));
                    }
                }
            }
        }
    }
}
