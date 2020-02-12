package space.xtack.api;

import space.xtack.api.model.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class Database {

    public static final String SYSTEM_ACCOUNT_UUID = "747deebc-6ec8-489a-974d-e146f0a3d0ec";

    private static Connection getConnection() throws URISyntaxException, SQLException {
        URI dbUri = new URI(System.getenv("DATABASE_URL"));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

        return DriverManager.getConnection(dbUrl, username, password);
    }
    public Database() throws URISyntaxException, SQLException {
        // Test connection
        Connection connection = getConnection();
        connection.close();
    }

    public Account getAccount(String uuid) throws URISyntaxException, SQLException {
        Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM accounts WHERE uuid = ?::uuid;");
        ps.setString(1, uuid);
        ResultSet rs = ps.executeQuery();
        connection.close();
        return getAccountFromResultSet(rs);
    }

    public Account getAccountFromEmailAndPassword(String email, String password) throws URISyntaxException, SQLException {
        Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM accounts WHERE email = ? AND password = ?;");
        ps.setString(1, email);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        connection.close();
        return getAccountFromResultSet(rs);
    }

    public Account getAccountFromSessionToken(String sessionToken) throws URISyntaxException, SQLException {
        Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM accounts WHERE session_token = ?;");
        ps.setString(1, sessionToken);
        ResultSet rs = ps.executeQuery();
        connection.close();
        return getAccountFromResultSet(rs);
    }

    public Question getQuestion(String uuid) throws URISyntaxException, SQLException {
        Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM questions WHERE uuid = ?::uuid;");
        ps.setString(1, uuid);
        ResultSet rs = ps.executeQuery();
        connection.close();
        return getQuestionFromResultSet(rs);
    }

    public Answer getAnswer(String uuid) throws URISyntaxException, SQLException {
        Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM answers WHERE uuid = ?::uuid;");
        ps.setString(1, uuid);
        ResultSet rs = ps.executeQuery();
        connection.close();
        return getAnswerFromResultSet(rs);
    }

    public Question getQuestionFromResultSet(ResultSet rs) throws SQLException {
        if (rs.next()) {
            String uuid = rs.getString("uuid");
            String title = rs.getString("title");
            String authorUuid = rs.getString("author_uuid");
            long bountyMin = rs.getLong("bounty_min");
            long bountyMax = rs.getLong("bounty_max");
            String body = rs.getString("body");
            int status = rs.getInt("status");
            ArrayList<Tag> tags = new ArrayList<>();
            // TODO: tags
            long score = rs.getLong("score");
            Timestamp timestamp = rs.getTimestamp("timestamp");
            String acceptedAnswerUuid = rs.getString("accepted_answer_uuid");
            return new Question(uuid, title, authorUuid, bountyMin, bountyMax,
                    body, status, tags, score, timestamp,
                    acceptedAnswerUuid);
        }
        return null;
    }

    public ArrayList<Question> getQuestionsList(String sortMethod, int page) throws URISyntaxException, SQLException {
        Connection connection = getConnection();
        PreparedStatement ps;
        if (sortMethod.equals("score")) {
            ps = connection.prepareStatement("SELECT * FROM questions ORDER BY score DESC LIMIT 25 OFFSET ?");
            ps.setInt(1, page * 25);
        }
        else {
            ps = connection.prepareStatement("SELECT * FROM questions ORDER BY timestamp DESC LIMIT 25 OFFSET ?");
            ps.setInt(1, page * 25);
        }
        ResultSet rs = ps.executeQuery();
        connection.close();
        ArrayList<Question> questions = new ArrayList<>();
        do {
            questions.add(getQuestionFromResultSet(rs));
        }
        while (questions.get(questions.size() - 1) != null);
        questions.remove(questions.size() - 1);
        return questions;
    }

    public ArrayList<Answer> getAnswersList(String questionUuid) throws URISyntaxException, SQLException {
        Connection connection = getConnection();
        PreparedStatement ps;
        ps = connection.prepareStatement("SELECT * FROM answers WHERE question_uuid = ?::uuid;");
        ps.setString(1, questionUuid);
        ResultSet rs = ps.executeQuery();
        connection.close();
        ArrayList<Answer> answers = new ArrayList<>();
        do {
            answers.add(getAnswerFromResultSet(rs));
        }
        while (answers.get(answers.size() - 1) != null);
        answers.remove(answers.size() - 1);
        return answers;
    }

    public Answer getAnswerFromResultSet(ResultSet rs) throws SQLException {
        if (rs.next()) {
            String uuid = rs.getString("uuid");
            String questionUuid = rs.getString("question_uuid");
            String authorUuid = rs.getString("author_uuid");
            Timestamp timestamp = rs.getTimestamp("timestamp");
            long score = rs.getLong("score");
            boolean isAccepted = rs.getBoolean("is_accepted");
            String body = rs.getString("body");
            return new Answer(uuid, questionUuid, authorUuid, timestamp, score, isAccepted, body);
        }
        return null;
    }

    public Account getAccountFromResultSet(ResultSet rs) throws SQLException {
        if (rs.next()) {
            String uuid = rs.getString("uuid");
            String username = rs.getString("username");
            String email = rs.getString("email");
            String password = rs.getString("password");
            String sessionToken = rs.getString("session_token");
            long balance = rs.getLong("balance");
            int destinationTag = rs.getInt("destination_tag");
            return new Account(uuid, username, email, password, sessionToken, balance, destinationTag);
        }
        return null;
    }

    public String createAccount(String username, String password, String email) throws URISyntaxException, SQLException {
        Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO accounts (uuid, username, password, email) VALUES (?::uuid, ?, ?, ?);");
        String uuid = UUID.randomUUID().toString();
        ps.setString(1, uuid);
        ps.setString(2, username);
        ps.setString(3, password);
        ps.setString(4, email);
        ps.execute();
        connection.close();
        return uuid;
    }

    public void createAuthToken(String accountUuid, String token) throws URISyntaxException, SQLException {
        Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement(
                "UPDATE accounts SET session_token = ? WHERE uuid = ?::uuid;");
        ps.setString(1, token);
        ps.setString(2, accountUuid);
        ps.execute();
        connection.close();
    }

    public String createQuestion(String title, String body, long bounty, String authorUuid)
            throws SQLException, URISyntaxException {
        Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO questions (title, author_uuid, body, bounty_min, uuid) VALUES (?, ?::uuid, ?, ?, ?::uuid);");
        String uuid = UUID.randomUUID().toString();
        ps.setString(1, title);
        ps.setString(2, authorUuid);
        ps.setString(3, body);
        ps.setLong(4, bounty);
        ps.setString(5, uuid);
        ps.execute();
        connection.close();
        return uuid;
    }

    public String createAnswer(String questionUuid, String authorUuid, String body) throws URISyntaxException, SQLException {
        Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO answers (uuid, question_uuid, author_uuid, body) VALUES (?::uuid, ?::uuid, ?::uuid, ?);");
        String uuid = UUID.randomUUID().toString();
        ps.setString(1, uuid);
        ps.setString(2, questionUuid);
        ps.setString(3, authorUuid);
        ps.setString(4, body);
        ps.execute();
        connection.close();
        return uuid;
    }

    public boolean acceptAnswer(String questionUuid, String answerUuid) throws SQLException, URISyntaxException {
        Connection connection = getConnection();
        PreparedStatement queryPs = connection.prepareStatement("SELECT * FROM questions WHERE accepted_answer_uuid IS NULL AND uuid = ?::uuid;");
        queryPs.setString(1, questionUuid);
        ResultSet rs = queryPs.executeQuery();
        if (rs.next()) {
            return false;
        }
        PreparedStatement questionsPs = connection.prepareStatement("UPDATE questions SET accepted_answer_uuid = ?::uuid WHERE uuid = ?::uuid;");
        questionsPs.setString(1, answerUuid);
        questionsPs.setString(2, questionUuid);
        questionsPs.execute();
        connection.close();
        return true;
    }

    public String createTransaction(String sourceAccountUuid, String destinationAccountUuid, long amount,
                                    XtackTransactionType type) throws SQLException, URISyntaxException {
        Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO transactions (uuid, src_account_uuid, dest_account_uuid, drops, type) " +
                        "VALUES (?::uuid, ?::uuid, ?::uuid, ?, ?);");
        String uuid = UUID.randomUUID().toString();
        ps.setString(1, uuid);
        ps.setString(2, sourceAccountUuid);
        ps.setString(3, destinationAccountUuid);
        ps.setLong(4, amount);
        ps.setString(5, type.name());
        ps.execute();
        return uuid;
    }

    public void addBalance(String accountUuid, long dropsToAdd) throws SQLException, URISyntaxException {
        Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE uuid = ?::uuid;");
        ps.setLong(1, dropsToAdd);
        ps.setString(2, accountUuid);
        ps.execute();
        connection.close();
    }

    public String getSystemValue(String key) throws URISyntaxException, SQLException {
        Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT value FROM system WHERE key = ?;");
        ps.setString(1, key);
        ResultSet rs = ps.executeQuery();
        String value = rs.getString("value");
        connection.close();
        return value;
    }

    public void setSystemValue(String key, String value) throws URISyntaxException, SQLException {
        Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement("UPDATE system SET value = ? WHERE key = ?;");
        ps.setString(1, value);
        ps.setString(2, key);
        ps.execute();
        connection.close();
    }
}
