package zuul.io.userInterfaces;

/**
 * The root interface of all user interfaces for the World of Zuul game engine.
 * <p>
 * A UserInterface represents all direct interactions with the player.
 * <p>
 * Such as:
 * <li>How the game state is displayed to the player</li>
 * <li>How input requested by the game engine is retrieved from the player</li>
 *
 * @author Timothy Shelton
 */

public interface UserInterface {

    /**
     * Prepares this UserInterface for the event immediately following this method call.
     * <p>
     * Behaviour of {@link #print}, {@link #printNextln()}, and {@link #getNextLine()} may be changed by this method
     * either directly or indirectly.
     * <p>
     * Event strings are formed by joining several "tags" delimited by a single space character:
     * <ul><li>
     *     "general class/interface name" "general event" "event information"
     * </li></ul>
     * <p>
     * Only the first two tags must be included.
     * Multiple "event information" tags may be included.
     * <p>
     * Examples:
     * <ul><li>
     *     game nextRound
     * </li>
     * <li>
     *     command error go noExit
     * </li></ul>
     * <p>
     * Event parameter can be ignored to preserve UserInterface behaviour regardless of event.
     *
     * @param event  a string that describes the event that immediately follows this update, not null
     */
    void update(String event);

    /**
     * Handles requests to print str to the player.
     *
     * @param str  the string requested to be printed to the player, not null
     */
    void print(String str);

    /**
     * Handles requests to direct {@link #print} to a new line.
     * <p>
     * For example {@link CommandLineInterface} could implement this as {@code System.out.println()}.
     */
    void printNextln();

    /**
     * Handles requests for player input.
     *
     * @return  the input retrieved from the player
     */
    String getNextLine();
}
