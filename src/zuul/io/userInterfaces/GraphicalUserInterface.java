package zuul.io.userInterfaces;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import zuul.Game;
import zuul.GameInterface;
import zuul.gameState.maps.ZuulMap;

import java.util.Scanner;

public class GraphicalUserInterface implements UserInterface {

    /**
     * Sets the {@link GameInterface} to an instance of this class and starts the {@link Game}.
     * <p>
     * This method should be called by {@link zuul.Main} to initialise this user interface.
     *
     * @param args  the program arguments, not used
     */
    public static void main(String[] args) {
    }

    private Game game;
    private Scene scene;

    public GraphicalUserInterface(Game game) {
        //this.game = game;
        GameInterface.set(this);

        Label textArea = new Label(game.getGameState().getWelcome());
        Button btn = new Button("New Game"); btn.setOnAction((e) -> {this.game = new Game(new ZuulMap());
                                                                        textArea.setText("New game started");});

        VBox root = new VBox();
        root.getChildren().add(textArea);
        root.getChildren().add(btn);
        scene = new Scene(root , 300 , 250);
    }

    public Scene getScene() {
        return this.scene;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public void update(String event) {
        if(event.equals("game start")) {
            //createView();
        }
    }

    private void createView() {

//        primaryStage.setTitle (" Hello World !");
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
