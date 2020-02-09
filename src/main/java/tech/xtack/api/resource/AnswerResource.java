package tech.xtack.api.resource;

import com.codahale.metrics.annotation.Timed;
import tech.xtack.api.Database;
import tech.xtack.api.model.Answer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import java.net.URISyntaxException;
import java.sql.SQLException;

@Path("/answer/{uuid}")
public class AnswerResource {

    private Database database;

    public AnswerResource(Database database) {
        this.database = database;
    }

    @GET
    @Timed
    public Answer get(@PathParam("uuid") String uuid) {
        try {
            Answer answer = database.getAnswer(uuid);
            if (answer != null) {
                return answer;
            }
            else {
                throw new WebApplicationException(404);
            }
        } catch (URISyntaxException | SQLException e) {
            e.printStackTrace();
            throw new WebApplicationException("Database error.", 503);
        }
    }
}
