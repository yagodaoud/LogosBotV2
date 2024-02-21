package main.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    private BlockingQueue<AudioTrack> queue;
    private BlockingQueue<AudioTrack> historyQueue;

    private boolean isRepeat = false;
    boolean isShuffled = false;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = this.historyQueue = new LinkedBlockingQueue<>();
    }

    public void queue(AudioTrack track) {
        System.out.println(track.getInfo().title);

        if (!this.player.startTrack(track, true)) {
//            this.historyQueue.add(track);
            this.queue.add(track);
        }
    }

    public String nextTrack() {
        if (player.isPaused()){
            player.setPaused(false);
        }

        AudioTrack nextTrack = queue.poll();;
        if (nextTrack != null && player.getPlayingTrack() != null) {
            this.player.startTrack(nextTrack, false);
            return "Skipped to the next track.";
        }
        if (nextTrack == null && player.getPlayingTrack() != null) {
            player.stopTrack();
            queue.clear();
            return "Skipped current track, the queue is now empty.";
        }
        return "The queue is already empty.";
    }

    public String stopQueue() {
        if (!player.isPaused() && player.getPlayingTrack() != null) {
            player.setPaused(true);
            return "Stopped the queue.";
        }

        if (player.getPlayingTrack() == null) {
            return "Nothing is being played right now.";
        }

        return "Queue already stopped.";
    }

    public String resumeQueue() {
        if (player.isPaused() && player.getPlayingTrack() != null) {
            player.setPaused(false);
            return "Resumed the queue.";
        }
        return "Queue already resumed.";
    }

    public String clearQueue() {
        if (!this.queue.isEmpty()) {
            this.queue.clear();
            return "Cleared the queue";
        }
        nextTrack();
        return "The queue is already empty.";
    }

    public String shuffleQueue() {
        BlockingQueue<AudioTrack> copyQueue = new LinkedBlockingQueue<>(queue);
        List<AudioTrack> list = new ArrayList<>(copyQueue);

        if (!isShuffled) {
            Collections.shuffle(list);
            this.queue = new LinkedBlockingQueue<>(list);
            isShuffled = true;
            return "Shuffle is on!";
        }

        this.queue.removeIf(track -> !copyQueue.contains(track));
        this.queue = copyQueue;
        isShuffled = false;
        return "Shuffle is off!";
    }

    public String loopQueue() {
        if (!isRepeat && player.getPlayingTrack() != null) {
            isRepeat = true;
            return "Loop is on!";
        }

        if (player.getPlayingTrack() == null) {
            return "Nothing is being played right now.";
        }

        isRepeat = false;
        return "Loop is off!";
    }

    public AudioTrack getCurrentTrack() {
        if (this.player.getPlayingTrack() != null) {
            return this.player.getPlayingTrack();
        }
        return null;
    }

    public List<AudioTrack> getQueueTracks(){
        return queue.stream().toList();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        System.out.println(endReason.toString());
        if (endReason.toString().equals("FINISHED") || endReason.mayStartNext) {
            this.player.startTrack(queue.poll(), false);
//            AudioTrack trackToBePlayed = queue.poll();
//
//            if (trackToBePlayed == null) {
//                return;
//            }
//
//            historyQueue.add(trackToBePlayed);
//
//            if (isRepeat) {
//                this.player.startTrack(historyQueue.peek(), false);
//                return;
//            }
//            this.player.startTrack(trackToBePlayed, false);
        }
    }
}
