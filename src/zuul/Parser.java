package zuul;

import zuul.commands.Command;
import zuul.commands.CommandFactory;
import zuul.io.Out;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

/**
 * A parser for the "World of Zuul" application.
 * This parser requests user input and tries to interpret it as a {@link Command}.
 *
 * @author  Michael Kolling and David J. Barnes
 * @author Timothy Shelton
 */
public class Parser 
{
    /**
     * Factory to handle {@link Command} instantiation.
     */
    CommandFactory commandFactory;

    /**
     * Constructor
     *
     * Creates a parser to interpret user input into a {@link Command}.
     */
    public Parser() {
        commandFactory = new CommandFactory();
    }

    /**
     * Reads input from user and tries to parse it as a valid command.
     * Only returns if a {@link Command} is successfully instantiated.
     * Prints an error message and calls this method recursively otherwise.
     * <p>
     * The returned Command may not contain all required modifiers to successfully {@link Command#execute execute}.
     *
     * @param caller  the name of the {@link zuul.gameState.characters.Player} that called this method, not null
     * @return the next Command instantiated from user input, not null
     */
    public Command getCommand(String caller)
    {
        String commandWord = null; //Initialise command word to null so "no input" will result in a null command.
        ArrayList<String> modifiers = new ArrayList<>();

        Out.print(caller + " > ");     // print prompt

        String inputLine = zuul.io.In.nextLine();

        Scanner tokenizer = new Scanner(inputLine);

        // Find commandWord
        if (tokenizer.hasNext()) {
            commandWord = tokenizer.next();
        }

        // Find all modifier words on the line.
        while(tokenizer.hasNext()) {
            modifiers.add(tokenizer.next());
        }

        tokenizer.close();  // Scanner cleanup

        // Try to create a command using the command word and modifiers
        Optional<Command> command = commandFactory.getCommand(commandWord, modifiers);

        if (command.isPresent()) {
            //If a command was successfully instantiated then return it
            return command.get();
        } else {
            //Could not instantiated a command from user input so try again
            GameInterface.get().update("parser error"); //update interface
            Out.println(GameText.getString("unrecognisedCommandError")); //error message
            return getCommand(caller);
        }
    }

    /**
     * Gets a {@link Command} without explicit calling character.
     *
     * @return the next Command instantiated from user input, not null
     */
    public Command getCommand() {
        return getCommand("");
    }

    /**
     * Gets the set of valid command words
     *
     * @return the set of all command words, not null
     */
    public Set<String> getCommandWords() {
        return GameText.getCommandWords();
    }
}
