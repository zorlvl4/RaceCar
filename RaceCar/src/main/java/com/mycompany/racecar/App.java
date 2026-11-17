package com.mycompany.racecar;

import com.mycompany.racecar.Model.CarRaceModel;
import java.net.URL;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        CarRaceModel model = new CarRaceModel(900);
        CarRaceView view = new CarRaceView();

        CarRaceController controller = new CarRaceController(model, view);
        Scene scene = new Scene(view.getRoot(), 1120, 760);

        URL css = App.class.getResource("css/style.css");

        if (css != null) {
            scene.getStylesheets().add(
                    css.toExternalForm());
        }
        stage.setTitle("Grand Prix - Car Race Simulation");
        stage.setMinWidth(1000);
        stage.setMinHeight(700);
        stage.setScene(scene);
        stage.show();

        controller.startSlideshow();
    }

    public static void main(String[] args) {
        launch();
    }

}