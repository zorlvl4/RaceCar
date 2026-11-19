package com.mycompany.racecar;

import com.mycompany.racecar.Model.RaceCar;
import com.mycompany.racecar.Model.CarRaceModel;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
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
    private Clip slideshowSound;

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
    private static final double CAR_WIDTH = 120;

    public CarRaceController(CarRaceModel model, CarRaceView view) {
        this.model = model;
        this.view = view;
        initialize();
    }

    private void initialize() {
        slideshowTimer = new PauseTransition(Duration.seconds(2.5));
        slideshowTimer.setOnFinished(event -> nextSlide());

        view.getStartButton().setOnAction(event -> startRace());
        view.getPauseButton().setOnAction(event -> pauseRace());
        view.getResetButton().setOnAction(event -> resetRace());

        view.getSpeedSlider().valueProperty().addListener(
                (observable, oldValue, newValue) -> updateSpeed(newValue.doubleValue()));

        updateSpeed(view.getSpeedSlider().getValue());
    }

    // SLIDESHOW
    public void startSlideshow() {
        slideshowIndex = 0;
        showSlide();
        playSlideshowSound();
        slideshowTimer.playFromStart();
    }

    private void showSlide() {
        RaceCar car = model.getCars().get(slideshowIndex);
        CarVisual visual = createCarVisual(car);

        visual.group.setScaleX(2.7);
        visual.group.setScaleY(2.7);

        view.getSlideCarHolder().getChildren().setAll(visual.group);
        view.getSlideGreetingLabel().setText("Welcome to the Grand Prix!");

        view.getSlideNameLabel().setText(
                car.getType() + "  |  #" + car.getNumber() + "  |  " + car.getColorName());

        view.getSlideCounterLabel().setText(
                "Car " + (slideshowIndex + 1) + " of " + model.getCars().size());

        view.getSlideProgressBar().setProgress(
                (slideshowIndex + 1.0) / model.getCars().size());
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
        stopSlideshowSound();

        view.getSlideshowPane().setVisible(false);
        view.getSlideshowPane().setManaged(false);

        view.getRacePane().setManaged(true);
        view.getRacePane().setVisible(true);

        buildTrack();

        view.getResultsArea().setText(
                "WELCOME TO THE RACE!\n\n"
                + "All four cars are ready.\n\n"
                + "Press START to begin.");

        view.getRaceStatusLabel().setText("READY TO RACE");
        view.getStartButton().setDisable(false);
        view.getPauseButton().setDisable(true);
        view.getResetButton().setDisable(false);
    }

    // SOUND
    private void playSlideshowSound() {
        try {
            File soundFile = createSoundFile();

            try (AudioInputStream stream =
                    AudioSystem.getAudioInputStream(soundFile)) {

                slideshowSound = AudioSystem.getClip();
                slideshowSound.open(stream);
            }

            slideshowSound.setFramePosition(0);
            slideshowSound.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (Exception e) {
            System.out.println("Sound error: " + e.getMessage());
        }
    }

    private File createSoundFile() throws Exception {
        int sampleRate = 22050;
        int duration = 2;
        int sampleCount = sampleRate * duration;

        byte[] audioData = new byte[sampleCount * 2];
        double phase = 0;

        for (int i = 0; i < sampleCount; i++) {
            double time = (double) i / sampleRate;

            double frequency =
                    95 + 35 * (0.5 + 0.5 * Math.sin(2 * Math.PI * 1.2 * time));

            phase += 2 * Math.PI * frequency / sampleRate;

            double sound = Math.sin(phase)
                    + 0.35 * Math.sin(2 * phase)
                    + 0.15 * Math.sin(3 * phase);

            double fadeIn = Math.min(1, time * 10);
            double fadeOut = Math.min(1, (duration - time) * 10);
            double envelope = fadeIn * fadeOut;

            short sample = (short) (sound * envelope * 6500);

            audioData[i * 2] = (byte) (sample & 0xff);
            audioData[i * 2 + 1] = (byte) ((sample >> 8) & 0xff);
        }

        AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
        File soundFile = File.createTempFile("grand-prix-", ".wav");
        soundFile.deleteOnExit();

        try (AudioInputStream stream = new AudioInputStream(
                new ByteArrayInputStream(audioData), format, sampleCount)) {

            AudioSystem.write(stream, AudioFileFormat.Type.WAVE, soundFile);
        }

        return soundFile;
    }

    private void stopSlideshowSound() {
        if (slideshowSound != null) {
            slideshowSound.stop();
            slideshowSound.close();
            slideshowSound = null;
        }
    }

    // CAR DRAWING
    private CarVisual createCarVisual(RaceCar car) {
        Group carGroup = new Group();

        // Wheels

        Group wheelOne = createWheel();
        wheelOne.setLayoutX(30);
        wheelOne.setLayoutY(42);

        Group wheelTwo = createWheel();
        wheelTwo.setLayoutX(90);
        wheelTwo.setLayoutY(42);

        // Rear spoiler
        Polygon spoiler = new Polygon(
                3, 15,
                22, 15,
                19, 20,
                5, 20
        );

        spoiler.setFill(Color.DARKSLATEGRAY);
        spoiler.setStroke(Color.BLACK);

        Rectangle spoilerSupport = new Rectangle(8, 18, 4, 7);
        spoilerSupport.setFill(Color.BLACK);

        // Main car body
        Polygon body = new Polygon(
                3, 30,
                12, 23,
                29, 20,
                40, 8,
                79, 8,
                94, 20,
                108, 23,
                120, 30,
                118, 40,
                5, 40
        );

        body.setFill(new LinearGradient(
                0, 0, 0, 1, true,
                CycleMethod.NO_CYCLE,
                new Stop(0, car.getColor().brighter()),
                new Stop(1, car.getColor().darker())
        ));

        body.setStroke(Color.BLACK);
        body.setStrokeWidth(1.5);

        // Roof
        Polygon roof = new Polygon(
                37, 20,
                47, 8,
                76, 8,
                90, 20
        );

        roof.setFill(car.getColor().darker());
        roof.setStroke(Color.BLACK);

        // Windows
        Polygon frontWindow = new Polygon(
                49, 10,
                60, 10,
                60, 18,
                43, 18
        );

        frontWindow.setFill(Color.web("#8DD5F0"));
        frontWindow.setStroke(Color.DARKSLATEGRAY);

        Rectangle rearWindow = new Rectangle(64, 10, 12, 8);
        rearWindow.setFill(Color.web("#8DD5F0"));
        rearWindow.setStroke(Color.DARKSLATEGRAY);

        // Body details
        Line sideStripe = new Line(13, 33, 109, 33);
        sideStripe.setStroke(Color.WHITE);
        sideStripe.setStrokeWidth(2);
        sideStripe.setOpacity(0.75);

        Line doorLine = new Line(82, 21, 82, 39);
        doorLine.setStroke(Color.BLACK);
        doorLine.setOpacity(0.5);

        // Lights
        Circle frontLight = new Circle(116, 29, 2.5, Color.YELLOW);
        Circle rearLight = new Circle(7, 28, 2.5, Color.RED);

        // Car number
        Text number = new Text(52, 31, String.valueOf(car.getNumber()));
        number.setFill(Color.WHITE);
        number.setFont(Font.font("Arial", FontWeight.BOLD, 10));

        carGroup.getChildren().addAll(
                wheelOne, wheelTwo,
                spoiler, spoilerSupport,
                body, roof,
                frontWindow, rearWindow,
                sideStripe, doorLine,
                frontLight, rearLight,
                number
        );

        // Wheel animations
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
        Circle tire = new Circle(0, 0, 10.5, Color.web("#111111"));

        Circle rim = new Circle(0, 0, 6, Color.SILVER);
        rim.setStroke(Color.WHITE);
        rim.setStrokeWidth(0.7);

        Circle center = new Circle(0, 0, 2.3, Color.DARKSLATEGRAY);

        Line spokeOne = new Line(-4, 0, 4, 0);
        Line spokeTwo = new Line(0, -4, 0, 4);
        Line spokeThree = new Line(-3, -3, 3, 3);
        Line spokeFour = new Line(-3, 3, 3, -3);

        spokeOne.setStroke(Color.DARKSLATEGRAY);
        spokeTwo.setStroke(Color.DARKSLATEGRAY);
        spokeThree.setStroke(Color.DARKSLATEGRAY);
        spokeFour.setStroke(Color.DARKSLATEGRAY);

        return new Group(
                tire, rim,
                spokeOne, spokeTwo,
                spokeThree, spokeFour,
                center
        );
    }

    // BUILD RACE TRACK
    private void buildTrack() {
        Pane track = view.getTrackPane();

        track.getChildren().clear();
        carGroups.clear();
        transitions.clear();
        wheelAnimations.clear();

        Rectangle background = new Rectangle(TRACK_WIDTH, TRACK_HEIGHT);

        background.setFill(new LinearGradient(
                0, 0, 0, 1, true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#303B49")),
                new Stop(1, Color.web("#171F2B"))
        ));

        track.getChildren().add(background);

        // Track borders
        Line topLine = new Line(20, 27, 980, 27);
        Line bottomLine = new Line(20, 355, 980, 355);

        topLine.setStroke(Color.WHITE);
        bottomLine.setStroke(Color.WHITE);
        topLine.setStrokeWidth(3);
        bottomLine.setStrokeWidth(3);

        track.getChildren().addAll(topLine, bottomLine);

        // Finish line
        Line finishLine = new Line(FINISH_LINE_X, 30, FINISH_LINE_X, 350);

        finishLine.setStroke(Color.WHITE);
        finishLine.setStrokeWidth(4);

        track.getChildren().add(finishLine);

        // Starting line
        Line startLine = new Line(START_LINE_X, 30, START_LINE_X, 350);
        startLine.setStroke(Color.web("#FACC15"));
        startLine.setStrokeWidth(4);

        track.getChildren().add(startLine);

        // Track labels
        Text startText = new Text(START_LINE_X - 25, 20, "START");
        startText.setFill(Color.web("#FACC15"));
        startText.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        Text finishText = new Text(FINISH_LINE_X - 25, 20, "FINISH");
        finishText.setFill(Color.WHITE);
        finishText.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        track.getChildren().addAll(startText, finishText);

        // Lane numbers
        for (int i = 0; i < model.getCars().size(); i++) {
            Text laneNumber = new Text(45, 68 + i * 85, "0" + (i + 1));
            laneNumber.setFill(Color.web("#AAB4C0"));
            laneNumber.setFont(Font.font("Arial", FontWeight.BOLD, 15));
            track.getChildren().add(laneNumber);
        }

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
                                    laneY));

            carGroups.put(car, visual.group);
            wheelAnimations.addAll(visual.animations);
            track.getChildren().add(visual.group);
        }
        updateSpeed(view.getSpeedSlider().getValue());
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

        for (Map.Entry<RaceCar, TranslateTransition> entry : transitions.entrySet()) {
            RaceCar car = entry.getKey();
            TranslateTransition transition = entry.getValue();

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
        view.getRaceStatusLabel().setText("RACE IN PROGRESS");
    }

    private void prepareTransitions() {
        double distance = FINISH_LINE_X - START_CAR_X - CAR_WIDTH + 2;

        for (RaceCar car : model.getCars()) {
            Group group = carGroups.get(car);

            double performance = ThreadLocalRandom.current()
                    .nextDouble(0.75, 1.25);

            double adjustedSpeed = car.getSpeed() * performance;

            TranslateTransition transition = new TranslateTransition(
                    Duration.seconds(distance / adjustedSpeed), group);

            transition.setToX(distance);
            transition.setInterpolator(Interpolator.LINEAR);
            transition.setRate(view.getSpeedSlider().getValue());
            transition.setOnFinished(event -> carFinished(car));

            transitions.put(car, transition);
        }
    }

    // PAUSE RACE
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
        view.getRaceStatusLabel().setText("RACE PAUSED");
    }

    // RESET RACE
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
        view.getRaceStatusLabel().setText("READY TO RACE");

        view.getResultsArea().setText(
                "The race has been reset.\n"
                + "All cars are back at the starting line.");
    }

    // WINNER AND RESULTS
    private void carFinished(RaceCar car) {
        model.recordFinish(car);
        updateResults();

        view.getRaceStatusLabel().setText(
                "Car #" + car.getNumber()
                + " finished in position " + model.getFinishOrder().size());

        if (model.getFinishOrder().size() == model.getCars().size()) {
            finishRace();
        }
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

    private void finishRace() {
        raceFinished = true;

        for (RotateTransition wheel : wheelAnimations) {
            wheel.stop();
        }

        view.getStartButton().setDisable(true);
        view.getPauseButton().setDisable(true);

        RaceCar winner = model.getWinner();

        StringBuilder result = new StringBuilder();

        result.append("CONGRATULATIONS!\n\n");
        result.append("WINNER: Car #")
                .append(winner.getNumber())
                .append("\n");

        result.append("Type: ")
                .append(winner.getType())
                .append("\n");

        result.append("Color: ")
                .append(winner.getColorName())
                .append("\n\n");

        result.append("FINAL RACE RESULTS\n");

        for (int i = 0; i < model.getFinishOrder().size(); i++) {
            RaceCar car = model.getFinishOrder().get(i);

            result.append(i + 1)
                    .append(". Car #")
                    .append(car.getNumber())
                    .append(" - ")
                    .append(car.getType())
                    .append(" (")
                    .append(car.getColorName())
                    .append(")\n");
        }

        result.append("\nAll cars completed the race!");

        view.getResultsArea().setText(result.toString());
        view.getRaceStatusLabel().setText("RACE COMPLETED!");
    }

    // SPEED CONTROL
    private void updateSpeed(double speed) {
        view.getSpeedValueLabel().setText(
                String.format(Locale.US, "%.1fx", speed));

        for (TranslateTransition transition : transitions.values()) {
            transition.setRate(speed);
        }

        for (RotateTransition wheel : wheelAnimations) {
            wheel.setRate(speed);
        }
    }

    // CAR VISUAL CLASS
    private static class CarVisual {
        private Group group;
        private List<RotateTransition> animations;

        public CarVisual(Group group, List<RotateTransition> animations) {
            this.group = group;
            this.animations = animations;
        }
    }
}
