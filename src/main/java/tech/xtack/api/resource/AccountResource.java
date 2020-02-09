package tech.xtack.api.resource;

import com.codahale.metrics.annotation.Timed;
import tech.xtack.api.Database;
import tech.xtack.api.model.Account;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.net.URISyntaxException;
import java.sql.SQLException;

@Path("/account/{uuid}")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

    private Database database;

    public AccountResource(Database database) {
        this.database = database;
    }

    @GET
    @Timed
    public Account get(@PathParam("uuid") String uuid) {
        try {
            Account account = database.getAccount(uuid);
            if (account != null) {
                return account;
            }
            else {
                throw new WebApplicationException(404);
            }
        }
        catch (URISyntaxException | SQLException e) {
            e.printStackTrace();
            throw new WebApplicationException(503);
        }
    }

}
