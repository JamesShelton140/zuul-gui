package zuul.gameState.characters;

import zuul.*;
import zuul.commands.Command;
import zuul.gameState.Room;

/**
 * A user-controlled {@link Character} for the "World of Zuul" application.
 * <p>
 *
 * @author Timothy Shelton
 */
public class Player extends Character {
    /**
     * Constructor initialises the player as a {@link Character} with specified name and starting {@link Room}.
     *
     * @param name  the name of this Player
     * @param startingRoom  the room that this player should start in
     */
    public Player(String name, Room startingRoom) {
        super(name, startingRoom);
    }

    /**
     * Attempts to generate and process a {@link Command}.
     *
     * Does not return until a command has been successfully processed.
     */
    @Override
    public void act() {

        boolean commandProcessed = false;
        do {
            Command command = Game.getInstance().getParser().getCommand(getName());
            commandProcessed = processCommand(command);
        } while (!commandProcessed);
    }

}
