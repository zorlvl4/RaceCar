/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.racecar;

import com.mycompany.racecar.Model.RaceCar;
import com.mycompany.racecar.Model.CarRaceModel;
import java.io.ByteArrayInputStream;
import java.io.File;
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

        // BUILD RACE TRACK

    private void buildTrack() {
        javafx.scene.layout.Pane track = view.getTrackPane();
        track.getChildren().clear();
        carGroups.clear();
        transitions.clear();
        wheelAnimations.clear();

        Rectangle background = new Rectangle(TRACK_WIDTH, TRACK_HEIGHT);
        background.setFill(Color.web("#29313B"));
        track.getChildren().add(background);

        // Track borders
        Line topLine = new Line(20, 27, 980, 27);
        Line bottomLine = new Line(20, 355, 980, 355);

        topLine.setStroke(Color.WHITE);
        bottomLine.setStroke(Color.WHITE);

        topLine.setStrokeWidth(3);
        bottomLine.setStrokeWidth(3);
        track.getChildren().addAll(topLine, bottomLine);

        // Lane divisions
        for (int i = 1; i <= 3; i++) {
            Line lane = new Line(20, 112 + (i - 1) * 85, 980, 112 + (i - 1) * 85);
            lane.setStroke(Color.WHITE);
            lane.setStrokeWidth(2);

            lane.getStrokeDashArray().addAll(15.0, 15.0);
            track.getChildren().add(lane);
        }

        // Starting line
        Line startLine = new Line(START_LINE_X, 30, START_LINE_X, 350);

        startLine.setStroke(Color.YELLOW);
        startLine.setStrokeWidth(4);
        track.getChildren().add(startLine);

        // Finish line
        for (int row = 0; row < 32; row++) {
            for (int column = 0; column < 2; column++) {
                Rectangle square = new Rectangle(FINISH_LINE_X + column * 8, 30 + row * 10, 8, 10);
                if ((row + column) % 2 == 0) {
                    square.setFill(Color.WHITE);
                } else {
                    square.setFill(Color.BLACK);
                }
                track.getChildren().add(square);
            }
        }

        // Labels
        Text startText = new Text(START_LINE_X - 25, 20, "START");
        startText.setFill(Color.YELLOW);

        Text finishText = new Text(FINISH_LINE_X - 25, 20, "FINISH");
        finishText.setFill(Color.WHITE);

        startText.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        finishText.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        track.getChildren().addAll(startText, finishText);

        // Add cars
        for (int i = 0; i < model.getCars().size(); i++) {
            RaceCar car = model.getCars().get(i);
            double laneY = 35 + i * 85;

            car.setPosition(START_CAR_X, laneY);
            CarVisual visual = createCarVisual(car);

            visual.group.setLayoutX(START_CAR_X);
            visual.group.setLayoutY(laneY);

            visual.group.translateXProperty().addListener(
                    (observable, oldValue, newValue) ->
                            car.setPosition(
                                    START_CAR_X + newValue.doubleValue(),
                                    car.getY()));
            carGroups.put(car, visual.group);
            wheelAnimations.addAll(visual.animations);
            track.getChildren().add(visual.group);
            buildTrack();
        }
    }
        
        // START RACE

    private void startRace() {
        if (!slideshowFinished || raceFinished) {
            return;
        }
        if (!raceStarted) {
            prepareTransitions();
            raceStarted = true;
        }

        for (Map.Entry<RaceCar, TranslateTransition> entry
                : transitions.entrySet()) {
            RaceCar car = entry.getKey();
            TranslateTransition transition =
                    entry.getValue();
            if (!model.getFinishOrder().contains(car)) {
                if (racePaused) {
                    transition.play();
                } else {
                    transition.playFromStart();
                }
            }
        }

        for (RotateTransition wheel : wheelAnimations) {
            wheel.play();
        }
        racePaused = false;

        view.getStartButton().setDisable(true);
        view.getStartButton().setText("Start");
        view.getPauseButton().setDisable(false);
        view.getRaceStatusLabel().setText(
                "Race in progress!");
    }

    private void prepareTransitions() {
        double distance =
                FINISH_LINE_X - START_CAR_X
                - CAR_WIDTH + 2;
        for (RaceCar car : model.getCars()) {
            Group group = carGroups.get(car);
            TranslateTransition transition =
                    new TranslateTransition(
                            Duration.seconds(
                                    distance / car.getSpeed()),
                            group);

            transition.setToX(distance);

            transition.setInterpolator(
                    Interpolator.LINEAR);

            transition.setRate(
                    view.getSpeedSlider().getValue());

            transition.setOnFinished(event ->
                    carFinished(car));

            transitions.put(car, transition);
        }
    }   
    
    private void carFinished(RaceCar car) {
        model.recordFinish(car);
        updateResults();
        view.getRaceStatusLabel().setText(
                "Car #" + car.getNumber()
                + " finished in position "
                + model.getFinishOrder().size());
        if (model.getFinishOrder().size()
                == model.getCars().size()) {
            finishRace();
    }
        
    private void updateResults() {

    StringBuilder result = new StringBuilder();

    result.append("RACE PROGRESS\n\n");

    result.append("Cars finished: ")
            .append(model.getFinishOrder().size())
            .append(" / ")
            .append(model.getCars().size())
            .append("\n\n");

    result.append("Finishing order:\n");

    for (int i = 0; i < model.getFinishOrder().size(); i++) {

        RaceCar car = model.getFinishOrder().get(i);

        result.append(i + 1)
                .append(". Car #")
                .append(car.getNumber())
                .append(" - ")
                .append(car.getColorName())
                .append("\n");
    }

    view.getResultsArea().setText(result.toString());
    }

    private void resetRace() {
        for (TranslateTransition transition : transitions.values()) {
            transition.stop();
        }

        for (RotateTransition wheel : wheelAnimations) {
            wheel.stop();
        }

        model.resetRace();

        raceStarted = false;
        racePaused = false;
        raceFinished = false;

        buildTrack();

        view.getStartButton().setDisable(false);
        view.getStartButton().setText("Start");

        view.getPauseButton().setDisable(true);
        view.getResetButton().setDisable(false);

        view.getRaceStatusLabel().setText("Ready to race");

        view.getResultsArea().setText(
                "The race has been reset.\n"
                + "All cars are back at the starting line.");
    }
    
    private void pauseRace() {
            if (!raceStarted || raceFinished || racePaused) {
                return;
            }

            racePaused = true;

            for (TranslateTransition transition : transitions.values()) {
                transition.pause();
            }

            for (RotateTransition wheel : wheelAnimations) {
                wheel.pause();
            }

            view.getStartButton().setDisable(false);
            view.getStartButton().setText("Resume");

            view.getPauseButton().setDisable(true);

            view.getRaceStatusLabel().setText("Race paused");
        }

        private void updateSpeed(double speed) {

        view.getSpeedValueLabel().setText(
                String.format("%.1fx", speed));

        for (TranslateTransition transition : transitions.values()) {
            transition.setRate(speed);
        }

        for (RotateTransition wheel : wheelAnimations) {
            wheel.setRate(speed);
        }
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
