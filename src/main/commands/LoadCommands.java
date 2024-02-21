package main.commands;

import main.crypto.CryptoCommandsList;
import main.music.MusicCommandList;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LoadCommands extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        List<CommandData> cryptoList = CryptoCommandsList.getCryptoCommandsList();
        List<CommandData> musicList = MusicCommandList.getMusicCommandsList();

        // Add general commands
        commandData.add(Commands.slash("help", "Get help with the bot's features"));

        // Add special commands
        commandData.addAll(cryptoList);
        commandData.addAll(musicList);

        event.getJDA().updateCommands().addCommands(commandData).queue();
        System.out.println("Bot is ready!");
    }
}
