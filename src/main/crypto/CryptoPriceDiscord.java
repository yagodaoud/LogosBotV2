package main.crypto;

import java.text.NumberFormat;
import java.util.Locale;

public class CryptoPriceDiscord {

    public static double getPrice(String symbol) {
        return CmcApiConnection.getPrice(symbol);
    }
    public static String getFormattedPrice(String symbol){
        symbol = symbol.toUpperCase();
        double price = getPrice(symbol);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        return ("The current price of " + symbol + " is " + formatter.format(price));
    }
}
