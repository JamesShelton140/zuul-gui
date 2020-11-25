package zuul.commands.actions;

import zuul.*;
import zuul.commands.Command;
import zuul.gameState.characters.Character;
import zuul.gameState.Inventory;
import zuul.gameState.Item;

import java.util.*;

/**
 * A Drop {@link Command} for the "World of Zuul" application.
 * This command drops an {@link Item} from a {@link Character} {@link Inventory}.
 * <p>
 * This command takes a single modifier word:
 * <li>
 *     The name of the item to be dropped.
 * </li>
 *
 * @author Timothy Shelton
 */
public class DropCommand extends Command {

    /**
     * Constructor
     *
     * Initialises a {@link Command} with locale-dependent command word of the key "drop".
     */
    public DropCommand(ArrayList<String> modifiers) {
        super(GameText.getString("CommandWordsBundle","drop"), modifiers);
    }

    /**
     * Tries to drop the item whose name is specified as the first modifier word of this {@link Command}
     * from the specified {@link Character}.
     *
     * @param character the Character that is trying to drop an item
     * @return true if item is dropped successfully, false otherwise
     */
    @Override
    public boolean commandLogic(Character character){

        Optional<String> opItemName = getModifier(0);

        if (opItemName.isEmpty()) {
            // if there is no second word, we don't know what to drop...
            updateErr("noModifier");
            zuul.io.Out.println(GameText.getString("dropHasNoModifiersError"));
            return false;
        }

        //modifier exists so unwrap it
        String itemName = opItemName.get();

        Optional<Item> opItem = character.getInventory().getItem(itemName);

        if (opItem.isEmpty()) {
            //Item not held by character
            updateErr("itemNotHeld");
            zuul.io.Out.println(GameText.getString("dropItemNotHeldError", new Object[]{itemName}));
            return false;
        }

        //Item found so unwrap Optional
        Item item = opItem.get();;

        character.getInventory().removeItem(item); //remove item from player
        character.getCurrentRoom().getInventory().addItem(item); //add item to room

        //tell the player the command was successful
        zuul.io.Out.println(GameText.getString("dropSuccessful", new Object[]{item}));

        return true;
    }

    @Override
    public Optional<Map<Integer, List<String>>> getPossibleModifiers(zuul.gameState.maps.Map map) {
        if(isValidForPlayer(map)) {
            java.util.Map<Integer, List<String>> possibleModifiers = new HashMap<>();

            List<String> possibleItems = map.getPlayer().getInventory().getItemList();;

            possibleModifiers.put(0, possibleItems);

            return Optional.of(possibleModifiers);
        }
        return Optional.empty();
    }

    @Override
    public boolean isValidForPlayer(zuul.gameState.maps.Map map) {
        return !map.getPlayer().getInventory().getItemList().isEmpty();
    }
}
