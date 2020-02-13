package space.xtack.api.resource;

import io.dropwizard.auth.Auth;
import org.checkerframework.checker.units.qual.Time;
import space.xtack.api.Database;
import space.xtack.api.model.*;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Optional;

@Path("/questions/{q_uuid}/answer/{a_uuid}/accept")
@Produces(MediaType.APPLICATION_JSON)
public class AcceptAnswerResource {

    public static final long PLATFORM_FEE = 35000;

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
                if (question.getAcceptedAnswerUuid() != null) {
                    throw new WebApplicationException("Question already has an accepted answer.", 400);
                }
                if (database.acceptAnswer(questionUuid, answerUuid)) {
                    Answer answer = database.getAnswer(answerUuid);
                    Account answerAuthor = database.getAccount(database.getAnswer(answerUuid).getAuthorUuid());
                    long bountyDrops = question.getBountyMin() - PLATFORM_FEE;
                    if (question.getBountyMin() == 0) {
                        bountyDrops = 0;
                    }
                    XtackTransaction transaction = new XtackTransaction(null,
                            Database.SYSTEM_ACCOUNT_UUID, answerAuthor.getUuid(),
                            bountyDrops, XtackTransactionType.ANSWER_BOUNTY, null, null);
                    database.createTransaction(transaction.getSourceAccountUuid(), transaction.getDestinationAccountUuid(),
                            transaction.getDrops(), transaction.getType(), null);
                    if (question.getBountyMin() != 0) {
                        database.createTransaction(transaction.getDestinationAccountUuid(), transaction.getSourceAccountUuid(),
                                PLATFORM_FEE, XtackTransactionType.PLATFORM_FEE, null);
                    }
                    database.addBalance(answerAuthor.getUuid(), bountyDrops);
                    return answer;
                }
                throw new WebApplicationException("Problem querying for this question in database.", 500);
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
