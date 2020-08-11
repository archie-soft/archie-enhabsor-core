package org.hilel14.archie;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import io.jsonwebtoken.impl.crypto.MacProvider;
import javax.sql.DataSource;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.dbcp2.BasicDataSource;
import org.hilel14.archie.enhabsor.core.Config;


/**
 *
 * @author hilel
 */
@javax.ws.rs.ApplicationPath("rest")
public class ApplicationConfig extends javax.ws.rs.core.Application {

    // ResourceConfig resourceConfig;
    static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfig.class);

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        //addRestResourceClasses(resources);
        return resources;
    }

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<>();
        addProperties(properties);
        return properties;
    }

    @Override
    public Set<Object> getSingletons() {
        Set<Object> singeltons = new HashSet<>();
        return singeltons;
    }

    private void addProperties(Map<String, Object> properties) {
        LOGGER.info("Loading Archie config properties");
        try {
            properties.put("archie.config", new Config());
        } catch (Exception ex) {
            LOGGER.error("Error when loading Archie config");
        }
        LOGGER.info("Adding other properties");
        properties.put("jwt.key", MacProvider.generateKey());
    }

    private ActiveMQConnectionFactory createJmsConnectionFactory(Properties props) {
        String brokerUrl = props.getProperty("archie.jms.broker");
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
        return factory;
    }

    private DataSource createJdbcDataSource(Properties props) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(props.getProperty("archie.jdbc.url"));
        dataSource.setDriverClassName(props.getProperty("archie.jdbc.driver"));
        dataSource.setUsername(props.getProperty("archie.jdbc.user"));
        dataSource.setPassword(props.getProperty("archie.jdbc.password"));
        dataSource.setMinIdle(5);
        dataSource.setMaxIdle(10);
        dataSource.setMaxOpenPreparedStatements(100);
        LOGGER.info("Database connection string: {}", props.getProperty("archie.jdbc.url"));
        return dataSource;
    }

    /*
    private void addRestResourceClasses(Set<Class<?>> resources) {        
        resources.add(org.glassfish.jersey.server.wadl.internal.WadlResource.class);
        resources.add(org.hilel14.archie.beeri.ws.About.class);
        resources.add(org.hilel14.archie.beeri.ws.Docs.class);
        resources.add(org.hilel14.archie.beeri.ws.Reports.class);
        resources.add(org.hilel14.archie.beeri.ws.Users.class);
        resources.add(org.hilel14.archie.beeri.ws.tools.AuthenticationFilter.class);
    }
*/

}
