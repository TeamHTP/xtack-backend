package space.xtack.api.resource;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.auth.Auth;
import space.xtack.api.adapter.XpringClient;
import space.xtack.api.model.Account;
import space.xtack.api.model.WalletAddresses;
import space.xtack.api.model.XtackWallet;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

@Path("/wallet")
@Produces(MediaType.APPLICATION_JSON)
public class WalletResource {

    @GET
    @Timed
    @RolesAllowed("USER")
    public XtackWallet get(@Auth Optional<Account> accOpt) {
        if (!accOpt.isPresent()) {
            throw new WebApplicationException(403);
        }
        Account account = accOpt.get();
        try {
            return new XtackWallet(
                    XpringClient.getEncodedAddresses(XtackWallet.MASTER_WALLET.getAddresses().getRAddress(),
                            account.getDestinationTag()),
                    BigInteger.valueOf(account.getBalance()), null);
        } catch (IOException e) {
            e.printStackTrace();
            throw new WebApplicationException(500);
        }
    }
}
