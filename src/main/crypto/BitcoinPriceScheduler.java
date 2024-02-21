package main.crypto;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BitcoinPriceScheduler {

    private static final String BTC = "BTC";
    private static double btcPrice;
    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private static final long ALERT_INTERVAL_SECONDS = 600;

    static {
        updateBtcPrice();
        scheduleBtcPriceUpdates();
    }

    public static double getBtcPrice() {
        return btcPrice;
    }

    private static void updateBtcPrice() {
        btcPrice = CmcApiConnection.getPrice(BTC);
    }

    private static void scheduleBtcPriceUpdates() {
        executorService.scheduleAtFixedRate(BitcoinPriceScheduler::updateBtcPrice, 0, ALERT_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }
}
