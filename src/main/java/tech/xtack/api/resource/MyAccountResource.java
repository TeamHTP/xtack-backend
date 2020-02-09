package tech.xtack.api.resource;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.auth.Auth;
import tech.xtack.api.model.Account;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
public class MyAccountResource {

    @GET
    @Timed
    @RolesAllowed("USER")
    public Account get(@Auth Optional<Account> accOpt) {
        return accOpt.get();
    }

}
