package zuul.gameState.maps;

import zuul.GameText;
import zuul.gameState.Item;
import zuul.gameState.Room;
import zuul.gameState.characters.Character;
import zuul.gameState.characters.Player;

import java.util.Arrays;

/**
 * World of Zuul standard {@link Map}.
 * <p>
 * This map has five {@link Room Rooms} (outside, theatre, pub, lab, and office) and one {@link Player} (player1)
 * <p>
 * Character "player1" starts in the room "outside".
 * <p>
 * Room "outside" contains a single item "notebook".
 *
 * @author Timothy Shelton
 */
public class ZuulMap extends Map {

    /**
     * Constructor
     */
    public ZuulMap() {
        super("World of Zuul!");
        createRooms();
        createCharacters();
    }

    /**
     * Creates a single {@link Player} with name "player1" in the {@link Room} {@code defaultStartingRoom}.
     */
    @Override
    protected void createCharacters() {
        Player player1 = new Player("player1", getDefaultStartingRoom().get());
        setPlayer(player1);

        Arrays.stream(new Character[] {player1}).forEach(this::addCharacter); //Add all characters to list
    }

    /**
     * Creates the {@link Room Rooms} outside, theatre, pub, lab, and office and
     * instantiates exits to create the following map:
     *
     * <table>
     *     <tr> <td>pub</td>    <td>--</td>  <td>outside</td>    <td>--</td>  <td>theatre</td>    </tr>
     *     <tr> <td></td>   <td></td>   <td>|</td>   </tr>
     *     <tr> <td></td>   <td></td>   <td>lab</td>    <td>--</td>  <td>office</td>  </tr>
     * </table>
     *
     * Adds an item "notebook" to outside.
     * <p>
     * Sets {@code defaultStartingRoom} to be outside.
     */
    @Override
    protected void createRooms() {

        Room outside, theatre, pub, lab, office;

        //Instantiate room names and descriptions
        outside = new Room("outside", GameText.getString("outside"));
        theatre = new Room("theatre", GameText.getString("theatre"));
        pub = new Room("pub", GameText.getString("pub"));
        lab = new Room("lab", GameText.getString("lab"));
        office = new Room("office", GameText.getString("office"));

        // initialise room exits and items
        //outside
        outside.setExits(new String[]{GameText.getString("east"), GameText.getString("south"), GameText.getString("west")},
                new Room[]{theatre, lab, pub});
        outside.getInventory().addItem(new Item(GameText.getString("notebook"), 2));
        outside.getInventory().addItem(new Item(GameText.getString("whip"), 2));
        //theatre
        theatre.setExits(new String[]{GameText.getString("west")}, new Room[]{outside});
        //pub
        pub.setExits(new String[]{GameText.getString("east")}, new Room[]{outside});
        //lab
        lab.setExits(new String[]{GameText.getString("north"), GameText.getString("east")},
                new Room[]{outside, office});
        //office
        office.setExits(new String[]{GameText.getString("west")}, new Room[]{lab});

        //Start game outside
        setDefaultStartingRoom(outside);

        //Add all rooms to this map
        Arrays.stream(new Room[]{outside, theatre, pub, lab, office}).forEach(this::addRoom);
    }

    @Override
    public String getWelcome() {
        String welcomeString = "\n" +
                GameText.getString("welcome_ln1") + "\n" +
                GameText.getString("welcome_ln2") + "\n" +
                (GameText.getString("welcome_ln3",
                        new Object[] {GameText.getString("CommandWordsBundle", "help")})) + "\n" +
                "\n";

        return welcomeString;
    }
}
