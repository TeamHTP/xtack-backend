package tech.xtack.api.resource;

import io.dropwizard.auth.Auth;
import org.checkerframework.checker.units.qual.Time;
import tech.xtack.api.Database;
import tech.xtack.api.model.Account;
import tech.xtack.api.model.Answer;
import tech.xtack.api.model.Question;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Optional;

@Path("/questions/{q_uuid}/answer/{a_uuid}/accept")
public class AcceptAnswerResource {

    private Database database;

    public AcceptAnswerResource(Database database) {
        this.database = database;
    }

    @GET
    @Time
    @RolesAllowed("USER")
    public Answer get(@PathParam("q_uuid") String questionUuid, @PathParam("a_uuid") String answerUuid,
                      @Auth Optional<Account> accOpt) {
        if (!accOpt.isPresent()) {
            throw new WebApplicationException(403);
        }
        Account account = accOpt.get();
        try {
            Question question = database.getQuestion(questionUuid);
            if (question.getAuthorUuid().equals(account.getUuid())) {
                database.acceptAnswer(questionUuid, answerUuid);
                return database.getAnswer(answerUuid);
            }
            else {
                throw new WebApplicationException(403);
            }
        } catch (SQLException | URISyntaxException e) {
            e.printStackTrace();
            throw new WebApplicationException(500);
        }
    }

}
