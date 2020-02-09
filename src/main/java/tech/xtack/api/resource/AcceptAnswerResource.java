package tech.xtack.api.resource;

import io.dropwizard.auth.Auth;
import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.XpringClient;
import io.xpring.xrpl.XpringKitException;
import org.checkerframework.checker.units.qual.Time;
import tech.xtack.api.Database;
import tech.xtack.api.WalletCache;
import tech.xtack.api.model.Account;
import tech.xtack.api.model.Answer;
import tech.xtack.api.model.Question;
import tech.xtack.api.xpring.XrpClient;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Optional;

@Path("/questions/{q_uuid}/answer/{a_uuid}/accept")
public class AcceptAnswerResource {

    private Database database;
    private XpringClient client;

    public AcceptAnswerResource(Database database, XpringClient client) {
        this.database = database;
        this.client = client;
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
                Answer answer = database.getAnswer(answerUuid);
                Account answerAuthor = database.getAccount(database.getAnswer(answerUuid).getAuthorUuid());
                Wallet wallet = WalletCache.getOrGenerate(answerAuthor.getWalletMnemonic());
                client.send(question.getBountyMin()
                        .multiply(BigInteger.valueOf(1000000))
                        .subtract(question.getBountyMin().multiply(BigInteger.valueOf(5000))), wallet.getAddress(),
                        WalletCache.MASTER_WALLET);
                return answer;
            }
            else {
                throw new WebApplicationException(403);
            }
        } catch (SQLException | URISyntaxException | XpringKitException e) {
            e.printStackTrace();
            throw new WebApplicationException(500);
        }
    }

}
