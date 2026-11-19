
package com.mycompany.racecar;

import com.mycompany.racecar.Model.CarRaceModel;
import java.io.File;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author BAF
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {

        CarRaceModel model = new CarRaceModel(900);
        CarRaceView view = new CarRaceView();

        CarRaceController controller =
                new CarRaceController(model, view);

        Scene scene = new Scene(view.getRoot(), 1120, 760);

        // Load CSS directly from the project folder

        File cssFile = new File("src/main/resources/style.css");

        if (cssFile.exists()) {
            scene.getStylesheets().add(cssFile.toURI().toString());
            System.out.println("CSS LOADED: " + cssFile.getAbsolutePath());
        } else {
            System.out.println("CSS FILE NOT FOUND!");
            System.out.println("Looking in: "
                    + cssFile.getAbsolutePath());
        }

        stage.setTitle("Grand Prix - Car Race Simulation");
        stage.setMinWidth(1000);
        stage.setMinHeight(700);
        stage.setScene(scene);
        stage.show();

        controller.startSlideshow();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
