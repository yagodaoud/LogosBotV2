package main.crypto;

import java.io.BufferedReader;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

public class CmcApiConnection {
    private static final String token = Dotenv.configure().load().get("TOKENCMC");


    public static double getPrice(String symbol) {
        try {
            URL url = new URL("https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest?symbol=" + symbol.toUpperCase());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-CMC_Pro_API_Key", token);

            int status = connection.getResponseCode();
            if (status == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject json = new JSONObject(response.toString());
                JSONObject symbolData = json.getJSONObject("data")
                        .getJSONObject(symbol)
                        .getJSONObject("quote")
                        .getJSONObject("USD");
                return symbolData.getDouble("price");
            } else {
                System.err.println("Failed to fetch data, status code: " + status);
                return 0.0;
            }
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            return 0.1;
        }
    }
}
