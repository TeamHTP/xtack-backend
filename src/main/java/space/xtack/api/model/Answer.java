package space.xtack.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class Answer {

    private String uuid;
    private String questionUuid;
    private String authorUuid;
    private Timestamp timestamp;
    private long score;
    private boolean isAccepted;
    private String body;

    public Answer() {}

    public Answer(String uuid, String questionUuid, String authorUuid, Timestamp timestamp,
                  long score, boolean isAccepted, String body) {
        this.uuid = uuid;
        this.questionUuid = questionUuid;
        this.authorUuid = authorUuid;
        this.timestamp = timestamp;
        this.score = score;
        this.isAccepted = isAccepted;
        this.body = body;
    }

    public String getUuid() {
        return uuid;
    }

    @JsonProperty("question_uuid")
    public String getQuestionUuid() {
        return questionUuid;
    }

    @JsonProperty("author_uuid")
    public String getAuthorUuid() {
        return authorUuid;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public long getScore() {
        return score;
    }

    @JsonProperty("is_accepted")
    public boolean isAccepted() {
        return isAccepted;
    }

    public String getBody() {
        return body;
    }
}
