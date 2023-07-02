package com.krish.physicssolver.equation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class Part {
    public static final int CALCULATION_SCALE = 100;
    public static final int DISPLAY_SCALE = 5;
    private BigDecimal coefficient;
    private final boolean isVar;

    private double power;

    public Part(BigDecimal coefficient, boolean isVar) {
        this.coefficient = coefficient.setScale(CALCULATION_SCALE, RoundingMode.HALF_UP);
        this.isVar = isVar;
        this.power = 1;
    }

    public Part(BigDecimal coefficient, boolean isVar, double power) {
        this(coefficient, isVar);
        //If not a variable, apply power
        if (!isVar) {
            this.coefficient = BigDecimal.valueOf(Math.pow(coefficient.doubleValue(), power)).setScale(CALCULATION_SCALE, RoundingMode.HALF_UP);
            this.power = 1.0;
        } else {
            this.power = power;
        }
    }

    public ArrayList<Part> add(Part other) {
        ArrayList<Part> result = new ArrayList<>();
        //If both parts are not variables, add their coefficients
        if (!this.isVar() && !other.isVar()) {
            result.add(new Part(this.getCoefficient().add(other.getCoefficient()).setScale(CALCULATION_SCALE, RoundingMode.HALF_UP), false));
        } else if (this.isVar() && other.isVar() && this.getPower() == other.getPower()) {
            //If both parts are variables, and they have the same power, add their coefficients as well
            result.add(new Part(this.getCoefficient().add(other.getCoefficient()).setScale(CALCULATION_SCALE, RoundingMode.HALF_UP), true, this.getPower()));
        } else {
            //Otherwise, we can't add them. Just push them both back into the stack
            result.add(this);
            result.add(other);
        }
        return result;
    }

    public ArrayList<Part> sub(Part other) {
        ArrayList<Part> result = new ArrayList<>();
        //If both parts are not variables, subtract their coefficients
        if (!this.isVar() && !other.isVar()) {
            result.add(new Part(this.getCoefficient().subtract(other.getCoefficient()).setScale(CALCULATION_SCALE, RoundingMode.HALF_UP), false));
        } else if (this.isVar() && other.isVar() && this.getPower() == other.getPower()) {
            //If both parts are variables, and they have the same power, subtract their coefficients as well
            result.add(new Part(this.getCoefficient().subtract(other.getCoefficient()).setScale(CALCULATION_SCALE, RoundingMode.HALF_UP), true, this.getPower()));
        } else {
            //Otherwise, push both back into the stack BUT NEGATE THE SECOND TERM (0 - coefficient)
            result.add(this);
            other.setCoefficient(other.getCoefficient().negate());
            result.add(other);
        }
        return result;
    }

    public ArrayList<Part> mul(Part other) {
        ArrayList<Part> result = new ArrayList<>();
        //If both parts are not variables, multiply their coefficients
        if (!this.isVar() && !other.isVar()) {
            result.add(new Part(this.getCoefficient().multiply(other.getCoefficient()).setScale(CALCULATION_SCALE, RoundingMode.HALF_UP), false));
        } else if (this.isVar() && other.isVar()) {
            //If both are variables, multiply their coefficients and add their powers
            result.add(new Part(this.getCoefficient().multiply(other.getCoefficient()).setScale(CALCULATION_SCALE, RoundingMode.HALF_UP), true, this.getPower() + other.getPower()));
        } else {
            //If one's a variable, just multiply the two coefficients and create a new variable
            result.add(new Part(this.getCoefficient().multiply(other.getCoefficient()).setScale(CALCULATION_SCALE, RoundingMode.HALF_UP), true));
        }
        return result;
    }

    public ArrayList<Part> div(Part other) {
        ArrayList<Part> result = new ArrayList<>();
        //If both parts are not variables, divide their coefficients
        if (!this.isVar() && !other.isVar()) {
            result.add(new Part(this.getCoefficient().divide(other.getCoefficient(), CALCULATION_SCALE, RoundingMode.HALF_UP), false));
        } else if (this.isVar() && other.isVar()) {
            //If both are variables, divide their coefficients and subtract their powers
            result.add(new Part(this.getCoefficient().divide(other.getCoefficient(), CALCULATION_SCALE, RoundingMode.HALF_UP), true, this.getPower() - other.getPower()));
        } else {
            //If one's a variable, just divide the two coefficients and create a new variable
            result.add(new Part(this.getCoefficient().divide(other.getCoefficient(), CALCULATION_SCALE, RoundingMode.HALF_UP), true));
        }
        return result;
    }

    public void setCoefficient(BigDecimal coefficient) {
        this.coefficient = coefficient;
    }

    public BigDecimal getCoefficient() {
        return coefficient;
    }

    public boolean isVar() {
        return isVar;
    }

    public double getPower() {
        return power;
    }
}
