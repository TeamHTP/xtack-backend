package space.xtack.api.adapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import space.xtack.api.model.RippleTransaction;
import space.xtack.api.model.WalletAddresses;
import space.xtack.api.model.XtackWallet;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;

public class XpringClient {

    private static final String XRP_ADAPTER_ROOT = "https://xrp-adapter.xtack.space";
    private static final String BEARER_TOKEN = System.getenv("BEARER_TOKEN");

    public static ArrayList<RippleTransaction> getTransactions(String address, String start) throws IOException {
        OkHttpClient client = new OkHttpClient();
        if (start == null) {
            start = "";
        }
        Request request = new Request.Builder()
                .url(XRP_ADAPTER_ROOT + "/ripple/transactions?address=" + address + "&start=" + start)
                .header("Authorization", "Bearer " + BEARER_TOKEN)
                .build();
        try (Response response = client.newCall(request).execute()) {
            Type type = new TypeToken<ArrayList<RippleTransaction>>(){}.getType();
            return new Gson().fromJson(response.body().string(), type);
        }
    }

    public static WalletAddresses getEncodedAddresses(String classicAddress, int tag) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(XRP_ADAPTER_ROOT + "/xrp/encode_addresses?classic=" + classicAddress + "&tag=" + tag)
                .header("Authorization", "Bearer " + BEARER_TOKEN)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return new Gson().fromJson(response.body().string(), WalletAddresses.class);
        }
    }

    public static BigInteger getBalance(String walletAddress) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(XRP_ADAPTER_ROOT + "/xrp/balance?wallet=" + walletAddress)
                .header("Authorization", "Bearer " + BEARER_TOKEN)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return new BigInteger(response.body().string());
        }
    }

    public static XtackWallet getRandomWallet() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(XRP_ADAPTER_ROOT + "/xrp/random_wallet")
                .header("Authorization", "Bearer " + BEARER_TOKEN)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return new Gson().fromJson(response.body().string(), XtackWallet.class);
        }
    }

    public static XtackWallet getWallet(String mnemonic) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(XRP_ADAPTER_ROOT + "/xrp/wallet_address?mnemonic=" + mnemonic)
                .header("Authorization", "Bearer " + BEARER_TOKEN)
                .build();
        try (Response response = client.newCall(request).execute()) {
            WalletAddresses walletAddresses = new Gson().fromJson(response.body().string(), WalletAddresses.class);
            return new XtackWallet(walletAddresses, getBalance(walletAddresses.getXAddress()), mnemonic);
        }
    }

    public static String send(BigInteger amount, String destAddr, XtackWallet srcWallet) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(XRP_ADAPTER_ROOT + "/xrp/send?amount=" + amount + "&destinationAddress=" + destAddr +
                        "&senderMnemonic=" + srcWallet.getMnemonic())
                .header("Authorization", "Bearer " + BEARER_TOKEN)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return new Gson().fromJson(response.body().string(), String.class);
        }
    }

    public static long getFee() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(XRP_ADAPTER_ROOT + "/ripple/fee")
                .header("Authorization", "Bearer " + BEARER_TOKEN)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return new Gson().fromJson(response.body().string(), Long.class);
        }
    }

}
