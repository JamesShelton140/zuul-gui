package zuul.gameState.characters;

import zuul.gameState.Room;

/**
 * An AI-controlled {@link Character} for the "World of Zuul" application.
 * <p>
 * This class is a skeleton. It implements no behaviour.
 * <p>
 * There are comments suggesting how the {@link #act()} method could be implemented using an "npcBehaviour" class
 * that represents the algorithm that determines how the NPC will act.
 *
 * @author Timothy Shelton
 */
public class NPC extends Character {
    //private npcBehaviour = behaviour;

    /**
     * Constructor
     *
     * @param name  the name of this NPC
     * @param startingRoom  the {@link Room} this NPC will start in
     */
    public NPC(String name, Room startingRoom) {
        super(name, startingRoom);
    }

    /**
     * The NPC performs an action by getting a command from its npcBehaviour and processing it.
     * Method returns regardless of outcome.
     */
    @Override
    public void act() {
        //Command command = behaviour.getNext();
        //processCommand(command);
    }
}
