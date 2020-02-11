package space.xtack.api.resource;

import com.codahale.metrics.annotation.Timed;
import space.xtack.api.Database;
import space.xtack.api.model.Answer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;

@Path("/question/{q_uuid}/answers")
@Produces(MediaType.APPLICATION_JSON)
public class IndexAnswersResource {

    private Database database;
    public IndexAnswersResource(Database database) {
        this.database = database;
    }

    @GET
    @Timed
    public ArrayList<Answer> get(@PathParam("q_uuid") String questionUuid) {
        try {
            return database.getAnswersList(questionUuid);
        } catch (URISyntaxException | SQLException e) {
            e.printStackTrace();
            throw new WebApplicationException(500);
        }
    }
}
