package zuul.io.userInterfaces;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import zuul.Game;
import zuul.GameInterface;
import zuul.gameState.maps.Map;
import zuul.gameState.maps.ZuulMap;
import zuul.commands.Command;
import zuul.commands.CommandFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

public class GraphicalUserInterface extends Application implements UserInterface {

    /**
     * Sets the {@link GameInterface} to an instance of this class and starts the {@link Game}.
     * <p>
     * This method should be called by {@link zuul.Main} to initialise this user interface.
     *
     * @param args  the program arguments, not used
     */
    public static void main(String[] args) {
        launch(args);
    }

    private Game game;
    private Scene scene;
    private Stage primaryStage;
    private CommandFactory commandFactory = new CommandFactory();
    private PrintStream consolePrintStream;

    public GraphicalUserInterface() {}

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        GameInterface.set(this);

        Label label = new Label("Click \"New Game\" to start.");
        Button btn = new Button("New Game");
        btn.setOnAction((e) -> {Optional<Game> newGame = newGame();
            newGame.ifPresent((theGame) -> {this.game = theGame;
                createGameView();
            });
        });

        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.getChildren().add(label);
        root.getChildren().add(btn);
        scene = new Scene(root , 300 , 250);

        primaryStage.setTitle (" World of Zuul Load");
        primaryStage.setScene(getScene());
        primaryStage.show();
    }

    public Scene getScene() {
        return this.scene;
    }

    public Optional<Game> newGame(){
        String[] choices = {"Default", "Custom"};
        ChoiceDialog<String> choiceDialog = new ChoiceDialog(choices[0],choices);

        choiceDialog.setTitle("New Game");
        choiceDialog.setHeaderText("Select the world you want to play:");

        choiceDialog.showAndWait();

        String selection = choiceDialog.getSelectedItem();

        if(selection.equals("Default")) {
            return Optional.of(new Game(new ZuulMap()));
        } else if(selection.equals("Custom")) {
            Map map = zuul.gameState.maps.MapFactory.createFromFile(getWorldDescriptionFile());
            return Optional.of(new Game(map));
        } else {
            return Optional.empty();
        }
    }

    private File getWorldDescriptionFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select World Description File");

        File selectedFile = fileChooser.showOpenDialog(new Stage());

        return selectedFile;
    }

    Text roomDescription;
    Text roomItemList;
    Text roomCharacterList;
    Text playerItemList;
    Text console;
    ScrollPane consolePane;
    VBox commandButtonsBox;

    private void createGameView() {

        Map gameState = game.getState();

        primaryStage.setTitle(gameState.getWorldName());

        /* -------------- Room ----------------- */
        roomDescription = new Text();
//        roomDescription.setText(gameState.getPlayer().getCurrentRoom().getDescription());

        roomItemList = new Text();
//        roomItemList.setText("Room Item List:\n" + gameState.getPlayer().getCurrentRoom().getInventory().listItems());

        roomCharacterList = new Text();
//        roomCharacterList.setText("Room Character List:\n" +
//                gameState.getPlayer().getCurrentRoom().getCharacters().stream()
//                        .filter(character -> !(character.getName().equals(gameState.getPlayer().getName())))
//                        .map(character -> character.getName())
//                        .collect(Collectors.joining("\n"))
//        );

        HBox roomContentsBox = new HBox();
        roomContentsBox.setAlignment(Pos.TOP_CENTER);
        roomContentsBox.getChildren().addAll(roomItemList, roomCharacterList);

        VBox roomBox = new VBox();
        roomBox.setAlignment(Pos.TOP_CENTER);
        roomBox.getChildren().addAll(roomDescription,roomContentsBox);

        /* -------------- Player ----------------- */
        playerItemList = new Text();
//        playerItemList.setText("Player Item List:\n" + gameState.getPlayer().getInventory().listItems());

        VBox playerBox = new VBox();
        playerBox.setAlignment(Pos.TOP_CENTER);
        playerBox.getChildren().add(playerItemList);

        /* -------------- Commands ----------------- */
//        Button commandButton = new Button("go east");
//        commandButton.setOnAction(actionEvent -> {
//            ArrayList<String> modifiers = new ArrayList<String>();
//            modifiers.add("east");
//            commandFactory.getCommand("go", modifiers).ifPresent(command -> command.execute(gameState.getPlayer()));
//        });

        commandButtonsBox = new VBox();
        commandButtonsBox.setAlignment(Pos.CENTER);
//        commandButtonsBox.getChildren().add(commandButton);

        /* -------------- Console ----------------- */
        console = new Text(gameState.getWelcome());
        consolePane = new ScrollPane();
        consolePane.setContent(console);
        consolePane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        consolePane.setPannable(true);
        consolePane.setPrefViewportHeight(50);

        Button newGameButton = new Button("New Game");
        newGameButton.setOnAction((e) -> {Optional<Game> newGame = newGame();
                                            newGame.ifPresent((theGame) -> {this.game = theGame;
                                                                            createGameView();
                                            });
        });

        HBox consoleBox = new HBox();
        consoleBox.getChildren().addAll(consolePane, newGameButton);

        consolePrintStream = new PrintStream(new ConsoleOutputStream(console), true);
        System.setOut(consolePrintStream);

        /* -------------- Root Pane ----------------- */
        BorderPane root = new BorderPane();
        root.setLeft(roomBox);
        root.setCenter(playerBox);
        root.setRight(commandButtonsBox);
        root.setBottom(consoleBox);

        /* -------------- Update Game State and Set Stage ----------------- */
        update("default");
        primaryStage.setScene( new Scene(root , 500 , 250));
    }

    public List<Button> createCommandButtons() {
        Map gameState = game.getState();

        ArrayList<Button> buttonList = new ArrayList<>();

        /* -------------- Go Command ----------------- */
        if(!gameState.getPlayer().getCurrentRoom().getExitDirections().isEmpty()) {
//            The current room has exits so create and add the "Go" command button.
            Button goCommand = new Button("Go");

            goCommand.setOnAction(evnt -> {
                ArrayList<String> modifiers = new ArrayList<>();
                modifiers.add(getModifier(game.getState().getPlayer().getCurrentRoom().getExitDirections().toArray(new String[0]), "Go where?"));
                commandFactory.getCommand("go", modifiers).ifPresent(command -> command.execute(gameState.getPlayer()));
            });

            buttonList.add(goCommand);
        }

        /* -------------- Take Command ----------------- */
        if(!gameState.getPlayer().getCurrentRoom().getInventory().getItemList().isEmpty()) {
//            The current room has items so create and add the "Take" command button.
            Button takeCommand = new Button("Take");

            takeCommand.setOnAction(evnt -> {
                ArrayList<String> modifiers = new ArrayList<>();
                modifiers.add(getModifier(game.getState().getPlayer().getCurrentRoom().getInventory().getItemList().toArray(new String[0]), "Take what?"));
                commandFactory.getCommand("take", modifiers).ifPresent(command -> command.execute(gameState.getPlayer()));
            });

            buttonList.add(takeCommand);
        }

        /* -------------- Drop Command ----------------- */
        if(!gameState.getPlayer().getInventory().getItemList().isEmpty()) {
//            The current player has items so create and add the "Drop" command button.
            Button dropCommand = new Button("Drop");
            dropCommand.setOnAction(evnt -> {
                ArrayList<String> modifiers = new ArrayList<>();
                modifiers.add(getModifier(game.getState().getPlayer().getInventory().getItemList().toArray(new String[0]), "Drop what?"));
                commandFactory.getCommand("drop", modifiers).ifPresent(command -> command.execute(gameState.getPlayer()));
            });

            buttonList.add(dropCommand);
        }
            return buttonList;
    }

    public String getModifier(String[] options, String context) {
        ChoiceDialog<String> choiceDialog = new ChoiceDialog<>(options[0], options);

        choiceDialog.setHeaderText(context);

        choiceDialog.showAndWait();

        String selection = choiceDialog.getSelectedItem();

        return selection;
    }

    @Override
    public void update(String event) {
        //create an alert dialog and redirect print output if an error occurs.
        if(event.contains("error")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
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
        roomDescription.setText(gameState.getPlayer().getCurrentRoom().getDescription());
        roomItemList.setText("Room Item List:\n" + gameState.getPlayer().getCurrentRoom().getInventory().listItems());
        roomCharacterList.setText("Room Character List:\n" +
                gameState.getPlayer().getCurrentRoom().getCharacters().stream()
                        .filter(character -> !(character.getName().equals(gameState.getPlayer().getName())))
                        .map(character -> character.getName())
                        .collect(Collectors.joining("\n"))
        );

        /* -------------- Player ----------------- */
        playerItemList.setText("Player Item List:\n" + gameState.getPlayer().getInventory().listItems());

        /* -------------- Commands ----------------- */
        List<Button> buttonList = createCommandButtons();
        commandButtonsBox.getChildren().setAll(buttonList);

    }

    private class DialogOutputStream extends OutputStream {

        private Dialog dialog;

        public DialogOutputStream(Dialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void write(int b) throws IOException {
            dialog.setContentText(dialog.getContentText() + String.valueOf((char) b));
        }
    }

    private class ConsoleOutputStream extends OutputStream {

        private Text console;

        public ConsoleOutputStream(Text console) {
            this.console = console;
        }

        @Override
        public void write(int b) throws IOException {
            console.setText(console.getText() + String.valueOf((char) b));
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
//        String currentText = console.getText();
//        console.setText(currentText + str);
        consolePane.setVvalue(1.0);
    }

    /**
     * Prints a new line to the standard output stream {@code System.out}.
     */
    @Override
    public void printNextln() {
        System.out.println();
//        String currentText = console.getText();
//        console.setText(currentText + "\n");
        consolePane.setVvalue(1.0);
    }

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
