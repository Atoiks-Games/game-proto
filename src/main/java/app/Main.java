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

package app;

import com.atoiks.proto.*;
import com.atoiks.proto.event.GKeyAdapter;
import com.atoiks.proto.event.GStateAdapter;

import app.entity.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;

import java.io.File;
import java.io.IOException;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
        new Main().run();
    }

    public void run () {
	final SquashGameScene scene2 = SquashGameScene.getInstance ();

	final AtomicBoolean ignoreKeys = new AtomicBoolean(false);
	final AtomicInteger playerSpeedX = new AtomicInteger(0);
        final AtomicInteger playerSpeedY = new AtomicInteger(0);

	System.err.println("Loading default floor");
        final Floor floor;
	try {
	    floor = new Floor(ImageIO.read(Main.class.getResourceAsStream("/default_floor.bmp")));
	} catch (IOException | IllegalArgumentException ex) {
	    System.err.println("Failed to load default floor");
	    return;
	}

        System.err.println("Loading main_char spr_1..3");
	final MainCharacter mainChar = new MainCharacter(new Point(300, 380), 8,
							 playerSpeedX,
							 playerSpeedY);
	mainChar.enable ();

	System.err.println("Loading red box");
	final Sprite redBox;
	try {
	    redBox = new Sprite(ImageIO.read(Main.class.getResourceAsStream("/red_box.bmp")),
				new Point(50, 75))
		{
		    @Override
		    public void onCollision (Sprite other, GFrame f) {
			if (other == mainChar) {
			    if (scene2.getPlayerScore() == 0 && scene2.getPyScore() == 0) {
			        JOptionPane.showMessageDialog (null, "You found me! NOW PLAY AGAINST ME!!!", "PY", JOptionPane.WARNING_MESSAGE, new ImageIcon(PyCharacter.DIRECTION_SHEET[1][0][0]));
				f.jumpToScene (1);
			    }
			}
		    }
		};
	    redBox.disable ();
	} catch (IOException | IllegalArgumentException ex) {
	    System.err.println("Failed to load red box");
	    return;
	}

	System.err.println("Loading squash court");
        final Group squashCourt;
	try {
	    final Floor rawFloor = new Floor(ImageIO.read(Main.class.getResourceAsStream("/squash_court/spr1.png")));
	    final Sprite rightBorder = new Sprite(ImageIO.read(Main.class.getResourceAsStream("/squash_court/border_right.png")),
						  new Point (700 - 186, 0))
		{
		    @Override
		    public void onCollision (Sprite comp, GFrame f) {
			if (comp == mainChar) {
			    // Has to be colliding on the left of border
			    mainChar.translate (-playerSpeedX.get() - 2, 0);
			}
		    }
		};
	    rightBorder.enable ();
	    final Sprite leftBorder = new Sprite(ImageIO.read(Main.class.getResourceAsStream("/squash_court/border_left.png")),
						 new Point (0, 52))
		{
		    @Override
		    public void onCollision (Sprite comp, GFrame f) {
			if (comp == mainChar) {
			    if (playerSpeedY.get() != 0) {
				mainChar.translate (0, -playerSpeedY.get() - 2);
			    } else {
				mainChar.translate (-playerSpeedX.get() + 2, 0);
			    }
			}
		    }
		};
	    leftBorder.enable ();
	    squashCourt = new Group (rawFloor, rightBorder, leftBorder);
	} catch (IOException | IllegalArgumentException ex) {
	    System.err.println("Failed to load squash court");
	    return;
	}

	System.err.println("Loading green box");
	final Sprite greenBox;
	try {
	    greenBox = new Sprite(ImageIO.read(Main.class.getResourceAsStream("/green_box.png")),
				  new Point(0, 0))
		{
		    @Override
		    public void onCollision (Sprite other, GFrame f) {
			if (other == mainChar) {
			    setCollidable (false);
			    ignoreKeys.set (true);
			    f.setVisible (false);
			    JOptionPane.showMessageDialog (null, "Solve this coding puzzle! If you don't, you cannot quit", "OOPS!", JOptionPane.ERROR_MESSAGE);
			    final Object dummy = new Object ();
			    final AsmMiniGame game = new AsmMiniGame (new int[]
				{ 0, 1, 2, 3, 4 }, new int[]
				{ 0, 1, 2, 3, 4 }, dummy);
			    game.setVisible (true);
			    new Thread (new Runnable ()
				{
				    @Override
				    public void run () {
					synchronized (dummy) {
					    while (!game.passFlag) {
						try {
						    dummy.wait ();
						} catch (InterruptedException ex) {
						}
					    }
					}
					f.setVisible (true);
					ignoreKeys.set (false);
				    }
				}).start ();
			}
		    }
		};
	    greenBox.setVisible (false);
	    greenBox.setCollidable (true);
	} catch (IOException | IllegalArgumentException ex) {
	    System.err.println("Failed to load red box");
	    return;
	}

	System.err.println("Initializing scenes");
	final GScene scene1 = new GScene (squashCourt, mainChar, redBox, greenBox);

	scene1.addGKeyListener (new GKeyAdapter()
	    {
		@Override
                public void keyReleased (KeyEvent e, GFrame f) {
		    if (e.getKeyCode () == KeyEvent.VK_ESCAPE) {
			if (f.isPaused ())
			    f.resume ();
			else
			    f.pause ();
			return;
		    }
		    mainChar.setIdleFrame ();
		    playerSpeedX.set(0);
		    playerSpeedY.set(0);
                }

                @Override
		public void keyPressed (KeyEvent e, GFrame f) {
		    if (!ignoreKeys.get()) {
			switch (e.getKeyCode())
			    {
			    case KeyEvent.VK_A:
                                playerSpeedX.set(-5);
				playerSpeedY.set(0);

				mainChar.directionLeft ();
				mainChar.setActiveFrame ();
				redTileConditions ();
				break;
			    case KeyEvent.VK_D:
                                playerSpeedX.set(5);
				playerSpeedY.set(0);

				mainChar.directionRight ();
				mainChar.setActiveFrame ();
				redTileConditions ();
				break;
			    case KeyEvent.VK_W:
				playerSpeedX.set(0);
				playerSpeedY.set(-5);

				mainChar.directionUp ();
				mainChar.setActiveFrame ();
				redTileConditions ();
				break;
			    case KeyEvent.VK_S:
				playerSpeedX.set(0);
				playerSpeedY.set(5);

				mainChar.directionDown ();
				mainChar.setActiveFrame ();
				redTileConditions ();
			 	break;
			    case KeyEvent.VK_Q:
			        JOptionPane.showMessageDialog(null, "Move around!");
				break;
			    }

			Point loc = mainChar.getLocation ();
			if (loc.y < 0) {
			    mainChar.move (loc.x, 0);
			}
			if (loc.y > 380) {
			    mainChar.move (loc.x, 380);
			}

			loc = mainChar.getLocation ();
			if (loc.x > 300 && loc.x < 333 && loc.y > 370) {
			    System.err.println ("Leaving squash court");
			    return;
			}
		    }
		}

	        private void redTileConditions () {
		    ignoreKeys.set (true);
		    if (Math.random() > 0.8) {
			// Domain: [150, 500]
			// Range: [72, 420]
			final int newX = (int) (Math.random() * (500 - 150) + 150);
			final int newY = (int) (Math.random() * (420 - 72) + 72);
			redBox.move(newX, newY);
		    	redBox.setCollidable (true);
			System.err.println ("X: " + newX + ", Y: " + newY);
		    }
		    ignoreKeys.set (false);
		}
	    });
	scene1.addGStateListener (new GStateAdapter()
	    {
		Point mainCharLastLoc;

		@Override
		public void onEnter () {
		    if (mainCharLastLoc == null) return;
		    mainChar.setLocation (mainCharLastLoc);
		    playerSpeedX.set(0);
		    playerSpeedY.set(0);
		}

		@Override
		public void onLeave () {
		    mainCharLastLoc = mainChar.getLocation ();
		}

		@Override
		public void onPause () {
		    ignoreKeys.set (true);
		}

		@Override
		public void onResume () {
		    ignoreKeys.set (false);
		}
	    });

	System.err.println("Launching in GUI mode");
	final GFrame app = new GFrame("Prototype", scene1, scene2);
	app.setVisible (true);
    }
}
