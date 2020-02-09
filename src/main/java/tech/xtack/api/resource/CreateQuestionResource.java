package tech.xtack.api.resource;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.auth.Auth;
import tech.xtack.api.Database;
import tech.xtack.api.model.Account;
import tech.xtack.api.model.Question;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
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
                         @FormParam("bounty") Optional<BigInteger> bountyParam, @Auth Optional<Account> accOpt) {
        if (!titleParam.isPresent() || !bodyParam.isPresent() || !bountyParam.isPresent() || !accOpt.isPresent()) {
            throw new WebApplicationException(400);
        }
        try {
            String title = titleParam.get();
            String body = bodyParam.get();
            BigInteger bounty = bountyParam.get();
            Account account = accOpt.get();
            String uuid = database.createQuestion(title, body, bounty, account.getUuid());
            return new Question(uuid, title, account.getUuid(), bounty, bounty, body, 0, new ArrayList<>(),
                    0, Timestamp.from(Instant.now()), null);
        } catch (SQLException | URISyntaxException e) {
            e.printStackTrace();
            throw new WebApplicationException(500);
        }
    }
}
