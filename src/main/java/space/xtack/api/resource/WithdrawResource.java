package space.xtack.api.resource;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.auth.Auth;
import space.xtack.api.Database;
import space.xtack.api.model.Account;
import space.xtack.api.model.XtackTransactionType;
import space.xtack.api.model.XtackWallet;
import space.xtack.api.adapter.XpringClient;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Optional;

@Path("/withdraw")
@Produces(MediaType.APPLICATION_JSON)
public class WithdrawResource {

    private Database database;
    public WithdrawResource(Database database) {
        this.database = database;
    }

    @GET
    @Timed
    @RolesAllowed("USER")
    public Boolean get(@QueryParam("address") Optional<String> addressParam, @Auth Optional<Account> accOpt) {
        if (!addressParam.isPresent() || !accOpt.isPresent()) {
            throw new WebApplicationException(400);
        }
        try {
            Account account = accOpt.get();
            long balance = account.getBalance();
            long fee = XpringClient.getFee();
            if (fee * 2 > balance) {
                throw new WebApplicationException("Not enough balance to cover transaction fee.", 400);
            }
            String rippleTransactionId = XpringClient.send(BigInteger.valueOf(balance - fee), addressParam.get(),
                    XtackWallet.MASTER_WALLET);
            database.createTransaction(account.getUuid(), Database.SYSTEM_ACCOUNT_UUID, balance - fee,
                    XtackTransactionType.WITHDRAW, rippleTransactionId);
            database.createTransaction(account.getUuid(), Database.SYSTEM_ACCOUNT_UUID, fee,
                    XtackTransactionType.RIPPLE_FEE, rippleTransactionId);
            database.addBalance(account.getUuid(), -balance);
            return true;
        } catch (IOException | SQLException | URISyntaxException e) {
            e.printStackTrace();
            throw new WebApplicationException(500);
        }
    }

}
