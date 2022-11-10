package Bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


public class TestBot extends TelegramLongPollingBot {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    private final List<String> notes = new ArrayList<>();
    private final String fileName = "src/main/resources/schedule.txt";

    @Override
    public String getBotUsername() {
        return "testBot";
    }

    @Override
    public String getBotToken() {
        return "...";
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message msg = update.getMessage();
        SendMessage message = new SendMessage();

        String name = update.getMessage().getFrom().getFirstName();
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chat_id = update.getMessage().getChatId();

            String[] split = msg.getText().split(" ");
            String dayName = null;
            if (split.length > 1)
                dayName = split[1];
            switch (split[0]) {
                case "/hello" -> {
                    message.setChatId(String.valueOf(chat_id));
                    message.setText("Привет, " + name + ", я бот, который пришлёт тебе расписание!");
                }
                case "/bye" -> {
                    message.setChatId(String.valueOf(chat_id));
                    message.setText("Пока, " + name);
                }
                case "/save" -> {
                    save(split);
                    message.setChatId(String.valueOf(chat_id));
                    message.setText("Заметка сохранена");
                }
                case "/show" -> {
                    message.setChatId(String.valueOf(chat_id));
                    message.setText(show());
                }
                case "/deleteNotes" -> {
                    message.setChatId(String.valueOf(chat_id));
                    notes.clear();
                    message.setText("Заметки удалены");
                }
                case "/today" -> {
                    message.setChatId(String.valueOf(chat_id));
                    try {
                        message.setText("Расписание на сегодня:" + getSchedule(determineDate(new Date())));
                    } catch (Exception e) {
                        message.setText("Нет расписания на сегодня");
                    }
                }
                case "/tomorrow" -> {
                    message.setChatId(String.valueOf(chat_id));
                    try {
                        message.setText("Расписание на завтра:" + getSchedule(determineNextDate()));
                    } catch (Exception e) {
                        message.setText("нет расписания на этот день");
                    }
                }
                case "/whatweek" -> {
                    message.setChatId(String.valueOf(chat_id));
                    String week = whatWeek(new Date());
                    message.setText("Сейчас " + week + " неделя");
                }
                case "/redact" -> {
                    message.setChatId(String.valueOf(chat_id));
                    message.setText(edit(split));
                }
                case "/day" -> {
                    message.setChatId(String.valueOf(chat_id));
                    if (dayName != null) {
                        try {
                            Date date = DATE_FORMAT.parse(dayName);
                            message.setText("Расписание на указанную дату:" +
                                    "" + getSchedule(determineDate(date)));
                        } catch (Exception e) {
                            message.setText("Укажите правильно дату");
                        }
                    } else
                        message.setText("Не указана дата");
                }
                default -> {
                    message.setChatId(String.valueOf(chat_id));
                    message.setText("Сообщение было введено некорректно");
                }
            }
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void save(String[] split) {
        String[] array = new String[split.length - 1];
        for (int i = 0, k = 1; i < split.length - 1; i++)
            array[i] = split[k++];
        String s = String.join(" ", array);
        notes.add(s);
    }

    private String show() {
        if (notes.isEmpty())
            return "Ничего";
        else
            return String.join("\r\n", notes);
    }

    private String determineDate(Date dt) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E", Locale.getDefault());
        String dayWeek = simpleDateFormat.format(dt);
        String week = whatWeek(dt);
        return week + "_" + dayWeek;
    }

    private String determineNextDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_WEEK, +1);
        Date nextDate = calendar.getTime();
        return determineDate(nextDate);
    }

    private String getSchedule(String date) throws Exception {
        List<String> schedule = new ArrayList<>();
        StringBuilder fullText = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            while (reader.ready()) {
                String s = reader.readLine();
                fullText.append(s).append("\r\n");
            }
        }
        String[] split = fullText.toString().split("@");
        Day day = Day.valueOfName(date);
        if (day == null)
            return "нет расписания на этот день";
        for (String value : split) {
            if (value.contains(day.getName().toLowerCase(Locale.ROOT)))
                schedule.add(value);
        }
        StringBuilder s = new StringBuilder();
        for (String value : schedule)
            s.append(value);
        return s.toString();
    }

    private String whatWeek(Date d) {
        SimpleDateFormat date = new SimpleDateFormat("w");
        int numberWeek = Integer.parseInt(date.format(d.getTime()));
        if (numberWeek % 2 == 0)
            return "чётная";
        else
            return "нечёт";
    }

    private String edit(String[] array) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 1; i < array.length; i++) {
                writer.write(array[i] + " ");
            }
            return "Расписание изменено";
        } catch (IOException e) {
            return "Не удалось изменить расписание";
        }
    }
}


