package space.xtack.api.resource;

import com.codahale.metrics.annotation.Timed;
import space.xtack.api.Database;
import space.xtack.api.model.Question;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

@Path("/question/list")
@Produces(MediaType.APPLICATION_JSON)
public class IndexQuestionResource {

    private Database database;
    public IndexQuestionResource(Database database) {
        this.database = database;
    }

    @GET
    @Timed
    public ArrayList<Question> get(@QueryParam("sort_method") Optional<String> sortMethodParam,
                                   @QueryParam("page") Optional<Integer> pageParam) {
        String sortMethod = sortMethodParam.orElse("recent");
        int page = pageParam.orElse(0);
        try {
            return database.getQuestionsList(sortMethod, page);
        } catch (URISyntaxException | SQLException e) {
            e.printStackTrace();
            throw new WebApplicationException(500);
        }
    }

}
