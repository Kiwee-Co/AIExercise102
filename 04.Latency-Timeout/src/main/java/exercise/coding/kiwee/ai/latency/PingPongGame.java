// PingPongGame.java
package exercise.coding.kiwee.ai.latency;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class PingPongGame extends Application {

    @Override
    public void start(Stage primaryStage) {
        var gameUI = new GameUI();
        var scene = new Scene(gameUI.getRoot(), 600, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setTitle("Ping Pong Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        // Clear existing icons
        primaryStage.getIcons().clear();

        // Set title bar icon (icon.png)
        try {
            var titleIcon = new Image(getClass().getResourceAsStream("/icon.png"), 32, 32, true, true);
            if (titleIcon.isError()) {
                System.err.println("Error: Title icon could not be loaded from /icon.png - " + titleIcon.getException());
            } else {
                primaryStage.getIcons().add(titleIcon);
                System.out.println("Title icon loaded successfully from /icon.png (32x32)");
            }
        } catch (NullPointerException e) {
            System.err.println("Error: /icon.png not found in resources - " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Warning: Could not load title icon: " + e.getMessage());
        }

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}