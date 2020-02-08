package tech.xtack.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.security.Principal;

public class Account implements Principal {

    private String uuid;
    private String username;
    private String email;
    private String password;
    private String walletMnemonic;

    public Account() {}

    public Account(String uuid, String username, String email, String password, String walletMnemonic) {
        this.uuid = uuid;
        this.username = username;
        this.email = email;
        this.password = password;
        this.walletMnemonic = walletMnemonic;
    }

    @JsonProperty
    public String getUuid() {
        return uuid;
    }

    @JsonProperty
    public String getUsername() {
        return username;
    }

    @JsonProperty
    public String getEmail() {
        return email;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    public String getWalletMnemonic() {
        return walletMnemonic;
    }

    @Override
    public String getName() {
        return username;
    }
}
