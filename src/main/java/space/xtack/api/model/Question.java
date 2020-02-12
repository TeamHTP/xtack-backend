package space.xtack.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Question {

    private String uuid;
    private String title;
    private String authorUuid;
    private long bountyMin;
    private long bountyMax;
    private String body;
    private int status;
    private ArrayList<Tag> tags;
    private long score;
    private Timestamp timestamp;
    private String acceptedAnswerUuid;

    public Question() {}

    public Question(String uuid, String title, String authorUuid, long bountyMin, long bountyMax, String body,
                    int status, ArrayList<Tag> tags, long score, Timestamp timestamp, String acceptedAnswerUuid) {
        this.uuid = uuid;
        this.title = title;
        this.authorUuid = authorUuid;
        this.bountyMin = bountyMin;
        this.bountyMax = bountyMax;
        this.body = body;
        this.status = status;
        this.tags = tags;
        this.score = score;
        this.timestamp = timestamp;
        this.acceptedAnswerUuid = acceptedAnswerUuid;
    }

    public String getUuid() {
        return uuid;
    }

    public String getTitle() {
        return title;
    }

    @JsonProperty("author_uuid")
    public String getAuthorUuid() {
        return authorUuid;
    }

    @JsonProperty("bounty_min")
    public long getBountyMin() {
        return bountyMin;
    }

    @JsonProperty("bounty_max")
    public long getBountyMax() {
        return bountyMax;
    }

    public String getBody() {
        return body;
    }

    public int getStatus() {
        return status;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public long getScore() {
        return score;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @JsonProperty("accepted_answer_uuid")
    public String getAcceptedAnswerUuid() {
        return acceptedAnswerUuid;
    }
}
