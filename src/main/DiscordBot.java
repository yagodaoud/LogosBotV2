package main;

import io.github.cdimascio.dotenv.Dotenv;
import main.commands.BotCommands;
import main.commands.LoadCommands;

import main.crypto.BitcoinPriceAlert;
import main.crypto.BitcoinPriceTrigger;
import main.crypto.BitcoinScheduledAlert;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.EnumSet;

public class DiscordBot {
    private static JDA bot;

    public DiscordBot() {
        String token = Dotenv.configure().load().get("TOKENDISCORD");
        bot = JDABuilder.createDefault(token,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.GUILD_VOICE_STATES
                )
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .disableCache(EnumSet.of(
                        CacheFlag.CLIENT_STATUS,
                        CacheFlag.ACTIVITY,
                        CacheFlag.EMOJI,
                        CacheFlag.STICKER,
                        CacheFlag.SCHEDULED_EVENTS
                ))
                .setChunkingFilter(ChunkingFilter.ALL)
                .addEventListeners(new LoadCommands(), new BotCommands())
                .enableCache(CacheFlag.VOICE_STATE)
                .build();
    }

    public static void main(String[] args) {
       new DiscordBot();
       bot.getPresence().setActivity(Activity.listening("/help"));
       BitcoinScheduledAlert.fetch();
       BitcoinPriceTrigger.fetch();
       BitcoinPriceAlert.fetch();
    }

    public static JDA getInstance() {
        return bot;
    }

    public void shutdown() {
        bot.shutdown();
    }
}
