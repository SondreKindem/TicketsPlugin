package no.sonkin.ticketscore;

/**
 * Class that makes it easier to create socketsClient instances
 */
public class SocketsClientHelper {
    private final String token;
    private final String guild;

    public SocketsClientHelper(String token, String guild) {
        this.token = token;
        this.guild = guild;
    }

    /**
     * Get a configured SocketsClient ready to go!
     *
     * @return a client for doing socket stuff
     */
    public SocketsClient getClient() {
        return new SocketsClient(token, guild);
    }
}
