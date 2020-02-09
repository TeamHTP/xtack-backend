package tech.xtack.api;

import tech.xtack.api.auth.AuthUtils;
import tech.xtack.api.model.Account;
import tech.xtack.api.model.Question;
import tech.xtack.api.model.Tag;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class Database {

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
            return new Question(uuid, title, authorUuid, bountyMin, bountyMax, body, status, tags, score, timestamp,
                    acceptedAnswerUuid);
        }
        return null;
    }

    public Account getAccountFromResultSet(ResultSet rs) throws SQLException {
        if (rs.next()) {
            String uuid = rs.getString("uuid");
            String username = rs.getString("username");
            String email = rs.getString("email");
            String password = rs.getString("password");
            String walletMnemonic = rs.getString("wallet_mnemonic");
            String sessionToken = rs.getString("session_token");
            return new Account(uuid, username, email, password, walletMnemonic, sessionToken);
        }
        return null;
    }

    public String createAccount(String username, String password, String email, String walletMnemonic) throws URISyntaxException, SQLException {
        Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement("INSERT INTO accounts (uuid, username, password, email, wallet_mnemonic) VALUES (?::uuid, ?, ?, ?, ?);");
        String uuid = UUID.randomUUID().toString();
        ps.setString(1, uuid);
        ps.setString(2, username);
        ps.setString(3, password);
        ps.setString(4, email);
        ps.setString(5, walletMnemonic);
        ps.execute();
        connection.close();
        return uuid;
    }

    public void createAuthToken(String accountUuid, String token) throws URISyntaxException, SQLException {
        Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement("UPDATE accounts SET session_token = ? WHERE uuid = ?::uuid;");
        ps.setString(1, token);
        ps.setString(2, accountUuid);
        ps.execute();
        connection.close();
    }

}
