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
import com.atoiks.proto.event.GStateAdapter;

import entity.*;

import javax.imageio.ImageIO;

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

    // FIXME:Reduce visibility. I dislike these global variables.
    double squash_ball_direction = 180;
    double squash_ball_speed = 5;

    Sprite squashBall;

    int py_score;
    int player_score;

    static Image squash_ball_blue;

    static {
        try {
            squash_ball_blue = ImageIO.read(Main.class.getResourceAsStream("squash_ball_blue.png"));
        } catch (IOException | IllegalArgumentException ex) {
            System.err.println ("Failed to load blue squash ball");
            System.exit (1);
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }

    public Main () {
    }

    public void run () {
	final AtomicBoolean ignoreKeys = new AtomicBoolean(false);
	final AtomicInteger player_speed_x = new AtomicInteger(0);
        final AtomicInteger player_speed_y = new AtomicInteger(0);

	System.err.println("Loading default floor");
        final Floor floor;
	try {
	    floor = new Floor(ImageIO.read(Main.class.getResourceAsStream("default_floor.bmp")));
	} catch (IOException | IllegalArgumentException ex) {
	    System.err.println("Failed to load default floor");
	    return;
	}

	System.err.println("Loading squash court");
        final Floor squashCourt;
	try {
	    squashCourt = new Floor(ImageIO.read(Main.class.getResourceAsStream("squash_court.png")));
	} catch (IOException | IllegalArgumentException ex) {
	    System.err.println("Failed to load squash court");
	    return;
	}

        System.err.println("Loading squash court 2");
        final Floor squashCourtSide;
        try {
            squashCourtSide = new Floor(ImageIO.read(Main.class.getResourceAsStream("squash_court_side.png")));
        } catch (IOException | IllegalArgumentException ex) {
            System.err.println("Failed to load squash court");
            return;
        }

        System.err.println("Loading main_char spr_1..3");
	final MainCharacter mainChar = new MainCharacter(new Point(300, 380), 8,
							 player_speed_x,
							 player_speed_y);
	mainChar.enable ();

	System.err.println("Loading red box");
	final Sprite redBox;
	try {
	    redBox = new Sprite(ImageIO.read(Main.class.getResourceAsStream("red_box.bmp")),
				new Point(50, 75))
		{
		    @Override
		    public void onCollision (Sprite other, GFrame f) {
			if (other == mainChar) {
			    if (player_score == 0 && py_score == 0) {
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

        System.err.println("Loading blue box");
        final Sprite blueBox;
        try {
	    blueBox = new Sprite(ImageIO.read(Main.class.getResourceAsStream("blue_box.png")),
				 new Point(342, 317))
		{
		    @Override
		    public void update(long milliseconds, GFrame f){
                        if (squashBall.getImage() != squash_ball_blue) {

                                double x_distance_to_target = squashBall.getLocation().x - origin.x;
                                double y_distance_to_target = squashBall.getLocation().y - origin.y;
                                if(x_distance_to_target == 0){
                                        return;
                                }
                                double target_angle = Math.atan2(y_distance_to_target, x_distance_to_target);
                                double speed = 4;
                                if (player_score == 10){
                                        speed = 10;
                                }
                                translate ((int) (speed * Math.cos(target_angle)), (int) (speed * Math.sin(target_angle)));
                        }
		    }
		};
	    blueBox.setCollidable(true);
        } catch (IOException | IllegalArgumentException ex) {
	    System.err.println("Failed to load blue box");
	    return;
        }

        System.err.println("Loading squash ball");
        try {
            final Image squash_ball_green = ImageIO.read(Main.class.getResourceAsStream("squash_ball_green.png"));
            final Image squash_ball = ImageIO.read(Main.class.getResourceAsStream("squash_ball.png"));

            squashBall = new Sprite(squash_ball, new Point(342, 217))
                {

                    @Override
                    public void onCollision (Sprite other, GFrame f) {
                        squash_ball_direction += 90 + 180 * Math.random();
                        squash_ball_speed += 0.3;
                        if (other == mainChar) {
			    setImage (squash_ball_green);
                        }
                        if (other == blueBox) {
			    setImage (squash_ball_blue);
                        }
                    }

                    public void update (long milliseconds, GFrame f){
                        double radian_direction = Math.toRadians(squash_ball_direction);
                        double verticle_value = Math.sin(radian_direction);
                        double horizontal_value = Math.cos(radian_direction);
                        translate((int) (horizontal_value * squash_ball_speed),
				  (int) (verticle_value * squash_ball_speed));

                        if (origin.x <= 0){
                            squash_ball_direction += 90 + 180 * Math.random();
                            squash_ball_speed -= 0.1;
                            origin.x = 1;
                        }

                        if (origin.y <= 0){
                            squash_ball_direction += 90 + 180 * Math.random();
                            squash_ball_speed -= 0.1;
                            origin.y = 1;
                        }

                        if (origin.x >= GFrame.WIDTH - image.getWidth(null) - 10) {
                            if (image == squash_ball_blue) {
                                ++ py_score;
                            }
                            if (image == squash_ball_green) {
                                ++ player_score;
                            }
                            image = squash_ball;
                            origin.x = 342;
                            origin.y = 217;
                            squash_ball_direction = 180;
                            squash_ball_speed = 5;
                        }

                        if (origin.y >= GFrame.HEIGHT - image.getHeight(null) - 30){
			    squash_ball_direction += 90 + 180 * Math.random();
			    squash_ball_speed -= 0.1;
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
        final Text squash_py_score = new Text("PY: 0\nPlayer: 0",
					      new Point(10, 15))
	    {
		@Override
		public void update (long milliseconds, GFrame f){
		    setText("PY: " + py_score + "\nPlayer: " + player_score);
		}
	    };

	final Sprite dummy = new Sprite (null, new Point (0, 0))
	    {
		@Override
		public void update (long milliseconds, GFrame f){
		    if (py_score >= 11) {
			// Destroy window
			f.dispose ();
			javax.swing.JOptionPane.showMessageDialog(null, "You're trash...");
		    }
		    if(player_score >= 11) {
			f.jumpToScene (0);
		    }
		}
	    };

	System.err.println("Initializing scenes");
	final GScene scene_1 = new GScene (squashCourt, mainChar, redBox);
	final GScene scene_2 = new GScene (squashCourtSide, dummy, blueBox, mainChar, squashBall, squash_py_score);

	scene_1.addGKeyListener (new GKeyAdapter()
	    {
		@Override
                public void keyReleased (KeyEvent e, GFrame f){
		    mainChar.setIdleFrame ();
		    player_speed_x.set(0);
		    player_speed_y.set(0);
                }

                @Override
		public void keyPressed (KeyEvent e, GFrame f) {
		    if (!ignoreKeys.get()) {
			switch (e.getKeyCode())
			    {
			    case KeyEvent.VK_A:
                                player_speed_x.set(-5);
				mainChar.directionLeft ();
				mainChar.setActiveFrame ();
				redTileConditions ();
				break;
			    case KeyEvent.VK_D:
                                player_speed_x.set(5);
				mainChar.directionRight ();
				mainChar.setActiveFrame ();
				redTileConditions ();
				break;
			    case KeyEvent.VK_W:
				player_speed_y.set(-5);
				mainChar.directionUp ();
				mainChar.setActiveFrame ();
				redTileConditions ();
				break;
			    case KeyEvent.VK_S:
				player_speed_y.set(5);
				mainChar.directionDown ();
				mainChar.setActiveFrame ();
				redTileConditions ();
			 	break;
			    case KeyEvent.VK_Q:
				javax.swing.JOptionPane.showMessageDialog(null, "Move around!");
				break;
			    case KeyEvent.VK_N: {
				ignoreKeys.set (true);
				f.setVisible (false);
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
				break;
			    }
			    }

			Point loc = mainChar.getLocation ();
			if (loc.y < 0) {
			    mainChar.move (loc.x, 0);
			}
			if (loc.y > 380) {
			    mainChar.move (loc.x, 380);
			}
			if (loc.x < 155) {
			    mainChar.move (155, loc.y);
			}
			if (loc.x > 480) {
			    mainChar.move (480, loc.y);
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
	scene_1.addGStateListener (new GStateAdapter()
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

	scene_2.addGKeyListener (new GKeyAdapter()
	    {
                @Override
                public void keyReleased (KeyEvent e, GFrame f) {
		    mainChar.setIdleFrame ();
                    player_speed_x.set(0);
                    player_speed_y.set(0);
		    boundCheck ();
                }

                @Override
		public void keyPressed (KeyEvent e, GFrame f) {
		    if (!ignoreKeys.get()) {
			switch (e.getKeyCode())
			    {
			    case KeyEvent.VK_A:
				player_speed_x.set(-5);
				mainChar.directionLeft ();
				mainChar.setActiveFrame ();
				break;
			    case KeyEvent.VK_D:
				player_speed_x.set(5);
				mainChar.directionRight ();
				mainChar.setActiveFrame ();
				break;
			    case KeyEvent.VK_W:
				player_speed_y.set(-5);
				mainChar.directionUp ();
				mainChar.setActiveFrame ();
				break;
			    case KeyEvent.VK_S:
				player_speed_y.set(5);
				mainChar.directionDown ();
				mainChar.setActiveFrame ();
				break;
			    case KeyEvent.VK_Q:
				javax.swing.JOptionPane.showMessageDialog(null, "Don't let the ball be blue when it hits the back wall! First to 5 wins!");
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
	scene_2.addGStateListener (new GStateAdapter()
	    {
		@Override
		public void onEnter () {
		    mainChar.move (342, 117);
		}
	    });

	System.err.println("Launching in GUI mode");
	final GFrame app = new GFrame("Prototype", scene_1, scene_2);
	app.setVisible (true);
    }
}
