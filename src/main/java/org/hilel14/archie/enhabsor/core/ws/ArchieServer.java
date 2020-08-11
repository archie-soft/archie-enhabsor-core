package org.hilel14.archie.enhabsor.core.ws;

import io.jsonwebtoken.impl.crypto.MacProvider;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.hilel14.archie.enhabsor.core.Config;

/**
 * ArchieServer class.
 *
 */
public class ArchieServer {

    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:9001/archie-enhabsor-ws/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this
     * application.
     *
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() throws Exception {

        ServiceLocator serviceLocator = ServiceLocatorUtilities.createAndPopulateServiceLocator();

        // create a resource config that scans for JAX-RS resources and providers
        // in com.example.rest package
        final ResourceConfig resourceConfig = new ResourceConfig().packages(true,
                "org.hilel14.archie.enhabsor.core.ws.resources");

        Map<String, Object> properties = new HashMap<>();
        properties.put("archie.config", new Config());
        properties.put("jwt.key", MacProvider.generateKey());
        resourceConfig.addProperties(properties);

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(
                URI.create(BASE_URI),
                resourceConfig,
                serviceLocator);
    }

    /**
     * ArchieServer method.
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws Exception {
        final HttpServer server = startServer();
        System.out.println("Server name: " + server.getServerConfiguration().getName());
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.shutdown();
    }
}
