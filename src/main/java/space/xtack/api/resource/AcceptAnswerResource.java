package space.xtack.api.resource;

import io.dropwizard.auth.Auth;
import org.checkerframework.checker.units.qual.Time;
import space.xtack.api.Database;
import space.xtack.api.model.Account;
import space.xtack.api.model.Answer;
import space.xtack.api.model.Question;
import space.xtack.api.model.XtackWallet;
import space.xtack.api.xpring.XrpClient;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.math.BigInteger;
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
                if (question.getAcceptedAnswerUuid() != null) {
                    throw new WebApplicationException("Question already has an accepted answer.", 400);
                }
                database.acceptAnswer(questionUuid, answerUuid);
                Answer answer = database.getAnswer(answerUuid);
                Account answerAuthor = database.getAccount(database.getAnswer(answerUuid).getAuthorUuid());
                XtackWallet wallet = XrpClient.getWallet(answerAuthor.getWalletMnemonic());

                XrpClient.send(question.getBountyMin()
                        .multiply(BigInteger.valueOf(1000000))
                        .subtract(question.getBountyMin().multiply(BigInteger.valueOf(5000))), wallet.getAddresses().getXAddress(),
                        XtackWallet.MASTER_WALLET);

                return answer;
            }
            else {
                throw new WebApplicationException(403);
            }
        } catch (SQLException | URISyntaxException | IOException e) {
            e.printStackTrace();
            throw new WebApplicationException(500);
        }
    }

}
