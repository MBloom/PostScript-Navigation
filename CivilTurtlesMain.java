/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package civilturtles;

import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class CivilTurtlesMain {

    private static final int HEIGHT = 460;
	private static final int WIDTH = 640;

	public static void main(String[] args) throws InterruptedException {
       JFileChooser fc = new JFileChooser();
       FileNameExtensionFilter filter = new FileNameExtensionFilter(
               "TEXT FILES", "txt", "text");
       fc.setFileFilter(filter);
       int returnVal = fc.showOpenDialog(null);
       if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                @SuppressWarnings("resource")
                Scanner inFile = new Scanner(fc.getSelectedFile());
                // Scanner inFile = new Scanner(new File("route.txt"));
                World foundation = new World(WIDTH, HEIGHT, Color.WHITE);
                

                int startX = 0; // (int)(Math.random() * (WIDTH/2));
                int startY = 0; // (int)(Math.random() * (HEIGHT/2));
                
                System.out.println("Starting point: " + startX + ", " + startY);
                
                Turtle mrRoboto = new Turtle(foundation, startX, startY);
                Thread.sleep(1000); // wait 1sec after opening before starting
                // maneuvers
                mrRoboto.setDelay(5); // change this to speed up or slow down the
                
                
                StateMachine gibson = new StateMachine(mrRoboto, inFile, new Point(startX, startY), 0);
                gibson.run();
                
            } catch (FileNotFoundException e) {
                System.out.println("Sorry - that file couldn't be opened!");
            }
       }
    }

}
