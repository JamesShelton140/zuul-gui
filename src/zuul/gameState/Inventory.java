package zuul.gameState;

import zuul.gameState.characters.Character;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * An inventory to store each {@link Item} held by a
 * {@link Room} or {@link Character} in the "World of Zuul" application.
 * <p>
 *
 * @author Timothy Shelton
 */
public class Inventory {
    /**
     * List of all items stored in this Inventory.
     *
     * Multiple non-unique items can be stored.
     */
    private ArrayList<Item> items;

    /**
     * The maximum {@link Item} weight that can be stored in this Inventory.
     */
    private int MAX_WEIGHT;

    /**
     * The default maximum {@link Item} weight to use is one if not specified during instantiation.
     */
    private static final int DEFAULT_MAX_WEIGHT = 10;

    /**
     * Default constructor.
     *
     * Initialises empty {@link Item} list and
     * sets the {@link #MAX_WEIGHT} of this Inventory to the default value, {@value #DEFAULT_MAX_WEIGHT}.
     */
    public Inventory() {
        this(DEFAULT_MAX_WEIGHT);
    }

    /**
     * Constructor with {@link #MAX_WEIGHT} specified.
     *
     * Initialises empty {@link Item} list and
     * sets the {@link #MAX_WEIGHT} of this Inventory to the value specified.
     *
     * @param maxWeight  the maximum Item weight that this inventory should be able to hold
     */
    public Inventory(int maxWeight) {
        items = new ArrayList<>();
        MAX_WEIGHT = maxWeight;
    }

    /**
     * Adds an {@link Item} to this Inventory.
     * <p>
     * No check is made on adding duplicate Items.
     *
     * @param item  the Item to be added to this inventory, not null
     */
    public void addItem(Item item) {
        items.add(item);
    }

    /**
     * Removes the specified {@link Item} from this Inventory.
     *
     * If more than one Item in this inventory equals the specified Item then the first occurrence is removed.
     *
     * @param item  the Item to be removed, not null
     */
    public void removeItem(Item item) {
        items.remove(item);
    }

    /**
     * Gets the index of the specified {@link Item} in this inventory.
     * This method return regardless of if the item specified is in this inventory.
     *
     * @param item  the item to check for, not null
     * @return the index of item parameter if it exists in the array, -1 otherwise
     */
    public int getItemIndex(Item item) {
        return items.indexOf(item);
    }

    /**
     * Gets the specified {@link Item} if it is in this inventory.
     *
     * @param item the Item to get, not null
     * @return an Optional containing the requested Item if found, empty otherwise
     */
    public Optional<Item> getItem(Item item) {
        //get the index to test if the item is contained in this inventory
        int index = getItemIndex(item);

        if(index == -1) {
            //item is not in inventory, return empty
            return Optional.empty();
        } else {
            //item found, return it
            return Optional.of(items.get(index));
        }
    }

    /**
     * Gets an {@link Item} with the specified name if it is in this inventory.
     *
     * @param name  the name of the Item to get, not null
     * @return an Optional containing the requested Item if found, empty otherwise
     */
    public Optional<Item> getItem(String name) {
        return getItem(new Item(name, 0));
    }

    /**
     * Gets the total weight of all {@link Item Items} in this inventory.
     *
     * @return the total weight currently held
     */
    public int getTotalWeight(){
        return items.stream().mapToInt(Item::getWeight).sum();
    }

    /**
     * Gets the maximum total weight of {@link Item Items} in this inventory.
     *
     * @return the maximum carry weight of this inventory
     */
    public int getMAX_WEIGHT() {
        return MAX_WEIGHT;
    }

    /**
     * Checks this inventory for the specified {@link Item}.
     *
     * @param item  the item to look for, not null
     * @return true if the room contains the specified item, false otherwise
     */
    public boolean containsItem(Item item) {
        return this.items.contains(item);
    }

    /**
     * Checks this inventory for an {@link Item} with the specified name.
     *
     * @param name  the name of the item to look for, not null
     * @return true if the room contains an Item with the specified name, false otherwise
     */
    public boolean containsItem(String name) {
        Optional<Item> item = items.stream()
                                    .filter(i -> i.getName().equals(name))
                                    .findAny();

        return item.isPresent();
    }

    /**
     * Creates a String representing the contents of this inventory.
     * <p>
     * Returns an empty String if this inventory is empty.
     *
     * @return a single line string of each {@link Item} in this inventory delimited by ", "
     */
    @Override
    public String toString() {
        return items.stream()
                .map(Item::toString)
                .collect(Collectors.joining(", "));
    }
}
