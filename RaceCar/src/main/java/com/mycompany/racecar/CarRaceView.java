/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.racecar;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author BAF
 */
public class CarRaceView {
    private BorderPane root;
    private VBox slideshowPane;
    private VBox racePane;
    private StackPane slideCarHolder;
    private Label slideGreetingLabel;
    private Label slideNameLabel;
    private Label slideCounterLabel;
    private ProgressBar slideProgressBar;
    private Pane trackPane;
    private Label raceStatusLabel;
    private TextArea resultsArea;
    private Button startButton;
    private Button pauseButton;
    private Button resetButton;
    private Slider speedSlider;
    private Label speedValueLabel;
    
    private void createView() {
        root = new BorderPane();
        root.setPrefSize(1120, 760);
        root.getStyleClass().add("root-pane");
        
        //Header
        VBox header = new VBox(5);
        header.setPadding(new Insets(20, 28, 16, 28));
        header.getStyleClass().add("header");
        
        Label title = new Label("GRAND PRIX");
        Label subtitle = new Label("JavaFX Car Race Simulation");
        
        subtitle.getStyleClass().add("sub-title");
        header.getChildren().addAll(title, subtitle);
        title.getStyleClass().add("main-title");
        root.setTop(header);

        //SlideShow
        slideshowPane = new VBox(18);
        slideshowPane.setAlignment(Pos.CENTER);
        slideshowPane.setPadding(new Insets(25));
        slideshowPane.getStyleClass().add("slideshow-panel");

        slideGreetingLabel = new Label("Welcome to the Grand Prix!");
        slideGreetingLabel.getStyleClass().add("slide-greeting");

        slideCarHolder = new StackPane();
        slideCarHolder.setPrefSize(720, 300);
        slideCarHolder.setMinHeight(250);
        slideCarHolder.getStyleClass().add("car-display");

        slideNameLabel = new Label("Car Information");
        slideNameLabel.getStyleClass().add("slide-name");
        
        slideProgressBar = new ProgressBar(0);
        slideProgressBar.setPrefWidth(350);

        slideCounterLabel = new Label("Car 1 of 4");

        slideCounterLabel.getStyleClass().add("slide-counter");
        slideshowPane.getChildren().addAll(
                slideGreetingLabel,
                slideCarHolder,
                slideNameLabel,
                slideProgressBar,
                slideCounterLabel
        );
        
        
    }
}
