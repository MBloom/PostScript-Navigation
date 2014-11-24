package civilturtles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;

/**
 * Turtles a la LOGO
 * 
 * @author Luther Tychonievich
 */
public class Turtle {
	// / version number based on date of creation
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 20140120L;

	private static final Map<String, BufferedImage> cachedPictures = new TreeMap<String, BufferedImage>();

	private World world;
	private double theta;

	private Point2D.Double location;
	private boolean isdown;
	// private GeneralPath trail; // not used
	private Color color;
	private double shellSize;
	private int pause = 200;

	private static final Color[] base = { Color.BLACK, Color.RED, Color.BLUE,
			Color.MAGENTA, Color.CYAN, };
	private static int baseIndex = 0;

	/**
	 * Makes a new turtle in the center of the world
	 * 
	 * @param w
	 *            the world
	 */
	public Turtle(World w) {
		this(w, 0, 0);
	}

	/**
	 * Makes a new turtle at the specified point within the world
	 * 
	 * @param w
	 *            the world
	 * @param x
	 *            the x coordinate, in pixels; 0 is the center; bigger numbers
	 *            to left
	 * @param y
	 *            the y coordinate, in pixels; 0 is the center; bigger numbers
	 *            down
	 */
	public Turtle(World w, double x, double y) {
		this.location = new Point2D.Double(x + w.centerX, w.centerY - y);
		// this.trail = new GeneralPath(); // not used
		// this.trail.moveTo(this.location.x, this.location.y); // not used
		this.theta = 0;
		this.world = w;
		this.color = Turtle.base[Turtle.baseIndex];
		Turtle.baseIndex = (Turtle.baseIndex + 1) % Turtle.base.length;
		this.penWidth = 1;
		this.isdown = true;
		this.shellSize = 8;
		w.addTurtle(this);
	}
	
	/**
	 * Makes a new turtle at the specified point within the world
	 * 
	 * @param w
	 *            the world
	 * @param x
	 *            the x coordinate, in pixels; 0 is the center; bigger numbers
	 *            to left
	 * @param y
	 *            the y coordinate, in pixels; 0 is the center; bigger numbers
	 *            down
	 * @param t
	 * 			  the initial heading, in degrees; 0 is right, rotates counter clockwise
	 */
	public Turtle(World w, double x, double y, double t) {
		this.location = new Point2D.Double(x + w.centerX, w.centerY - y);
		// this.trail = new GeneralPath(); // not used
		// this.trail.moveTo(this.location.x, this.location.y); // not used
		this.theta = t;
		this.world = w;
		this.color = Turtle.base[Turtle.baseIndex];
		Turtle.baseIndex = (Turtle.baseIndex + 1) % Turtle.base.length;
		this.penWidth = 1;
		this.isdown = true;
		this.shellSize = 8;
		w.addTurtle(this);
	}

	/**
	 * Moves the turtle in the direction it is facing
	 * 
	 * @param d
	 *            the number of pixels to move
	 */
	public void forward(double d) {
		this.cornerGoTo(this.location.x + Math.cos(this.theta) * d,
				this.location.y + Math.sin(this.theta) * d);
	}

	public void incForward(double d) {
		while (d > 1) {
			forward(1);
			d--;
		}
		forward(d);
		this.pause(500);
	}

	/**
	 * Moves the turtle in the opposite direction from the one it is facing
	 * 
	 * @param d
	 *            the number of pixels to move
	 */
	public void backward(double d) {
		this.forward(-d);
	}

	public void incBackward(double d) {
		while (d > 1) {
			backward(1);
			d--;
		}
		backward(d);
	}

	/**
	 * Turns the turtle clockwise in place
	 * 
	 * @param degrees
	 *            the number of degrees to turn
	 */
	public void right(double degrees) {
		this.theta += Math.PI * degrees / 180;
		while (this.theta > Math.PI)
			this.theta -= Math.PI * 2;
		while (this.theta <= -Math.PI)
			this.theta += Math.PI * 2;
		world.turtleMoved();
		this.pause();
	}

	public void incRight(double degrees) {
		while (degrees > 1) {
			right(1);
			degrees--;
		}
		right(degrees);
	}

	public void incLeft(double degrees) {
		while (degrees > 1) {
			left(1);
			degrees--;
		}
		left(degrees);
	}

	public void left90TurnManuever() {
		this.pause(500);
		this.isdown = false;
		this.incForward(10.5);
		this.incLeft(90);
		this.incForward(10.5);
		this.isdown = true;
		this.pause(500);
	}

	public void leftTurnManuever(double d) {
		double forward = 1.7332 * Math.pow(Math.E, 0.0203 * d);
		this.pause(500);
		
		System.out.println("\tMove Past Corner (Left)");
		this.incForward(forward);
		
		System.out.println("\tTurn Left (CCW)");
		this.incLeft(d);
		
		System.out.println("\tTurned Far Enough Left");		
		System.out.println("\tMove to Corner");
		this.incForward(forward);
		
		this.pause(500);
	}

	public void rightTurnManuever(double degrees) {
		double backward = 1.7332 * Math.pow(Math.E, 0.0203 * degrees);
		this.pause(500);
		
		System.out.println("\tMove Back from Corner (Right)");
		this.incBackward(backward);
		
		System.out.println("\tTurn Right (CW)");
		this.incRight(degrees);
		
		System.out.println("\tTurned Far Enough Right");
		System.out.println("\tMove back to Corner");
		this.incBackward(backward);
		
		this.pause(500);
	}

	/**
	 * Turns the turtle counterclockwise in place
	 * 
	 * @param degrees
	 *            the number of degrees to turn
	 */
	public void left(double degrees) {
		this.right(-degrees);
	}

	/**
	 * Stops the turtle from leaving a trail
	 */
	public void pickPenUp() {
		this.isdown = false;
	}

	/**
	 * Causes the turtle to leave a trail
	 */
	public void putPenDown() {
		this.isdown = true;
	}

	/**
	 * Check the pen state
	 * 
	 * @return true if the pen is down, false otherwise
	 */
	public boolean isPenDown() {
		return this.isdown;
	}

	/**
	 * Draws the arrow that represents the turtle. Should only be called by
	 * World class
	 * 
	 * @param g
	 *            the graphics object to draw with
	 */
	void _how_world_draw_turtles(Graphics2D g) {

		// The following draws a picture of a turtle

		// three shapes
		GeneralPath back = new GeneralPath(); // the bigger shell
		GeneralPath back2 = new GeneralPath(); // the paler inner shell
		GeneralPath body = new GeneralPath(); // the head, legs, and tail
		double c = Math.cos(this.theta);
		double s = Math.sin(this.theta);
		double x = this.location.x - (world.centerX - 20);
		double y = this.location.y + (world.centerY - 20);
		double w = this.shellSize;

		body.moveTo(x + w * 0.9 * c + w * 0.4 * s, y + w * 0.9 * s - w * 0.4
				* c);
		body.lineTo(x + w * 1.8 * c + w * 0.3 * s, y + w * 1.8 * s - w * 0.3
				* c);

		body.closePath();

		back.moveTo(x + w * 1.2 * c, y + w * 1.2 * s);
		back.curveTo(x + w * 1.2 * c + w * 0.6 * s, y + w * 1.2 * s - w * 0.6
				* c, x + w * 0.7 * c + w * s, y + w * 0.7 * s - w * c, x + w
				* s, y - w * c);
		back.curveTo(x - w * 0.7 * c + w * s, y - w * 0.7 * s - w * c, x - w
				* 1.2 * c + w * 0.6 * s, y - w * 1.2 * s - w * 0.6 * c, x - w
				* 1.2 * c, y - w * 1.2 * s);
		back.curveTo(x - w * 1.2 * c - w * 0.6 * s, y - w * 1.2 * s + w * 0.6
				* c, x - w * 0.7 * c - w * s, y - w * 0.7 * s + w * c, x - w
				* s, y + w * c);
		back.curveTo(x + w * 0.7 * c - w * s, y + w * 0.7 * s + w * c, x + w
				* 1.2 * c - w * 0.6 * s, y + w * 1.2 * s + w * 0.6 * c, x + w
				* 1.2 * c, y + w * 1.2 * s);

		w *= 0.7;
		back2.moveTo(x + w * 1.2 * c, y + w * 1.2 * s);
		back2.curveTo(x + w * 1.2 * c + w * 0.6 * s, y + w * 1.2 * s - w * 0.6
				* c, x + w * 0.7 * c + w * s, y + w * 0.7 * s - w * c, x + w
				* s, y - w * c);
		back2.curveTo(x - w * 0.7 * c + w * s, y - w * 0.7 * s - w * c, x - w
				* 1.2 * c + w * 0.6 * s, y - w * 1.2 * s - w * 0.6 * c, x - w
				* 1.2 * c, y - w * 1.2 * s);
		back2.curveTo(x - w * 1.2 * c - w * 0.6 * s, y - w * 1.2 * s + w * 0.6
				* c, x - w * 0.7 * c - w * s, y - w * 0.7 * s + w * c, x - w
				* s, y + w * c);
		back2.curveTo(x + w * 0.7 * c - w * s, y + w * 0.7 * s + w * c, x + w
				* 1.2 * c - w * 0.6 * s, y + w * 1.2 * s + w * 0.6 * c, x + w
				* 1.2 * c, y + w * 1.2 * s);

		int gap = 48;
		Color midColor = new Color(Math.max(
				Math.min(color.getRed(), 255 - gap), gap), Math.max(
				Math.min(color.getGreen(), 255 - gap), gap), Math.max(
				Math.min(color.getBlue(), 255 - gap), gap));

		g.setColor(midColor);
		g.fill(back);
		if (isdown) {
			g.setColor(Color.RED);
		} else {
			g.setColor(Color.WHITE);
		}
		g.fill(back2);
	}

	/**
	 * Place a picture on the screen where the turtle currently is
	 * 
	 * @param filename
	 *            the file name or URL of the image to be drawn
	 * @param size
	 *            how big the image should be in pixels
	 * @return true if the image was found, false otherwise
	 */
	public boolean dropPicture(String filename, double size) {
		try {
			BufferedImage pic;
			if (cachedPictures.containsKey(filename)) {
				pic = cachedPictures.get(filename);
			} else {
				try {
					pic = ImageIO.read(new URL(filename).openStream());
				} catch (Throwable ex) {
					pic = ImageIO.read(new File(filename));
				}
				cachedPictures.put(filename, pic);
			}
			double scale = size / Math.max(pic.getWidth(), pic.getHeight());

			AffineTransform af = new AffineTransform();
			af.translate(this.location.x, this.location.y);
			af.rotate(this.theta + Math.PI / 2);
			af.translate(-size / 2, -size / 2);
			af.scale(scale, scale);
			this.world.drawImage(pic, af);
			this.pause();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		world.turtleMoved();
		this.pause();
	}

	private double penWidth;

	public double getPenWidth() {
		return penWidth;
	}

	public void setPenWidth(double width) {
		if (width <= 0)
			throw new IllegalArgumentException("Width must be positive");
		this.penWidth = width;
	}

	public double getShellSize() {
		return shellSize;
	}

	public void setShellSize(double shellSize) {
		this.shellSize = shellSize;
		world.turtleMoved();
		this.pause();
	}

	/**
	 * Find out what direction the Turtle is facing
	 * 
	 * @return angle in degrees; 0 is right, 90 is up, etc
	 */
	public double getHeading() {
		return theta * 180 / Math.PI;
	}

	/**
	 * Set the direction the Turtle is facing
	 * 
	 * @param angle
	 *            in degrees; 0 is right, 90 is up, etc
	 */
	public void setHeading(double angle) {
		this.theta = (-1 * angle) * Math.PI / 180;
		world.turtleMoved();
		this.pause();
	}

	/**
	 * Find out where the turtle is located
	 * 
	 * @return The location of the turtle. (0,0) is the center of the screen, +x
	 *         is rightward, +y is downward.
	 */
	public Point2D getLocation() {
		return new Point2D.Double(this.location.x - world.centerX,
				(-this.location.y - world.centerY));
	}

	/**
	 * Move the turtle to a particular location. It might leave a trail
	 * depending on if the pen is down or not.
	 * 
	 * @param where
	 *            The new location for the turtle. (0,0) is the top left of the
	 *            screen, +x is rightward, +y is downward.
	 */
	protected void cornerGoTo(Point2D where) {
		this.cornerGoTo(where.getX(), where.getY());
	}

	/**
	 * Move the turtle to a particular location. It might leave a trail
	 * depending on if the pen is down or not.
	 * 
	 * @param where
	 *            The new location for the turtle. (0,0) is the center of the
	 *            screen, +x is rightward, +y is downward.
	 */
	public void goTo(Point2D where) {
		this.cornerGoTo(where.getX() + world.centerX,
				(-where.getY() + world.centerY));
	}

	/**
	 * Move the turtle to a particular location. It might leave a trail
	 * depending on if the pen is down or not.
	 * 
	 * @param x
	 *            The new x location for the turtle. 0 is the center of the
	 *            screen, bigger numbers to the right
	 * @param y
	 *            The new y location for the turtle. 0 is the center of the
	 *            screen, bigger numbers lower down
	 */
	protected void cornerGoTo(double x, double y) {
		double ox = this.location.x;
		double oy = this.location.y;
		this.location.x = x;
		this.location.y = y;
		if (this.isdown) {
			world.drawLine(this.location, ox, oy, this.penWidth, this.color);
			world.turtleMoved();
			this.pause();
		} else {
			world.turtleMoved();
			this.pause();
		}

	}

	public void goTo(double x, double y) {
		this.cornerGoTo(x + world.centerX, (-y + world.centerY));
	}

	/**
	 * Seconds to pause between each turtle movement
	 * 
	 * @return the seconds currently paused
	 */
	public double getDelay() {
		return pause;
	}

	/**
	 * Seconds to pause between each turtle movement
	 * 
	 * @param seconds
	 *            The seconds to pause
	 */
	public void setDelay(double seconds) {
		this.pause = (int) (seconds);
	}

	private void pause() {
		try {
			Thread.sleep(this.pause);
		} catch (InterruptedException e) {
		}
	}

	private void pause(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
		}

	}
}
