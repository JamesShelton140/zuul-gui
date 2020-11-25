package zuul.commands.actions;

import java.util.*;

import zuul.*;
import zuul.commands.Command;
import zuul.gameState.characters.Character;
import zuul.gameState.Inventory;
import zuul.gameState.Item;
import zuul.gameState.Room;

/**
 * A Take {@link Command} for the "World of Zuul" application.
 * This command takes an {@link Item} from a {@link Room} {@link Inventory}.
 * <p>
 * This command takes a single modifier word:
 * <li>
 *     The name of the item to be taken.
 * </li>
 *
 * @author Timothy Shelton
 */
public class TakeCommand extends Command {

    /**
     * Constructor
     *
     * Initialises a {@link Command} with locale-dependent command word of the key "take".
     */
    public TakeCommand(ArrayList<String> modifiers) {
        super(GameText.getString("CommandWordsBundle", "take"), modifiers);
    }

    /**
     * Tries to take the item whose name is specified as the first modifier word of this {@link Command}
     * from the {@link Inventory} of the current {@link Room} of the specified {@link Character}.
     *
     * @param character the Character that is trying to take an item
     * @return true if item is taken successfully, false otherwise
     */
    @Override
    public boolean commandLogic(Character character) {

        Optional<String> opItemName = getModifier(0);

        if (opItemName.isEmpty()) {
            // if there is no second word, we don't know what to take...
            updateErr("noModifier");
            zuul.io.Out.println(GameText.getString("takeNoModifiersError"));
            return false;
        }

        String itemName = opItemName.get();

        Optional<Item> opItem = character.getCurrentRoom().getInventory().getItem(itemName);

        if (opItem.isEmpty()) {
            // The item is not in the room
            updateErr("itemNotFound");
            zuul.io.Out.println(GameText.getString("takeItemNotInRoomError", new Object[]{itemName}));
            return false;
        }

        //The item was found, unwrap it from Optional<Item>
        Item item = opItem.get();

        if (character.getInventory().getTotalWeight() + item.getWeight() > character.getInventory().getMAX_WEIGHT()) {
            // The player is carrying too much
            updateErr("itemTooHeavy");
            zuul.io.Out.println(GameText.getString("takeItemTooHeavyError", new Object[]{itemName}));
            return false;
        }

        // OK we can pick it up
        character.getCurrentRoom().getInventory().removeItem(item); //Remove item from room
        character.getInventory().addItem(item); //Give item to player

        //tell the player the command was successful
        zuul.io.Out.println(GameText.getString("takeSuccessful", new Object[]{itemName}));

        return true;
    }

    @Override
    public Optional<Map<Integer, List<String>>> getPossibleModifiers(zuul.gameState.maps.Map map) {
        if(isValidForPlayer(map)) {
            java.util.Map<Integer, List<String>> possibleModifiers = new HashMap<>();

            List<String> possibleItems = map.getPlayer().getCurrentRoom().getInventory().getItemList();;

            possibleModifiers.put(0, possibleItems);

            return Optional.of(possibleModifiers);
        }
        return Optional.empty();
    }

    @Override
    public boolean isValidForPlayer(zuul.gameState.maps.Map map) {
        return !map.getPlayer().getCurrentRoom().getInventory().getItemList().isEmpty();
    }
}
