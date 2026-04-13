package com.Pink_Cats.createschematicchecker.core.attach;

public class ConveyorDegree {

    // for conveyor
    private double sideA;
    private double sideB;


    public ConveyorDegree(double sideA, double sideB) {
        this.sideA = sideA;
        this.sideB = sideB;
    }


    public double calculateHypotenuse() {
        return java.lang.Math.sqrt(sideA * sideA + sideB * sideB);
    }


    public double calculateAngleA() {
        return java.lang.Math.toDegrees(java.lang.Math.atan(sideB / sideA));
    }

    public double calculateAngleB() {
        return java.lang.Math.toDegrees(java.lang.Math.atan(sideA / sideB));
    }

    public double calculateAngleC() {
        return 90.0;
    }


    public static void main(String[] args) {
        ConveyorDegree triangle = new ConveyorDegree(3, 4);
        double hypotenuse = triangle.calculateHypotenuse();
        double angleA = triangle.calculateAngleA();
        double angleB = triangle.calculateAngleB();
        double angleC = triangle.calculateAngleC();

        System.out.println("slide length: " + hypotenuse);
        System.out.println("A: " + angleA + "degree");
        System.out.println("B: " + angleB + "degree");
        System.out.println("C: " + angleC + "degree");
    }
}
