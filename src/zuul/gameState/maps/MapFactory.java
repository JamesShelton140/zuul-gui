package zuul.gameState.maps;

import zuul.commands.Command;
import zuul.gameState.Item;
import zuul.gameState.Room;
import zuul.gameState.characters.Character;
import zuul.gameState.characters.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Map builder for the World of Zuul application.
 *
 * Provides methods for creating a {@link Map} from a world description file or world name.
 *
 * @author Timothy Shelton
 */
public class MapFactory {

    /**
     * Creates a {@link Map} from mapName string by building the class name using reflection
     *
     * @param mapName
     * @return
     */
    public static Optional<Map> createFromClass(String mapName) {
        String mapClassName = mapName.substring(0, 1).toUpperCase() + mapName.substring(1).toLowerCase() + "Map";

        try {
            return Optional.of((Map) Class
                    .forName("zuul.gameState.maps." + mapClassName)
                    .getConstructor(new Class<?>[]{})
                    .newInstance());
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Tries to create a {@link Map} from the given {@link File}.
     *
     * @param customWorld the file to create a map from
     * @return an optional of the created map, or an empty optional if a
     */
    public static Optional<Map> createFromFile(File customWorld) {

        Scanner fileScanner;

        try {
            fileScanner = new Scanner(customWorld);
        } catch (FileNotFoundException e) {
            return Optional.empty();
        }

        //The map to add details from custom world file to
        Map customMap = new CustomMap();

        List<String> roomStrings = new ArrayList<>();

        //break file stream into separate room description lines
        while(fileScanner.hasNextLine()) {
            roomStrings.add(fileScanner.nextLine());
        }

        //convert room description lines into lists of details
        List<List<String>> roomDetailsLists = roomStrings.stream()
                .map(str -> Arrays.asList(str.split("\\s*,\\s*")))
                .collect(Collectors.toList());

        //create list of rooms from room details lists
        //Rooms have no exits at this point
        List<Room> roomList = roomDetailsLists.stream()
                .map(MapFactory::createRoom)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        if(roomList.size() == 0) {
            //No rooms created so cannot create a Map
            return Optional.empty();
        }

        //Instantiate the exits of each room and add any items
        roomList.forEach(room -> {
            List<String> roomDetailsList = roomDetailsLists.stream()
                    .filter(list -> list.get(0).equals(room.getName()))
                    .findFirst().get();

            instantiateExits(room, roomDetailsList,roomList);
            addItems(room, roomDetailsList);
        });

        //Add all the rooms to the map
        roomList.forEach(customMap::addRoom);

        //set the default starting room
        customMap.setDefaultStartingRoom(roomList.get(0));

        //create a player character and add it to the map in the first room
        // TODO: 20/11/2020  allow player to choose starting room
        Player player1 = new Player("player1", customMap.getDefaultStartingRoom().orElse(roomList.get(0)));
        customMap.addCharacter(player1);
        customMap.setPlayer(player1);

        return Optional.of(customMap);
    }

    /**
     * Initializes a {@link Room} with a name and description from a list of String details.
     *
     * @param roomDetailsList the list of details describing the room to be created
     *                        with element 0 as the room name and element 1 as the room description
     * @return an optional of the created room or an empty optional if one could not be created
     */
    private static Optional<Room> createRoom(List<String> roomDetailsList) {

        if(roomDetailsList.size() < 2) {
            //not enough details to create a room
            return Optional.empty();
        }

        Room room = new Room(roomDetailsList.get(0), roomDetailsList.get(1));

        return Optional.of(room);
    }

    /**
     * Instantiates the exits of the given {@link Room} to link to rooms from roomList
     * as specified by elements 2-5 (inclusive) of roomDetailsList.
     * <p>
     *     The exit directions are parsed as follows:
     *     <ul>
     *         <li>Entry 2: North</li>
     *         <li>Entry 3: East</li>
     *         <li>Entry 4: South</li>
     *         <li>Entry 5: West</li>
     *     </ul>
     * </p>
     *
     *
     * @param room the room to instantiate the exits of
     * @param roomDetailsList the list of details describing the room with exit directions in entries 2-5 (inclusive)
     * @param roomList the list of rooms that can be used as exit destinations
     */
    private static void instantiateExits(Room room, List<String> roomDetailsList, List<Room> roomList) {
        if(roomDetailsList.size() < 3) {
            return;
        }

        List<String> exitList = roomDetailsList.subList(2, 6);

        //convert exit room names to optionals containing corresponding Room if it exists
        // and empty otherwise (including null exits)
        List<Optional<Room>> exits = exitList.stream()
                .map(exit -> {
                    return roomList.stream()
                            .filter(rm -> rm.getName().equals(exit))
                            .findFirst();
                })
                .collect(Collectors.toList());

        //add each exit to the room if it was connected to a room from roomList
        exits.get(0).ifPresent(exit -> room.addExit("north", exit));
        exits.get(1).ifPresent(exit -> room.addExit("east", exit));
        exits.get(2).ifPresent(exit -> room.addExit("south", exit));
        exits.get(3).ifPresent(exit -> room.addExit("west", exit));
    }

    /**
     * Adds each {@link Item} specified by roomDetailsList entries 6+ to the given {@link Room}.
     * <p>
     *     An item is only added if both a name and weight are specified.
     *     An item must be specified by a string (name) in an even entry of roomDetailsList
     *     followed by a String representing an integer (weight) in the immediately following entry.
     * </p>
     *
     * @param room the room to add any specified items to
     * @param roomDetailsList the list of details describing the room with item details in entries 6+
     */
    private static void addItems(Room room, List<String> roomDetailsList) {
        if(roomDetailsList.size() < 8) {
            return;
        }

        //get the list of details describing items
        List<String> itemsList = roomDetailsList.subList(6, roomDetailsList.size());

        //split the item details list into a list of item names and a list of item weights
        java.util.Map<Boolean,List<String>> itemNamesAndWeights = itemsList.stream()
                .collect(Collectors.partitioningBy(str -> {
                    try {
                        Integer.parseInt(str);
                    } catch (NumberFormatException e) {
                        return false;
                    }

                    return true;
                }));

        //get the items names list
        List<String> itemNames = itemNamesAndWeights.get(false);
        //get the item weights list as a list of Integers
        List<Integer> itemWeights = itemNamesAndWeights.get(true).stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        //add all the items to the room
        for(int i = 0; i < itemNames.size(); i++) {
            room.getInventory().addItem(new Item(itemNames.get(i), itemWeights.get(i)));
        }
    }
}
