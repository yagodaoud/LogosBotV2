package main.crypto;

import main.DiscordBot;
import main.db.EventData;
import main.db.RegisterEvent;
import main.db.SelectEvent;
import main.db.Tables;
import main.music.PlayerManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BitcoinPriceAlert {

    private static double VARIATION_THRESHOLD;

    private static double THRESHOLD;
    private static final long ALERT_INTERVAL= 3600;
    private boolean alerted;
    private double lastPrice;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final DecimalFormat priceFormatter = new DecimalFormat("#,##0.00");
    private static BitcoinPriceAlert INSTANCE;


    public BitcoinPriceAlert() {}
    public BitcoinPriceAlert(double percentage, Map<String, String> info){
        lastPrice = getBtcPrice();
        THRESHOLD = percentage;
        VARIATION_THRESHOLD = percentage;

        info.put("Type", "pAlert");
        info.put("Threshold", String.valueOf(percentage));
        info.put("PreviousThreshold", String.valueOf(lastPrice));
        RegisterEvent.registerCryptoEventOnDB(Tables.CRYPTO.getTableName(), RegisterEvent.INSERT ,info);
    }
    public double getBtcPrice() {
        return BitcoinPriceScheduler.getBtcPrice();
    }

    public double priceVariationCalculator(double currentPrice, double lastPrice){
        /*Formula to get the
          variation e.g. The price was 100,
          now it's 120 -> (120 - 100) / 100 = 0.2 * 100 = 20%
        */
        return ((currentPrice - lastPrice) / lastPrice) * 100;
    }

    public static void fetch() {
        String textChannelId;
        TextChannel channel;
        String threshold;
        String previousThreshold;
        String type;

        JDA bot = DiscordBot.getInstance();
        List<EventData> list = SelectEvent.getEvent(Tables.CRYPTO.getTableName());

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        for (EventData eventData : list) {
            textChannelId = eventData.getTextChannelId();
            threshold = eventData.getThreshold();
            previousThreshold = eventData.getPreviousThreshold();
            type = eventData.getType();
            if (textChannelId != null && threshold != null && type.equals("pAlert")) {
                channel = bot.getTextChannelById(textChannelId);
                BitcoinPriceAlert bitcoinPriceAlert = BitcoinPriceAlert.getInstance();
                bitcoinPriceAlert.startAlert(channel);
                bitcoinPriceAlert.lastPrice =  Double.parseDouble(previousThreshold);
                VARIATION_THRESHOLD = Double.parseDouble(threshold);
            }
        }
    }
    public void startAlert(TextChannel channel) {
        executorService.scheduleAtFixedRate(() -> {

            double currentPrice = getBtcPrice();
            double variation = priceVariationCalculator(currentPrice, lastPrice);

            System.out.println(variation);
            System.out.println("Current price is " + currentPrice);
            System.out.println("Variation is: " + String.format("%.2f%%", variation));

            if (Math.abs(variation) >= VARIATION_THRESHOLD) {
                String direction = variation > 0 ? "up" : "down";
                String emoji = variation > 0 ? "📈" : "📉";
                String priceString = "$" + priceFormatter.format(currentPrice);
                String variationString = String.format("%.2f%%", variation);
                String bitcoinAlert = "Bitcoin is " + direction + "! " + priceString +
                        " (" + variationString + " in the last hour) " + emoji;
                System.out.println(bitcoinAlert);
                lastPrice = currentPrice;
                channel.sendMessage(bitcoinAlert).queue();
            } else {
                alerted = Math.abs(variation) < THRESHOLD && alerted;
            }
        }, 0, ALERT_INTERVAL, TimeUnit.SECONDS);
    }

    public String stop(Map<String, String> info) {
        info.put("Type", "pAlert");

        int affectedRows = RegisterEvent.registerCryptoEventOnDB(Tables.CRYPTO.getTableName(), RegisterEvent.DELETE, info);

        if (affectedRows > 0) {
            executorService.shutdown();
            return "Bitcoin price alert disabled";
        }
        return "The command is not active";
    }

    public static BitcoinPriceAlert getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BitcoinPriceAlert();
        }
        return INSTANCE;
    }
}

