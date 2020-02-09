package tech.xtack.api.resource;

import com.codahale.metrics.annotation.Timed;
import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.WalletGenerationResult;
import io.xpring.xrpl.XpringKitException;
import tech.xtack.api.Database;
import tech.xtack.api.auth.AuthUtils;
import tech.xtack.api.model.Account;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
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
        WalletGenerationResult wgr;
        try {
            wgr = Wallet.generateRandomWallet();
        } catch (XpringKitException e) {
            e.printStackTrace();
            throw new WebApplicationException(503);
        }
        try {
            String mnemonic = wgr.getMnemonic();
            String uuid = database.createAccount(username, AuthUtils.hashPassword(password), email, mnemonic);
            return new Account(uuid, username, AuthUtils.hashPassword(password), email, mnemonic, null);
        } catch (URISyntaxException | SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new WebApplicationException(503);
        }
    }
}
