package tech.xtack.api.model;

import java.math.BigInteger;

public class XtackWallet {

    private String address;
    private BigInteger balance;

    public XtackWallet() {}

    public XtackWallet(String address, BigInteger balance) {
        this.address = address;
        this.balance = balance;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setBalance(BigInteger balance) {
        this.balance = balance;
    }

    public BigInteger getBalance() {
        return balance;
    }

}
