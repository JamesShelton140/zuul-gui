package zuul.commands.actions;

import zuul.*;
import zuul.gameState.characters.Character;
import zuul.commands.Command;
import zuul.gameState.Inventory;
import zuul.gameState.Item;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A Give {@link Command} for the "World of Zuul" application.
 * This command gives an {@link Item} from a {@link Character} {@link Inventory} to another Character Inventory.
 * <p>
 * This command takes two modifier words:
 * <ul>
 *     <li>
 *         The name of the Item to be given, and
 *     </li>
 *     <li>
 *         The name of the Character to give the item to.
 *     </li>
 * </ul>
 *
 * @author Timothy Shelton
 */
public class GiveCommand extends Command {

    /**
     * Constructor
     *
     * Initialises a {@link Command} with locale-dependent command word of the key "give".
     */
    public GiveCommand(ArrayList<String> modifiers) {
        super(GameText.getString("CommandWordsBundle", "give"), modifiers);
    }

    /**
     * Tries to give the item whose name is specified as the first modifier word of this {@link Command},
     * from the specified {@link Character},
     * to the Character specified as the second modifier word of this Command.
     *
     * @param character the Character that is trying to give an item
     * @return true if item is given successfully, false otherwise
     */
    @Override
    public boolean commandLogic(Character character) {

        Optional<String> opItemName = getModifier(0);

        if (opItemName.isEmpty()) {
            // if there is no second word, we don't know what to give...
            updateErr("noModifier item");
            zuul.io.Out.println(GameText.getString("giveNoItemError"));
            return false;
        }

        Optional<String> opWhom = getModifier(1);

        if (opWhom.isEmpty()) {
            // if there is no third word, we don't to whom to give it...
            updateErr("noModifier target");
            zuul.io.Out.println(GameText.getString("giveNoCharacterError"));
            return false;
        }

        //modifiers exist so unwrap them
        String itemName = opItemName.get();
        String whom = opWhom.get();


        //get the new character by name
        Optional<Character> opRecipient = character.getCurrentRoom().getCharacter(whom);

        if (opRecipient.isEmpty()) {
            // cannot give it if the character is not here
            updateErr("targetNotFound");
            zuul.io.Out.println(GameText.getString("giveCharacterNotInRoomError", new Object[]{whom}));
            return false;
        }

        //Character found so unwrap Optional
        Character recipient = opRecipient.get();

        //Check if the item is currently held
        Optional<Item> opItem = character.getInventory().getItem(itemName);

        if (opItem.isEmpty()) {
            //Item not held by character
            updateErr("itemNotHeld");
            zuul.io.Out.println(GameText.getString("giveItemNotHeldError", new Object[]{itemName}));
            return false;
        }

        //Item found so unwrap Optional
        Item item = opItem.get();


        character.getInventory().removeItem(item); //remove the item from the current character
        recipient.getInventory().addItem(item);  //give item to new character

        //tell the player the command was successful
        zuul.io.Out.println(GameText.getString("giveSuccessful", new Object[]{recipient.getName(), item}));

        return true;
    }

    @Override
    public Optional<Map<Integer, List<String>>> getPossibleModifiers(zuul.gameState.maps.Map map) {
        if(isValidForPlayer(map)) {
            java.util.Map<Integer, List<String>> possibleModifiers = new HashMap<>();

            List<String> possibleItems = map.getPlayer().getInventory().getItemList();;

            possibleModifiers.put(0, possibleItems);

            List<String> possibleCharacters = map.getPlayer().getCurrentRoom().getCharacters().stream()
                    .map(Character::getName)
                    .filter(name -> !name.equals(map.getPlayer().getName()))
                    .collect(Collectors.toList());

            possibleModifiers.put(1, possibleCharacters);

            return Optional.of(possibleModifiers);
        }
        return Optional.empty();
    }

    @Override
    public boolean isValidForPlayer(zuul.gameState.maps.Map map) {
        return !map.getPlayer().getInventory().getItemList().isEmpty()
                && map.getPlayer().getCurrentRoom().getCharacters().stream()
                .anyMatch(character -> !character.getName().equals(map.getPlayer().getName()));
    }

}
