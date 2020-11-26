package zuul.commands;

import zuul.GameText;
import zuul.gameState.maps.Map;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A utility class for testing and manipulating {@link Command} classes for the World of Zuul application.
 *
 * @author Timothy Shelton
 */
public class CommandUtils {
    /**
     * Static instance of commandFactory to be used by all methods to create Commands where needed.
     */
    private static final CommandFactory commandFactory = new CommandFactory();


    /**
     * Returns a list of command words for all possible valid commands for the player in the specified {@link Map}.
     *
     * @param map the map containing the player character to find valid commands for
     * @return a list commands words for all valid commands for the player in the specified map, may be empty
     */
    public static List<String> getValidForPlayer(Map map) {
        List<String> validCommands;

        validCommands = GameText.getCommandWords().stream()
                .map(commandWord -> GameText.getString("CommandWordsBundle", commandWord))
                .filter(commandWord -> isValidForPlayer(commandWord, map))
                .collect(Collectors.toList());

        return validCommands;
    }

    /**
     * Returns the value of {@link Command#isValidForPlayer} for the specified {@link Map}
     * if a command can be instantiated for the specified command word.
     * Otherwise returns false.
     *
     * @param commandWord the command word of the command to test validity of
     * @param map the map containing the player to test validity of the command on
     * @return the value of Command.isValidForPlayer if a command can be instantiated, false otherwise
     */
    public static boolean isValidForPlayer(String commandWord, Map map) {
        System.out.println("(isValidForPlayer)" + commandWord);
        Optional<Command> command = commandFactory.getCommand(commandWord, new ArrayList<String>(0));

        return command.map(value -> value.isValidForPlayer(map)).orElse(false);
    }

    /**
     * Returns the value of {@link Command#getPossibleModifiers} for the specified {@link Map}
     * if a command can be instantiated for the specified command word.
     * Otherwise returns empty.
     *
     * @param commandWord the command word of the command to get possible modifiers of
     * @param map the map containing the player character to get all possible modifiers of the specified command word for
     * @return the value of Command.getPossibleModifiers if a command can be instantiated, otherwise an empty optional
     */
    public static Optional<java.util.Map<Integer, List<String>>> getPossibleModifiers(String commandWord, Map map) {
        System.out.println("(getPossibleModifiers)" + commandWord);
        Optional<Command> command = commandFactory.getCommand(commandWord, new ArrayList<String>(0));

        if(command.isPresent()) {
           return command.get().getPossibleModifiers(map);
        }

        return Optional.empty();
    }
}
