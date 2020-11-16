package zuul.commands;

import zuul.GameText;

import java.util.ArrayList;
import java.util.Optional;

/**
 * A factory to build {@link Command} objects for the "World of Zuul" application.
 *
 * @author Timothy Shelton
 */
public class CommandFactory {

    /**
     * Constructor does nothing
     */
    public CommandFactory() {
        //do nothing
    }

    /**
     * Creates a {@link Command} from commandWord String by building the class name using reflection.
     * <p>
     * All exceptions thrown are caught and treated as an unrecognised command word and an empty optional is returned.
     *
     * @param commandWord  the command word of the Command to create, not null
     * @param modifiers  the list of modifier words to instantiate the created Command with, not null
     * @return an Optional containing a Command object if one was successfully instantiated, otherwise an empty Optional
     */
    public Optional<Command> getCommand(String commandWord, ArrayList<String> modifiers) {
        try{
            //Get the english name of the command class from the resource bundle for this locale
            //If it is not found throw a NoSuchElementException
            String commandClassName = getCommandClassName(commandWord).orElseThrow();

            //If the commandClassName is found then use it to create an instance of that command
            return Optional.of((Command) Class
                    .forName("zuul.commands.actions." + commandClassName.substring(0, 1).toUpperCase() + commandClassName.substring(1).toLowerCase() + "Command")
                    .getConstructor(new Class<?>[]{ArrayList.class})
                    .newInstance(modifiers));
        } catch (Exception e) {
            //Catch all exceptions and return a "no command" optional
            return Optional.empty();
        }
    }

    /**
     * Gets the commandWord part of the simple class name of the Command specified
     * by the locale-dependant commandWord supplied.
     *
     * @param commandWord  the locale-dependent commandWord to get the Command Class name of, not null
     * @return  an Optional containing the commandWord part of the simple class name of the command
     *          corresponding to the supplied command word if it is found, otherwise an empty Optional
     */
    private Optional<String> getCommandClassName(String commandWord) {
        return GameText.getCommandWords().stream()
                .filter(key -> GameText.getString("CommandWordsBundle", key).equalsIgnoreCase(commandWord))
                .findAny();
    }

}
