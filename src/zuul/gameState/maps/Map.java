package zuul.gameState.maps;

import zuul.Game;
import zuul.gameState.Item;
import zuul.gameState.Room;
import zuul.gameState.characters.Character;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * An abstract map of the game.
 * <p>
 * A concrete extension to this is a map of the game and represents the game state that is made up of
 * every {@link Room} and {@link Character} that can be interacted with by the {@link Game}.
 * An {@link Item} must be held by a Character or be in a Room
 * so they are not represented by this class as a separate part of the game state.
 *
 * @author Timothy Shelton
 */
public abstract class Map {

    /**
     * The default starting room for every {@link Character}.
     */
    private Room defaultStartingRoom;

    /**
     * Collection to store every {@link Room} on this Map.
     *
     * Uses {@link java.util.Map} to ensure all elements are unique and can be retrieved.
     */
    private java.util.Map<Room, Room> rooms;

    /**
     * Collection to store every {@link Character} on this Map.
     *
     * Uses {@link java.util.Map} to ensure all elements are unique and can be retrieved.
     */
    private java.util.Map<Character, Character> characters;

    /**
     * Constructor
     */
    public Map() {
        this.rooms = new HashMap<Room, Room>();
        this.characters = new HashMap<Character, Character>();
    }

    /**
     * Ensures the given {@link Character} is included on this Map.
     *
     * @param character  the Character whose presence on this Map is to be ensured, not null
     */
    public void addCharacter(Character character) {
        characters.put(character, character);
    }

    /**
     * Gets the {@link Character} specified if it is on this Map.
     *
     * @param character  the Character to get, not null
     * @return optional containing the Character if found, empty Optional otherwise
     */
    public Optional<Character> getCharacter(Character character) {
        Character characterFound = characters.get(character);

        if(characterFound == null) {
            return Optional.empty();
        }

        return Optional.of(characterFound);
    }

    /**
     * Applies the given consumer function on each {@link Character} in the Map.
     *
     * @param consumer  the Consumer to be applied to each Character, not null
     */
    public void forEachCharacter(Consumer<? super Character> consumer) {
        characters.keySet().forEach(consumer);
    }

    /**
     * Sets the default starting room for every {@link Character} on this Map.
     * <p>
     * Change will only affect Characters added to this Map afterwards.
     *
     * @param room  the Room to set as the default starting room, not null
     */
    public void setDefaultStartingRoom(Room room) {
        this.defaultStartingRoom = room;
    }

    /**
     * Gets the default starting {@link Room} for every {@link Character} on this Map.
     *
     * @return the default starting Room on this Map
     */
    public Room getDefaultStartingRoom() {
        return this.defaultStartingRoom;
    }

    /**
     * Ensures the given {@link Room} is included on this Map.
     *
     * @param room  the Room whose presence on this Map is to be ensured, not null
     */
    public void addRoom(Room room) {
        rooms.put(room, room);
        Object o = new Object().equals(room);
    }

    /**
     * Applies the given Consumer function to each {@link Room} in the map.
     *
     * @param consumer  the Consumer to be applied to each Room, not null
     */
    public void forEachRoom(Consumer<? super Room> consumer) {
        rooms.keySet().forEach(consumer);
    }

    /**
     * Creates every {@link Room} that is part of the initial game state.
     * <p>
     * Every room should be fully instantiated with its initial state. This includes exits to other rooms
     * and each {@link Item} the room holds.
     * Fully instantiated rooms should then be added to this map to include them in the initial game state.
     */
    protected abstract void createRooms();

    /**
     * Creates every {@link Character} that is part of the initial game state.
     * <p>
     * Every character should be fully instantiated with its initial state. This includes its starting {@link Room}
     * and each {@link Item} the character holds.
     * Fully instantiated characters should then be added to this map to include them in the initial game state.
     */
    protected abstract void createCharacters();

    /**
     * Prints a welcome message to the player.
     * <p>
     * This should be called at the start of the game flow logic.
     * <p></p>
     * This method should introduce the player to this Map.
     * <p>
     * It is recommended that a prompt to type the 'help' command be printed in this method.
     */
    public abstract void printWelcome();
}
