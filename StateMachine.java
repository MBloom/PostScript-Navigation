package civilturtles;

public class StateMachine {

	private Turtle iRobot;
	private int targetHeading, currentHeading, array_pos;
	private double distance, curX, curY, nextX, nextY;
	private boolean penState;
	private String[] path;

	public StateMachine(Turtle t, String[] p) {
		iRobot = t;
		path = p;
		array_pos = 0;
		penState = false;
	}

	public int getHeading() {
		return currentHeading;
	}

	public double getDistance() {
		return distance;
	}

	public boolean penUp() {
		return penState;
	}

	public void parseNextCommand() {
		switch (path[array_pos]) {
		case "newpath":
			break;
		case "moveto":
			calcManeuver(path[array_pos - 2], path[array_pos - 1]);
			iRobot.pickPenUp();
			turn();
			move();
			break;
		case "lineto":
			calcManeuver(path[array_pos - 2], path[array_pos - 1]);
			iRobot.pickPenUp();
			turn();
			iRobot.putPenDown();
			move();
			break;
		default:
			break;
		}
		array_pos++;
	}

	public void calcManeuver(String x, String y) {
		nextX = Double.parseDouble(x);
		nextY = Double.parseDouble(y);
		distance = Math.sqrt((nextX - curX) * (nextX - curX) + (nextY - curY)
				* (nextY - curY));
		targetHeading = (int) Math.toDegrees(Math.atan2(nextY - curY, nextX - curX));
	}

	public void turn() {
		if ( (targetHeading == 0 && currentHeading >= 180) || (targetHeading-currentHeading >= 0 && targetHeading-currentHeading <= 180))
			iRobot.leftTurnManuever(targetHeading-currentHeading);
		else
			iRobot.rightTurnManuever(targetHeading-currentHeading);
		currentHeading = targetHeading;
	}

	public void move() {
		iRobot.incForward(distance);
		curX = nextX;
		curY = nextY;
	}
}
