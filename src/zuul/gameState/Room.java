package zuul.gameState;

import zuul.GameInterface;
import zuul.GameText;
import zuul.gameState.characters.Character;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A room in the "World of Zuul" application.
 *
 * This represents one location in the scenery of the game.
 * It is connected to other rooms via exits.
 * For each exit direction, the room stores a reference to the neighboring room.
 * The room has an inventory that can store items.
 * The room stores a list of {@link Character Characters} it contains.
 * 
 * @author  Michael Kolling and David J. Barnes
 * @author Timothy Shelton
 */
public class Room {

    /**
     * The name of this Room.
     */
    private final String name;

    /**
     * The description of this Room.
     */
    private String description;

    /**
     * All exits from this room.
     *
     * HashMap ensures all exit directions are unique.
     */
    private final HashMap<String, Room> exits;

    /**
     * An inventory to store items held by this room
     */
    private final Inventory inventory;

    /**
     * A list of each {@link Character} currently in this room.
     */
    private final ArrayList<Character> characters;

    /**
     * Creates a room with name "name" and described "description".
     * This room does not contain exits, {@link Item Items} or {@link Character Characters}.
     * <p>
     * "description" is something like "a kitchen" or "an open court yard".
     *
     * @param name  the name of the room, not null
     * @param description  the room's description, not null
     */
    public Room(String name, String description) {
        this.name = name;
        this.exits = new HashMap<String, Room>();
        this.description = description;
        this.characters = new ArrayList<Character>();
        this.inventory = new Inventory();
    }

    /**
     * Defines the exits of this room.
     * <p>
     * Every direction must lead to another room.
     * Only the first exit will be added for each unique direction name.
     *
     * @param directions  the set of exit directions, not null
     * @param rooms  the set of rooms lead to by the exit directions, not null
     */
    public void setExits(String[] directions, Room[] rooms) {

        if (directions.length > rooms.length) {
            //not every exit leads to a room!
            GameInterface.get().update("room error exitNoRoom");
            zuul.io.Out.println(GameText.getString("exitNoRoomError"));
            return;
        }

        for(int i = 0; i < directions.length; i++) {
            if(rooms[i] == null){
                //next exit would not lead to a room!
                GameInterface.get().update("room error exitNoRoom");
                zuul.io.Out.println(GameText.getString("exitNoRoomError"));
                return;
            }
            //for each direction link the corresponding room
            addExit(directions[i], rooms[i]);
        }
    }

    /**
     * Ensures that the specified exit is part of this Room's state.
     *
     * @param direction  the direction of the exit, not null
     * @param room  the room the exit leads to, not null
     */
    public void addExit(String direction, Room room) {
        this.exits.put(direction, room);
    }

    /**
     * Gets the description of this room.
     *
     * @return the description of this room, not null
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets a shallow copy of the list of each {@link Character} in the room.
     * <p>
     * Only a shallow copy is performed so that Characters are not duplicated.
     *
     * @return the list characters currently in the room, not null
     */
    public ArrayList<Character> getCharacters() {
        return (ArrayList<Character>) characters.clone();
    }

    /**
     * Gets the {@link Character} specified by name if it is in this room.
     * <p>
     * Always returns
     *
     * @param name  the name of the character to find, not null
     * @return an Optional containing the Character with the name specified if found in this room's state,
     *          an empty optional otherwise, not null
     */
    public Optional<Character> getCharacter(String name){
        return characters.stream()
                .filter(character -> character.getName().equals(name))
                .findAny();
    }

    /**
     * Gets the Room in the direction specified if there is an exit in that direction in this room.
     *
     * @param exit  the direction of exit from this room to retrieve the corresponding room for
     * @return an Optional containing the room in direction exit or null if it doesn't exist
     */

    public Optional<Room> getExit(String exit) {
        //Check if exit exists
        if (!this.exits.containsKey(exit)) {
            return Optional.empty();
        }

        //the exit exists so return the room it leads to
        return Optional.of(this.exits.get(exit));
    }

    /**
     * Gets the set of directions for all exits from this room.
     *
     * @return a set containing the direction of each exit from this room
     */
    public Set<String> getExitDirections() {
        return exits.keySet();
    }

    /**
     * Prints the state of this room.
     * <p>
     * The state of this room includes the description, exits, and {@link Inventory} of this Room.
     */
    public void printInfo() {
        zuul.io.Out.println("You are " + getDescription());
        zuul.io.Out.print(GameText.getString("exitsDisplay"));
        zuul.io.Out.print(String.join(" ", exits.keySet()));
        zuul.io.Out.println();
        zuul.io.Out.print(GameText.getString("itemsDisplay"));
        if (inventory != null) {
            zuul.io.Out.print(inventory);
        }
        zuul.io.Out.println();
        zuul.io.Out.print(GameText.getString("charactersDisplay"));
        if (characters != null && characters.size() != 0) {
            zuul.io.Out.println(characters.stream().map(Character::toString).collect(Collectors.joining(", ")));
        }
        zuul.io.Out.println();
    }

    /**
     * Ensures that the specified {@link Character} is part of this Room's state.
     *
     * @param character the Character to ensure is in this room
     */
    public void addCharacter(Character character) {
        this.characters.add(character);
    }

    /**
     * Removes a {@link Character} from this room.
     *
     * @param character The character to remove.
     */
    public void removeCharacter(Character character) {
        this.characters.remove(character);
    }

    /**
     * Gets the {@link Inventory} of this Room.
     * <p>
     * It is returned directly so that it can be modified without {@link Item} duplication.
     *
     * @return this Room's Inventory
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Indicates if the given object reference is "equal to" this Room using {@link #name}.
     * <p>
     * The given object reference is considered "equal to" this if it:
     * <ul>
     *     <li>is a reference to the same object</li>
     * </ul>
     * or
     * <ul>
     *     <li>is an instance of {@code Room}, and
     *     <li>{@code o.name} {@link String#equals equals} {@code this.name}
     * </ul>
     * (Method automatically generated by IntelliJ Idea.)
     *
     * @param o  the object to compare to this
     * @return true if {@code o == this} or {@code o} is a Room and
     * {@code o.name} {@link String#equals equals} {@code this.name}, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;
        Room room = (Room) o;
        return Objects.equals(name, room.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    /**
     * Gets the name of this room.
     *
     * @return the name of this room
     */
    public String getName() {
        return this.name;
    }

    /**
     * Indicates if this room has any exits.
     *
     * @return true if this room has at least one exit linked to a room, false otherwise
     */
    public boolean hasExits() {
        return this.exits.keySet().size() > 0 &&
                this.exits.entrySet().stream().anyMatch(entry -> entry.getValue() != null);
    }

    /**
     * Removed all exits that are linked to the supplied room.
     *
      * @param room the destination of exits from this room to remove
     */
    public void removeExit(Room room) {
        exits.entrySet().removeIf(entry -> entry.getValue().equals(room));
    }
}
