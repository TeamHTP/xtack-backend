package space.xtack.api.model;

import java.sql.Timestamp;

public class RippleTransaction {

    private String id;
    private int tag;
    private long drops;
    private Timestamp timestamp;

    public RippleTransaction() {}

    public String getId() {
        return id;
    }

    public int getTag() {
        return tag;
    }

    public long getDrops() {
        return drops;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
