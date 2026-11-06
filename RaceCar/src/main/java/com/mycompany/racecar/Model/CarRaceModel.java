/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.racecar.Model;

import java.util.ArrayList;
import java.util.Collections;
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
    private final double finishLineX;

    public CarRaceModel(double finishLineX) {
        this.finishLineX = finishLineX;
        
        cars.add(new RaceCar("GT Sport", 7, "Blue", Color.DODGERBLUE, 175));
        cars.add(new RaceCar("Rally Coupe", 12, "Red", Color.CRIMSON, 160));
        cars.add(new RaceCar("Roadster", 21, "Orange", Color.DARKORANGE, 185));
        cars.add(new RaceCar("Supercar", 44, "Green", Color.LIMEGREEN, 168));
    }
    
    public List<RaceCar> getCars() {
        return List.copyOf(cars);
    }

    public List<RaceCar> getFinishOrder() {
        return List.copyOf(finishOrder);
    }

    public RaceCar getWinner() {
        return winner;
    }

    public void updatePosition(RaceCar car, double x) {
        car.setX(x);
        
        if (x >= finishLineX && !finishOrder.contains(car)) {
            finishOrder.add(car);
            
            if (!finishOrder.isEmpty()) {
                winner = finishOrder.get(0);
            }
        }
    }
    
    public void resetRace() {
        finishOrder.clear();
        winner = null;

        for (RaceCar car : cars) {
            car.setPosition(0, 0);
        }
    }
}
