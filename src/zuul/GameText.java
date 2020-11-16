package zuul;

import java.text.Collator;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * A helper class representing the locale of the "World of Zuul" application.
 * <p>
 * This class stores the locale for the running instance of the application.
 * Handles retrieval and formatting of locale-dependent text from resource bundles.
 * Handles retrieval of locale-independent command words for the application.
 * <p>
 * Structure for helper class adapted from Utku Özdemir on stack overflow:
 * https://stackoverflow.com/questions/19686476/java-internationalization-do-i-have-to-load-a-resource-bundle-for-every-class
 *
 * @author Tiomthy Shelton
 */
public class GameText {

    /**
     * The locale of the application.
     */
    private static Locale locale;

    /**
     * A Collator of this locale.
     */
    private static Collator localeCollator;

    /**
     * A formatter to be used to format locale-dependent text.
     */
    private static MessageFormat formatter = new MessageFormat("");

    /**
     * The package directory of resource bundles for this application.
     */
    private static final String BUNDLE_DIR = "zuul.resourceBundles.";

    /**
     * Sets the locale of this application.
     *
     * @param localeToSet  the locale to set this application to, not null
     */
    public static void setLocale(Locale localeToSet) {
        locale = localeToSet;
        localeCollator = Collator.getInstance(locale); //locale specific collator for string comparison
        formatter.setLocale(locale); //set the formatter to the application locale
    }

    /**
     * Gets the locale-dependent String for the specified key from the standard resource bundle
     * ("GameTextBundle") for the current locale.
     * <p>
     * This method throws a {@code java.util.MissingResourceException} if no String for the given key can be found.
     *
     * @param key  the locale-independent key corresponding to the locale-dependent String, not null
     * @return the locale-dependent String corresponding to the key, not null
     * @throws java.util.MissingResourceException  if no String for the given key can be found
     */
    public static String getString(String key) {
        return GameText.getString("GameTextBundle", key);
    }

    /**
     * Gets the locale-dependent String for the specified key from the standard resource bundle
     * ("GameTextBundle") for the current locale and formats it with the specified arguments.
     * <p>
     * This method throws a {@code java.util.MissingResourceException} if no String for the given key can be found.
     *
     * @param key  the locale-independent key corresponding to the locale-dependent String, not null
     * @param arguments  the arguments to be used in formatting the locale-dependent String, not null
     * @return the locale-dependent String corresponding to the key formatted with the specified arguments, not null
     * @throws java.util.MissingResourceException  if no String for the given key can be found
     */
    public static String getString(String key, Object[] arguments) {
        formatter.applyPattern(GameText.getString(key));
        return formatter.format(arguments);
    }

    /**
     * Gets the locale-dependent String for the specified key from the specified resource bundle for the current locale.
     * <p>
     * This method throws a {@code java.util.MissingResourceException} if no String for the given key can be found.
     *
     * @param bundle  the resource bundle to search for the key, not null
     * @param key  the locale-independent key corresponding to the locale-dependent String, not null
     * @return the locale-dependent String corresponding to the key, not null
     * @throws java.util.MissingResourceException  if no String for the given key can be found
     */
    public static String getString(String bundle, String key) {
        return ResourceBundle.getBundle(BUNDLE_DIR +bundle, locale).getString(key);
    }

    /**
     * Gets the locale-independent command words from the CommandWords resource bundle for the current locale.
     *
     * @return the set of locale-independent command words from the CommandWords resource bundle for the current locale, not null
     */
    public static Set<String> getCommandWords() {
        return ResourceBundle.getBundle("zuul.resourceBundles.CommandWordsBundle", locale).keySet();
    }

    /**
     * Gets the locale-specific Collator for sorting locale-dependent Strings.
     *
     * @return the locale-specific Collator for the current locale, not null
     */
    public static Collator getCollator() {
        return GameText.localeCollator;
    }
}
