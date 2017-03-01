import javax.imageio.ImageIO;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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
	
	System.err.println("Launching in GUI mode");
	final GFrame app = new GFrame("Prototype");
	app.addKeyListener (new KeyListener()
	    {
		@Override
		public void keyTyped (KeyEvent e) {
		}

		@Override
		public void keyPressed (KeyEvent e) {
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
			    }
		    }
		}

		@Override
		public void keyReleased (KeyEvent e) {
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
	app.pushBackGComp (blackBox);
	app.pushBackGComp (redBox);
	app.setFloor (floor);
	app.setVisible (true);
    }
}