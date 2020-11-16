package zuul;

import zuul.io.userInterfaces.UserInterface;

/**
 * The interface to be used for the "World of Zuul" application.
 * <p>
 * Enables static access to the active {@link UserInterface}.
 * For example: {@link zuul.io.userInterfaces.CommandLineInterface CommandLineInterface}
 *
 * @author Timothy Shelton
 */
public class GameInterface {
    /**
     * The currently active {@link UserInterface}.
     */
    private static UserInterface userInterface;

    /**
     * Sets the active {@link UserInterface} for the application.
     *
     * @param userInterface  the UserInterface to set to the application, not null
     */
    public static void set(UserInterface userInterface) {
        GameInterface.userInterface = userInterface;
    }

    /**
     * Gets the currently active {@link UserInterface}.
     *
     * @return the currently active user interface, not null
     */
    public static UserInterface get() {
        return userInterface;
    }
}
