import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class Once {
    public static void main(String[] args) throws LoginException {
        JDABuilder.createDefault(Config.get("token"))
                .setActivity(Activity.listening("Twice"))
                .enableCache(CacheFlag.VOICE_STATE)
                .addEventListeners(new Commands())
                .build();
    }
}
