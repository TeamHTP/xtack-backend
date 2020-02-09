package tech.xtack.api.xpring;

import io.xpring.xrpl.TransactionStatus;
import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.XpringClient;
import io.xpring.xrpl.XpringKitException;

import java.math.BigInteger;

public class XrpClient {

    private XpringClient client;
    private Wallet wallet;

    public XrpClient(Wallet wallet) {
        this.client = new XpringClient();
        this.wallet = wallet;
    }

    public BigInteger getBalance() throws XpringKitException {
        return client.getBalance(wallet.getAddress());
    }

    public Wallet getWallet() {
        return wallet;
    }

    public TransactionStatus getTransactionStatus(String txHash) {
        return client.getTransactionStatus(txHash);
    }

    public String send(BigInteger amount, String destAddr) throws XpringKitException {
        return client.send(amount, destAddr, wallet);
    }

}
