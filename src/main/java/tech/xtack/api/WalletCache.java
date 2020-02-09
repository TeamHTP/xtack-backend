package tech.xtack.api;

import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.XpringKitException;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WalletCache {

    public static Wallet MASTER_WALLET;

    static {
        try {
            MASTER_WALLET = new Wallet(System.getenv("MASTER_WALLET_MNEMONIC"), null);
        } catch (XpringKitException e) {
            e.printStackTrace();
        }
    }

    private static final int MAX_CACHE_SIZE = 200;
    private static HashMap<String, Wallet> WALLET_MAP = new HashMap<>();
    private static HashMap<String, Long> LAST_ACCESS_MAP = new HashMap<>();

    public static Wallet getOrGenerate(String mnemonic) throws XpringKitException {
        Wallet wallet =  WALLET_MAP.getOrDefault(mnemonic, new Wallet(mnemonic, null));
        WALLET_MAP.put(mnemonic, wallet);
        LAST_ACCESS_MAP.put(mnemonic, System.currentTimeMillis());
        if (WALLET_MAP.size() > MAX_CACHE_SIZE) {
            Optional<Map.Entry<String, Long>> oldestEntry = LAST_ACCESS_MAP.entrySet().stream().min(Comparator.comparing(Map.Entry::getValue));
            if (oldestEntry.isPresent()) {
                WALLET_MAP.remove(oldestEntry.get().getKey());
                LAST_ACCESS_MAP.remove(oldestEntry.get().getKey());
            }
        }
        return wallet;
    }

}
