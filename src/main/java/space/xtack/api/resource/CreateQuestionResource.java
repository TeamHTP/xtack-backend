package space.xtack.api.resource;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.auth.Auth;
import space.xtack.api.Database;
import space.xtack.api.model.Account;
import space.xtack.api.model.Question;
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
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

@Path("/question")
@Produces(MediaType.APPLICATION_JSON)
public class CreateQuestionResource {

    private Database database;

    public CreateQuestionResource(Database database) {
        this.database = database;
    }

    @POST
    @Timed
    @RolesAllowed("USER")
    public Question post(@FormParam("title") Optional<String> titleParam, @FormParam("body") Optional<String> bodyParam,
                         @FormParam("bounty") Optional<Double> bountyParam, @Auth Optional<Account> accOpt) {
        if (!titleParam.isPresent() || !bodyParam.isPresent() || !bountyParam.isPresent() || !accOpt.isPresent()) {
            throw new WebApplicationException(400);
        }
        try {
            String title = titleParam.get();
            String body = bodyParam.get();
            double bounty = bountyParam.get();
            Account account = accOpt.get();

            if (bounty != 0 && (bounty < 1 || bounty > 1000000)) {
                throw new WebApplicationException("Bounty must be between 1 and 1000000 XRP", 400);
            }

            long bountyDrops = (long) (bounty * 1000000);
            if (bountyDrops > account.getBalance()) {
                throw new WebApplicationException("Your balance does not have enough XRP to cover this bounty.", 400);
            }
            database.createTransaction(account.getUuid(), Database.SYSTEM_ACCOUNT_UUID, bountyDrops,
                    XtackTransactionType.QUESTION_CREATION);
            database.addBalance(account.getUuid(), -bountyDrops);
            String uuid = database.createQuestion(title, body, bountyDrops, account.getUuid());

            return new Question(uuid, title, account.getUuid(), bountyDrops, bountyDrops, body,
                    0, new ArrayList<>(),
                    0, Timestamp.from(Instant.now()), null);
        } catch (SQLException | URISyntaxException e) {
            e.printStackTrace();
            throw new WebApplicationException(500);
        }
    }
}
