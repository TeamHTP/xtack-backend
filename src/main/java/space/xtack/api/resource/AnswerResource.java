package space.xtack.api.resource;

import com.codahale.metrics.annotation.Timed;
import space.xtack.api.Database;
import space.xtack.api.model.Answer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.net.URISyntaxException;
import java.sql.SQLException;

@Path("/answer/{uuid}")
@Produces(MediaType.APPLICATION_JSON)
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
