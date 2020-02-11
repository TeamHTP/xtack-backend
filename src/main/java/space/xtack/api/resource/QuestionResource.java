package space.xtack.api.resource;

import com.codahale.metrics.annotation.Timed;
import space.xtack.api.Database;
import space.xtack.api.model.Question;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.net.URISyntaxException;
import java.sql.SQLException;

@Path("/question/{uuid}")
@Produces(MediaType.APPLICATION_JSON)
public class QuestionResource {

    private Database database;

    public QuestionResource(Database database) {
        this.database = database;
    }

    @GET
    @Timed
    public Question get(@PathParam("uuid") String uuid) {
        try {
            Question question = database.getQuestion(uuid);
            if (question != null) {
                return question;
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
