package main.music;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MusicCommandList {

    public static List<CommandData> getMusicCommandsList() {
        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put("join", "Join the current channel");
        hashMap.put("leave", "Leave the current channel");
        hashMap.put("play", "Play a song or a playlist");
        hashMap.put("skip", "Skips to next track");
        hashMap.put("stop", "Stop the queue");
        hashMap.put("resume", "Stop the queue");
        hashMap.put("clear", "Clear the queue");
        hashMap.put("loop", "Loop the queue");
        hashMap.put("shuffle", "Shuffle the queue");
        hashMap.put("queue", "Show the queue");
        hashMap.put("now-playing", "Show the current track");

        List<CommandData> commandDataList = new ArrayList<>();

        for (HashMap.Entry<String, String> entry : hashMap.entrySet()) {
            SlashCommandData commandData = Commands.slash(entry.getKey(), entry.getValue());

            if (entry.getKey().equals("play")) {
                OptionData songUrlOrName = new OptionData(OptionType.STRING, "song_search_or_link", "Enter the song search or url", true);
                commandData.addOptions(songUrlOrName);
            }

            commandDataList.add(commandData);
        }

        return commandDataList;
    }
}
