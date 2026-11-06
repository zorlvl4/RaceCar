/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.racecar.Model;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;

/**
 *
 * @author BAF
 */
public class CarRaceModel {
    private final List<RaceCar> cars = new ArrayList<>();
    private final List<RaceCar> finishOrder = new ArrayList<>();
    private RaceCar winner;

    public CarRaceModel() {
        cars.add(new RaceCar("GT Sport", 7, "Blue", Color.DODGERBLUE, 175));
        cars.add(new RaceCar("Rally Coupe", 12, "Red", Color.CRIMSON, 160));
        cars.add(new RaceCar("Roadster", 21, "Orange", Color.DARKORANGE, 185));
        cars.add(new RaceCar("Supercar", 44, "Green", Color.LIMEGREEN, 168));
    }
    
    
}
