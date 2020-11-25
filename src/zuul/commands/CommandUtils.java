package zuul.commands;

import zuul.GameText;
import zuul.gameState.maps.Map;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Timothy Shelton
 */
public class CommandUtils {
    private static final CommandFactory commandFactory = new CommandFactory();


    /**
     * @param map
     * @return
     */
    public static List<String> getValidForPlayer(Map map) {
        List<String> validCommands;

        validCommands = GameText.getCommandWords().stream()
                .filter(commandWord -> isValidForPlayer(commandWord, map))
                .collect(Collectors.toList());

        return validCommands;
    }

    /**
     * @param commandWord
     * @param map
     * @return
     */
    public static boolean isValidForPlayer(String commandWord, Map map) {
        Optional<Command> command = commandFactory.getCommand(commandWord, new ArrayList<String>(0));

        return command.map(value -> value.isValidForPlayer(map)).orElse(false);
    }

    /**
     * @param commandWord
     * @param map
     * @return
     */
    public static Optional<java.util.Map<Integer, List<String>>> getPossibleModifiers(String commandWord, Map map) {

       Optional<Command> command = commandFactory.getCommand(commandWord, new ArrayList<String>(0));

       if(command.isPresent()) {
           return command.get().getPossibleModifiers(map);
       }

       return Optional.empty();
    }
}
