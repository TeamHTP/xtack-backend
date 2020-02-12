package space.xtack.api.resource;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.auth.Auth;
import space.xtack.api.model.Account;
import space.xtack.api.model.XtackWallet;
import space.xtack.api.adapter.XpringClient;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

@Path("/withdraw")
@Produces(MediaType.APPLICATION_JSON)
public class WithdrawResource {

    @GET
    @Timed
    @RolesAllowed("USER")
    public Boolean get(@QueryParam("address") Optional<String> addressParam, @Auth Optional<Account> accOpt) {
        if (!addressParam.isPresent() || !accOpt.isPresent()) {
            throw new WebApplicationException(400);
        }
        try {
            BigInteger balance = BigInteger.valueOf(accOpt.get().getBalance());
            XpringClient.send(balance, addressParam.get(), XtackWallet.MASTER_WALLET);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            throw new WebApplicationException(500);
        }
    }

}
