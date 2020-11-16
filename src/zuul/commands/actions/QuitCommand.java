package zuul.commands.actions;

import zuul.commands.Command;
import zuul.Game;
import zuul.GameText;
import zuul.gameState.characters.Character;
import zuul.gameState.characters.Player;

import java.util.ArrayList;

/**
 * A Quit {@link Command} for the "World of Zuul" application.
 * This command quits the application.
 * <p>
 * This command takes no modifier words.
 *
 * @author Timothy Shelton
 */
public class QuitCommand extends Command {

    /**
     * Constructor without modifiers.
     *
     * Initialises a {@link Command} with locale-dependent command word of the key "quit".
     */
    public QuitCommand() {
        super(GameText.getString("CommandWordsBundle", "quit"));
    }

    /**
     * Constructor with modifiers.
     *
     * Initialises a {@link Command} with locale-dependent command word of the key "quit".
     */
    public QuitCommand(ArrayList<String> modifiers) {
        super(GameText.getString("CommandWordsBundle", "quit"), modifiers);
    }

    /**
     * Quits the application if this {@link Command} has no modifier words and
     * the specified {@link Character} is a {@link Player}.
     *
     * @param character  the character that is trying to quit the application
     * @return true if the {@link Game} has been signalled to quit, false otherwise
     */
    @Override
    public boolean commandLogic(Character character) {
        if(!(character instanceof Player)) {
            //Only players can quit the game
            updateErr("notPlayer");
            return false;
        }

        if (hasModifiers()) {
            //Only quit if we're really sure
            updateErr("hasModifier");
            zuul.io.Out.println(GameText.getString("quitHasModifiersError"));
            return false;
        } else {
            Game.getInstance().finish(); // signal that we want to quit
            return true;
        }
    }
}
