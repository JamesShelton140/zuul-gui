package zuul.commands.actions;

import zuul.*;
import zuul.gameState.characters.Character;
import zuul.commands.Command;
import zuul.gameState.Room;

import java.util.*;

/**
 * A Look {@link Command} for the "World of Zuul" application.
 * This command prints the state of the current {@link Room} of a {@link Character}.
 * <p>
 * This command takes no modifier words.
 *
 * @author Timothy Shelton
 */
public class LookCommand extends Command {

    /**
     * Constructor without modifiers.
     *
     * Initialises a {@link Command} with locale-dependent command word of the key "look".
     */
    public LookCommand() {
        super(GameText.getString("CommandWordsBundle", "look"));
    }

    /**
     * Constructor with modifiers.
     *
     * Initialises a {@link Command} with locale-dependent command word of the key "look".
     */
    public LookCommand(ArrayList<String> modifiers) {
        super(GameText.getString("CommandWordsBundle", "look"));
    }

    /**
     * Prints the state information of the current {@link Room} of the specified {@link Character}.
     * <p>
     *     Allows the {@link Character} looking around its room to act on another command before returning.
     * </p>
     *
     * @param character the Character looking around its current room
     * @return true always
     */
    @Override
    public boolean commandLogic(Character character) {
        character.getCurrentRoom().printInfo(); //look around the room
        //character.act(); //allow character to perform another action
        return true;
    }

    @Override
    public Optional<Map<Integer, List<String>>> getPossibleModifiers(zuul.gameState.maps.Map map) {
        return Optional.of(new HashMap<>());
    }

    @Override
    public boolean isValidForPlayer(zuul.gameState.maps.Map map) {
        return true;
    }
}
