package tutorial;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class Bot extends TelegramLongPollingBot {

    private boolean screaming = false;

    private InlineKeyboardMarkup kbm1;
    private InlineKeyboardMarkup kbm2;
    private boolean menu2;

    // methods below -------------------------------------------------


    @Override
    public String getBotUsername() {
        return "AexChaplain_bot";
    }

    @Override
    public String getBotToken() {
        return "6296481122:AAE61O9UltAacJVmIQMQR-2cRkU0R5WtXvI";
    }

    @Override
    public void onUpdateReceived(Update update) {

        var msg = update.getMessage();
        var txt = msg.getText();
        var user = msg.getFrom();
        var id = user.getId();

        System.out.println(user.getFirstName() + " wrote \"" + msg.getText() + "\"");

        if (msg.isCommand()) {
            commandHandler(txt, id);
            return; // this is so the bot can take commands without returning them to the user
        }
        if (screaming) { // if 'screaming' is  true, call a custom method
            scream(id, msg);
        } else {
            copyMessage(id, msg.getMessageId()); // proceed normally if 'screaming' = false
        }
    }

    private void commandHandler (String txt, Long id){
        if (txt.equals("/start"))
            start(id);
        else if (txt.equals("/help"))
            help(id);
        else if (txt.equals("/scream"))
            screaming = true;
        else if (txt.equals("/whisper"))
            screaming = false;
        else if (txt.equals("/menu"))
            sendMenu(id, "<b>Menu 1</b>");

    }

    /**
     * Makes a copy of recived message and sends copy back to the user who sent it
     *
     * @param who
     * @param msgId
     */
    public void copyMessage(Long who, Integer msgId) {
        CopyMessage cm = CopyMessage.builder()
                .fromChatId(who.toString()) // Copy from the user
                .chatId(who.toString()) //Send it back to the user
                .messageId(msgId) // specify the message
                .build();
        try {
            execute(cm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    // Command methods -------------------------------------------
    private void scream(long id, Message msg) {
        if (msg.hasText()) {
            sendText(id, msg.getText().toUpperCase()); // convert message text to uppercase
        } else {
            copyMessage(id, msg.getMessageId()); // Normal response: we can't scream unless sent message has text.
        }
    }

    private void help(long who) {
        String info = "This bot (the Pope) will send any received messages back to the user who sends them.\n" +
                "The /scream command will cause the Pope to scream his return messages in CAPITOL Letters.\n" +
                "The /whisper command will quiet the Pope down, reverting his speech to basic lowercase letters. " +
                "If he is already calm, the /whisper command will not change anything.\n" +
                "Enter /menu for more options";
        sendText(who, info);
    }

    private void start(long who) {
        String temp = "Welcome to the Vatican, I truly believe you will enjoy your stay. My goal is to remind your " +
                "heathenish protestant soul about events such as Hall Bible Study and Hall Prayer. Enter /help for " +
                "more.";
        sendText(who, temp);
    }

    private void sendMenu(Long who, String txt) {

        InlineKeyboardMarkup kb = menuHelper();

        SendMessage sm = SendMessage.builder().chatId(who.toString())
                .parseMode("HTML").text(txt)
                .replyMarkup(kb).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private InlineKeyboardMarkup menuHelper(){
        //create three buttons for the user
        var next = InlineKeyboardButton.builder()
                .text("Next") // what the user will see
                .callbackData("next") //what's sent to the update method, allows us to see which button was clicked
                .build(); // inherited method from the telegram API

        var back = InlineKeyboardButton.builder()
                .text("Back").callbackData("back").build();

        var url = InlineKeyboardButton.builder()
                .text("Tutorial")
                .url("https://core.telegram.org/bots/api").build();
                /*The link/url option does not need callback-data. Its behavior is
                 predefined (i.e., it will open the given link when tapped */

        // build and assign keyboards
        kbm1 = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(next))
                .build(); // We have two menus to demonstrate 'next' button in menu 1 leading to a second menu
        kbm2 = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(back)) // List.of() creates immutable list, no null elements allowed
                .keyboardRow(List.of(url))
                .build();

        return kbm1;
    } // creates buttons and returns keyboard to 'SendMenu'
    public void sendText(Long who, String what) {

        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //Who are we sending a message to
                .text(what).build();    //Message content
        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

    public void sendPhoto(Long who, String file_id) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString())
                .text(file_id).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    } // unused  for the moment
}