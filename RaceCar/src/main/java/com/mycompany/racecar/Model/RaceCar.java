/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.racecar.Model;

import javafx.scene.paint.Color;

/**
 *
 * @author BAF
 */
public class RaceCar {
    private String type;
    private int number;
    private double x;
    private double y;
    private double speed;
    private String colorName;
    private Color color;
    
    
    public RaceCar(String type, int number, String colorName, Color color, double speed) {
        this.type = type;
        this.number = number;
        this.colorName = colorName;
        this.color = color;
        this.speed = speed;
    }
    
    
}
