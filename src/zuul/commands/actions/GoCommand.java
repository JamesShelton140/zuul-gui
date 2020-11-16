package zuul.commands.actions;

import zuul.*;
import zuul.gameState.characters.Character;
import zuul.commands.Command;
import zuul.gameState.Room;

import java.util.ArrayList;
import java.util.Optional;

/**
 * A Go {@link Command} for the "World of Zuul" application.
 * This command moves a {@link Character} to a {@link Room} connected to its current room by an exit.
 * <p>
 * This command takes a single modifier word:
 * <li>
 *     The direction of the exit.
 * </li>
 *
 * @author Timothy Shelton
 */
public class GoCommand extends Command {

    /**
     * Constructor
     *
     * Initialises a {@link Command} with locale-dependent command word of the key "go".
     */
    public GoCommand(ArrayList<String> modifiers) {
        super(GameText.getString("CommandWordsBundle", "go"), modifiers);
    }

    /**
     * Tries to move the specified {@link Character} to a new {@link Room} linked to the character's current room
     * by the exit in the direction specified as the first modifier word of this {@link Command}.
     *
     * @param character the character that is trying to move
     * @return true if the character is moved successfully, false otherwise.
     */
    @Override
    public boolean commandLogic(Character character) {

        Optional<String> opDirection = getModifier(0);

        if (opDirection.isEmpty()) {
            // if there is no modifier, we don't know where to go...
            updateErr("noModifier");
            zuul.io.Out.println(GameText.getString("goHasNoModifiersError"));
            return false;
        }

        String direction = opDirection.get();

        // Try to leave current room.
        Optional<Room> opNextRoom = character.getCurrentRoom().getExit(direction);

        if (opNextRoom.isEmpty()) {
            //Cannot go in a direction if there is no exit
            updateErr("noExit");
            zuul.io.Out.println(GameText.getString("goNoExitError"));
            return false;
        } else {
            //Exit room exists so unwrap it
            Room nextRoom = opNextRoom.get();

            character.moveRoom(nextRoom); //Move character to next room

            //tell the player the command was successful
            zuul.io.Out.println(GameText.getString("goSuccessful", new Object[]{direction}));
            zuul.io.Out.println();

            nextRoom.printInfo(); //look around the new room
            return true;
        }
    }
}
