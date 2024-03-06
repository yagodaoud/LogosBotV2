package main.music;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.net.URL;

public class AudioEventHandler {

    private Member member;
    private GuildVoiceState voiceState;
    private static boolean joined = false;

    public void start(Member member, GuildVoiceState voiceState) {
        this.member = member;
        this.voiceState = voiceState;
    }

    public String joinVoiceChannel(GuildVoiceState voiceState, Guild guild, SlashCommandInteractionEvent event) {
        if (!voiceState.inAudioChannel()) {
            return ("You must be in a voice channel to use this command.");
        }

        AudioChannel audioChannel = voiceState.getChannel();

        if (audioChannel == null) {
            return ("Failed to join voice channel.");
        }

        if (!joined) {
            net.dv8tion.jda.api.managers.AudioManager audioManager = guild.getAudioManager();
            audioManager.openAudioConnection(audioChannel);
            event.getGuild().getAudioManager().setSelfDeafened(true);
            joined = true;
            return ("Joining voice channel: `" + audioChannel.getName() + "`.");
        }

        return ("I'm already in a voice channel.");
    }

    public void joinVoiceChannelByPlayCommand(GuildVoiceState voiceState, Guild guild, SlashCommandInteractionEvent event) {
        AudioChannel audioChannel = voiceState.getChannel();
        net.dv8tion.jda.api.managers.AudioManager audioManager = guild.getAudioManager();
        audioManager.openAudioConnection(audioChannel);
        event.getGuild().getAudioManager().setSelfDeafened(true);
        joined = true;
    }

    public String leaveVoiceChannel(Guild guild, boolean inactive) {
        final AudioChannel connectedChannel = guild.getSelfMember().getVoiceState().getChannel();

        if (connectedChannel != null) {
            connectedChannel.getGuild().getAudioManager().closeAudioConnection();
            joined = false;
            if (inactive) {
                return "Left the channel due to inactivity";
            }
            return "Left the voice channel.";
        }

        return ("Not connected to a voice channel.");
    }

    public String skipTrack(Guild guild) {
        final AudioManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        return PlayerManager.getInstance().skipTrack(musicManager);
    }

    public String stopTrack(Guild guild) {
        final AudioManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        return PlayerManager.getInstance().stopQueue(musicManager);
    }

    public String resumeTrack(Guild guild) {
        final AudioManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        return PlayerManager.getInstance().resumeQueue(musicManager);
    }

    public String clearQueue(Guild guild){
        final AudioManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        return PlayerManager.getInstance().clearQueue(musicManager);
    }

    public String loopTrack(Guild guild) {
        final AudioManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        return PlayerManager.getInstance().loopQueue(musicManager);
    }

    public String shuffleQueue(Guild guild){
        final AudioManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        return PlayerManager.getInstance().shuffleQueue(musicManager);
    }

    public String getCurrentTrack(TextChannel channel){
        return PlayerManager.getInstance().getCurrentTrack(channel).toString();
    }

    public String getQueueTracks(TextChannel channel){
        return PlayerManager.getInstance().getQueueTracks(channel).toString();
    }

    public String handle(TextChannel channel, String url, Event event) {
        AudioChannel audioChannel = voiceState.getChannel();

        if (!(audioChannel instanceof VoiceChannel)) {
            return ("You must be in a voice channel to use this command.");
        }

        VoiceChannel voiceChannel = (VoiceChannel) audioChannel;
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.getChannel().equals(voiceChannel)) {
            return ("You must be in the same voice channel as me.");
        }

        joinVoiceChannelByPlayCommand(voiceState, audioChannel.getGuild(), (SlashCommandInteractionEvent) event);

        if (url != null) {
            if (isUrl(url)) {
                return PlayerManager.getInstance().loadAndPlay(channel, url);
            } else if (!isUrl(url)) {
                String search = String.join(" ", url);
                String link = "ytsearch:" + search;
                return PlayerManager.getInstance().loadAndPlay(channel, link);
            }
        }
        return ("An error occurred.");
    }

    private boolean isUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
