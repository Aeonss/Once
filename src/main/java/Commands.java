import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Commands extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent e) {

        String[] args = e.getMessage().getContentRaw().split(" ");

        Random r = new Random();

        switch (args[0].toLowerCase()) {

            /////////////////////
            //      HELP       //
            /////////////////////
            case "$help":
                e.getTextChannel().sendMessageEmbeds(new EmbedBuilder()
                        .setColor(Color.RED)
                        .setTitle("ONCE")
                                .addField("", "---------------------------------", false)
                                .addField("$twice", "Plays a random Twice song", false)
                                .addField("$twice5", "Plays 5 random Twice songs", false)
                                .addField("$play", "Plays music from a Youtube URL", false)
                                .addField("$skip", "Skips the current song", false)
                                .addField("$song", "Displays the current playing song", false)
                                .addField("$queue", "Displays the music queue", false)
                                .addField("$stop", "Stops the player and clears the playlist", false)
                                .addField("$clear", "Clears the playlist", false)
                                .addField("$remove", "Removes a song from the playlist", false)
                                .addField("", "---------------------------------", false)
                                .addField("$idol", "Shows a random Twice image", false)
                                .addField("$fact", "Gives a random Twice fact", false)
                                .addField("$agra", "Agra's favorite Twice song", false)
                                .addField("$rose", "Rose's favorite Twice song", false)
                                .addField("$basic", "Basic's favorite Twice song", false)
                                .addField("$mars", "Mars's favorite Twice song", false)
                                .addField("$squad", "Plays the squad's favorite Twice songs", false)
                                .addField("", "---------------------------------", false)
                                .addField("$8ball","Roll the 8 ball", false)
                                .addField("$sus","Get the sussyness of a user", false)
                                .addField("$vcsus","Get the sussyness of the vc", false)
                                .addField("$cute","See how cute someone is", false)
                                .addField("$cap","See how much someone is capping", false)
                                .addField("$roll","Roll an n sided side (default = 6)", false)
                                .addField("", "---------------------------------", false)
                                .addField("", "Developed by Agra with JDA and Lavaplayer", true)
                        .setThumbnail(e.getJDA().getSelfUser().getAvatarUrl())
                        .build()).queue();
                break;

            /////////////////////////////
            //      PLAY / TWICE       //
            /////////////////////////////
            case "$play":
            case "$twice":
            case "$twice5":
            case "$agra":
            case "$rose":
            case "$basic":
            case "$mars":
            case "$squad":
                if (!e.getMember().getVoiceState().inAudioChannel()) {
                    e.getTextChannel().sendMessageEmbeds(new EmbedBuilder()
                            .setColor(Color.RED)
                            .setDescription("You need to be in a voice channel to play music!")
                            .build()).queue();
                    return;
                }

                final AudioManager audioManager = e.getGuild().getAudioManager();
                final VoiceChannel memberChannel = (VoiceChannel) e.getMember().getVoiceState().getChannel();

                audioManager.openAudioConnection(memberChannel);

                if (args[0].equalsIgnoreCase("$play")) {
                    if (args.length < 2) {
                        e.getTextChannel().sendMessageEmbeds(new EmbedBuilder()
                                .setColor(Color.RED)
                                .setDescription("$play <URL>")
                                .build()).queue();
                        return;
                    }
                    else {
                        if (isURL(args[1])) {
                            PlayerManager.getINSTANCE().loadAndPlay(e.getTextChannel(), args[1]);
                        }
                        else {
                            PlayerManager.getINSTANCE().loadAndPlay(e.getTextChannel(), "ytsearch:"
                                    + e.getMessage().getContentRaw().replace("$play ", ""));
                        }
                    }
                }
                else if (args[0].equals("$twice5")) {
                    for (int i = 0; i < 5; i++) {
                        PlayerManager.getINSTANCE().loadAndPlay(e.getTextChannel(), randomLine("songlist.txt"));
                    }
                }

                // Perfect World
                else if (args[0].equals("$agra")) {
                    PlayerManager.getINSTANCE().loadAndPlay(e.getTextChannel(), "https://www.youtube.com/watch?v=fmOEKOjyDxU");
                }

                // Moonlight
                else if (args[0].equals("$rose")) {
                    PlayerManager.getINSTANCE().loadAndPlay(e.getTextChannel(), "https://www.youtube.com/watch?v=Q8nCjQCvWD4");
                }

                // The Feels
                else if (args[0].equals("$basic")) {
                    PlayerManager.getINSTANCE().loadAndPlay(e.getTextChannel(), "https://www.youtube.com/watch?v=Vb8ITQRe3es");
                }

                // Cry for Me
                else if (args[0].equals("$mars")) {
                    PlayerManager.getINSTANCE().loadAndPlay(e.getTextChannel(), "https://www.youtube.com/watch?v=FF50-LY2Kro");
                }

                // Add the squad's favorite Twice songs
                else if (args[0].equals("$squad")) {
                    e.getTextChannel().sendMessageEmbeds(new EmbedBuilder()
                            .setColor(Color.RED)
                            .setDescription("Queuing the Squad's favorite Twice songs...")
                            .build()).queue();

                    List<String> bestSongs = new ArrayList<>();
                    bestSongs.add("https://www.youtube.com/watch?v=fmOEKOjyDxU");
                    bestSongs.add("https://www.youtube.com/watch?v=Q8nCjQCvWD4");
                    bestSongs.add("https://www.youtube.com/watch?v=Vb8ITQRe3es");
                    bestSongs.add("https://www.youtube.com/watch?v=FF50-LY2Kro");
                    Collections.shuffle(bestSongs);
                    for (String bestSong : bestSongs) {
                        PlayerManager.getINSTANCE().loadAndPlay(e.getTextChannel(), bestSong);
                    }
                }

                else {
                    PlayerManager.getINSTANCE().loadAndPlay(e.getTextChannel(), randomLine("songlist.txt"));
                }

                break;



            /////////////////////
            //      SKIP       //
            /////////////////////
            case "$skip":
                PlayerManager.getINSTANCE().Skip(e.getTextChannel());
                break;



            /////////////////////
            //      SONG       //
            /////////////////////
            case "$song":
            case "$s":
                PlayerManager.getINSTANCE().getSong(e.getTextChannel());
                break;



            //////////////////////
            //      QUEUE       //
            //////////////////////
            case "$queue":
            case "$list":
            case "$q":
                PlayerManager.getINSTANCE().getQueue(e.getTextChannel());
                break;



            ////////////////////
            //      STOP      //
            ////////////////////
            case "$stop":
            case "$leave":
            case "$quit":
                e.getTextChannel().sendMessageEmbeds(new EmbedBuilder()
                        .setColor(Color.RED)
                        .setDescription("Left  " + e.getGuild().getAudioManager().getConnectedChannel().getName())
                        .build()).queue();
                e.getGuild().getAudioManager().closeAudioConnection();
                PlayerManager.getINSTANCE().clearQueue(e.getTextChannel());
                break;


            /////////////////////
            //      CLEAR      //
            /////////////////////
            case "$clear":
                PlayerManager.getINSTANCE().clearQueue(e.getTextChannel());

                e.getTextChannel().sendMessageEmbeds(new EmbedBuilder()
                        .setColor(Color.RED)
                        .setDescription("Cleared Queue")
                        .build()).queue();
                break;


            //////////////////////
            //      REMOVE      //
            //////////////////////
            case "$delete":
            case "$remove":
            case "$r":
                if (args.length < 2) {
                    e.getTextChannel().sendMessageEmbeds(new EmbedBuilder()
                            .setColor(Color.RED)
                            .setDescription("$r <song #>")
                            .build()).queue();
                    return;
                }
                int num = Integer.parseInt(args[1]);
                PlayerManager.getINSTANCE().deleteSong(e.getTextChannel(), num);
                break;


            /////////////////////
            //      IDOL       //
            /////////////////////
            case "$idol":
                String[] idols = {"Tzuyu", "Momo", "Nayeon", "Sana", "Mina", "Jeongyeon", "Jihyo", "Dahyun", "Twice"};

                e.getTextChannel().sendMessageEmbeds(new EmbedBuilder()
                        .setColor(Color.RED)
                        .setImage(getImage(idols[r.nextInt(idols.length)]))
                        .build()).queue();
                break;



            /////////////////////
            //      FACT       //
            /////////////////////
            case "$fact":
                e.getTextChannel().sendMessageEmbeds(new EmbedBuilder()
                        .setColor(Color.RED)
                        .setTitle("Twice Facts")
                        .setDescription(randomLine("facts.txt"))
                        .setThumbnail(getImage("twice"))
                        .build()).queue();
                break;


            /////////////////////
            //      BDAY       //
            /////////////////////
            case "$birthday":
            case "$bday":
                List<Member> memberList= e.getMessage().getMentions().getMembers();
                if (args.length < 2 || memberList.size() == 0) {
                    e.getTextChannel().sendMessageEmbeds(new EmbedBuilder()
                            .setColor(Color.RED)
                            .setDescription("$bday <user>")
                            .build()).queue();
                    return;
                }
                else {
                    Member mem = memberList.get(0);
                    e.getTextChannel().sendMessageEmbeds(new EmbedBuilder()
                            .setColor(Color.RED)
                            .setTitle("\uD83C\uDF89 HAPPY BIRTHDAY \uD83C\uDF89")
                            .setThumbnail(mem.getEffectiveAvatarUrl())
                            .addField("", "ur a cutie ❤ " + mem.getAsMention(), false)
                            .addField("", randomLine("bday-quotes.txt"), false)
                            .build()).queue();

                }
                break;



            /////////////////////
            //      8BALL      //
            /////////////////////
            case "$8ball":
            case "$8":

                if (args.length < 2) {
                    e.getTextChannel().sendMessageEmbeds(new EmbedBuilder()
                            .setColor(Color.RED)
                            .setDescription("$8 <question>")
                            .build()).queue();
                    return;
                }

                String[] choices = {"It is certain.", " It is decidedly so.", "Without a doubt.", "Yes definitely.",
                        "You may rely on it.","Reply hazy, try again.", "Ask again later.", "Better not tell you now.",
                        "Cannot predict now.", "Concentrate and ask again.", "Don't count on it.", "My reply is no.",
                        "My sources say no.", "Outlook not so good.", "Very doubtful."};

                e.getTextChannel().sendMessageEmbeds(new EmbedBuilder()
                        .setColor(Color.RED)
                        .setDescription(choices[r.nextInt(choices.length)])
                        .build()).queue();

            ///////////////////
            //      SUS      //
            ///////////////////
            case "$sus":
                List<Member> memberList2 = e.getMessage().getMentions().getMembers();
                if (args.length < 2 || memberList2.size() == 0) {
                    e.getTextChannel().sendMessageEmbeds(new EmbedBuilder()
                            .setColor(Color.RED)
                            .setDescription("$sus <user>")
                            .build()).queue();
                    return;
                }

                int sus = r.nextInt(101);

                e.getTextChannel().sendMessageEmbeds(new EmbedBuilder()
                        .setColor(Color.RED)
                        .setTitle("Sussyness of " + memberList2.get(0).getNickname() + " \uD83D\uDC40")
                        .setDescription(createProgressBar(sus) + " " + sus + "%")
                        .build()).queue();
                break;



            case "$cute":
                List<Member> memberList3 = e.getMessage().getMentions().getMembers();
                if (args.length < 2 || memberList3.size() == 0) {
                    e.getTextChannel().sendMessageEmbeds(new EmbedBuilder()
                            .setColor(Color.RED)
                            .setDescription("$cute <user>")
                            .build()).queue();
                    return;
                }

                int cute = r.nextInt(101);

                e.getTextChannel().sendMessageEmbeds(new EmbedBuilder()
                        .setColor(Color.RED)
                        .setTitle("Cuteness of " + memberList3.get(0).getNickname() + " ♥")
                        .setDescription(createProgressBar(cute) + " " + cute + "%")
                        .build()).queue();
                break;



            case "$cap":
                List<Member> memberList4 = e.getMessage().getMentions().getMembers();
                if (args.length < 2 || memberList4.size() == 0) {
                    e.getTextChannel().sendMessageEmbeds(new EmbedBuilder()
                            .setColor(Color.RED)
                            .setDescription("$cap <user>")
                            .build()).queue();
                    return;
                }

                int cap = r.nextInt(101);

                e.getTextChannel().sendMessageEmbeds(new EmbedBuilder()
                        .setColor(Color.RED)
                        .setTitle("Cap Meter of " + memberList4.get(0).getNickname() + " \uD83E\uDDE2")
                        .setDescription(createProgressBar(cap) + " " + cap + "%")
                        .build()).queue();
                break;


            /////////////////////
            //      VCSUS      //
            /////////////////////
            case "$vcsus":
                if (!e.getGuild().getAudioManager().isConnected()) {
                    e.getTextChannel().sendMessageEmbeds(new EmbedBuilder()
                            .setColor(Color.RED)
                            .setDescription("I'm not currently in a voice channel...")
                            .build()).queue();
                    return;
                }

                if (e.getGuild().getAudioManager().getConnectedChannel().getMembers().size() == 0) {
                    e.getTextChannel().sendMessageEmbeds(new EmbedBuilder()
                            .setColor(Color.RED)
                            .setDescription("There's no one in the voice channel...")
                            .build()).queue();
                    return;
                }

                EmbedBuilder eb = new EmbedBuilder().setColor(Color.RED).setTitle("Sussyness of everyone in VC");

                List<Member> memberList5 = e.getGuild().getAudioManager().getConnectedChannel().getMembers();
                for (Member member : memberList5) {
                    if (!member.getUser().isBot()) {
                        int sus2 = r.nextInt(100);
                        eb.addField(member.getNickname() + "", createProgressBar(sus2) + " " + sus2 + "%", false);
                    }
                }
                e.getTextChannel().sendMessageEmbeds(eb.build()).queue();

                break;

            ////////////////////
            //      ROLL      //
            ////////////////////
            case "$roll":
                int sides = 6;
                if (args.length == 2) {
                    sides = Integer.parseInt(args[1]);
                }

                e.getTextChannel().sendMessageEmbeds(new EmbedBuilder()
                        .setColor(Color.RED)
                        .setDescription("You rolled a " + (r.nextInt(sides)+1))
                        .build()).queue();
                break;



            default:
                break;
        }
    }


    // Gets a random line from a local .txt file
    private String randomLine(String file) {
        InputStream in = getClass().getClassLoader().getResourceAsStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        List<String> lines = new ArrayList<>();
        try {
            for (String line; (line = br.readLine()) != null; ) {
                lines.add(line);
            }
        } catch (IOException ex) {
            System.out.println("Cannot read line");
        }

        Random r = new Random();
        return lines.get(r.nextInt(lines.size()));
    }

    // Gets a random image from the kpop imageboard
    private String getImage(String name) {
        Document doc;

        try {
            Random r = new Random();
            String url = "https://kpop.asiachan.com/" + name + "?d=1&t=0&s=fav";
            doc = Jsoup.connect(url).get();
            Elements img = doc.select("img");

            // Gets a random image from the image board HTML
            String image = img.get(r.nextInt(img.size())).attr("src");
            while (image.contains("download")) {
                image = img.get(r.nextInt(img.size())).attr("src");
            }

            return image;

        } catch (IOException ex) {
            return null;
        }
    }

    private boolean isURL(String url) {
        try {
            new URL(url);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private String createProgressBar(double percent) {
        StringBuilder bar = new StringBuilder().append("**[**");
        for (int i = 0; i < percent / 10; i++) {
            bar.append("===");
        }
        for (int i = 0; i < 10 - percent / 10; i++) {
            bar.append("-----");
        }
        bar.append("**]**");
        return bar.toString();
    }

}
