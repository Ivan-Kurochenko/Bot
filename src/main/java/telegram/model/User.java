package telegram.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="user")
public class User {
    @Id
    @PrimaryKeyJoinColumn
    @GeneratedValue
    private Long id;

    @Column( name = "chat_id", columnDefinition = "BIGINT" )
    private Long chatId;
    private String input;
    private String result;
    private String exchange_rate;
    private Date date;

    public User() {
    }

    public User(Long chatId) {
        this.chatId = chatId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }


    public String getInput() {
        return input;
    }

    public void setInput(String phone) {
        this.input = phone;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String email) {
        this.result = email;
    }

    public String getExchange_rate() {
        return exchange_rate;
    }

    public void setExchange_rate(String exchange_rate) {
        this.exchange_rate = exchange_rate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
