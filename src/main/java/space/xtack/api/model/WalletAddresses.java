package space.xtack.api.model;

public class WalletAddresses {

    private String x;
    private String r;

    public WalletAddresses() {}

    public WalletAddresses(String x, String r) {
        this.x = x;
        this.r = r;
    }

    public String getXAddress() {
        return x;
    }

    public String getRAddress() {
        return r;
    }

}
