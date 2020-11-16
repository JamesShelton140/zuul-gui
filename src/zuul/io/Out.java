package zuul.io;

import zuul.GameInterface;

/**
 * Output controller class for the "World of Zuul" application.
 * <p>
 * This class takes output requests from the {@link zuul.Game} and delegates them to the {@link GameInterface}.
 *
 * @author Timothy Shelton
 */
public class Out {

    /**
     * Prints the given string to the {@link GameInterface}.
     *
     * @param str  the string to be printed to the user
     */
    public static void print(String str) {
        GameInterface.get().print(str);
    }

    /**
     * Prints {@code .toString} for the specified object to the {@link GameInterface}.
     *
     * @param obj  the object whose {@code .toString} is to be printed to the user
     */
    public static void print(Object obj) {
        print(obj.toString());
    }

    /**
     * Prints the specified string with a line break following to the {@link GameInterface}.
     *
     * @param str  the string to be printed to the user
     */
    public static void println(String str) {
        print(str);
        println();
    }

    /**
     * Prints {@code .toString} for the specified object with a line break following to the {@link GameInterface}.
     *
     * @param obj  the object whose {@code .toString} is to be printed to the user
     */
    public static void println(Object obj) {
        print(obj);
        println();
    }

    /**
     * Print a line break to the {@link GameInterface} by calling {@link #nextln()}.
     */
    public static void println() {
        nextln();
    }

    /**
     * Print a line break to the {@link GameInterface}.
     */
    public static void nextln() {
        GameInterface.get().printNextln();
    }
}
