package civilturtles;

import java.awt.Point;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class StateMachine {

    private Turtle iRobot;
    private int arrayIndex;
    private double distance, origX, origY, currX, currY, nextX, nextY;
    private double angle, startHeading;
    private boolean currentPenState, intendedPenState;
    private boolean isNumeric;
    private Scanner epsFile;
    private String[] path;

    public StateMachine(Turtle t, Scanner f, Point p, double dir) {
        iRobot = t;
        epsFile = f;
        origX = p.x;
        origY = p.y;
        currX = p.x;
        currY = p.y;
        startHeading = dir;
    }

    public void run() {
        readFileFromServer();
        initialize();
        while(arrayIndex < path.length) {
            parseCommand();
            if (isNumeric) {
                continue;
            }
            //setStartAngle
        }
    }

    private void readFileFromServer() {
        readFile();
    }

    private void readFile() {
        // simulation
        StringBuilder sb = new StringBuilder();
        while (epsFile.hasNextLine()) {
            sb.append(epsFile.nextLine());
            sb.append(" ");
        }
        path = sb.toString().split(" ");
    }

    private void initialize() {
        arrayIndex = 0;
        distance = 0;
        angle = 0;
        nextX = 0;
        nextY = 0;
        currentPenState = false;
        intendedPenState = false;
        isNumeric = false;
    }

    private void parseCommand() {
        //Put Outputs in map
        HashMap<String, Object> inputs = new HashMap<String, Object>();
        inputs.put("currPenState", currentPenState);
        inputs.put("origX", origX);
        inputs.put("origY", origY);
        inputs.put("currX", currX);
        inputs.put("currY", currY);
        inputs.put("angle", angle);
        inputs.put("distance", distance);
        inputs.put("index", arrayIndex);

        Map<String, Object> outputs = commandParser(inputs);

        startHeading = angle;
        intendedPenState = (boolean) outputs.get("intendedPenState");
        origX = (double) outputs.get("origX");
        origY = (double) outputs.get("origY");
        currX = (double) outputs.get("currX");
        currY = (double) outputs.get("currY");
        angle = (double) outputs.get("angle");
        distance = (double) outputs.get("distance");
        arrayIndex = (int) outputs.get("index");
        isNumeric = (boolean) outputs.get("isNumericOrNewpath");
    }

    private Map<String, Object> commandParser(Map<String, Object> inputs) {
        //get inputs from map
        String[] inputPath = (String[]) inputs.get("path");
        double inputOrigX = (double) inputs.get("origX");
        double inputOrigY = (double) inputs.get("origY");
        double inputCurrX = (double) inputs.get("currX");
        double inputCurrY = (double) inputs.get("currY");
        double inputDistance = (double) inputs.get("distance");
        double inputAngle = (double) inputs.get("angle");
        boolean inputCurrPenState = (boolean) inputs.get("currPenState");
        int inputArrayIndex = (int) inputs.get("index");

        //create output variables
        double outputOrigX = 0, outputOrigY = 0, outputCurrX = 0, outputCurrY = 0;
        double outputDistance = 0, outputAngle = 0;
        boolean outputIsNumericOrNewPath = false;
        int outputArrayIndex = 0;
        boolean outputIntendedPenState = false;

        //Do Parsing
        String currentStr = inputPath[inputArrayIndex];
        boolean isNumeric = isNumeric(currentStr);
        outputIsNumericOrNewPath = isNumeric || currentStr.equals("newpath");

        outputArrayIndex = inputArrayIndex + 1;
        if (isNumeric) {
            outputOrigX = inputOrigX;
            outputOrigY = inputOrigY;
            outputCurrX = inputCurrX;
            outputCurrY = inputCurrY;
            outputDistance = inputDistance;
            outputAngle = inputAngle;
            outputIntendedPenState = inputCurrPenState;
        } else {
            double cmdX = Double.parseDouble(inputPath[inputArrayIndex - 2]);
            double cmdY = Double.parseDouble(inputPath[inputArrayIndex - 1]);
            if (currentStr.equals("line")) {
                HashMap<String, String> distAndAngleInputs = new HashMap<String, String>();
                distAndAngleInputs.put("currX", inputCurrX + "");
                distAndAngleInputs.put("currY", inputCurrY + "");
                distAndAngleInputs.put("nextX", cmdX + "");
                distAndAngleInputs.put("nextY", cmdY + "");

                Map<String, Double> distAndAngleOutputs = computeDistanceAndAngle(distAndAngleInputs);

                outputDistance = distAndAngleOutputs.get("distance");
                outputAngle = distAndAngleOutputs.get("angle");
                outputIntendedPenState = true;
                outputOrigX = inputOrigX;
                outputOrigY = inputOrigY;
                outputCurrX = cmdX;
                outputCurrY = cmdY;
            } else if (currentStr.equals("moveTo")) {
                HashMap<String, String> distAndAngleInputs = new HashMap<String, String>();
                distAndAngleInputs.put("currX", inputCurrX + "");
                distAndAngleInputs.put("currY", inputCurrY + "");
                distAndAngleInputs.put("nextX", cmdX + "");
                distAndAngleInputs.put("nextY", cmdY + "");

                Map<String, Double> distAndAngleOutputs = computeDistanceAndAngle(distAndAngleInputs);

                outputDistance = distAndAngleOutputs.get("distance");
                outputAngle = distAndAngleOutputs.get("angle");
                outputIntendedPenState = false;
                outputOrigX = inputOrigX;
                outputOrigY = inputOrigY;
                outputCurrX = cmdX;
                outputCurrY = cmdY;
            } else {
                //newpath or default
                outputOrigX = Double.parseDouble(inputPath[inputArrayIndex + 1]);
                outputOrigY = Double.parseDouble(inputPath[inputArrayIndex + 2]);
                outputDistance = inputDistance;
                outputAngle = inputAngle;
                outputCurrX = inputCurrX;
                outputCurrY = inputCurrY;
                outputIntendedPenState = inputCurrPenState;

            }
        }

        //Put Outputs in map
        HashMap<String, Object> outputs = new HashMap<String, Object>();
        outputs.put("origX", outputOrigX);
        outputs.put("origY", outputOrigY);
        outputs.put("currX", outputCurrX);
        outputs.put("currY", outputCurrY);
        outputs.put("distance", outputDistance);
        outputs.put("angle", outputAngle);
        outputs.put("index", outputArrayIndex);
        outputs.put("intendedPenState", outputIntendedPenState);
        outputs.put("isNumericOrNewpath", outputIsNumericOrNewPath);
    }

    private boolean isNumeric(String s) {
        try {
            double d = Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private Map<String, Double> computeDistanceAndAngle(Map<String, String> inputs) {
        Double currX = Double.parseDouble(inputs.get("currX"));
        Double currY = Double.parseDouble(inputs.get("currY"));
        Double nextX = Double.parseDouble(inputs.get("nextX"));
        Double nextY = Double.parseDouble(inputs.get("nextY"));

        Double outputDist = Math.sqrt(Math.pow(nextX - currX, 2) + Math.pow(nextY - currY, 2));
        Double outputAngle = Math.toDegrees(Math.atan2(nextY - currY, nextX - currX));
        if (outputAngle < 0) {
            outputAngle += 360;
        }

        HashMap<String, Double> outputs = new HashMap<String, Double>();
        outputs.put("distance", outputDist);
        outputs.put("angle", outputAngle);
        return outputs;
    }
//    public double getHeading() {
//        return currentHeading;
//    }
//
//    public double getDistance() {
//        return distance;
//    }
//
//    public boolean penUp() {
//        return penState;
//    }
//
//    public void parseNextCommand() {
//        switch (path[array_pos]) {
//            case "newpath":
//                break;
//            case "moveto":
//                calcManeuver(path[array_pos - 2], path[array_pos - 1]);
//                iRobot.pickPenUp();
//                turn();
//                move();
//                break;
//            case "lineto":
//                calcManeuver(path[array_pos - 2], path[array_pos - 1]);
//                iRobot.pickPenUp();
//                turn();
//                iRobot.putPenDown();
//                move();
//                break;
//            default:
//                break;
//        }
//        array_pos++;
//    }
//
//    public void calcManeuver(String x, String y) {
//        nextX = Double.parseDouble(x);
//        nextY = Double.parseDouble(y);
//        distance = Math.sqrt((nextX - curX) * (nextX - curX) + (nextY - curY)
//                * (nextY - curY));
//        targetHeading = (int) Math.toDegrees(Math.atan2(nextY - curY, nextX - curX));
//    }
//
//    public void turn() {
//        if ((targetHeading == 0 && currentHeading >= 180) || (targetHeading - currentHeading >= 0 && targetHeading - currentHeading <= 180)) {
//            iRobot.leftTurnManuever(targetHeading - currentHeading);
//        } else {
//            iRobot.rightTurnManuever(targetHeading - currentHeading);
//        }
//        currentHeading = targetHeading;
//    }
//
//    public void move() {
//        iRobot.incForward(distance);
//        curX = nextX;
//        curY = nextY;
//    }
}
