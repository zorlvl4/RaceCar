/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.racecar;

import com.mycompany.racecar.Model.RaceCar;
import com.mycompany.racecar.Model.CarRaceModel;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 *
 * @author BAF
 */
public class CarRaceController {
    private CarRaceModel model;
    private CarRaceView view;
    private PauseTransition slideshowTimer;
    private int slideshowIndex = 0;
    private boolean slideshowFinished = false;
    
    private boolean raceStarted = false;
    private boolean racePaused = false;
    private boolean raceFinished = false;
    
    private final Map<RaceCar, Group> carGroups = new LinkedHashMap<>();
    private final Map<RaceCar, TranslateTransition> transitions = new LinkedHashMap<>();
    private final List<RotateTransition> wheelAnimations = new ArrayList<>();
    
    private static final double TRACK_WIDTH = 1000;
    private static final double TRACK_HEIGHT = 380;
    
    private static final double START_CAR_X = 150;
    private static final double START_LINE_X = 265;
    private static final double FINISH_LINE_X = 900;
    private static final double CAR_WIDTH = 115;
    
    public CarRaceController(CarRaceModel model, CarRaceView view) {
        this.model = model;
        this.view = view;
        initialize();
    }

    private void initialize() {
        slideshowTimer = new PauseTransition(Duration.seconds(2.5));
        slideshowTimer.setOnFinished(event -> nextSlide());
      //  view.getStartButton().setOnAction(event -> startRace());
        //view.getPauseButton().setOnAction(event -> pauseRace());
        //view.getResetButton().setOnAction(event -> resetRace());
    }
    
    public void startSlideshow() {
        slideshowIndex = 0;
        showSlide();
        slideshowTimer.playFromStart();
    }

    // SLIDESHOW

    private void showSlide() {
        RaceCar car = model.getCars().get(slideshowIndex);
        CarVisual visual = createCarVisual(car);

        visual.group.setScaleX(2.7);
        visual.group.setScaleY(2.7);

        view.getSlideCarHolder().getChildren().setAll(visual.group);

        view.getSlideGreetingLabel().setText("Welcome to the Grand Prix!");

        view.getSlideNameLabel().setText(
                car.getType()
                + "  |  #" + car.getNumber()
                + "  |  " + car.getColorName());

        view.getSlideCounterLabel().setText(
                "Car " + (slideshowIndex + 1)
                + " of " + model.getCars().size());

        view.getSlideProgressBar().setProgress(
                (slideshowIndex + 1.0)
                / model.getCars().size());
    }

    private void nextSlide() {
        if (slideshowIndex < model.getCars().size() - 1) {
            slideshowIndex++;
            showSlide();
            slideshowTimer.playFromStart();

        } else {
            finishSlideshow();
        }
    }

    private void finishSlideshow() {
        slideshowFinished = true;
        slideshowTimer.stop();

        view.getSlideshowPane().setVisible(false);
        view.getSlideshowPane().setManaged(false);

        view.getRacePane().setManaged(true);
        view.getRacePane().setVisible(true);

        view.getResultsArea().setText(
                "The slideshow is complete.\n"
                + "All cars are ready at the starting line.\n\n"
                + "Press START to begin the race.");

        view.getRaceStatusLabel().setText("Ready to race");

        view.getStartButton().setDisable(false);
        view.getPauseButton().setDisable(true);
        view.getResetButton().setDisable(false);
    }

    private CarVisual createCarVisual(RaceCar car) {
        Group carGroup = new Group();

        Group wheelOne = createWheel();
        wheelOne.setLayoutX(28);
        wheelOne.setLayoutY(43);

        Group wheelTwo = createWheel();
        wheelTwo.setLayoutX(87);
        wheelTwo.setLayoutY(43);

        Polygon body = new Polygon(
                3, 22,
                18, 10,
                79, 10,
                96, 22,
                115, 26,
                115, 42,
                3, 42
        );

        body.setFill(car.getColor());
        body.setStroke(Color.BLACK);

        Polygon roof = new Polygon(
                31, 10,
                43, 0,
                78, 0,
                91, 10
        );

        roof.setFill(car.getColor().darker());
        roof.setStroke(Color.BLACK);

        Rectangle windowOne = new Rectangle(45, 2, 14, 8);
        windowOne.setFill(Color.LIGHTBLUE);
        windowOne.setStroke(Color.BLACK);

        Rectangle windowTwo = new Rectangle(62, 2, 14, 8);
        windowTwo.setFill(Color.LIGHTBLUE);
        windowTwo.setStroke(Color.BLACK);

        Text number = new Text(
                48, 35, String.valueOf(car.getNumber()));

        number.setFill(Color.WHITE);
        number.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        carGroup.getChildren().addAll(
                wheelOne, wheelTwo,
                body, roof,
                windowOne, windowTwo,
                number
        );

        RotateTransition spinOne =
                new RotateTransition(Duration.millis(420), wheelOne);

        spinOne.setByAngle(360);
        spinOne.setInterpolator(Interpolator.LINEAR);
        spinOne.setCycleCount(Animation.INDEFINITE);

        RotateTransition spinTwo =
                new RotateTransition(Duration.millis(420), wheelTwo);

        spinTwo.setByAngle(360);
        spinTwo.setInterpolator(Interpolator.LINEAR);
        spinTwo.setCycleCount(Animation.INDEFINITE);

        List<RotateTransition> animations = new ArrayList<>();

        animations.add(spinOne);
        animations.add(spinTwo);

        return new CarVisual(carGroup, animations);
    }

    private Group createWheel() {
        Circle outer = new Circle(0, 0, 11, Color.BLACK);
        Circle center = new Circle(0, 0, 3.5, Color.LIGHTGRAY);

        Line spoke = new Line(0, 0, 0, -8);
        spoke.setStroke(Color.WHITE);
        spoke.setStrokeWidth(2);

        return new Group(outer, spoke, center);
    }



    private static class CarVisual {
        private Group group;
        private List<RotateTransition> animations;
        public CarVisual(Group group, List<RotateTransition> animations) {
            this.group = group;
            this.animations = animations;
    }
        
    }
}    
