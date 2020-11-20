package zuul.gameState.maps;

/**
 * A blank {@link Map} class to be used by {@link MapFactory} to create custom maps loaded from files or otherwise.
 *
 * Most methods are left blank as the properties of this map will be built by MapFactory.
 */
public class CustomMap extends Map{

    public CustomMap() {
        super("Custom World!");
    }

    @Override
    protected void createRooms() {}

    @Override
    protected void createCharacters() {}

    @Override
    public void printWelcome() {

    }

    @Override
    public String getWelcome() {
        return null;
    }
}
