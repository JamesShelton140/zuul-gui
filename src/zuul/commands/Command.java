package zuul.commands;

import zuul.gameState.characters.Character;
import zuul.GameInterface;

import java.util.ArrayList;
import java.util.Optional;

/**
 * A Command for the "World of Zuul" application.
 * This class holds information about a command that was issued by the user.
 * A command consists of two parts: a command word and a list of modifier words
 * (for example, if the user input was "take map", then the command state would be
 *  {@code commandWord = "take"} and {@code modifiers = {"map"}}).
 * <p>
 * The way this is used is: Commands are already checked for being valid
 * command words. If the user entered an invalid command (a word that is not
 * known) then then no command word should be created.
 * <p>
 * If the command does not require modifiers then modifiers can be null.
 * 
 * @author  Michael Kolling and David J. Barnes
 * @author Timothy Shelton
 */

public abstract class Command
{
    /**
     * The first word of a user-entered command
     */
    private final String COMMAND_WORD;

    /**
     * The list of all other words of a user-entered command.
     */
    private final ArrayList<String> MODIFIERS;

    /**
     * Creates a command object. Command word must be supplied but modifiers may be null.
     *
     * @param commandWord  the first word of the command, not null
     * @param modifiers  the array of modifier words to the command word. Empty or null if no modifiers input.
     */
    public Command(String commandWord, ArrayList<String> modifiers) {
        this.COMMAND_WORD = commandWord;
        this.MODIFIERS = modifiers;

    }

    /**
     * Creates a command object with only Command word supplied.
     * <p>
     * Sets modifiers to an empty list.
     *
     * @param commandWord  the first word of the command, not null
     */
    public Command(String commandWord) {
        this(commandWord, new ArrayList<String>());
    }

    /**
     * Executes this command on the specified {@link Character}.
     * <p>
     * Informs the {@link GameInterface} that a command has started before running the command logic of this command.
     * <p>
     * Informs the {@link GameInterface} that a command has ended after running the command logic of this command.
     *
     * @param character  the character that is acting on this command, not null
     * @return true if the command executed successfully, false otherwise
     */
    public boolean execute(Character character) {
        GameInterface.get().update("command start " + COMMAND_WORD); //tell the interface that a command is starting
        boolean result = commandLogic(character); //run the command logic
        GameInterface.get().update("command end " + COMMAND_WORD); //tell the interface that a command has finished
        return result;
    }

    /**
     * Informs the {@link GameInterface} that an error has occurred during the executing of this command.
     * <p>
     * Includes the commandWord of this command and the specified specific error message.
     *
     * @param error  the specific error message, not null
     */
    protected void updateErr(String error) {
        GameInterface.get().update("command error " + COMMAND_WORD + " " + error);
    }

    /**
     * The command logic of this command.
     * This is run when {@link #execute(Character)} is called.
     *
     * @param character  the character that is acting on this command, not null
     * @return true if the command logic completes successfully, false otherwise
     */
    protected abstract boolean commandLogic(Character character);

    /**
     * Gets the modifier word in the specified index of {@link #MODIFIERS}.
     * Can handle indexes outside of the size of MODIFIERS of this command.
     *
     * @param i  the index to check for a modifier word
     * @return an Optional containing the modifier in the specified index if it exists, empty Optional otherwise
     */
    public Optional<String> getModifier(int i) {
        if(hasModifier(i)) {
            return Optional.of(MODIFIERS.get(i));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Indicates if this command has at least one modifier word in {@link #MODIFIERS}.
     *
     * @return true if command has at least one modifier word, false otherwise
     */
    public boolean hasModifiers() {
        return (MODIFIERS != null) && (MODIFIERS.size() != 0);
    }

    /**
     * Indicates if this command has a non-null modifier in the specified index of {@link #MODIFIERS}.
     *
     * @param i the index to check
     * @return true if MODIFIERS has an index i and a non-null modifier word exists in that index, false otherwise.
     */
    public boolean hasModifier(int i) {
        return (MODIFIERS.size() > i) && (MODIFIERS.get(i) != null);
    }
}

