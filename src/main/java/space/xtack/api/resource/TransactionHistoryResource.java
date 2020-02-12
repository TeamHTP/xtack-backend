package space.xtack.api.resource;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.auth.Auth;
import space.xtack.api.Database;
import space.xtack.api.model.Account;
import space.xtack.api.model.Answer;
import space.xtack.api.model.XtackTransaction;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

@Path("/transactions")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionHistoryResource {

    private Database database;

    public TransactionHistoryResource(Database database) {
        this.database = database;
    }

    @GET
    @Timed
    @RolesAllowed("USER")
    public ArrayList<XtackTransaction> get(@Auth Optional<Account> accOpt) {
        try {
            Account account = accOpt.get();
            return database.getTransactionsForAccount(account.getUuid());
        } catch (URISyntaxException | SQLException e) {
            e.printStackTrace();
            throw new WebApplicationException(500);
        }
    }

}
