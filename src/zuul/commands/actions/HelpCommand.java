package zuul.commands.actions;

import zuul.*;
import zuul.gameState.characters.Character;
import zuul.commands.Command;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * A Help {@link Command} for the "World of Zuul" application.
 * This command prints a help message including a list of all valid command words.
 * <p>
 * This command takes no modifier words.
 *
 * @author Timothy Shelton
 */
public class HelpCommand extends Command {

    /**
     * Constructor without modifiers.
     *
     * Initialises a {@link Command} with locale-dependent command word of the key "help".
     */
    public HelpCommand() {
        super(GameText.getString("CommandWordsBundle", "help"));
    }

    /**
     * Constructor with modifiers.
     *
     * Initialises a {@link Command} with locale-dependent command word of the key "help".
     */
    public HelpCommand(ArrayList<String> modifiers) {
        super(GameText.getString("CommandWordsBundle", "help"));
    }

    /**
     * Prints a help message to the user that includes a list of all valid {@link Command} words.
     * <p>
     *     Allows the {@link Character} asking for help to act on another command before returning.
     * </p>
     *
     * @param character the character that is asking for help
     * @return true always
     */
    @Override
    public boolean commandLogic(Character character) {
        //print the Standard help message
        zuul.io.Out.println(GameText.getString("help_ln1"));
        zuul.io.Out.println(GameText.getString("help_ln2"));
        zuul.io.Out.println();
        zuul.io.Out.println(GameText.getString("helpCommandWordsDisplay"));

        //Get the command words from the CommandWords resource bundle for the current locale and print them
        zuul.io.Out.println(GameText.getCommandWords().stream()
                .map(key -> GameText.getString("CommandWordsBundle", key))
                .sorted(Comparator.comparing(String::toString, GameText.getCollator())) //sort the command words alphabetically by locale.
                .collect(Collectors.joining(", "))
        );
        zuul.io.Out.println();

        //allow character to perform another action
        character.act();

        return true;
    }
}
