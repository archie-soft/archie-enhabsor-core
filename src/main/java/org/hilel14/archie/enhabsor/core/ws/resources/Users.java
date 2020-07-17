package org.hilel14.archie.enhabsor.core.ws.resources;

import java.sql.SQLException;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.hilel14.archie.enhabsor.core.Config;

import org.hilel14.archie.enhabsor.core.users.Credentials;
import org.hilel14.archie.enhabsor.core.users.User;
import org.hilel14.archie.enhabsor.core.users.UserManager;

/**
 * Root resource (exposed at "users" path)
 *
 * @author hilel14
 */
@Path("users")
public class Users {

    static final Logger LOGGER = LoggerFactory.getLogger(Users.class);
    final Config config;
    final UserManager userManager;

    public Users() throws Exception {
        this.config = new Config();
        userManager = new UserManager(config.getDataSource(), config.getKey());
    }

    @GET
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getAllUsers() throws SQLException {
        return userManager.getAllUsers();
    }

    @POST
    @Path("authenticate")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User authenticate(Credentials credentials) throws Exception {
        LOGGER.debug("authenticating user {}", credentials.getUsername());
        User user = userManager.authenticate(credentials);
        LOGGER.info("authentication {} for user {}",
                user == null ? "failed" : "succeeded", credentials.getUsername());
        return user;
    }

    @POST
    @Path("create")
    @RolesAllowed("manager")
    @Consumes(MediaType.APPLICATION_JSON)
    public void createUser(User user) throws Exception {
        userManager.createUser(user);
    }

    @PUT
    @Path("password")
    @RolesAllowed("manager")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updatePassword(User user) throws Exception {
        LOGGER.info("changing password of user {} ", user.getUsername());
        userManager.updatePassword(user);
    }

    @PUT
    @Path("role")
    @RolesAllowed("manager")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateRole(User user) throws Exception {
        LOGGER.info("changing role of user {} ", user.getUsername());
        userManager.updateRole(user);
    }

    @DELETE
    @RolesAllowed("manager")
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteUser(User user) throws Exception {
        LOGGER.info("deleting user {} ", user.getUsername());
        userManager.deleteUser(user);
    }

}
