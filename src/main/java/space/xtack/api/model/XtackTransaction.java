package space.xtack.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class XtackTransaction {

    private String uuid;
    private String sourceAccountUuid;
    private String destinationAccountUuid;
    private long drops;
    private XtackTransactionType type;
    private Timestamp timestamp;

    public XtackTransaction() {}

    public XtackTransaction(String uuid, String sourceAccountUuid, String destinationAccountUuid, long drops,
                            XtackTransactionType type, Timestamp timestamp) {
        this.uuid = uuid;
        this.sourceAccountUuid = sourceAccountUuid;
        this.destinationAccountUuid = destinationAccountUuid;
        this.drops = drops;
        this.type = type;
        this.timestamp = timestamp;
    }

    @JsonProperty
    public String getUuid() {
        return uuid;
    }

    @JsonProperty("src_account_uuid")
    public String getSourceAccountUuid() {
        return sourceAccountUuid;
    }

    @JsonProperty("dest_account_uuid")
    public String getDestinationAccountUuid() {
        return destinationAccountUuid;
    }

    @JsonProperty()
    public long getDrops() {
        return drops;
    }

    @JsonProperty("type")
    public XtackTransactionType getType() {
        return type;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
