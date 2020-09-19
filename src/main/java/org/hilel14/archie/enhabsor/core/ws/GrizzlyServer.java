package org.hilel14.archie.enhabsor.core.ws;

import io.jsonwebtoken.impl.crypto.MacProvider;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.hilel14.archie.enhabsor.core.Config;
import org.slf4j.LoggerFactory;

/**
 * ArchieServer class.
 *
 */
public class GrizzlyServer {

    public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(GrizzlyServer.class);
    // Logger l = Logger.getLogger("org.glassfish.grizzly.http.server.HttpHandler");

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this
     * application.
     *
     * @param args
     */
    public static void main(String[] args) {

        ServiceLocator serviceLocator = ServiceLocatorUtilities.createAndPopulateServiceLocator();

        // create a resource config that scans for JAX-RS resources and providers
        // in the dedicated package
        final ResourceConfig resourceConfig = new ResourceConfig().packages(true,
                "org.hilel14.archie.enhabsor.core.ws.resources");

        // config
        Map<String, Object> properties = new HashMap<>();
        Config config = null;
        try {
            config = new Config();
        } catch (Exception ex) {
            LOGGER.error("Error while starting grizzly server", ex);
            System.exit(1);
        }
        properties.put("archie.config", config);
        properties.put("jwt.key", MacProvider.generateKey());
        resourceConfig.addProperties(properties);

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        URI baseUri = URI.create(config.getGrizzlyBaseUri());
        LOGGER.info("Starting Grizzly server and exposing the Jersey application at {}", baseUri.toString());
        GrizzlyHttpServerFactory.createHttpServer(baseUri, resourceConfig, serviceLocator);
    }

}
