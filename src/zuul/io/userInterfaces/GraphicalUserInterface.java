package zuul.io.userInterfaces;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import zuul.Game;
import zuul.GameInterface;

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

    @Override
    public void start(Stage primaryStage) {
        GameInterface.set(this);
        System.out.println("GUI successfully lunched!");
        primaryStage . setTitle (" Hello World !");

        Text textArea = new Text();
        textArea.setText("GUI successfully lunched!");

        StackPane root = new StackPane ();
        root.getChildren().add(textArea);
        primaryStage.setScene( new Scene(root , 300 , 250));
        primaryStage.show();

        //Game.getInstance().play();
    }

//    @Override
//    public void update(String event) {
//
//    }
//
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

//  Command line implementation for testing.
    /**
     * Does nothing. This interface does not change its behaviour based on {@link Game} events.
     *
     * @param event  a string that describes the event that immediately follows this update, not null
     */
    @Override
    public void update(String event) {
        //Do nothing, we don't want to change the standard behaviour.
    }

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
