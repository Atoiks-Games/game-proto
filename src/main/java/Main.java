/**
 * MIT License
 *
 * Copyright (c) 2017 Paul T.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import com.atoiks.proto.*;
import com.atoiks.proto.event.GKeyAdapter;

import javax.imageio.ImageIO;

import java.awt.Point;
import java.awt.event.KeyEvent;

import java.io.File;
import java.io.IOException;

import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    public final class WeightWrapper<T> {

	private T data;

	private double weight;

	public WeightWrapper (T data, double weight) {
	    this.data = data;
	    this.weight = weight;
	}

	public T get () {
	    return data;
	}

	public void set (T data) {
	    this.data = data;
	}

	public double getWeight () {
	    return weight;
	}

	public void setWeight (double weight) {
	    this.weight = weight;
	}
    }

    public static WeightWrapper<?> weightedRandom (WeightWrapper<?>[] array) {
	double totalWeight = 0;
	for (WeightWrapper<?> w : array)
	    totalWeight += w.getWeight();
	double randomVal = Math.random() * totalWeight;
	for (WeightWrapper<?> w : array) {
	    randomVal -= w.getWeight();
	    if (randomVal <= 0)
		return w;
	}
	return null;
    }

    public static void main(String[] args) {
	// Global-ish variables
	final AtomicBoolean ignoreKeys = new AtomicBoolean(false);

	System.err.println("Loading default floor");
        final Floor floor;
	try {
	    floor = new Floor(ImageIO.read(Main.class.getResourceAsStream("default_floor.bmp")));
	} catch (IOException | IllegalArgumentException ex) {
	    System.err.println("Failed to load default floor");
	    return;
	}
	
	System.err.println("Loading black box");
	final Sprite blackBox;
	try {
	    blackBox = new Sprite(ImageIO.read(Main.class.getResourceAsStream("black_box.bmp")),
				  new Point(0, 0));
	    blackBox.setCollidable (true);
	} catch (IOException | IllegalArgumentException ex) {
	    System.err.println("Failed to load black box");
	    return;
	}
	
	System.err.println("Loading red box");
	final Sprite redBox;
	try {
	    redBox = new Sprite(ImageIO.read(Main.class.getResourceAsStream("red_box.bmp")),
				new Point(50, 75))
		{
		    @Override
		    public void onCollision (Sprite other) {
			if (other == blackBox) {
			    ignoreKeys.set(true);
			    javax.swing.JOptionPane.showMessageDialog(null, "GODDAMN!!!");
			    blackBox.move (0, 0);
			    disable ();
			    ignoreKeys.set(false);
			}
		    }
		};
	    redBox.disable ();
	} catch (IOException | IllegalArgumentException ex) {
	    System.err.println("Failed to load red box");
	    return;
	}

	System.err.println("Initializing scene");
	final GScene scene = new GScene (floor, blackBox, redBox);
	scene.addGKeyListener (new GKeyAdapter()
	    {
		@Override
		public void keyPressed (KeyEvent e, GFrame f) {
		    if (!ignoreKeys.get()) {
			switch (e.getKeyCode())
			    {
			    case KeyEvent.VK_A:
				blackBox.translate (-5, 0);
				redTileConditions ();
				break;
			    case KeyEvent.VK_D:
				blackBox.translate (5, 0);
				redTileConditions ();
				break;
			    case KeyEvent.VK_W:
				blackBox.translate (0, -5);
				redTileConditions ();
				break;
			    case KeyEvent.VK_S:
				blackBox.translate (0, 5);
				redTileConditions ();
				break;
			    case KeyEvent.VK_Q:
				javax.swing.JOptionPane.showMessageDialog(null, "Move around!");
				break;
			    case KeyEvent.VK_N: {
				ignoreKeys.set (true);
				f.setVisible (false);
				final AsmMiniGame game = new AsmMiniGame (new int[]
				    { 0, 1, 2, 3, 4 }, new int[]
				    { 0, 1, 2, 3, 4 });
				game.setVisible (true);
				while (!game.passFlag) {
				    // Do nothing
				}
			        ignoreKeys.set (false);
				break;
			    }
			    }
		    }
		}

	        private void redTileConditions () {
		    ignoreKeys.set (true);
		    if (Math.random() > 0.8) {
			final int newX = (int) (Math.random() * 700);
			final int newY = (int) (Math.random() * 400);
			redBox.move(newX, newY);
		    	redBox.setCollidable (true);
			System.err.println ("X: " + newX + ", Y: " + newY);
		    }
		    ignoreKeys.set (false);
		}
	    });

	System.err.println("Launching in GUI mode");
	final GFrame app = new GFrame("Prototype", scene);
	app.setVisible (true);
    }
}
