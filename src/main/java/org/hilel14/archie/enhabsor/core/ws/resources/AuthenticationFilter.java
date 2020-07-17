package org.hilel14.archie.enhabsor.core.ws.resources;

import io.jsonwebtoken.Jwts;
import java.lang.reflect.Method;
import java.security.Key;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.glassfish.jersey.server.wadl.internal.WadlResource;

@Provider
public class AuthenticationFilter implements javax.ws.rs.container.ContainerRequestFilter {

    static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Context
    private ResourceInfo resourceInfo;

    @Context
    private Configuration config;

    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    //private static final String AUTHENTICATION_SCHEME = "Basic"; // Bearer

    @Override
    public void filter(ContainerRequestContext requestContext) {

        if (resourceInfo.getResourceClass() == WadlResource.class) {
            return;
        }

        Method method = resourceInfo.getResourceMethod();

        if (method.isAnnotationPresent(PermitAll.class)) {
            return;
        }

        //Access denied for all
        if (method.isAnnotationPresent(DenyAll.class)) {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                    .entity("Access blocked for all users !!").build());
            return;
        }

        //Get request headers
        final MultivaluedMap<String, String> headers = requestContext.getHeaders();

        //Fetch authorization header
        final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);

        //If no authorization information present; block access
        if (authorization == null || authorization.isEmpty()) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("No authorization information present").build());
            return;
        }

        // get token and extract role
        Key key = (Key) config.getProperty("jwt.key");
        String token = authorization.get(0);
        String role = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject();

        //Verify user access
        if (method.isAnnotationPresent(RolesAllowed.class)) {
            RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
            Set<String> rolesSet = new HashSet<>(Arrays.asList(rolesAnnotation.value()));
            if (!rolesSet.contains(role)) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                        .entity("You cannot access this resource").build());
            }
        }

    }
}
