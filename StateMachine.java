package civilturtles;

import java.awt.Point;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class StateMachine {

    private static final String KEY_IS_NUMERIC_OR_NEWPATH = "isNumericOrNewpath";
	private static final String KEY_INTENDED_PEN_STATE = "intendedPenState";
	private static final String KEY_NEXT_Y = "nextY";
	private static final String KEY_NEXT_X = "nextX";
	private static final String KEY_INDEX = "index";
	private static final String KEY_CURR_PEN_STATE = "currPenState";
	private static final String KEY_ANGLE = "angle";
	private static final String KEY_DISTANCE = "distance";
	private static final String KEY_CURR_Y = "currY";
	private static final String KEY_CURR_X = "currX";
	private static final String KEY_ORIG_Y = "origY";
	private static final String KEY_ORIG_X = "origX";
	private static final String KEY_PATH = "path";
	
	private Turtle iRobot;
    private int arrayIndex;
    private double origX, origY, currX, currY, nextX, nextY;
    private double angle, startHeading, accumAngleAtStartOfManeuver;
    private double distance, distanceAtStartOfManeuver;
    private double netAngle, netDistance;
    private boolean currentPenState, intendedPenState;
    private boolean isNumeric;
    private Scanner epsFile;
    private String[] path;

    public StateMachine(Turtle t, Scanner f, Point p, double dir) {
        iRobot = t;
        netAngle = 0;
        netDistance = 0;
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
            } else if (arrayIndex-1 == path.length) {
            	continue;
            }
            setManeuverStartAngle();
            if (angle != startHeading) {
            	turnManeuver();
            } else {
            	// don't turn
            }
            movePen();
            setManeuverStartDistance();
            move();
        }
    }

    private void readFileFromServer() {
    	System.out.println("Read File From Server");
    	
        readFile();
    }

    private void initialize() {
    	System.out.println("Initialize");
    	
        arrayIndex = 0;
        nextX = 0;
        nextY = 0;
        currentPenState = false;
        intendedPenState = false;
        isNumeric = false;
        iRobot.pickPenUp();
        
    }

    private void parseCommand() {
    	System.out.println("Parse Command");
    	
        //Put Outputs in map
        HashMap<String, Object> inputs = new HashMap<String, Object>();
        inputs.put(KEY_PATH, path);
        inputs.put(KEY_CURR_PEN_STATE, currentPenState);
        inputs.put(KEY_ORIG_X, origX);
        inputs.put(KEY_ORIG_Y, origY);
        inputs.put(KEY_CURR_X, currX);
        inputs.put(KEY_CURR_Y, currY);
        inputs.put(KEY_ANGLE, angle);
        inputs.put(KEY_DISTANCE, distance);
        inputs.put(KEY_INDEX, arrayIndex);

        Map<String, Object> outputs = commandParser(inputs);

        startHeading = angle;
        intendedPenState = (boolean) outputs.get(KEY_INTENDED_PEN_STATE);
        origX = (double) outputs.get(KEY_ORIG_X);
        origY = (double) outputs.get(KEY_ORIG_Y);
        currX = (double) outputs.get(KEY_CURR_X);
        currY = (double) outputs.get(KEY_CURR_Y);
        angle = (double) outputs.get(KEY_ANGLE);
        distance = (double) outputs.get(KEY_DISTANCE);
        arrayIndex = (int) outputs.get(KEY_INDEX);
        isNumeric = (boolean) outputs.get(KEY_IS_NUMERIC_OR_NEWPATH);
    }

    private void setManeuverStartAngle() {
    	System.out.println("Set Maneuver Start Angle");
    	
    	accumAngleAtStartOfManeuver = netAngle;
    }
    
    private void turnManeuver() {
    	System.out.println("Turn Maneuver");
    	
    	System.out.println("\tPen Up");
    	iRobot.pickPenUp();
    	currentPenState = false;
    	
    	if ( (angle == 0 && startHeading >= 180) || ( angle != 0 && ( angle - startHeading >= 0 && angle - startHeading <= 180)) || ( (angle + 360) - startHeading >= 0 && (angle + 360 ) - startHeading <= 180))  {	// left
    		double turn = Math.abs(angle - startHeading);
    		if(turn > 180) {
    			turn = 360 - turn;
    		}
    		iRobot.leftTurnManuever(turn);
    	} else if ( ( angle == 0 && startHeading < 180) || ( angle != 0 && (angle - startHeading < 0 || angle - startHeading > 180)) ) {	// right
    		double turn = angle - startHeading;
    		if(turn > 180) {
    			turn = 360 - turn;
    		}
    		iRobot.rightTurnManuever(Math.abs(turn));
    	}
    	
    	netAngle += angle;
    	startHeading = angle;
    }
    
    private void movePen() {
    	System.out.println("Move Pen");
    	
    	motorMover();
    	currentPenState = intendedPenState;
    }
    
    private void setManeuverStartDistance() {
    	System.out.println("Set Maneuver Start Distance");
    	
    	distanceAtStartOfManeuver = netDistance;
    }
    
    private void move() {
    	System.out.println("Moving");
    	iRobot.incForward(distance);
    	netDistance += distance;
    	
    	if(netDistance == (distance + distanceAtStartOfManeuver)) {	// redundant check for guard
    		System.out.println("Stop Moving");
    	}
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

    private Map<String, Object> commandParser(Map<String, Object> inputs) {
        //get inputs from map
        String[] inputPath = (String[]) inputs.get(KEY_PATH);
        double inputOrigX = (double) inputs.get(KEY_ORIG_X);
        double inputOrigY = (double) inputs.get(KEY_ORIG_Y);
        double inputCurrX = (double) inputs.get(KEY_CURR_X);
        double inputCurrY = (double) inputs.get(KEY_CURR_Y);
        double inputDistance = (double) inputs.get(KEY_DISTANCE);
        double inputAngle = (double) inputs.get(KEY_ANGLE);
        boolean inputCurrPenState = (boolean) inputs.get(KEY_CURR_PEN_STATE);
        int inputArrayIndex = (int) inputs.get(KEY_INDEX);

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
            if (currentStr.equals("line")) {
            	double cmdX = Double.parseDouble(inputPath[inputArrayIndex - 2]);
                double cmdY = Double.parseDouble(inputPath[inputArrayIndex - 1]);
                
                HashMap<String, String> distAndAngleInputs = new HashMap<String, String>();
                distAndAngleInputs.put(KEY_CURR_X, inputCurrX + "");
                distAndAngleInputs.put(KEY_CURR_Y, inputCurrY + "");
                distAndAngleInputs.put(KEY_NEXT_X, cmdX + "");
                distAndAngleInputs.put(KEY_NEXT_Y, cmdY + "");

                Map<String, Double> distAndAngleOutputs = computeDistanceAndAngle(distAndAngleInputs);

                outputDistance = distAndAngleOutputs.get(KEY_DISTANCE);
                outputAngle = distAndAngleOutputs.get(KEY_ANGLE);
                outputIntendedPenState = true;
                outputOrigX = inputOrigX;
                outputOrigY = inputOrigY;
                outputCurrX = cmdX;
                outputCurrY = cmdY;
            } else if (currentStr.equals("moveto")) {
            	double cmdX = Double.parseDouble(inputPath[inputArrayIndex - 2]);
                double cmdY = Double.parseDouble(inputPath[inputArrayIndex - 1]);
                
                HashMap<String, String> distAndAngleInputs = new HashMap<String, String>();
                distAndAngleInputs.put(KEY_CURR_X, inputCurrX + "");
                distAndAngleInputs.put(KEY_CURR_Y, inputCurrY + "");
                distAndAngleInputs.put(KEY_NEXT_X, cmdX + "");
                distAndAngleInputs.put(KEY_NEXT_Y, cmdY + "");

                Map<String, Double> distAndAngleOutputs = computeDistanceAndAngle(distAndAngleInputs);

                outputDistance = distAndAngleOutputs.get(KEY_DISTANCE);
                outputAngle = distAndAngleOutputs.get(KEY_ANGLE);
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
        outputs.put(KEY_ORIG_X, outputOrigX);
        outputs.put(KEY_ORIG_Y, outputOrigY);
        outputs.put(KEY_CURR_X, outputCurrX);
        outputs.put(KEY_CURR_Y, outputCurrY);
        outputs.put(KEY_DISTANCE, outputDistance);
        outputs.put(KEY_ANGLE, outputAngle);
        outputs.put(KEY_INDEX, outputArrayIndex);
        outputs.put(KEY_INTENDED_PEN_STATE, outputIntendedPenState);
        outputs.put(KEY_IS_NUMERIC_OR_NEWPATH, outputIsNumericOrNewPath);
        
        return outputs;
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
        Double currX = Double.parseDouble(inputs.get(KEY_CURR_X));
        Double currY = Double.parseDouble(inputs.get(KEY_CURR_Y));
        Double nextX = Double.parseDouble(inputs.get(KEY_NEXT_X));
        Double nextY = Double.parseDouble(inputs.get(KEY_NEXT_Y));

        Double outputDist = Math.sqrt(Math.pow(nextX - currX, 2) + Math.pow(nextY - currY, 2));
        Double outputAngle = Math.toDegrees(Math.atan2(nextY - currY, nextX - currX));
        
        if (outputAngle < 0) {
            outputAngle += 360;
        }

        HashMap<String, Double> outputs = new HashMap<String, Double>();
        outputs.put(KEY_DISTANCE, outputDist);
        outputs.put(KEY_ANGLE, outputAngle);
        return outputs;
    }

    private void motorMover() {
    	boolean moveMotor = currentPenState ^ intendedPenState;
    	
    	if (moveMotor) {
    		if (intendedPenState) {
    			iRobot.putPenDown();
    		} else {
    			iRobot.pickPenUp();
    		}
    	}
    }

}
