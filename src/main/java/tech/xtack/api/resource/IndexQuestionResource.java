package tech.xtack.api.resource;

import com.codahale.metrics.annotation.Timed;
import tech.xtack.api.Database;
import tech.xtack.api.model.Question;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

@Path("/question")
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
