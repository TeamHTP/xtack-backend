package tech.xtack.api.resource;

import com.codahale.metrics.annotation.Timed;
import io.grpc.StatusRuntimeException;
import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.XpringKitException;
import tech.xtack.api.Database;
import tech.xtack.api.model.Account;
import tech.xtack.api.xpring.XrpClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.math.BigInteger;
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
                Wallet wallet = new Wallet(account.getWalletMnemonic(), null);
                XrpClient client = new XrpClient(wallet);
                try {
                    account.setXrpBalance(client.getBalance());
                }
                catch (StatusRuntimeException e) {
                    System.out.println(e.getMessage());
                    if (e.getMessage().equals("UNKNOWN: Account not found.")) {
                        account.setXrpBalance(BigInteger.ZERO);
                    }
                    else if (!e.getMessage().equals("ManagedChannel allocation site")) {
                        e.printStackTrace();
                    }
                }
                account.setXrpAddress(wallet.getAddress());
                return account;
            }
            else {
                throw new WebApplicationException(404);
            }
        }
        catch (URISyntaxException | SQLException | XpringKitException e) {
            e.printStackTrace();
            throw new WebApplicationException(503);
        }
    }

}
