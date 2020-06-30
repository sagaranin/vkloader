package ru.larnerweb.vkloader.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ru.larnerweb.vkloader.entity.vk.UsersGet;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotService extends TelegramLongPollingBot {
    private static final Logger log = LoggerFactory.getLogger(TelegramBotService.class);

    @Autowired
    private VKClientService vkClientService;

    @Autowired
    private InMemoryGraphService graph;

    @Value("${app.telegram.token}")
    private String token;

    @Override
    public String getBotToken() {
        return this.token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("Received message {}", update);

        if (update.hasMessage() && update.getMessage().hasText()){

            Message message = update.getMessage();

            switch (message.getText().split(" ")[0]){
                case "/search":
                    if (message.getText().split(" ").length == 1) {
                        sendMsg(message, "Неверный формат запроса. \nПравильный формат: /search <i>user_from user_to</i>, где <i>user_from</i> и <i>user_to</i> - идентификаторы пользователей. \nПодробнее в /help\n\n", false);
                        break;
                    }

                    sendMsg(message, "Запрос принят! \u2705 \n\nПоиск цепочки друзей может занять некоторое время... \u231B\n\n", false ); // (нет 😏)
                    String from = message.getText().split(" ")[1];
                    String to = message.getText().split(" ")[2];
                    Integer from_id = vkClientService.getIdByDomain(from);
                    Integer to_id = vkClientService.getIdByDomain(to);

                    List<Integer> resultPath = graph.bfs(from_id, to_id);

                    if (resultPath != null) {
                        List<UsersGet> users = vkClientService.getDataByIds(resultPath);

                        sendMsg(message, "<b>Цепочка друзей найдена!</b>",true);

                        for (UsersGet user : users) {
                            String resultMessage = "\u2B07\n" +
                                    String.format("<b>%s %s</b> \n<i>https://vk.com/%s</i>\n", user.getFirst_name(), user.getLast_name(), user.getDomain());
                            sendMsg(message, resultMessage,true);
                        }


                    }
                    else
                        sendMsg(message, "Нет данных по одному из указанных пользователей, попробуйте повторить запрос позднее...\n\n", true);
                    break;

                case "/info":
                    sendMsg(message, String.format(
                            "Согласно <a href='https://ru.wikipedia.org/wiki/Теория_шести_рукопожатий'>Теории шести рукопожатий</a> любые два человека в мире разделены не более чем пятью уровнями общих знакомых.\n\n" +
                            "Этот бот может помочь Вам проверить данную теорию и найти цепочку общих знакомых среди любых пользователей сети 'ВКонтакте'.\n\n" +
                            "В данный момент в базе бота %,d пользователей и %,d дружеских связей.\n\n" +
                            "Данные дополняются ежедневно\n",
                            InMemoryGraphService.nodeCount, InMemoryGraphService.edgeCount), true
                    );
                    break;

                case "/help":
                    sendMsg(message, "<b>Доступные команды</b>\n /info - описание бота\n /search <i>user_from user_to</i> - поиск пути в графе друзей, где <i>user_from</i> и <i>user_to</i> - идентификаторы пользователей. \n" +
                            "Идентификатор пользователя можно найти в правой части ссылки на страницу, например https://vk.com/<b>durov</b>.", false);
                    break;
                default:
                    break;
            }
        }
    }

    private void sendMsg(Message message, String text, boolean preview) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableHtml(true);
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText(text);

        if (!preview)
            sendMessage.disableWebPagePreview();

        try {
            execute(sendMessage); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getBotUsername() {
        return "SixHandShakeBot";
    }

    @PostConstruct
    private void initBot() {
        TelegramBotsApi api = new TelegramBotsApi();
        try {
            api.registerBot(this);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

}
