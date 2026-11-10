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
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Group;

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
    
    
}
