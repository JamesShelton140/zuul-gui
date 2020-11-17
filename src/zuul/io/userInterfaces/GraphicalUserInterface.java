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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
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

    private void createGameView() {

        Map gameState = game.getState();

        primaryStage.setTitle(gameState.getWorldName());

        /* -------------- Room ----------------- */
        roomDescription = new Text();
        roomDescription.setText(gameState.getPlayer().getCurrentRoom().getDescription());

        roomItemList = new Text();
        roomItemList.setText("Room Item List:\n" + gameState.getPlayer().getCurrentRoom().getInventory().listItems());

        roomCharacterList = new Text();
        roomCharacterList.setText("Room Character List:\n" +
                gameState.getPlayer().getCurrentRoom().getCharacters().stream()
                        .filter(character -> !(character.getName().equals(gameState.getPlayer().getName())))
                        .map(character -> character.getName())
                        .collect(Collectors.joining("\n"))
        );

        HBox roomContentsBox = new HBox();
        roomContentsBox.setAlignment(Pos.TOP_CENTER);
        roomContentsBox.getChildren().addAll(roomItemList, roomCharacterList);

        VBox roomBox = new VBox();
        roomBox.setAlignment(Pos.TOP_CENTER);
        roomBox.getChildren().addAll(roomDescription,roomContentsBox);

        /* -------------- Player ----------------- */
        playerItemList = new Text();
        playerItemList.setText("Player Item List:\n" + gameState.getPlayer().getInventory().listItems());

        VBox playerBox = new VBox();
        playerBox.setAlignment(Pos.TOP_CENTER);
        playerBox.getChildren().add(playerItemList);

        /* -------------- Commands ----------------- */
        Button commandButton = new Button("go east");
        commandButton.setOnAction(actionEvent -> {ArrayList<String> modifiers = new ArrayList<String>();
            modifiers.add("east");
            commandFactory.getCommand("go", modifiers).ifPresent(command -> command.execute(gameState.getPlayer()));
        });

        VBox commandButtonsBox = new VBox();
        commandButtonsBox.setAlignment(Pos.CENTER);
        commandButtonsBox.getChildren().add(commandButton);

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

        /* -------------- Root Pane ----------------- */
        BorderPane root = new BorderPane();
        root.setLeft(roomBox);
        root.setCenter(playerBox);
        root.setRight(commandButtonsBox);
        root.setBottom(consoleBox);

        /* -------------- Set Stage ----------------- */
        primaryStage.setScene( new Scene(root , 500 , 250));
    }

    public List<Button> createCommandButtons() {
        return null;
    }

    @Override
    public void update(String event) {
        //createGameView();
    }

    /**
     * Prints the specified string to the standard output stream {@code System.out}.
     *
     * @param str  the string requested to be printed to the player, not null
     */
    @Override
    public void print(String str) {
        String currentText = console.getText();
        console.setText(currentText + str);
        consolePane.setVvalue(1.0);
    }

    /**
     * Prints a new line to the standard output stream {@code System.out}.
     */
    @Override
    public void printNextln() {
        String currentText = console.getText();
        console.setText(currentText + "\n");
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
