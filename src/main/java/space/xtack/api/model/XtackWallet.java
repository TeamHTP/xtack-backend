package space.xtack.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigInteger;

public class XtackWallet {

    public static XtackWallet MASTER_WALLET;

    private WalletAddresses addresses;
    private long balance;
    private String mnemonic;

    public XtackWallet() {}

    public XtackWallet(WalletAddresses addresses, long balance, String mnemonic) {
        this.addresses = addresses;
        this.balance = balance;
        this.mnemonic = mnemonic;
    }

    public WalletAddresses getAddresses() {
        return addresses;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public long getBalance() {
        return balance;
    }

    @JsonIgnore
    public String getMnemonic() {
        return mnemonic;
    }
}
