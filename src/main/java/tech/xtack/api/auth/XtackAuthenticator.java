package tech.xtack.api.auth;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import tech.xtack.api.Database;
import tech.xtack.api.model.Account;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Optional;

public class XtackAuthenticator implements Authenticator<String, Account> {

    private Database database;

    public XtackAuthenticator(Database database) {
        this.database = database;
    }

    @Override
    public Optional<Account> authenticate(String sessionToken) throws AuthenticationException {
        try {
            return Optional.ofNullable(database.getAccountFromSessionToken(sessionToken));
        } catch (URISyntaxException | SQLException e) {
            e.printStackTrace();
            throw new AuthenticationException("Database error");
        }
    }
}
