import org.junit.Test;
import space.xtack.api.Database;
import space.xtack.api.Tasks;

import java.net.URISyntaxException;
import java.sql.SQLException;

public class TestFetchTransactionsTask {

    @Test
    public void testFetchTransactions() throws URISyntaxException, SQLException {
        Tasks.startFetchTransactionsTask(new Database());
    }

}
