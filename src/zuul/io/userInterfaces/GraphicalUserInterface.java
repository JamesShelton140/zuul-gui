package zuul.io.userInterfaces;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import zuul.Game;
import zuul.GameInterface;
import zuul.gameState.maps.Map;
import zuul.gameState.maps.ZuulMap;

import java.io.File;
import java.util.Optional;
import java.util.Scanner;

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

    public GraphicalUserInterface() {}

//    public GraphicalUserInterface(Game game) {
//        //this.game = game;
//        GameInterface.set(this);
//
//        Label textArea = new Label(game.getGameState().getWelcome());
//        Button btn = new Button("New Game"); btn.setOnAction((e) -> {Optional<Game> newGame = newGame();
//                                                                        newGame.ifPresent((theGame) -> {this.game = theGame;
//                                                                                                        textArea.setText("New game started");
//                                                                                                        });
//                                                                        });
//
//        VBox root = new VBox();
//        root.getChildren().add(textArea);
//        root.getChildren().add(btn);
//        scene = new Scene(root , 300 , 250);
//    }

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

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        GameInterface.set(this);

        Label label = new Label("Click \"New Game\" to start.");
        Button btn = new Button("New Game");
        btn.setOnAction((e) -> {Optional<Game> newGame = newGame();
                                newGame.ifPresent((theGame) -> {this.game = theGame;
                                                                label.setText("New game started");
                                                                createGameView();
                                                                });
                                });

        VBox root = new VBox();
        root.getChildren().add(label);
        root.getChildren().add(btn);
        scene = new Scene(root , 300 , 250);

        primaryStage.setTitle (" World of Zuul Load");
        primaryStage.setScene(getScene());
        primaryStage.show();
    }

    private File getWorldDescriptionFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select World Description File");

        File selectedFile = fileChooser.showOpenDialog(new Stage());

        return selectedFile;
    }


    @Override
    public void update(String event) {
        if(event.equals("game start")) {
            //createView();
        }
    }

    private void createGameView() {

        primaryStage.setTitle ("World of Zuul!");
//
//        Label textArea = new Label("GUI successfully lunched!");
//
//        StackPane root = new StackPane ();
//        root.getChildren().add(textArea);
//        primaryStage.setScene( new Scene(root , 300 , 250));
//        primaryStage.show();
    }

//    @Override
//    public void print(String str) {
//
//    }
//
//    @Override
//    public void printNextln() {
//
//    }
//
//    @Override
//    public String getNextLine() {
//        return null;
//    }

        /* -------------- Command line implementation for testing. ----------------*/

//    /**
//     * Does nothing. This interface does not change its behaviour based on {@link Game} events.
//     *
//     * @param event  a string that describes the event that immediately follows this update, not null
//     */
//    @Override
//    public void update(String event) {
//        //Do nothing, we don't want to change the standard behaviour.
//    }

    /**
     * Prints the specified string to the standard output stream {@code System.out}.
     *
     * @param str  the string requested to be printed to the player, not null
     */
    @Override
    public void print(String str) {
        System.out.print(str);
    }

    /**
     * Prints a new line to the standard output stream {@code System.out}.
     */
    @Override
    public void printNextln() {
        System.out.println();
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
