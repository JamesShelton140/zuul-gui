package zuul.gameState.maps;

import zuul.gameState.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MapChecker {

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
     * A room is considered degenerate if it doesn't contain any items or have any exits.
     *
     * @param room the room to check for degeneracy
     * @return true if the room is degenerate, false otherwise
     */
    public static boolean isDegenerate(Room room) {
        return room.getInventory().isEmpty() || !room.hasExits();
    }

    public static boolean hasValidStartingRoom(Map map) {

        Optional<Room> startingRoomOpt =  map.getDefaultStartingRoom();
        Room startingRoom;

        return startingRoomOpt.filter(room -> !isDegenerate(room)).isPresent();
    }
}
