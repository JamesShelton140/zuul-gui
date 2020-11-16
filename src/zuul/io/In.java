package zuul.io;

import zuul.GameInterface;

/**
 * Input controller class for the "World of Zuul" application.
 * <p>
 * This class takes input requests from the {@link zuul.Game} and delegates them to the {@link GameInterface}.
 *
 * @author Timothy Shelton
 */
public class In {

    /**
     * Gets the next line of input from the {@link GameInterface}.
     *
     * @return the next line of input from the GameInterface
     */
    public static String nextLine() {
        return GameInterface.get().getNextLine();
    }
}
