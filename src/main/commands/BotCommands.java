package main.commands;

import main.crypto.*;
import main.helpView.*;
import main.music.AudioEventHandler;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;
import java.util.*;

public class BotCommands extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        BitcoinScheduledAlert scheduledAlert = null;
        String command = event.getName();
        Member member = event.getMember();
        String userName = event.getUser().getName();
        String userId = event.getUser().getId();
        Guild guild = event.getGuild();
        GuildVoiceState voiceState = member.getVoiceState();
        String guildId = guild.getId();
        TextChannel channel = event.getChannel().asTextChannel();
        String channelId = channel.getId();
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        AudioEventHandler audioEventHandler = new AudioEventHandler();
        Locale locale = Locale.US;
        String optionName = event.getOptions().isEmpty() ? "" : event.getOptions().get(0).getName();
        String optionValue = event.getOptions().isEmpty() ? "" : event.getOptions().get(0).getAsString();
        Map<String, String> info  = new HashMap<>() {{
            put("UserName", userName);
            put("UserId", userId);
            put("GuildId", guildId);
            put("TextChannelId", channelId);
            put("DateAdded", now);
            put("Type", null);
        }};

        CommandListenerDB.registerEventOnDB(command, optionName, optionValue, info);

        try {
            switch (command) {
                case "help" -> event.reply(HelpView.getHelpView()).queue();
                case "crypto-price" -> event.reply(CryptoPriceDiscord.getFormattedPrice(event.getOption("crypto-symbol").getAsString())).queue();
                case "bitcoin-alert-start" -> {
                    double percentage = event.getOption("percentage").getAsDouble();
                    BitcoinPriceAlert bitcoinPriceAlert = new BitcoinPriceAlert(percentage, info);
                    bitcoinPriceAlert.startAlert(channel);
                    event.reply("Tracking Bitcoin price when its variation is greater than " + percentage + "%!").queue();
                }
                case "bitcoin-alert-stop" -> {
                    BitcoinPriceAlert alert = new BitcoinPriceAlert();
                    event.reply(alert.stop(info)).queue();
                }
                case "bitcoin-scheduled-alert-start" -> {
                    scheduledAlert = new BitcoinScheduledAlert();
                    scheduledAlert.start(info);
                    event.reply("The daily closing price of Bitcoin will be displayed from now on!").queue();
                }
                case "bitcoin-scheduled-alert-stop" -> {
                    scheduledAlert = new BitcoinScheduledAlert();
                    event.reply(scheduledAlert.stop(info)).queue();
                }
                case "bitcoin-price-trigger-start" -> {
                    double targetPrice = event.getOption("target-price").getAsDouble();
                    BitcoinPriceTrigger.start(targetPrice, info);
                    event.reply(String.format(locale, "Tracking Bitcoin price when it reaches $%,.2f!", targetPrice)).queue();
                }
                case "bitcoin-price-trigger-stop" -> event.reply(BitcoinPriceTrigger.stop(info)).queue();
                case "play" -> {
                    audioEventHandler.start(member, voiceState);
                    event.reply(audioEventHandler.handle(channel,  event.getOption("song_search_or_link").getAsString(), event)).queue();
                }
                case "join" -> event.reply(audioEventHandler.joinVoiceChannel(voiceState, guild, event)).queue();
                case "leave" -> event.reply(audioEventHandler.leaveVoiceChannel(guild)).queue();
                case "skip" -> event.reply(audioEventHandler.skipTrack(guild)).queue();
                case "stop" -> event.reply(audioEventHandler.stopTrack(guild)).queue();
                case "resume" -> event.reply(audioEventHandler.resumeTrack(guild)).queue();
                case "clear" -> event.reply(audioEventHandler.clearQueue(guild)).queue();
                case "shuffle" -> event.reply(audioEventHandler.shuffleQueue(guild)).queue();
                case "loop" -> event.reply(audioEventHandler.loopTrack(guild)).queue();
                case "now-playing" -> event.reply(audioEventHandler.getCurrentTrack(channel)).queue();
                case "queue" -> event.reply(audioEventHandler.getQueueTracks(channel)).queue();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            event.reply("An error occurred.").queue();
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals("menu:help")) {
            for (String option : event.getValues()) {
                if (option.equals("Music")) {
                    event.reply(HelpMusicView.getMusicView()).queue();
                } else if (option.equals("Crypto")) {
                    event.reply(HelpCryptoView.getCryptoView()).queue();
                }
            }
        }
    }
}
