package space.xtack.api.resource;

import com.codahale.metrics.annotation.Timed;
import space.xtack.api.Database;
import space.xtack.api.auth.AuthUtils;
import space.xtack.api.model.Account;
import space.xtack.api.xpring.XrpClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Optional;

@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
public class CreateAccountResource {

    private Database database;

    public CreateAccountResource(Database database) {
        this.database = database;
    }

    @POST
    @Timed
    public Account post(@FormParam("username") Optional<String> usernameParam,
                       @FormParam("password") Optional<String> passwordParam,
                       @FormParam("email") Optional<String> emailParam) {
        if (!usernameParam.isPresent() || !passwordParam.isPresent() || !emailParam.isPresent()) {
            throw new WebApplicationException("Missing registration params", 400);
        }
        String username = usernameParam.get();
        String password = passwordParam.get();
        String email = emailParam.get();
        try {
            String mnemonic = XrpClient.getRandomWallet().getMnemonic();
            String uuid = database.createAccount(username, AuthUtils.hashPassword(password), email, mnemonic);
            return new Account(uuid, username, AuthUtils.hashPassword(password), email, mnemonic, null);
        } catch (URISyntaxException | SQLException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            throw new WebApplicationException(503);
        }
    }
}
