package telegram.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import telegram.model.User;
import telegram.repository.UserRepository;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Connection getConnection(String connectionUrl) throws SQLException {
        return DriverManager.getConnection(connectionUrl, "root", "database432");
    }

    public String getUrl() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("src/main/resources/application.properties")) {
            properties.load(input);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return properties.getProperty("spring.datasource.url");
    }

    public String findAllActionsById(long id) {
        StringBuilder sb = new StringBuilder();
        try (PreparedStatement ps = getConnection(getUrl()).prepareStatement("select*from user where chat_id=(?)")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sb.append("#Input: ").append(rs.getString(5)).append("\n")
                            .append("#Date: ").append(rs.getDate(3)).append('\n')
                            .append("#Result: ").append(rs.getString(6).replaceAll("\n","")).append('\n')
                            .append("#Exchange rate: ").append(rs.getString(4)).append("\n");
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return sb.toString().equals("") ? "History is empty":sb.toString();
    }

    public boolean deleteAllActionsById(long id) {
        try (PreparedStatement ps = getConnection(getUrl()).prepareStatement("delete from user where chat_id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }

    @Transactional
    public void addUser(User user) {
        userRepository.save(user);
    }
}
