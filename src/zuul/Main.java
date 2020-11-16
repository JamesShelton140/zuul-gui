package zuul;

import zuul.io.userInterfaces.CommandLineInterface;
import zuul.io.userInterfaces.UserInterface;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Handles setting the locale and {@link zuul.io.userInterfaces.UserInterface UserInterface}
 * and initialises the UserInterface.
 *
 * @author rej
 * @author Timothy Shelton
 */
public class Main {

    //Locale class variables
    /**
     * The program argument that signifies language and country codes for the desired locale follow.
     * <p></p>
     * The language code must immediately follow this argument.
     * <p>
     * The country code must immediately follow the language code.
     */
    private static final String LOCALE_KEY_WORD = "locale";
    /**
     * The default locale language code.
     * <p></p>
     * Default value: {@value DEFAULT_LANGUAGE}
     */
    private static final String DEFAULT_LANGUAGE = "en";
    /**
     * The default locale country code.
     * <p></p>
     * Default value: {@value DEFAULT_COUNTRY}
     */
    private static final String DEFAULT_COUNTRY = "GB";

    //UserInterface class variables
    /**
     * The program argument that signifies the name of the desired
     * {@link zuul.io.userInterfaces.UserInterface UserInterface} follows.
     * <p></p>
     * The name must immediately follow this argument.
     */
    private static final String INTERFACE_KEY_WORD = "interface";
    /**
     * The default {@link zuul.io.userInterfaces.UserInterface UserInterface} name.
     * <p></p>
     * Default value: {@value DEFAULT_INTERFACE}
     */
    private static final String DEFAULT_INTERFACE = "CommandLine";
    /**
     * The name of the {@link zuul.io.userInterfaces.UserInterface UserInterface} to be initialised.
     */
    private static String interfaceName = DEFAULT_INTERFACE;

    /**
     * Sets the locale for the game in {@link GameText}.
     * <p>
     * Sets a default value if no locale specified in program arguments.
     * <p></p>
     * Default value: ({@value DEFAULT_LANGUAGE}, {@value DEFAULT_COUNTRY})
     * <p></p>
     * @param args  the program arguments
     * @return  the program arguments with locale information removed if it existed
     */
    private static String[] setLocale(String[] args) {

        //convert args to List<String> for easier manipulation
        List<String> argsList = new ArrayList<String>(Arrays.asList(args));

        String language;
        String country;

        //Arg check adapted from java i18n trail: https://docs.oracle.com/javase/tutorial/i18n/intro/after.html
        if (argsList.contains(LOCALE_KEY_WORD) && (argsList.indexOf(LOCALE_KEY_WORD) + 2 <= argsList.size())) {
            //'locale' argument exists and at least 2 more arguments exist
            //So set get the 2 arguments following 'locale' and remove all three entries from the list
            int index = argsList.indexOf(LOCALE_KEY_WORD);
            country = new String(argsList.remove(index + 2));
            language = new String(argsList.remove(index + 1));
            argsList.remove(index);
        } else {
            //No locale specified so set default locale
            language = DEFAULT_LANGUAGE;
            country = DEFAULT_COUNTRY;
        }

        //set the locale
        GameText.setLocale(new Locale(language, country));

        //return the args without locale information
        return argsList.toArray(new String[0]);
    }


    /**
     * Sets the name of the desired {@link zuul.io.userInterfaces.UserInterface UserInterface} from program arguments.
     * <p>
     * Sets a default value if no UserInterface specified in program arguments.
     * <p></p>
     * Default value: {@value DEFAULT_INTERFACE}
     * <p></p>
     * @param args  the program arguments
     * @return  the program arguments with UserInterface information removed if it existed
     */
    private static String[] setUserInterface(String[] args) {
        //convert args to List<String> for easier manipulation
        List<String> argsList = new ArrayList<String>(Arrays.asList(args));

        //Arg check adapted from java i18n trail: https://docs.oracle.com/javase/tutorial/i18n/intro/after.html
        if (argsList.contains(INTERFACE_KEY_WORD) && (argsList.indexOf(INTERFACE_KEY_WORD) + 1 <= argsList.size())) {
            //'locale' argument exists and at least 2 more arguments exist
            //So set get the 2 arguments following 'locale' and remove all three entries from the list
            int index = argsList.indexOf(INTERFACE_KEY_WORD);
            interfaceName = new String(argsList.remove(index + 1));
            argsList.remove(index);
        } else {
            //No locale specified so set default locale
            interfaceName = DEFAULT_INTERFACE;
        }

        //return the args without locale information
        return argsList.toArray(new String[0]);
    }

    /**
     * Sets the locale from program arguments and initialises the {@link zuul.io.userInterfaces.UserInterface UserInterface}.
     *
     * @param args the program arguments
     */
    public static void main(String[] args) {

        //Set the locale for the game.
        args = setLocale(args);

        //Set the user interface
        args = setUserInterface(args);

        //Initialise the UserInterface to be used for the Game.
        try {
            Class.forName("zuul.io.userInterfaces." + interfaceName + "Interface").getMethod("main",String[].class).invoke(null, (Object) args);
        } catch (Exception e) {
            //if interface initialisation fails then initialise the command line interface
            CommandLineInterface.main(args);
        }
    }
}
