package zuul;

import zuul.gameState.characters.Character;
import zuul.gameState.maps.Map;
import zuul.gameState.maps.ZuulMap;

/**
 * The World of Zuul game engine. This stores the state of the game and handles overall flow of game logic.
 * <p>
 * The {@link zuul.io.userInterfaces.UserInterface UserInterface} should call {@code Game.getInstance().play()}
 * to create the game and start the game logic.
 * <p>
 * This class holds a static instance of itself that stores the {@link Map} (game state) and {@link Parser}.
 *
 * @author Michael Kolling and David J. Barnes
 * @author Timothy Shelton
 */
public class Game {

    /**
     * Static instance of zuul.Game class to ensure a single instance is active.
     */
    private static Game gameInstance;

    //zuul.Game fields
    /**
     * The map to be used for this Game.
     */
    private final Map map;
    /**
     * The Parser to be used for this Game.
     */
    private final Parser parser;
    /**
     * Marker for if this Game should finish.
     */
    private Boolean finished = false;

    /**
     * Creates the game and initialise its {@link Map} and {@link Parser}.
     * This constructor is declared {@code private} to limit instantiation to a single {@code static} instance.
     *
     * To get the static instance call {@link Game#getInstance()}.
     */
    private Game() {
        map = new ZuulMap();
        parser = new Parser();
    }

    /**
     * Gets the static instance of this class.
     * An instance is created if it does not already exist.
     *
     * @return the static instance of Game, not null
     */
    public static Game getInstance() {
        //Create new instance of zuul.Game if one does not yet exist
        if (gameInstance == null) {
            gameInstance = new Game();
        }

        //Return the static instance of zuul.Game
        return gameInstance;
    }

    /**
     * Starts main game flow. Loops until {@link #finish()} is called.
     * <p>
     * Each loop every character in the {@link #map} is told to {@link Character#act()}.
     */
    public void play() {
        GameInterface.get().update("game start");
        //start the game by printing the welcome message provided by the map
        map.printWelcome();

        // Enter the main command loop.
        // Here we ask every character in the map to act until a player quits the game.
        while (!finished) {
            GameInterface.get().update("game nextRound");
            map.forEachCharacter(Character::act);
        }

        zuul.io.Out.println(GameText.getString("goodBye"));
    }

    // Getters and setters for class fields.

    /**
     * Gets the {@link Parser} associated to this Game.
     *
     * @return the parser associated to this Game.
     */
    public Parser getParser(){
        return parser;
    }

    /**
     * Sets the finished marker to true.
     * <p>
     * This will cause game logic to stop executing.
     */
    public void finish() {
        this.finished = true;
    }

    public Map getGameState() {
        return this.map;
    }
}
