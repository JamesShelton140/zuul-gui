package zuul.gameState.maps;

/**
 * A blank {@link Map} class to be used by {@link MapFactory} to create custom maps loaded from files or otherwise.
 *
 * Most methods are left blank as the properties of this map will be built by MapFactory.
 *
 * @author Timothy Shelton
 */
public class CustomMap extends Map{

    /**
     * Constructor
     */
    public CustomMap() {
        super("Custom World!");
    }

    @Override
    protected void createRooms() {}

    @Override
    protected void createCharacters() {}

    @Override
    public String getWelcome() {
        String message = "Welcome to a World of Zuul Custom World!\n" +
                "World of Zuul is a new, incredibly boring adventure game.\n";
        return message;
    }
}
