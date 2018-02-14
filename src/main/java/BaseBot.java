import com.github.xaanit.d4j.oauth.Scope;
import com.github.xaanit.d4j.oauth.util.DiscordOAuthBuilder;
import io.vertx.core.http.HttpServerOptions;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.util.DiscordException;

/**
 * This represents a SUPER basic bot (literally all it does is login).
 * This is used as a base for all example bots.
 */
public class BaseBot {

    public static BaseBot INSTANCE; // Singleton instance of the bot.
    public IDiscordClient client; // The instance of the discord client.
    public static final String TOKEN = "I'm too lazy to get this from args";

    public static void main(String[] args) { // Main method
        INSTANCE = login(TOKEN); // Creates the bot instance and logs it in.
    }

    public BaseBot(IDiscordClient client) {
        this.client = client; // Sets the client instance to the one provided
    }

    public static BaseBot login(String token) {
        BaseBot bot = null; // Initializing the bot variable

        ClientBuilder builder = new ClientBuilder(); // Creates a new client builder instance
        builder.withToken(token); // Sets the bot token for the client

        try {
            IDiscordClient client = builder.login(); // Builds the IDiscordClient instance and logs it in
            bot = new BaseBot(client); // Creating the bot instance
            System.out.println("logged in");
            EventDispatcher dispatcher = client.getDispatcher(); // Gets the client's event dispatcher
            dispatcher.registerListener(new PongListener(client)); // Registers this bot as an event listener

        } catch (DiscordException e) { // Error occurred logging in
            System.err.println("Error occurred while logging in!");
            e.printStackTrace();
        }

        return bot;
    }
}