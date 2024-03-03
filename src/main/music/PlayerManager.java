package main.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class PlayerManager {
    private static PlayerManager INSTANCE;
    private final Map<Long, AudioManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
    private final AudioManager guildMusicManager = new AudioManager(this.audioPlayerManager);

    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public AudioManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
            return guildMusicManager;
        });
    }

    public String loadAndPlay(TextChannel channel, String trackUrl) {
        final AudioManager musicManager = this.getMusicManager(channel.getGuild());
        final AtomicReference<String> messageContainer = new AtomicReference<>();
        CompletableFuture<Void> future = new CompletableFuture<>();

        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.scheduler.queue(track);
                long durationMs = track.getDuration();
                String formattedDuration = formatDuration(durationMs);

                StringBuilder messageBuilder = new StringBuilder("Added to queue: `")
                        .append(track.getInfo().title)
                        .append(" (")
                        .append(formattedDuration)
                        .append(")` by `")
                        .append(track.getInfo().author)
                        .append("`");

                String message = messageBuilder.toString();
                messageContainer.set(message);
                future.complete(null);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                final List<AudioTrack> tracks = playlist.getTracks();
                final AudioTrack firstTrack = tracks.get(0);
                musicManager.scheduler.queue(firstTrack);

                long durationMs = firstTrack.getDuration();
                String formattedDuration = formatDuration(durationMs);

                StringBuilder messageBuilder = new StringBuilder("Added to queue: `")
                        .append(firstTrack.getInfo().title)
                        .append(" (")
                        .append(formattedDuration)
                        .append(")` by `")
                        .append(firstTrack.getInfo().author)
                        .append("`");

                if (trackUrl.contains("/playlist")) {
                    messageBuilder.append(" and `")
                            .append(tracks.size() - 1)
                            .append("` more");

                    for (AudioTrack track : tracks) {
                        if (track == tracks.get(0))
                            continue;
                        musicManager.scheduler.queue(track);
                    }
                }
                String message = messageBuilder.toString();
                messageContainer.set(message);
                future.complete(null);
            }

            @Override
            public void noMatches() {
                future.completeExceptionally(new RuntimeException("No matches"));
            }

            @Override
            public void loadFailed(FriendlyException e) {
                future.completeExceptionally(new RuntimeException("Load Failed"));
            }
        });
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return messageContainer.get();
    }

    public String skipTrack(AudioManager musicManager) {
        return musicManager.scheduler.nextTrack();
    }

    public String stopQueue(AudioManager musicManager) {
        return musicManager.scheduler.stopQueue();
    }

    public String resumeQueue(AudioManager musicManager) {
        return musicManager.scheduler.resumeQueue();
    }

    public String clearQueue(AudioManager musicManager) {
        return musicManager.scheduler.clearQueue();
    }

    public String shuffleQueue(AudioManager musicManager){
        return musicManager.scheduler.shuffleQueue();
    }

    public String loopQueue(AudioManager musicManager) {
        return musicManager.scheduler.loopQueue();
    }

    public StringBuilder getCurrentTrack(TextChannel channel) {
        final AudioManager musicManager = this.getMusicManager(channel.getGuild());
        final AudioTrack currentTrack = musicManager.scheduler.getCurrentTrack();
        StringBuilder queueMessage = new StringBuilder();

        if (currentTrack != null) {
            long durationMs = currentTrack.getDuration();
            String formattedDuration = formatDuration(durationMs);

            queueMessage.append("Current track: `")
                    .append(currentTrack.getInfo().title)
                    .append(" (")
                    .append(formattedDuration)
                    .append(")` by `")
                    .append(currentTrack.getInfo().author)
                    .append("`");
            return queueMessage;
        } else {
            return queueMessage.append("Nothing is being played right now.");
        }
    }

    public StringBuilder getQueueTracks(TextChannel channel) {
        final AudioManager musicManager = this.getMusicManager(channel.getGuild());
        List<String> message = new ArrayList<>();
        int iterator = 1;

        for (AudioTrack track : musicManager.scheduler.getQueueTracks()) {
            String trackInfo = String.format("%d - `%s` - `%s`", iterator, track.getInfo().title, track.getInfo().author);
            message.add(trackInfo);
            iterator++;
        }

        StringBuilder queueMessage = new StringBuilder();
        if (!message.isEmpty()) {
            if (message.size() >= 10) {
                queueMessage.append(getCurrentTrack(channel)).append("\n");
                queueMessage.append("Next 10 songs:\n");
            } else {
                queueMessage.append(String.format("Next %d songs: \n", message.size()));
            }

            for (int i = 0; i < message.size(); i++) {
                queueMessage.append(message.get(i)).append("\n");
                if (i == 9){
                    break;
                }
            }
        } else {
            return queueMessage.append("The queue is empty.");
        }

        return queueMessage;
    }

    private String formatDuration(long durationMs) {
        long seconds = (durationMs / 1000) % 60;
        long minutes = (durationMs / (1000 * 60)) % 60;
        long hours = (durationMs / (1000 * 60 * 60)) % 24;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    public static PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }
}
