package main.crypto;


import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CryptoCommandsList {

    public static List<CommandData> getCryptoCommandsList() {
        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put("crypto-price", "Get the price of a crypto");
        hashMap.put("bitcoin-alert-start", "Create a tracker for Bitcoin");
        hashMap.put("bitcoin-alert-stop", "Disable previous tracker for Bitcoin");
        hashMap.put("bitcoin-scheduled-alert-start", "Send the price of Bitcoin at 12:00 AM UTC everyday");
        hashMap.put("bitcoin-scheduled-alert-stop", "Disable the scheduled alert");
        hashMap.put("bitcoin-price-trigger-start", "Bitcoin Price tracker, if the value is reached, the bot will send a notification");
        hashMap.put("bitcoin-price-trigger-stop", "Disable the bitcoin price tracker");

        List<CommandData> commandDataList = new ArrayList<>();

        for (HashMap.Entry<String, String> entry : hashMap.entrySet()) {
            SlashCommandData commandData = Commands.slash(entry.getKey(), entry.getValue());

            if (entry.getKey().equals("crypto-price")) {
                OptionData cryptoSymbolOption = new OptionData(OptionType.STRING, "crypto-symbol", "Enter the crypto symbol", true);
                commandData.addOptions(cryptoSymbolOption);
            }

            if (entry.getKey().equals("bitcoin-alert-start")) {
                OptionData cryptoSymbolOption = new OptionData(OptionType.STRING, "percentage", "Percentage that will trigger the alert (in %)", true);
                commandData.addOptions(cryptoSymbolOption);
            }

            if (entry.getKey().equals("bitcoin-price-start")) {
                OptionData cryptoSymbolOption = new OptionData(OptionType.STRING, "target-price", "Target price desired", true);
                commandData.addOptions(cryptoSymbolOption);
            }

            commandDataList.add(commandData);
        }

        return commandDataList;
    }
}