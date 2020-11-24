package zuul.gameState.maps;

import zuul.gameState.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A utility class for the World of Zuul application.
 * This class provides several static methods to check the state of a Map built by {@link MapFactory}.
 *
 * @author Timothy Shelton
 */
public class MapChecker {

    /**
     * Find and returns a list of 'degenerate' rooms in the supplied {@link Map}.
     * <p>
     *     A room is considered degenerate if the method {@link #isDegenerate} returns true.
     * </p>
     *
     * @param map the map to check for degenerate rooms.
     * @return a list of rooms determined to be degenerate, may be empty
     */
    public static List<Room> findDegenerateRooms(Map map) {
        List<Room> degenerateRooms = new ArrayList<>();

        map.forEachRoom(room -> {
            if(isDegenerate(room)) {
                degenerateRooms.add(room);
            }
        });

        return degenerateRooms;
    }

    /**
     * Indicates whether a room is considered degenerate.
     * <p>
     * A room is considered degenerate if it doesn't contain any items or have any exits.
     *
     * @param room the room to check for degeneracy
     * @return true if the room is degenerate, false otherwise
     */
    public static boolean isDegenerate(Room room) {
        return room.getInventory().isEmpty() || !room.hasExits();
    }

    /**
     * Indicates if the supplied {@link Map} has a valid default starting room.
     *
     * @param map the map to check for a valid starting room
     * @return true if the given map has a valid default starting room, false otherwise
     */
    public static boolean hasValidStartingRoom(Map map) {

        Optional<Room> startingRoomOpt =  map.getDefaultStartingRoom();

        return startingRoomOpt.filter(room -> !isDegenerate(room)).isPresent();
    }
}
