package space.xtack.api.auth;

import io.dropwizard.auth.Authorizer;
import space.xtack.api.model.Account;

public class XtackAuthorizer implements Authorizer<Account> {
    @Override
    public boolean authorize(Account account, String s) {
        return account.getUuid() != null && s.equals("USER");
    }
}
