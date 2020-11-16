package zuul.gameState;

import java.util.Objects;

/**
 * An Item in the "World of Zuul" application.
 *
 * @author Timothy Shelton
 */
public class Item {
    /**
     * The name of this Item.
     */
    private String name;

    /**
     * The weight of this Item.
     */
    private int weight;

    /**
     * The description of this Item.
     */
    private String description;

    /**
     * Constructor with name and weight specified.
     * <p>
     * The description of this Item is set to "A {@code name}".
     *
     * @param name  the name of this Item, not null
     * @param weight  the weight of this Item
     */
    public Item(String name, int weight) {
        this(name, weight, "A " + name);
    }

    /**
     * Constructor with name, weight and description specified.
     *
     * @param name  the name of this Item, not null
     * @param weight  the weight of this Item
     * @param description  the description of this item, not null
     */
    public Item(String name, int weight, String description) {
        this.name = name;
        this.weight = weight;
        this.description = description;
    }

    //Getters and Setters

    /**
     * Gets the name of this Item.
     *
     * @return the name of this Item, not null
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this item to the specified value.
     *
     * @param name  the name to set for this Item, not null
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the weight of this Item.
     *
     * @return the weight of this Item
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Sets the weight of this Item. Performs no checks on the value of weight parameter.
     *
     * @param weight  the weight to set for this Item
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * Gets the description of this Item.
     *
     * @return the description of this Item
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this Item.
     *
     * @param description  the description to set for this Item
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Indicates if the given object reference is "equal to" this Item using {@link #name}.
     *
     * The given object reference is considered "equal to" this if it:
     * <ul>
     *     <li>is a reference to the same object</li>
     * </ul>
     * or
     * <ul>
     *     <li>is an instance of {@code Item}, and
     *     <li>{@code o.name} {@link String#equals equals} {@code this.name}
     * </ul>
     * Method automatically generated by IntelliJ Idea.
     *
     * @param o  the object to compare to this, not null
     * @return true if o references this Item or o is an Item and
     * {@code o.name} {@link String#equals equals} {@code this.name},
     *          false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        Item item = (Item) o;
        return name.equals(item.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    /**
     * Creates and returns a String that represents the state of this Item.
     *
     * @return the String "{@code this.name} ({@code this.weight})", not null
     */
    @Override
    public String toString() {
        return name + '(' + weight + ')';
    }
}
