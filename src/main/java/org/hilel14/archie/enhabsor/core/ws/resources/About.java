package org.hilel14.archie.enhabsor.core.ws.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author hilel14
 */
@Path("about")
public class About {

    static final Logger LOGGER = LoggerFactory.getLogger(About.class);

    @PermitAll
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Properties getConfiguration() throws Exception {
        Properties props = getMavenProperties();
        return props;
    }

    private Properties getMavenProperties() throws IOException {
        Properties props = new Properties();
        // war classpath current default directory is WEB-INF/classes
        //String resourceName = "../../META-INF/maven/org.hilel14/archie-beeri-ws/pom.properties";
        String resourceName = "META-INF/maven/org.hilel14/archie-enhabsor-core/pom.properties";
        InputStream inStream = About.class.getClassLoader().getResourceAsStream(resourceName);
        if (inStream == null) {
            LOGGER.warn("Unable to load {}", resourceName);
        } else {
            props.load(inStream);
        }
        LOGGER.debug("groupId = {}", props.getProperty("groupId"));
        LOGGER.debug("artifactId = {}", props.getProperty("artifactId"));
        LOGGER.debug("version = {}", props.getProperty("version"));
        return props;
    }

}
