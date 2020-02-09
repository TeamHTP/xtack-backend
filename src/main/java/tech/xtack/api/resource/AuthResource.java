package tech.xtack.api.resource;

import com.codahale.metrics.annotation.Timed;
import tech.xtack.api.Database;
import tech.xtack.api.auth.AuthUtils;
import tech.xtack.api.model.Account;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Optional;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    private Database database;

    public AuthResource(Database database) {
        this.database = database;
    }

    @GET
    @Timed
    public Account get(@QueryParam("email") Optional<String> emailParam, @QueryParam("password") Optional<String> passwordParam, @Context HttpServletResponse response) {
        if (!emailParam.isPresent() || !passwordParam.isPresent()) {
            throw new WebApplicationException("Missing email or password.", 400);
        }
        try {
            Account account = database.getAccountFromEmailAndPassword(emailParam.get(), AuthUtils.hashPassword(passwordParam.get()));
            if (account != null) {
                String authToken = AuthUtils.generateAuthToken();
                database.createAuthToken(account.getUuid(), authToken);
                account.setSessionToken(authToken);
                response.addHeader("Set-Cookie", "session_token=" + authToken + "; HttpOnly; Max-Age=" + (7 * 24 * 60 * 60));
                return account;
            }
            else {
                throw new WebApplicationException(404);
            }
        } catch (URISyntaxException | SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new WebApplicationException("Database error.", 503);
        }
    }

}
