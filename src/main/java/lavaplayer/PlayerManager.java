package lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerManager {

    private static PlayerManager INSTANCE;
    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
            return guildMusicManager;
        });
    }


    // Plays the track
    public void loadAndPlay(TextChannel textChannel, String trackURL) {
        final GuildMusicManager musicManager = this.getMusicManager(textChannel.getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManager, trackURL, new AudioLoadResultHandler() {

            // URL
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                musicManager.scheduler.queue(audioTrack);
                textChannel.sendMessageEmbeds(new EmbedBuilder()
                        .setColor(Color.RED)
                        .setDescription("Queued: **" + audioTrack.getInfo().title
                                + "** (" + getVideoDuration(audioTrack.getDuration()) + ")")
                        .build()).queue();
            }

            // YOUTUBE SEARCH
            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                final List<AudioTrack> tracks = audioPlaylist.getTracks();

                if (!tracks.isEmpty()) {
                    musicManager.scheduler.queue(tracks.get(0));
                    textChannel.sendMessageEmbeds(new EmbedBuilder()
                            .setColor(Color.RED)
                            .setDescription("Queued: **" + tracks.get(0).getInfo().title
                                    + "** (" + getVideoDuration(tracks.get(0).getDuration()) + ")")
                            .build()).queue();
                }
            }

            @Override
            public void noMatches() {
                textChannel.sendMessageEmbeds(new EmbedBuilder()
                        .setColor(Color.RED)
                        .setDescription("An error occurred!")
                        .build()).queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                textChannel.sendMessageEmbeds(new EmbedBuilder()
                        .setColor(Color.RED)
                        .setDescription("An error occurred!")
                        .build()).queue();
            }
        });
    }


    // Skip the current track
    public void Skip(TextChannel textChannel) {
        final GuildMusicManager musicManager = this.getMusicManager(textChannel.getGuild());
        musicManager.scheduler.nextTrack();
        textChannel.sendMessageEmbeds(new EmbedBuilder()
                .setColor(Color.RED)
                .setDescription("Song skipped... now playing: **" + musicManager.scheduler.audioPlayer.getPlayingTrack().getInfo().title + "**")
                .build()).queue();
    }


    // Get the current playing song
    public void getSong(TextChannel textChannel) {
        final GuildMusicManager musicManager = this.getMusicManager(textChannel.getGuild());
        AudioTrack track = musicManager.scheduler.audioPlayer.getPlayingTrack();
        if (track != null) {

            int totalSec = getSec(track.getDuration()) + getMin(track.getDuration()) * 60;
            int currentSec = getSec(track.getPosition()) + getMin(track.getPosition()) * 60;

            double percent = (double) currentSec / totalSec;

            String bar = createProgressBar(percent);

            textChannel.sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setTitle("Currently playing \uD83C\uDFB5")
                    .setDescription("**" + track.getInfo().title
                            + "**\n" + track.getInfo().uri
                            + "\n" + bar + " (" + getVideoDuration(track.getPosition())
                            + "/" + getVideoDuration(track.getDuration()) + ")")
                    .build()).queue();

        } else {
            textChannel.sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setDescription("No song is currently playing!")
                    .build()).queue();
        }
    }


    // Get the track queue
    public void getQueue(TextChannel textChannel) {
        final GuildMusicManager musicManager = this.getMusicManager(textChannel.getGuild());

        AudioTrack currentTrack = musicManager.scheduler.audioPlayer.getPlayingTrack();

        if (currentTrack == null) {
            textChannel.sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setDescription("Queue is currently empty!")
                    .build()).queue();
            return;
        }

        StringBuilder sb = new StringBuilder()
                .append("**>** 1. **")
                .append(currentTrack.getInfo().title)
                .append("** (")
                .append(getVideoDuration(currentTrack.getPosition()))
                .append("/")
                .append(getVideoDuration(currentTrack.getDuration()))
                .append(")\n");

        Object[] queue = musicManager.scheduler.queue.toArray();
        for (int i = 0; i < queue.length; i++) {
            AudioTrack track = (AudioTrack) queue[i];
            sb.append((i+2))
                    .append(". **")
                    .append(track.getInfo().title)
                    .append("** (")
                    .append(getVideoDuration(track.getDuration()))
                    .append(")\n");
        }

        textChannel.sendMessageEmbeds(new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Playlist \uD83C\uDFB5")
                .setDescription(sb.toString())
                .build()).queue();
    }


    // Skips the current song and clears the track queue
    public void clearQueue(TextChannel textChannel) {
        final GuildMusicManager musicManager = this.getMusicManager(textChannel.getGuild());
        List<AudioTrack> drainedQueue = new ArrayList<>();
        musicManager.scheduler.queue.drainTo(drainedQueue);
        musicManager.scheduler.nextTrack();
    }


    // Delete song in # queue
    public void deleteSong(TextChannel textChannel, int num) {
        final GuildMusicManager musicManager = this.getMusicManager(textChannel.getGuild());
        if (num == 1) {
            textChannel.sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setDescription("**" + musicManager.scheduler.audioPlayer.getPlayingTrack().getInfo().title + "** has been removed from the queue.")
                    .build()).queue();
            musicManager.scheduler.nextTrack();
        }
        else {
            AudioTrack track = (AudioTrack) musicManager.scheduler.queue.toArray()[num - 2];

            musicManager.scheduler.queue.remove(musicManager.scheduler.queue.toArray()[num - 2]);

            textChannel.sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setDescription("**" + track.getInfo().title + "** has been removed from the queue.")
                    .build()).queue();
        }
    }


    // Get the current instance of the PlayerManager
    public static PlayerManager getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }


    // Parses the getDuration() to minutes and seconds (MM:SS)
    public String getVideoDuration(Long l) {
        int sec = getSec(l);
        int min = getMin(l);

        if (sec < 10) {
            return min + ":0" + sec;
        } else {
            return min + ":" + sec;
        }
    }


    public int getSec(Long l) {
        return (int) (l / 1000) % 60;
    }


    public int getMin(Long l) {
        return (int) (l / (1000 * 60)) % 60;
    }

    public String createProgressBar(double percent) {
        StringBuilder bar = new StringBuilder().append("**[**");
        for (int i = 0; i < Math.round((percent * 100) / 10); i++) {
            bar.append("===");
        }
        for (int i = 0; i < 10 - Math.round((percent * 100) / 10); i++) {
            bar.append("-----");
        }
        bar.append("**]**");
        return bar.toString();
    }

}
