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

    public double squashBallDirection = 180;
    public double squashBallSpeed = 5;

    public Sprite squashBall;

    public int pyScore;
    public int playerScore;

    public Image squashBallBlue;

    private static Image resSquashBallBlue;

    static {
        try {
            resSquashBallBlue = ImageIO.read(Main.class.getResourceAsStream("/squash_ball_blue.png"));
        } catch (IOException | IllegalArgumentException ex) {
            System.err.println ("Failed to load blue squash ball");
            System.exit (1);
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }

    public Main () {
	squashBallBlue = resSquashBallBlue;
    }

    public void run () {
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
			    if (playerScore == 0 && pyScore == 0) {
			        JOptionPane.showMessageDialog (null, "You found me! NOW PLAY AGAINST ME!!!", "PY", JOptionPane.WARNING_MESSAGE);
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

        System.err.println("Loading squash court 2");
        final Floor squashCourtSide;
        try {
            squashCourtSide = new Floor(ImageIO.read(Main.class.getResourceAsStream("/squash_court_side.png")));
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

        System.err.println("Loading py");
        final PyCharacter pyChar = new PyCharacter (new Point (342, 317),
						    8, this);
	pyChar.setCollidable (true);

        System.err.println("Loading squash ball");
        try {
            final Image squashBallGreen = ImageIO.read(Main.class.getResourceAsStream("/squash_ball_green.png"));
            final Image squashBallDefault = ImageIO.read(Main.class.getResourceAsStream("/squash_ball.png"));

            squashBall = new Sprite(squashBallDefault, new Point(342, 217))
                {

                    @Override
                    public void onCollision (Sprite other, GFrame f) {
                        squashBallDirection += 90 + 180 * Math.random();
                        squashBallSpeed += 0.3;
                        if (other == mainChar) {
			    setImage (squashBallGreen);
                        }
                        if (other == pyChar) {
			    setImage (squashBallBlue);
                        }
                    }

                    public void update (long milliseconds, GFrame f) {
                        final double radianDirection = Math.toRadians(squashBallDirection);
                        double verticalValue = Math.sin(radianDirection);
                        double horizontalValue = Math.cos(radianDirection);
                        translate((int) (horizontalValue * squashBallSpeed),
				  (int) (verticalValue * squashBallSpeed));

                        if (origin.x <= 0) {
			    squashBallDirection = 180 - squashBallDirection;
                            squashBallSpeed -= 0.1;
                            origin.x = 1;
                        }

                        if (origin.y <= 0) {
			    squashBallDirection *= -1;
                            squashBallSpeed -= 0.1;
                            origin.y = 1;
                        }

                        if (origin.x >= GFrame.WIDTH - image.getWidth(null) - 10) {
                            if (image == squashBallBlue) {
                                ++ pyScore;
                            }
                            if (image == squashBallGreen) {
                                ++ playerScore;
                            }
                            image = squashBallDefault;
                            origin.x = 342;
                            origin.y = 217;
                            squashBallDirection = 180;
                            squashBallSpeed = 5;
                        }

                        if (origin.y >= GFrame.HEIGHT - image.getHeight(null) - 30) {
			    squashBallDirection *= -1;
			    squashBallSpeed -= 0.1;
			    origin.y = GFrame.HEIGHT - image.getHeight(null) - 31;
                        }
                    }
                };
	    squashBall.setCollidable(true);
        } catch (IOException | IllegalArgumentException ex) {
            System.err.println("Failed to load squash ball");
            return;
        }

        System.err.println("Making the squash score board");
        final Text squashScoreBoard = new Text("PY: 0\nPlayer: 0",
					       new Point(10, 15))
	    {
		@Override
		public void update (long milliseconds, GFrame f){
		    setText("PY: " + pyScore + "\nPlayer: " + playerScore);
		}
	    };

	final Sprite dummy = new Sprite (null, new Point (0, 0))
	    {
		@Override
		public void update (long milliseconds, GFrame f){
		    if (pyScore >= 11) {
			// Destroy window
			f.dispose ();
		        JOptionPane.showMessageDialog(null, "You're trash...");
		    }
		    if(playerScore >= 11) {
			f.jumpToScene (0);
		    }
		}
	    };

	System.err.println("Initializing scenes");
	final GScene scene1 = new GScene (squashCourt, mainChar, redBox, greenBox);
	final GScene scene2 = new GScene (squashCourtSide, dummy, pyChar, mainChar, squashBall, squashScoreBoard);

	scene1.addGKeyListener (new GKeyAdapter()
	    {
		@Override
                public void keyReleased (KeyEvent e, GFrame f){
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
		}

		@Override
		public void onLeave () {
		    mainCharLastLoc = mainChar.getLocation ();
		}
	    });

	scene2.addGKeyListener (new GKeyAdapter()
	    {
                @Override
                public void keyReleased (KeyEvent e, GFrame f) {
		    mainChar.setIdleFrame ();
                    playerSpeedX.set(0);
                    playerSpeedY.set(0);
		    boundCheck ();
                }

                @Override
		public void keyPressed (KeyEvent e, GFrame f) {
		    if (!ignoreKeys.get()) {
			switch (e.getKeyCode())
			    {
			    case KeyEvent.VK_A:
				playerSpeedX.set(-5);
				mainChar.directionLeft ();
				mainChar.setActiveFrame ();
				break;
			    case KeyEvent.VK_D:
				playerSpeedX.set(5);
				mainChar.directionRight ();
				mainChar.setActiveFrame ();
				break;
			    case KeyEvent.VK_W:
				playerSpeedY.set(-5);
				mainChar.directionUp ();
				mainChar.setActiveFrame ();
				break;
			    case KeyEvent.VK_S:
				playerSpeedY.set(5);
				mainChar.directionDown ();
				mainChar.setActiveFrame ();
				break;
			    case KeyEvent.VK_Q:
				JOptionPane.showMessageDialog(null, "Don't let the ball be blue when it hits the back wall! First to 11 wins!");
				break;
			    }
		    }
		    boundCheck ();
		}

		private void boundCheck () {
		    final Point loc = mainChar.getLocation ();
		    if (loc.y < 0) {
			mainChar.move (loc.x, 0);
		    }
		    if (loc.y > GFrame.HEIGHT - 64) {
			mainChar.move (loc.x, GFrame.HEIGHT - 64);
		    }
		    if (loc.x < 0) {
			mainChar.move (0, loc.y);
		    }
		    if (loc.x > GFrame.WIDTH - 32) {
			mainChar.move (GFrame.WIDTH - 32, loc.y);
		    }
		}
	    });
	scene2.addGStateListener (new GStateAdapter()
	    {
		@Override
		public void onEnter () {
		    playerSpeedX.set (0);
		    playerSpeedY.set (0);
		    mainChar.move (342, 117);
		}
	    });

	System.err.println("Launching in GUI mode");
	final GFrame app = new GFrame("Prototype", scene1, scene2);
	app.setVisible (true);
    }
}
