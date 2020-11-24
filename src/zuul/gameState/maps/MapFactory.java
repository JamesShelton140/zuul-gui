package zuul.gameState.maps;

import zuul.gameState.Item;
import zuul.gameState.Room;
import zuul.gameState.characters.Character;
import zuul.gameState.characters.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class MapFactory {

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
        customMap.setPlayerCharacter(player1);

        return Optional.of(customMap);
    }

    private static Optional<Room> createRoom(List<String> roomDetailsList) {

        if(roomDetailsList.size() < 2) {
            //not enough details to create a room
            return Optional.empty();
        }

        Room room = new Room(roomDetailsList.get(0), roomDetailsList.get(1));

        return Optional.of(room);
    }

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

        //add each exit to the room if it was connected to a room
        exits.get(0).ifPresent(exit -> room.addExit("north", exit));
        exits.get(1).ifPresent(exit -> room.addExit("east", exit));
        exits.get(2).ifPresent(exit -> room.addExit("south", exit));
        exits.get(3).ifPresent(exit -> room.addExit("west", exit));
    }

    private static void addItems(Room room, List<String> roomDetailsList) {
        if(roomDetailsList.size() < 8) {
            return;
        }

        //get the list of details describing items
        List<String> itemsList = roomDetailsList.subList(6, roomDetailsList.size());

        //split the item details list into a list of item names and a list of item weights
        java.util.Map<Boolean,List<String>> itemNamesAndWeights = itemsList.stream()
                .collect(Collectors.partitioningBy(str -> java.lang.Character.isDigit(str.charAt(0))));

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

    public static void main(String[] args) {
        String str = "test, string , 1, 2,3,   4 , final";

        List<String> strings = Arrays.asList(str.split("\\s*,\\s*"));

        strings.forEach(System.out::println);

        strings = strings.stream().map(String::strip).collect(Collectors.toList());

        strings.forEach(System.out::println);

        Scanner roomDetailsScanner = new Scanner(str).useDelimiter("\\s*,\\s*");
        while(roomDetailsScanner.hasNext()) {
            System.out.println(roomDetailsScanner.next());
        }
    }
}
