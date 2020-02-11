package space.xtack.api.resource;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.auth.Auth;
import space.xtack.api.Database;
import space.xtack.api.model.Account;
import space.xtack.api.model.Answer;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Path("/question/{q_uuid}/answer")
@Produces(MediaType.APPLICATION_JSON)
public class CreateAnswerResource {

    private Database database;

    public CreateAnswerResource(Database database) {
        this.database = database;
    }

    @POST
    @Timed
    @RolesAllowed("USER")
    public Answer post(@PathParam("q_uuid") String questionUuid, @FormParam("body") Optional<String> bodyParam,
                       @Auth Optional<Account> accOpt) {
        if (!bodyParam.isPresent() || !accOpt.isPresent()) {
            throw new WebApplicationException(400);
        }
        try {
            String body = bodyParam.get();
            Account account = accOpt.get();
            String uuid = database.createAnswer(questionUuid, account.getUuid(), body);
            return new Answer(uuid, questionUuid, account.getUuid(), Timestamp.from(Instant.now()), 0, false, body);
        } catch (SQLException | URISyntaxException e) {
            e.printStackTrace();
            throw new WebApplicationException(500);
        }
    }
}
