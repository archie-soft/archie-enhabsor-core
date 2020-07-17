package org.hilel14.archie.enhabsor.core.ws.resources;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.hilel14.archie.enhabsor.core.model.Cat;

@Path("r1")
public class MyResource {

    @GET
    @Path("m1")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Cat getChikita() {
        Cat cat = new Cat();
        cat.setId(1);
        cat.setName("Chikita");
        return cat;
    }

    @GET
    @Path("m2")
    @DenyAll
    @Produces(MediaType.APPLICATION_JSON)
    public Cat getKoka() {
        Cat cat = new Cat();
        cat.setId(2);
        cat.setName("Koka");
        return cat;
    }

    @GET
    @Path("m3")
    @RolesAllowed("manager")
    @Produces(MediaType.APPLICATION_JSON)
    public Cat getMuki() {
        Cat cat = new Cat();
        cat.setId(3);
        cat.setName("Muki");
        return cat;
    }
}
