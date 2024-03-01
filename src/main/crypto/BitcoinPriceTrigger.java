package main.crypto;

import com.mysql.cj.xdevapi.Table;
import main.DiscordBot;
import main.db.EventData;
import main.db.RegisterEvent;
import main.db.SelectEvent;
import main.db.Tables;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BitcoinPriceTrigger {

    private static double targetPrice;
    private static int priceTrendDesired;  //1 for uptrend, 0 for downtrend
    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final Locale locale = Locale.US;


    public static void start(Double price, Map<String, String> info) {
        targetPrice = price;
        double btcStartPrice = BitcoinPriceScheduler.getBtcPrice();
        if (targetPrice > btcStartPrice){
            priceTrendDesired = 1;
        } else {
            priceTrendDesired = 0;
        }

        info.put("Type", "Trigger");
        info.put("Threshold", String.valueOf(price));
        info.put("PreviousThreshold", String.valueOf(btcStartPrice));
        info.put("PriceTrendDesired", String.valueOf(priceTrendDesired));
        RegisterEvent.registerCryptoEventOnDB(Tables.CRYPTO.getTableName(), RegisterEvent.INSERT, info);
        fetch();
    }

    public static void fetch() {
        String textChannelId;
        TextChannel channel;
        String price;
        String priceTrend;
        String userId;
        String type;

        JDA bot = DiscordBot.getInstance();
        List<EventData> list = SelectEvent.getEvent(Tables.CRYPTO.getTableName());

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        for (EventData eventData : list) {
            userId = eventData.getUserId();
            textChannelId = eventData.getTextChannelId();
            System.out.println(eventData.getThreshold());
            price = eventData.getThreshold();
            priceTrend = eventData.getPriceTrendDesired();
            type = eventData.getType();
            if (textChannelId != null && price != null && type.equals("Trigger")) {
                channel = bot.getTextChannelById(textChannelId);
                setPriceForNotification(channel, Double.valueOf(price), Double.valueOf(priceTrend), userId);
            }
        }
    }

    public static void setPriceForNotification(TextChannel channel, Double targetPrice, Double priceTrendDesired,  String id) {
        if (targetPrice == null) return;
        long ALERT_INTERVAL = 600;
        executorService.scheduleAtFixedRate(() -> {
            double priceNow = BitcoinPriceScheduler.getBtcPrice();
            if (targetPrice == priceNow) {
                String message = String.format("Bitcoin has reached $%,.2f, now at $%,.2f <@%s>!", targetPrice, priceNow, id);
                channel.sendMessage(message).queue();
                System.out.println(message);
                executorService.shutdown();
            } else if (priceNow > targetPrice && priceTrendDesired == 1) {
                String message = String.format("Bitcoin has exceeded $%,.2f, now at $%,.2f <@%s>!", targetPrice, priceNow, id);
                channel.sendMessage(message).queue();
                System.out.println(message);
                executorService.shutdown();
            } else if (priceNow < targetPrice && priceTrendDesired == 0) {
                String message = String.format("Bitcoin has gone below $%,.2f, now at $%,.2f <@%s>!", targetPrice, priceNow, id);
                channel.sendMessage(message).queue();
                System.out.println(message);
                executorService.shutdown();
            }

        },0, ALERT_INTERVAL, TimeUnit.SECONDS);
    }

    public static String stop(Map<String, String> info) {
        info.put("Type", "Trigger");

        int affectedRows = RegisterEvent.registerCryptoEventOnDB(Tables.CRYPTO.getTableName(), RegisterEvent.DELETE, info);

        if (affectedRows > 0) {
            executorService.shutdown();
            return "Bitcoin price tracker disabled";
        }
        return "The command is not active";
    }
}