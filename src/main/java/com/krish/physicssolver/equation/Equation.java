package com.krish.physicssolver.equation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Equation {
    /**
     * Returns if the string is a grouper (parenthesis)
     */
    public static boolean isGrouper(String c) {
        return c.equals("(") || c.equals(")");
    }

    /**
     * Returns the precedence of the inputted string
     * -1 - No precedence
     * 1 - + or -
     * 2 - * or /
     * 3 - ^
     */
    public static int getPrecedence(String c) {
        return switch (c) {
            case "^" -> 3;
            case "*", "/" -> 2;
            case "+", "-" -> 1;
            default -> -1;
        };
    }

    /**
     * Return if the operator is left associative or not
     */
    public static boolean isLeftAssociative(String c) {
        //+, -, /, * are left associative (precedence 1, 2)
        int precedence = getPrecedence(c);
        return precedence == 1 || precedence == 2;
    }

    /**
     * Take in a string equation, and return an ArrayList of its individual tokens
     * (variables, operators, and groupers in their own strings)
     */
    public static ArrayList<String> tokenizeEquation(String equation) {
        //Make a string builder to hold the current token
        StringBuilder token = new StringBuilder();

        ArrayList<String> tokenizedEquation = new ArrayList<>();

        for (String c : equation.split("")) {
            //If c is an operator or grouper, push the current token onto the token array list
            if (isGrouper(c) || getPrecedence(c) > 0) {
                //Check if there is a current token
                String trimmedToken = token.toString().trim();
                if (trimmedToken.length() > 0) {
                    tokenizedEquation.add(trimmedToken);
                }

                //Add the operator to the arraylist too
                tokenizedEquation.add(c.trim());

                //Clear the string builder after token is added
                token.setLength(0);
            } else {
                //Otherwise, append to the token string builder
                token.append(c.trim());
            }
        }

        //Add any remaining tokens in the string builder to the arraylist
        String trimmedToken = token.toString().trim();
        if (trimmedToken.length() > 0) {
            tokenizedEquation.add(trimmedToken);
        }

        return tokenizedEquation;
    }

    /**
     * Use the shunting-yard algorithm to convert the infix tokens into a postfix queue
     */
    public static Queue<String> toPostfix(ArrayList<String> tokens) {
        Stack<String> operators = new Stack<>();
        Queue<String> postfix = new LinkedList<>();

        for (String token : tokens) {
            if (token.equalsIgnoreCase("(")) {
                //If the token is an opening parenthesis, push it to the operator stack
                operators.push(token);
            } else if (token.equalsIgnoreCase(")")) {
                //If the token is a closing parenthesis, offer all the operators on the stack to the
                //queue until the opening parenthesis is reached
                while (!operators.isEmpty() && !operators.peek().equalsIgnoreCase("(")) {
                    postfix.offer(operators.pop());
                }

                //Discard the opening parenthesis
                operators.pop();
            } else if (getPrecedence(token) > 0) {
                //If the token is an operator, offer all the operators that are of a greater precedence
                //to the queue and push the operator after

                //If left associative, offer all operators the current operator is less than or equal to in
                //precedence. If it's right associative, only those that it's less than in precedence to.
                while (!operators.isEmpty() &&
                        (isLeftAssociative(token) && getPrecedence(token) <= getPrecedence(operators.peek()))
                        || (!isLeftAssociative(token) && getPrecedence(token) < getPrecedence(operators.peek()))
                ) {
                    postfix.offer(operators.pop());
                }

                //Push the current operator onto the stack
                operators.push(token);
            } else {
                //Otherwise, offer the token to the queue
                postfix.offer(token);
            }
        }

        //Pop the remaining operator stack
        while (!operators.isEmpty()) {
            postfix.offer(operators.pop());
        }

        return postfix;
    }

    /**
     * Turn a given equation into a format that is workable with the parser.
     *  1. Change the equation from form y = x + ... to 0 = 0 - y + x + ...
     *  2. Convert it into a postfix queue
     */
    public static Equation parseEquation(String equation) {
        //Remove all spaces
        equation = equation.replace(" ", "");


        //Split the equation at the = sign, and get the left side
        String[] splitEquation = equation.split("=");

        //Move the left side to the right side, and negate it by subtracting from 0
        String reformedEquation = "0-(" + splitEquation[0].trim() + ")+(" + splitEquation[1].trim() + ")";

        return new Equation(toPostfix(tokenizeEquation(reformedEquation)), equation);
    }

    /**
     * Confirm if an inputted equation is valid
     */
    public static boolean isValidEquation(String equation) {
        if (equation.length() == 0) return false;
        int parenthesis = 0;
        boolean equalsPresent = false;
        for (int i = 0; i < equation.length(); i++) {
            char current = equation.charAt(i);
            if (getPrecedence(String.valueOf(current)) > 0) {
                //If the current token is an operator
                //Nothing before/after an operator
                if (i - 1 < 0 || i + 1 >= equation.length()) return false;
                //Operator right before another
                char nextCharacter = equation.charAt(i + 1);
                if (getPrecedence(String.valueOf(nextCharacter)) > 0 || nextCharacter == '=') return false;
            } else if (current == '(') {
                //Check for balanced parenthesis
                parenthesis++;
                //Check if there's no operator before the grouper
                if (i - 1 >= 0) {
                    char previousCharacter = equation.charAt(i - 1);
                    if (getPrecedence(String.valueOf(previousCharacter)) < 0 && previousCharacter != '=') return false;
                }
            } else if (current == ')') {
                //Check for balanced parenthesis
                parenthesis--;
                //Check if there's no operator after the grouper
                if (i + 1 < equation.length()) {
                    char nextCharacter = equation.charAt(i + 1);
                    if (getPrecedence(String.valueOf(nextCharacter)) < 0 && nextCharacter != '=') return false;
                }
            } else if (current == '=') {
                equalsPresent = true;
                //Check if all parenthesis were balanced on left hand side
                if (parenthesis != 0) return false;
                //Check if anything is after/before the equals sign
                if (i - 1 < 0 || i + 1 >= equation.length()) return false;
            }
        }
        //If unbalanced parenthesis on the right hand side
        if (parenthesis != 0) return false;
        //If no equals sign
        return equalsPresent;
    }

    private final Queue<String> equation;
    private final String displayEquation;
    private final ArrayList<String> variables;

    public Equation(Queue<String> equation, String displayEquation) {
        this.equation = equation;
        this.displayEquation = displayEquation;
        this.variables = new ArrayList<>();
        for (String token : equation) {
            if (!isGrouper(token) && getPrecedence(token) < 0 && !token.matches("-?\\d+(\\.\\d+)?")) variables.add(token);
        }
    }

    /**
     * Take in a variables ArrayList, and return variables that the current
     * equation has that the inputted ArrayList doesn't
     */
    public ArrayList<String> getMissingVariables(Collection<String> input) {
        ArrayList<String> clonedVariables = new ArrayList<>(this.variables);
        clonedVariables.removeAll(input);
        return clonedVariables;
    }

    /**
     * Takes in a variable to solve for and a HashMap with values for all the other
     * variables, and returns the value of the missing variable
     */
    public BigDecimal solveForMissing(String variable, Map<String, BigDecimal> inputs) {
        //Confirm all the other variables are provided in input
        for (String knownVar : this.variables) {
            if (!knownVar.equalsIgnoreCase(variable) && !inputs.containsKey(knownVar)) return null;
        }

        Stack<Part> stack = new Stack<>();

        //Begin to parse the postfix queue
        for (String p : this.equation) {
            //Check if the part is a variable
            if (getPrecedence(p) < 0) {
                //Check if the part is a number. If it is, make a part for it and push it to the stack
                try {
                    double value = Double.parseDouble(p);
                    stack.push(new Part(BigDecimal.valueOf(value), false));
                } catch(Exception ignored) {
                    //If it's not a number, check if it's a variable we have a value for
                    if (inputs.containsKey(p)) {
                        //If we have its value, push it as a number (with its value)
                        stack.push(new Part(inputs.get(p), false));
                    } else {
                        //Otherwise, it's an unknown. Push it as a variable with a coefficient of 1
                        stack.push(new Part(BigDecimal.ONE, true));
                    }
                }
            } else {
                //Otherwise, it's an operator. Perform the appropriate operation
                Part p2 = stack.pop();
                Part p1 = stack.pop();
                switch (p) {
                    case "+" -> stack.addAll(p1.add(p2));
                    case "-" -> stack.addAll(p1.sub(p2));
                    case "*" -> stack.addAll(p1.mul(p2));
                    case "/" -> stack.addAll(p1.div(p2));
                }
            }
        }

        //Take all non-variable parts, and add their value to the "other side" (isolate the unknown quantity)
        BigDecimal otherSide = BigDecimal.ZERO;

        //Check if the part is non-variable, add its value and remove it from stack
        Iterator<Part> partIterator = stack.iterator();
        while (partIterator.hasNext()) {
            Part part = partIterator.next();
            if (part.isVar()) continue;
            otherSide = otherSide.add(part.getCoefficient().negate()).setScale(Part.CALCULATION_SCALE, RoundingMode.HALF_UP);
            partIterator.remove();
        }

        //Confirm there's only one term left
        if (stack.size() == 1) {
            //Divide other side by coefficient and apply appropriate exponent to get quantity
            Part unknown = stack.pop();
            return BigDecimal.valueOf(Math.pow(otherSide.divide(unknown.getCoefficient(), Part.CALCULATION_SCALE, RoundingMode.HALF_UP).doubleValue(), 1 / unknown.getPower())).setScale(Part.CALCULATION_SCALE, RoundingMode.HALF_UP);
        }

        return null;
    }

    public Queue<String> getEquation() {
        return equation;
    }

    public ArrayList<String> getVariables() {
        return variables;
    }

    public boolean equals(Equation other) {
        return this.equation.equals(other.getEquation());
    }

    public String toString() {
        return this.displayEquation;
    }
}
