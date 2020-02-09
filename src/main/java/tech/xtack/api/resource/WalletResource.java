package tech.xtack.api.resource;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.auth.Auth;
import io.grpc.StatusRuntimeException;
import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.XpringKitException;
import tech.xtack.api.Database;
import tech.xtack.api.model.Account;
import tech.xtack.api.model.XtackWallet;
import tech.xtack.api.xpring.XrpClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import java.math.BigInteger;
import java.util.Optional;

@Path("/wallet")
@Produces(MediaType.APPLICATION_JSON)
public class WalletResource {

    @GET
    @Timed
    public XtackWallet get(@Auth Optional<Account> accOpt) {
        try {
            if (!accOpt.isPresent()) {
                throw new WebApplicationException(403);
            }
            Account account = accOpt.get();
            if (account != null) {
                Wallet wallet = new Wallet(account.getWalletMnemonic(), null);
                XrpClient client = new XrpClient(wallet);
                XtackWallet xtackWallet = new XtackWallet();
                try {
                    xtackWallet.setBalance(client.getBalance());
                }
                catch (StatusRuntimeException e) {
                    System.out.println(e.getMessage());
                    if (e.getMessage().equals("NOT_FOUND: account not found")) {
                        xtackWallet.setBalance(BigInteger.ZERO);
                    }
                    else if (!e.getMessage().equals("ManagedChannel allocation site")) {
                        e.printStackTrace();
                    }
                }
                xtackWallet.setAddress(wallet.getAddress());
                return xtackWallet;
            }
            else {
                throw new WebApplicationException(404);
            }
        }
        catch (XpringKitException e) {
            e.printStackTrace();
            throw new WebApplicationException(503);
        }
    }
}
