package space.xtack.api;

import space.xtack.api.adapter.XpringClient;
import space.xtack.api.model.Account;
import space.xtack.api.model.RippleTransaction;
import space.xtack.api.model.XtackTransactionType;

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
                String lastProcessedTransactionId = database.getSystemValue("last_processed_transaction_id");
                ArrayList<RippleTransaction> transactions = XpringClient.getTransactions(
                        System.getenv("MASTER_WALLET_ADDRESS"), lastProcessedTransactionId);
                transactions.sort(Comparator.comparing(RippleTransaction::getTimestamp));
                for (RippleTransaction transaction : transactions) {
                    if (transaction.getId().equals(lastProcessedTransactionId)) {
                        continue;
                    }
                    System.out.println(transaction.getId());
                    Account account = database.getAccountFromDestinationTag(transaction.getTag());
                    if (account == null) {
                        continue;
                    }
                    database.createTransaction(Database.SYSTEM_ACCOUNT_UUID, account.getUuid(), transaction.getDrops(),
                            XtackTransactionType.DEPOSIT);
                    database.addBalance(account.getUuid(), transaction.getDrops());
                }
                database.setSystemValue("last_processed_transaction_id", transactions.get(transactions.size() - 1).getId());
                System.out.println(transactions.size() + " transactions processed");
            } catch (IOException | URISyntaxException | SQLException e) {
                e.printStackTrace();
            }
        }, 0, 15, TimeUnit.SECONDS);
    }

}
