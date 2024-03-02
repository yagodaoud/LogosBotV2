package main.crypto;

import main.DiscordBot;
import main.db.EventData;
import main.db.RegisterEvent;
import main.db.SelectEvent;
import main.db.Tables;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class BitcoinScheduledAlert { //Bitcoin update at every candle close (12 am GMT [9 pm BRT])
    private static final Timer timer = new Timer();
    private static TimerTask task;
    public void start(Map<String, String> info) { //Getting the time from BotCommands parameter
        info.put("Type", "sAlert");
        RegisterEvent.registerCryptoEventOnDB(Tables.CRYPTO.getTableName(), RegisterEvent.INSERT, info);
        fetch();
    }

    public static void fetch() {
        JDA bot = DiscordBot.getInstance();
        List<EventData> list = SelectEvent.getEvent(Tables.CRYPTO.getTableName());

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        String textChannelId;
        TextChannel channel = null;
        String type;
        for (EventData eventData : list) {
            textChannelId = eventData.getTextChannelId();
            type = eventData.getType();
            if (textChannelId != null && type.equals("sAlert")) {
                channel = bot.getTextChannelById(textChannelId);
            }
        }

        LocalTime time = LocalTime.of(0, 0);

        TextChannel finalChannel = channel;
        task = new TimerTask() {
            @Override
            public void run() {
                double price = BitcoinPriceScheduler.getBtcPrice();

                NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
                String priceString = formatter.format(price);

                System.out.println((priceString));
                finalChannel.sendMessage("The closing price of Bitcoin is " + priceString).queue();
            }
        };

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("GMT"));
        ZonedDateTime scheduledTime = ZonedDateTime.of(now.toLocalDate(), time, now.getZone());

        if (now.compareTo(scheduledTime) > 0) {
            scheduledTime = scheduledTime.plusDays(1); //If the command has been triggered after time in LocalTime, set it to next day at the set time
        }

        long delay = Duration.between(now, scheduledTime).toMillis();
        timer.schedule(task, delay, TimeUnit.DAYS.toMillis(1));
    }

    public String stop(Map<String, String> info) {
        info.remove("UserId");
        info.put("Type", "sAlert");

        int affectedRows = RegisterEvent.registerCryptoEventOnDB(Tables.CRYPTO.getTableName(), RegisterEvent.DELETE, info);

        if (affectedRows > 0) {
            if (task != null) {
                task.cancel();
                task = null;
            }
            return "Disabled the daily closing price of Bitcoin on this channel";
        }
        return "The command is not active";
    }
}

