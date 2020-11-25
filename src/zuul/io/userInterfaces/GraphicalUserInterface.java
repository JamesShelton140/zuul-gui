package zuul.io.userInterfaces;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import zuul.Game;
import zuul.GameInterface;
import zuul.GameText;
import zuul.commands.CommandUtils;
import zuul.gameState.Item;
import zuul.gameState.Room;
import zuul.gameState.characters.Character;
import zuul.gameState.maps.Map;
import zuul.gameState.maps.MapChecker;
import zuul.gameState.maps.MapFactory;
import zuul.commands.Command;
import zuul.commands.CommandFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A graphical {@link UserInterface} for the "World of Zuul" application.
 * <p>
 * This user interface creates a GUI that the user can fully navigate with the mouse.
 *
 * @author Timothy Shelton
 */
public class GraphicalUserInterface extends Application implements UserInterface {

    /**
     * List of command words for commands to not show to the user.
     */
    private final List<String> INVALID_PLAYER_COMMANDS = Arrays.stream(new String[]{"quit", "look", "help"})
            .map(commandWord -> GameText.getString("CommandWordsBundle", commandWord))
            .collect(Collectors.toList());

    /**
     * The game instance to be played.
     */
    private Game game;
    /**
     * The primary stage to be used to display this interface.
     */
    private Stage primaryStage;
    /**
     * CommandFactory to build commands for player interaction.
     */
    private final CommandFactory commandFactory = new CommandFactory();
    /**
     * PrintStream to print to the console integrated into the main game view.
     */
    private PrintStream consolePrintStream;

    /**
     * Launches the {@link javafx} application.
     * <p>
     * This method should be called by {@link zuul.Main} to initialise this user interface.
     *
     * @param args  the program arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Constructor (empty)
     */
    public GraphicalUserInterface() {}

    /**
     * Creates and shows a default "start game" menu.
     *
     * @param stage the primary stage for this interface
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        //save the primary stage and set this as the game interface
        this.primaryStage = stage;
        GameInterface.set(this);

        //Default start menu
        Label label = new Label(GameText.getString("GuiTextBundle", "startNewGamePrompt"));
        Button btn = new Button(GameText.getString("GuiTextBundle","newGameButtonLabel"));
        btn.setOnAction((e) -> {
            //Get a new instance of Game from the user
            Optional<Game> newGame = newGame();
            newGame.ifPresent((theGame) -> {
                this.game = theGame;
                //create the in-game interface view
                createGameView();
            });
        });

        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.getChildren().add(label);
        root.getChildren().add(btn);
        root.setSpacing(20);
        Scene scene = new Scene(root, 400, 250);
        scene.getStylesheets().add("zuul/io/userInterfaces/mainGuiStyle.css");

        //Setup and show the primary stage
        primaryStage.setTitle (GameText.getString("GuiTextBundle", "startTitle"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Creates a dialog to allow the user to select which world to load.
     * Options are "Default" or "Custom".
     *
     * @return an optional containing a {@link Game} of the selected map, or an empty optional if non selected
     */
    public Optional<Game> newGame(){
        Dialog<ButtonType> worldTypeDialog = new Dialog<>();
        worldTypeDialog.setTitle(GameText.getString("GuiTextBundle", "newGameTitle"));
        worldTypeDialog.setHeaderText(GameText.getString("GuiTextBundle", "newGameHeader"));
        worldTypeDialog.getDialogPane().getStylesheets().add("zuul/io/userInterfaces/mainGuiStyle.css");

        //Create and set the buttons
        ButtonType defaultButtonType = new ButtonType(
                GameText.getString("GuiTextBundle", "newGameDefaultButtonLabel"), ButtonBar.ButtonData.OTHER);
        ButtonType customButtonType = new ButtonType(
                GameText.getString("GuiTextBundle", "newGameCustomButtonLabel"), ButtonBar.ButtonData.OTHER);

        worldTypeDialog.getDialogPane().getButtonTypes().addAll(defaultButtonType, customButtonType, ButtonType.CANCEL);

        //show the dialog to the user
        Optional<ButtonType> result = worldTypeDialog.showAndWait();

        //return behaviour
        //dialog cancelled
        if(result.isEmpty()) {
            return Optional.empty();
        }

        //default button pressed
        if(result.get().equals(defaultButtonType)) {
            Optional<Map> map = MapFactory.createFromClass("zuul");
            return map.map(Game::new);
        }

        //custom button pressed
        if(result.get().equals(customButtonType)) {
            try {
                Optional<Map> map = MapFactory.createFromFile(getWorldDescriptionFile());
                preGameChecks(map.get());
                return map.map(Game::new);
            } catch(Exception e) {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    /* --------------------------------- Custom World Loading ----------------------------------- */

    /**
     * Gets a {@link File} from the user with extension ".txt" or ".wld".
     *
     * @return the File selected by the user
     */
    private File getWorldDescriptionFile() {
        System.out.println("Trying to get world description file");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(GameText.getString("GuiTextBundle", "getWorldDescriptionFileTitle"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(
                        GameText.getString("GuiTextBundle", "worldDescriptionFileExtension"),
                        "*.txt", "*.wld"
                )
        );

        return fileChooser.showOpenDialog(new Stage());
    }

    /**
     * Passes the given {@link Map} to several check functions.
     *
     * The map may be modified by these check functions.
     *
     * @param map the map to be checked
     */
    private void preGameChecks(Map map) {
        checkRooms(map);
        addItems(map);
    }

    /**
     * Checks if each {@link Room} in the given {@link Map} is valid or degenerate
     * and calls a function that can remove these rooms.
     *
     * @param map the map whose rooms should be checked
     */
    private void checkRooms(Map map) {
        List<Room> degenerateRooms = MapChecker.findDegenerateRooms(map);
        if(degenerateRooms.size() == 0) {
            //No degenerate rooms found
            return;
        }

        //Give user the option to remove rooms identified as degenerate
        removeDegenerateRooms(map, degenerateRooms);
    }

    /**
     * Creates a dialog that allows the user to select for each {@link Room} passed in degenerateRooms
     * to remove it from the given {@link Map} or not.
     *
     * @param map the map to be modified
     * @param degenerateRooms the list of rooms to potentially remove
     */
    private void removeDegenerateRooms(Map map, List<Room> degenerateRooms) {
        List<Room> roomsToRemove = new ArrayList<>();

        Dialog<ButtonType> roomSelectionDialog = new Dialog<>();
        roomSelectionDialog.setTitle(GameText.getString("GuiTextBundle", "removeDegRoomsTitle"));
        roomSelectionDialog.setHeaderText(GameText.getString("GuiTextBundle", "removeDegRoomsHeader")
                /*"Select rooms to be removed.\n" +
                "Reasons you may want to remove each room is shown in brackets."*/);
        roomSelectionDialog.getDialogPane().getStylesheets().add("zuul/io/userInterfaces/mainGuiStyle.css");

        //create button type to display and add to dialog
        ButtonType removeButtonType = new ButtonType(
                GameText.getString("GuiTextBundle", "removeDegRoomsSelectedButtonLabel"),
                ButtonBar.ButtonData.APPLY
        );

        ButtonType cancelButtonType = new ButtonType(
                GameText.getString("GuiTextBundle", "removeDegRoomsCancelButtonLabel"),
                ButtonBar.ButtonData.CANCEL_CLOSE
        );

        roomSelectionDialog.getDialogPane().getButtonTypes().addAll(removeButtonType, cancelButtonType);

        //Create the list of rooms and checkboxes
        VBox vbox = new VBox();
        vbox.setSpacing(20);

        //Build check boxes for each degenerate room
        List<CheckBox> roomCheckBoxes = degenerateRooms.stream()
                .map(room -> {
                    List<String> reasons = new ArrayList<>();

                    if(!room.hasExits()) {
                        reasons.add(GameText.getString("GuiTextBundle", "removeDegRoomsNoExits"));
                    }
                    if(room.getInventory().isEmpty()) {
                        reasons.add(GameText.getString("GuiTextBundle", "removeDegRoomsNoItems"));
                    }

                    String cBoxLabel = room.getName() + " (" + String.join(", ", reasons) + ")"; //System.out.println(cBoxLabel);

                    CheckBox cBox = new CheckBox(cBoxLabel);

                    cBox.setAllowIndeterminate(false);

                    cBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                        if(newValue) {
                            roomsToRemove.add(room);
                        } else {
                            roomsToRemove.remove(room);
                        }
                    });

                    return cBox;
                })
                .collect(Collectors.toList());

        //add checkboxes to the dialog
        roomCheckBoxes.forEach(vbox.getChildren()::add);
        roomSelectionDialog.getDialogPane().setContent(vbox);

        //Show the dialog and get result from user.
        Optional<ButtonType> result = roomSelectionDialog.showAndWait();

        if(roomSelectionDialog.getResult().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE || result.isEmpty()) {
            return;
        }

        //remove the selected rooms
        roomsToRemove.forEach(map::safeRemoveRoom);

        if(!MapChecker.hasValidStartingRoom(map)) {
            Room room = pickStartingRoom(map);
            map.setDefaultStartingRoom(room);
            map.getPlayer().setCurrentRoom(room);
        }
    }

    /**
     * Creates a dialog that allows the user to pick the starting room for the player character on the given {@link Map}.
     * <p>
     *      This method only returns once a room has been selected.
     *      If the User tries to exit without selecting a room
     *      then an error alert is shown and this method is called recursively.
     * </p>
     *
     * @param map the map to selected a starting room from
     * @return the room selected by the user
     */
    private Room pickStartingRoom(Map map) {
        List<String> roomsList = new ArrayList<>();
        map.forEachRoom(room -> roomsList.add(room.getName()));

        ChoiceDialog<String> startingRoomSelectionDialog = new ChoiceDialog<>(roomsList.get(0), roomsList);
        startingRoomSelectionDialog.setTitle(GameText.getString("GuiTextBundle", "pickStartingRoomTitle"));
        startingRoomSelectionDialog.setHeaderText(GameText.getString("GuiTextBundle", "pickStartingRoomHeader"));
        startingRoomSelectionDialog.getDialogPane().getStylesheets().add("zuul/io/userInterfaces/mainGuiStyle.css");

        Optional<String> result = startingRoomSelectionDialog.showAndWait();

        if(result.isPresent()) {
            String selection = startingRoomSelectionDialog.getSelectedItem();
            return map.getRoom(selection).get();
        }

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(GameText.getString("GuiTextBundle", "noSelectionAlertHeader"));
        alert.showAndWait();

        return pickStartingRoom(map);
    }

    /**
     * Creates a dialog that allows the user to add items to each {@link Room} on the given {@link Map} that has exits.
     *
     * @param map the map to modify
     */
    private void addItems(Map map) {

        //Dialog creation and format
        Dialog<Void> addItemsDialog = new Dialog<>();
        addItemsDialog.setTitle(GameText.getString("GuiTextBundle", "addItemsTitle"));
        addItemsDialog.setHeaderText(GameText.getString("GuiTextBundle", "addItemsHeader"));
        addItemsDialog.getDialogPane().getStylesheets().add("zuul/io/userInterfaces/mainGuiStyle.css");

        //Dialog Buttons
        ButtonType doneButtonType = new ButtonType(
                GameText.getString("GuiTextBundle", "addItemDoneButtonLabel"), ButtonBar.ButtonData.OK_DONE);
        addItemsDialog.getDialogPane().getButtonTypes().add(doneButtonType);

        //Dialog content
        GridPane grid = new GridPane();

        List<Room> validRooms = new ArrayList<>();

        map.forEachRoom(room -> {
            if(room.hasExits()) {
                validRooms.add(room);
            }
        });

        for(int i = 0; i < validRooms.size(); i++) {
            grid.add(new Label(validRooms.get(i).getName()), 0, i);

            Button addItemButton = new Button(GameText.getString("GuiTextBundle", "addItemAddItemButtonLabel"));
            int finalI = i;
            addItemButton.setOnAction(event -> addItemToRoom(validRooms.get(finalI)));
            grid.add(addItemButton, 1, i);
        }

        addItemsDialog.getDialogPane().setContent(grid);

        //Display the dialog
        addItemsDialog.showAndWait();
    }

    /**
     * Creates a dialog that allows the user to add an {@link Item} to the given {@link Room}.
     * The user is allowed to cancel this dialog without adding an item.
     *
     * @param room the room to add an item to
     */
    private void addItemToRoom(Room room) {
        Dialog<Pair<String,Integer>> itemDialog = new Dialog<>();
        itemDialog.setTitle(GameText.getString("GuiTextBundle", "addItemToRoomTitle"));
        itemDialog.setHeaderText(
                GameText.getString("GuiTextBundle", "addItemToRoomHeader", new Object[] {room.getName()}));
        itemDialog.getDialogPane().getStylesheets().add("zuul/io/userInterfaces/mainGuiStyle.css");

        //Add the confirmation button
        ButtonType addItemButtonType = new ButtonType(
                GameText.getString("GuiTextBundle", "addItemToRoomAddItemButtonLabel"), ButtonBar.ButtonData.APPLY);
        itemDialog.getDialogPane().getButtonTypes().addAll(addItemButtonType, ButtonType.CANCEL);

        //User input fields
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        TextField itemName = new TextField();
        itemName.setPromptText(GameText.getString("GuiTextBundle", "addItemToRoomNameFieldPrompt"));

        TextField itemWeight = new TextField();
        itemWeight.setPromptText(GameText.getString("GuiTextBundle", "addItemToRoomWeightFieldPrompt"));

        grid.add(new Label(GameText.getString("GuiTextBundle", "addItemToRoomNameFieldLabel")), 0 ,0);
        grid.add(itemName, 1, 0);
        grid.add(new Label(GameText.getString("GuiTextBundle", "addItemToRoomWeightFieldLabel")), 0 ,1);
        grid.add(itemWeight, 1, 1);

        itemDialog.getDialogPane().setContent(grid);

        //Validate the inputs
        // Do not allow the dialog to close and submit until validation is passed
        final Button addItemButton = (Button) itemDialog.getDialogPane().lookupButton(addItemButtonType);

        addItemButton.addEventFilter(ActionEvent.ACTION, event -> {
            String errors = "";

            if(itemName.getText().strip().isEmpty()) {
                //no name has been input
                errors = String.join("\n", errors,
                        GameText.getString("GuiTextBundle", "validationAlertErrorNoName")
                );
            }

            if(itemWeight.getText().strip().isEmpty()) {
                //no weight has been input
                errors = String.join("\n", errors,
                        GameText.getString("GuiTextBundle", "validationAlertErrorNoWeight")
                );
            } else if(!isInteger(itemWeight.getText())) {
                //a non-integer value has been input into the weight field
                errors = String.join("\n", errors,
                        GameText.getString("GuiTextBundle", "validationAlertErrorNotInt")
                );
            }

            if(!errors.isEmpty()) {
                //there are errors, alert the user and consume the event
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(GameText.getString("GuiTextBundle", "validationAlertTitle"));
                alert.setHeaderText(errors);

                alert.showAndWait();

                event.consume();
            }
        });

        //Set the result converter
        itemDialog.setResultConverter(dialogButton -> {
            if(dialogButton == addItemButtonType) {
                return new Pair<>(itemName.getText(), Integer.parseInt(itemWeight.getText()));
            }
            return null;
        });

        //Show the dialog and get the user input
        Optional<Pair<String, Integer>> result = itemDialog.showAndWait();

        result.ifPresent(stringIntegerPair -> {
            room.getInventory().addItem(new Item(stringIntegerPair.getKey(), stringIntegerPair.getValue()));
        });
    }

    /**
     * Indicates if a given {@link String} represents an {@link Integer}.
     *
     * @param str the string to test
     * @return true if the given string represents an integer, false otherwise
     */
    private boolean isInteger(String str) {
        if(str == null) {
            return false;
        }
        try{
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    /* --------------------------------- General Game GUI ----------------------------------- */

    /**
     * The main content fields for the game view.
     */
    private Label roomDescription;
    private Label roomItemList;
    private Label roomCharacterList;
    private Label playerItemList;
    private Label console;
    private ScrollPane consolePane;
    private VBox commandButtonsBox;

    /**
     * Creates the view of the active {@link Game}.
     * <p>
     * This displays the current game state and allows the user to control the game using only the mouse.
     */
    private void createGameView() {

        Map gameState = game.getState();

        primaryStage.setTitle(gameState.getWorldName());

        double nodeSpacing = 20;

        /* -------------- Room ----------------- */
        roomDescription = new Label();
        Pane roomDescriptionPane = new Pane();
        roomDescriptionPane.setPrefHeight(100);
        roomDescriptionPane.getChildren().add(roomDescription);
        roomDescriptionPane.setId("bordered");
        HBox roomDescriptionBox = new HBox();
        roomDescriptionBox.setSpacing(nodeSpacing);
        roomDescriptionBox.getChildren().add(roomDescriptionPane);
        roomDescriptionBox.setHgrow(roomDescriptionPane, Priority.ALWAYS);

        roomItemList = new Label();
        Pane roomItemListPane = new Pane();
        roomItemListPane.getChildren().add(roomItemList);
        roomItemListPane.setId("bordered");

        roomCharacterList = new Label();
        Pane roomCharacterListPane = new Pane();
        roomCharacterListPane.getChildren().add(roomCharacterList);
        roomCharacterListPane.setId("bordered");

        HBox roomContentsBox = new HBox();
        roomContentsBox.setSpacing(nodeSpacing);
        roomContentsBox.setAlignment(Pos.TOP_CENTER);
        roomContentsBox.getChildren().addAll(roomItemListPane, roomCharacterListPane);

        VBox roomBox = new VBox();

        roomBox.setSpacing(nodeSpacing);
        roomBox.setAlignment(Pos.TOP_CENTER);
        roomBox.getChildren().addAll(roomDescriptionBox,roomContentsBox);
        roomBox.setVgrow(roomContentsBox, Priority.ALWAYS);

        /* -------------- Player ----------------- */
        playerItemList = new Label();
        Pane playerItemListPane = new Pane();
        playerItemListPane.getChildren().add(playerItemList);
        playerItemListPane.setId("bordered");

        VBox playerBox = new VBox();
        playerBox.setVgrow(playerItemListPane, Priority.ALWAYS);
        playerBox.setAlignment(Pos.TOP_CENTER);
        playerBox.getChildren().add(playerItemListPane);

        /* -------------- Commands ----------------- */
        commandButtonsBox = new VBox();
        commandButtonsBox.setAlignment(Pos.CENTER);

        /* -------------- Console ----------------- */
        console = new Label(gameState.getWelcome());
        consolePane = new ScrollPane();
        consolePane.setContent(console);
        consolePane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        consolePane.setPannable(true);
        consolePane.setPrefViewportHeight(80);
        consolePane.setVvalue(1.0);

        //Set the new game button as this will always be available regardless of game state
        Button newGameButton = new Button(GameText.getString("GuiTextBundle", "gameViewNewGameButtonLabel"));
        newGameButton.setOnAction((e) -> {Optional<Game> newGame = newGame();
                                            newGame.ifPresent((theGame) -> {this.game = theGame;
                                                                            createGameView();
                                            });
        });

        HBox consoleBox = new HBox();
        consoleBox.getChildren().addAll(consolePane, newGameButton);
        consoleBox.setHgrow(consolePane, Priority.ALWAYS);

        consolePrintStream = new PrintStream(new LabelOutputStream(console), true);
        System.setOut(consolePrintStream);

        /* -------------- Root Pane ----------------- */
        BorderPane root = new BorderPane();
        root.setLeft(roomBox);
        root.setCenter(playerBox);
        root.setRight(commandButtonsBox);
        root.setBottom(consoleBox);

        /* -------------- Update Game State and Set Stage ----------------- */
        update("default");
        Scene primaryScene = new Scene(root , 1000 , 560);
        primaryScene.getStylesheets().add("zuul/io/userInterfaces/mainGuiStyle.css");

        primaryStage.setScene( primaryScene);
    }

    /**
     * Returns a list of buttons that correspond to each valid {@link Command} for the player for the current game state.
     *
     * @return a list of buttons, one for each valid command for the player for the current game state
     */
    public List<Button> createCommandButtons() {
        Map gameState = game.getState();

        ArrayList<Button> buttonList = new ArrayList<>();

        GameText.getCommandWords().forEach(commandWord -> {
            if(CommandUtils.isValidForPlayer(commandWord, gameState) && !INVALID_PLAYER_COMMANDS.contains(commandWord)) {
                createCommandButton(commandWord).ifPresent(buttonList::add);
            }
        });

        buttonList.forEach(btn -> btn.setPrefSize(80, 40));

        return buttonList;
    }

    /**
     * @param commandWord
     * @return
     */
    public Optional<Button> createCommandButton(String commandWord) {

        Map gameState = game.getState();

        Button commandButton = new Button(commandWord);

        commandButton.setOnAction(actionEvent -> {

            Optional<java.util.Map<Integer, List<String>>> possibleModifiersOpt;
            possibleModifiersOpt = CommandUtils.getPossibleModifiers(commandWord, gameState);

            ArrayList<String> modifiers;

            if(possibleModifiersOpt.isPresent()) {
                java.util.Map<Integer, List<String>> possibleModifiers = possibleModifiersOpt.get();

                modifiers = new ArrayList<>(possibleModifiers.entrySet().size());

                for(Integer key : possibleModifiers.keySet()) {
                    getUserChoice(possibleModifiers.get(key), "Select Modifier")
                            .ifPresent(string -> modifiers.add(key, string));
                }

                commandFactory.getCommand(commandWord, modifiers)
                        .ifPresent(command -> command.execute(gameState.getPlayer()));
            }
        });


        return Optional.of(commandButton);
    }

    /**
     * Creates a dialog that allows the user to select an option from a given list of options.
     *
     * @param options the list of options to present to the user
     * @param context the text to be printed in the dialog to explain the choice
     * @return an optional of the option chosen by the user, or an empty optional if no option was selected
     */
    public Optional<String> getUserChoice(List<String> options, String context) {
        ChoiceDialog<String> choiceDialog = new ChoiceDialog<>(options.get(0), options);
        choiceDialog.setHeaderText(context);
        choiceDialog.getDialogPane().getStylesheets().add("zuul/io/userInterfaces/mainGuiStyle.css");

        choiceDialog.showAndWait();

        if(choiceDialog.getResult() == null) {
            return Optional.empty();
        } else {
            return Optional.of(choiceDialog.getSelectedItem());
        }
    }

    /**
     *
     *
     * @param event  a string that describes the event that immediately follows this update, not null
     */
    @Override
    public void update(String event) {
        //create an alert dialog and redirect print output if an error occurs.
        if(event.contains("error")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().getStylesheets().add("zuul/io/userInterfaces/mainGuiStyle.css");
            System.setOut(new PrintStream(new DialogOutputStream(alert), true));
            alert.show();
        }

        //reset print output after a command has finished
        if(event.contains("command") && event.contains("end")) {
            System.setOut(consolePrintStream);
        }

        //Get the current game state and update the view
        Map gameState = game.getState();

        /* -------------- Room ----------------- */
        roomDescription.setText(
                GameText.getString("GuiTextBundle", "gameViewRoomName") + gameState.getPlayer().getCurrentRoom().getName() + "\n"
                + GameText.getString("GuiTextBundle", "gameViewRoomDescription") + "\n"
                + gameState.getPlayer().getCurrentRoom().getDescription()
        );
        roomItemList.setText(GameText.getString("GuiTextBundle", "gameViewRoomItemList") + "\n"
                + gameState.getPlayer().getCurrentRoom().getInventory().listItems()
        );
        roomCharacterList.setText(GameText.getString("GuiTextBundle", "gameViewRoomCharacterList") + "\n"
                + gameState.getPlayer().getCurrentRoom().getCharacters().stream()
                        .filter(character -> !(character.getName().equals(gameState.getPlayer().getName())))
                        .map(Character::getName)
                        .collect(Collectors.joining("\n"))
        );

        /* -------------- Player ----------------- */
        playerItemList.setText(
                GameText.getString("GuiTextBundle", "gameViewPlayerItemList") + "\n"
                        + gameState.getPlayer().getInventory().listItems()
        );

        /* -------------- Commands ----------------- */
        List<Button> buttonList = createCommandButtons();
        commandButtonsBox.getChildren().setAll(buttonList);

    }

    /**
     * An output stream that writes to the contentText of a dialog supplied to the constructor.
     */
    private class DialogOutputStream extends OutputStream {

        /**
         * The dialog to write to.
         */
        private Dialog dialog;

        /**
         * Constructor
         *
         * @param dialog the dialog to write to
         */
        public DialogOutputStream(Dialog dialog) {
            this.dialog = dialog;
        }

        /**
         * Append the character represented by the supplied int
         * to the contentText of the dialog this output stream is initialized to.
         *
         * @param b the int that represents the character to be written
         * @throws IOException
         */
        @Override
        public void write(int b) throws IOException {
            dialog.setContentText(dialog.getContentText() + String.valueOf((char) b));
        }
    }

    /**
     * An output stream that writes to the Label node supplied to the constructor.
     */
    private class LabelOutputStream extends OutputStream {

        /**
         * The {@link Label} Node to write to.
         */
        private Label labelNode;

        /**
         * Constructor
         *
         * @param labelNode the text node to write to
         */
        public LabelOutputStream(Label labelNode) {
            this.labelNode = labelNode;
        }

        /**
         * Append the character represented by the supplied int
         * to the Label Node this output stream is initialized to.
         *
         * @param b the int that represents the character to be written
         * @throws IOException
         */
        @Override
        public void write(int b) throws IOException {
            labelNode.setText(labelNode.getText() + String.valueOf((char) b));
        }
    }

    /**
     * Prints the specified string to the standard output stream {@code System.out}.
     *
     * @param str  the string requested to be printed to the player, not null
     */
    @Override
    public void print(String str) {
        System.out.println(str);
        consolePane.setVvalue(1.0);
    }

    /**
     * Prints a new line to the standard output stream {@code System.out}.
     */
    @Override
    public void printNextln() {
        System.out.println();
        consolePane.setVvalue(1.0);
    }

    /**
     * Standard input stream
     */
    private Scanner reader = new Scanner(System.in);

    /**
     * Gets the next line of input from the standard input stream {@code System.in}.
     *
     * @return the next line of input from {@code System.in}.
     */
    @Override
    public String getNextLine() {
        return reader.nextLine();
    }


}
