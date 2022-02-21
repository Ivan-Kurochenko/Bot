package telegram.bot;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegram.currencyMethods.Currency;
import telegram.model.User;
import telegram.service.UserService;

import java.util.Calendar;

@Component
@PropertySource("classpath:telegram.properties")
public class ChatBot extends TelegramLongPollingBot {

    private static final Logger LOGGER = LogManager.getLogger(ChatBot.class);

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}") // считываем данные с проперти файла
    private String botToken;

    private final UserService userService;
    private final Currency currency;

    public ChatBot(UserService userService) {
        this.userService = userService;
        this.currency = new Currency();
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() && !update.getMessage().hasText()) {
            return;
        }

        Long chatId = update.getMessage().getChatId();
        String message = update.getMessage().getText();

        if (message.equals("/start")) {
            sendMsg(chatId, "Hello, let's execute your first query. If you have problems, write /help");
            LOGGER.log(Level.INFO, "User " + chatId + " executed query " + message);
            return;
        }
        if (message.equals("/top")) {
            sendMsg(chatId, currency.getTopOfCurrencies());
            LOGGER.log(Level.INFO, "User " + chatId + " executed query " + message);
            return;
        }
        if (message.equals("/help")) {
            sendMsg(chatId, "This bot was crated to convert any currency \uD83D\uDCB4\n\nExample: (100 eur usd) OR (100 EUR USD) \n\nAny questions❓: write admin @ioaaan");
            LOGGER.log(Level.INFO, "User " + chatId + " executed query " + message);
            return;
        }
        if (message.equals("/history")) {
            sendMsg(chatId, userService.findAllActionsById(chatId));
            LOGGER.log(Level.INFO, "User " + chatId + " executed query " + message);
            return;
        }
        if (message.equals("/clear_history")) {
            if (userService.deleteAllActionsById(chatId)) {
                sendMsg(chatId, "✅History has been deleted successfully!");
                LOGGER.log(Level.INFO, "User " + chatId + " deleted own history");
                return;
            }
            sendMsg(chatId, "Something went wrong");
            LOGGER.log(Level.INFO, "User " + chatId + " did something wrong");
            return;
        }
        if (message.equals("/support")) {
            sendMsg(chatId, "You can support \uD83D\uDCB8 the author by donate: 4149510068907361");
            LOGGER.log(Level.INFO, "User " + chatId + " executed query " + message);
            return;
        }
        if (currency.checkForInput(message)) {
            User user = new User(chatId);
            user.setInput(message);
            user.setResult(currency.getExchange(message));
            user.setExchange_rate(currency.getExchangeRate(message));
            user.setDate(Calendar.getInstance().getTime());
            userService.addUser(user);

            sendMsg(chatId, currency.getExchange(message) + currency.getExchangeRate(message));
            LOGGER.log(Level.INFO, "User " + chatId + " executed query " + message);
        } else {
            sendMsg(chatId, "\uD83D\uDEABIncorrect input\uD83D\uDEAB");
        }
    }

    private void sendMsg(Long chatId, String text) {
        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            userService.deleteAllActionsById(chatId);
            e.printStackTrace();
        }
    }
}
