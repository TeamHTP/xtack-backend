package space.xtack.api;

import space.xtack.api.adapter.XpringClient;
import space.xtack.api.model.RippleTransaction;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Tasks {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void startFetchTransactionsTask(Database database) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                ArrayList<RippleTransaction> transactions = XpringClient.getTransactions(
                        System.getenv("MASTER_WALLET_ADDRESS"),
                        database.getSystemValue("last_processed_transaction_id"));
                transactions.sort(Comparator.comparing(RippleTransaction::getTimestamp));
                for (RippleTransaction transaction : transactions) {
                    System.out.println(transaction.getTimestamp());
                }
            } catch (IOException | URISyntaxException | SQLException e) {
                e.printStackTrace();
            }
        }, 0, 15, TimeUnit.SECONDS);
    }

}
