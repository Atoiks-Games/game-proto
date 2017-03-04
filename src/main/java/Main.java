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

import java.awt.Image;
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

    // FIXME:Reduce visibility. I dislike these global variables.
    static double squash_ball_direction = 180;
    static double squash_ball_speed = 5;

    static int py_score;
    static int player_score;

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
		    public void onCollision (Sprite other, GFrame f) {
			if (other == blackBox) {
			    f.jumpToScene (1);
			}
		    }
		};
	    redBox.disable ();
	} catch (IOException | IllegalArgumentException ex) {
	    System.err.println("Failed to load red box");
	    return;
	}

        System.err.println("Loading green box");
        final Sprite greenBox;
        try {
	    greenBox = new Sprite(ImageIO.read(Main.class.getResourceAsStream("green_box.png")),
				  new Point(342, 117));
	    greenBox.setCollidable(true);
        } catch (IOException | IllegalArgumentException ex) {
	    System.err.println("Failed to load green box");
	    return;
        }

        System.err.println("Loading blue box");
        final Sprite blueBox;
        try {
	    blueBox = new Sprite(ImageIO.read(Main.class.getResourceAsStream("blue_box.png")),
				 new Point(342, 317));
	    blueBox.setCollidable(true);
        } catch (IOException | IllegalArgumentException ex) {
	    System.err.println("Failed to load blue box");
	    return;
        }

        System.err.println("Loading squash ball");
        final Sprite squashBall;
        try {
            final Image squash_ball_green = ImageIO.read(Main.class.getResourceAsStream("squash_ball_green.png"));
            final Image squash_ball_blue = ImageIO.read(Main.class.getResourceAsStream("squash_ball_blue.png"));
            final Image squash_ball = ImageIO.read(Main.class.getResourceAsStream("squash_ball.png"));

            squashBall = new Sprite(squash_ball, new Point(342, 217))
                {

                    @Override
                    public void onCollision (Sprite other, GFrame f) {
                        squash_ball_direction += 90 + 180 * Math.random();
                        squash_ball_speed += 0.1;
                        if (other == greenBox) {
			    setImage (squash_ball_green);
                        }
                        if (other == blueBox) {
			    setImage (squash_ball_blue);
                        }
                    }

                    public void update (long milliseconds){
                        double radian_direction = Math.toRadians(squash_ball_direction);
                        double verticle_value = Math.sin(radian_direction);
                        double horizontal_value = Math.cos(radian_direction);
                        translate((int) (horizontal_value * squash_ball_speed),
				  (int) (verticle_value * squash_ball_speed));

                        if (origin.x <= 0){
                            squash_ball_direction += 90 + 180 * Math.random();
                            squash_ball_speed += 0.1;
                            origin.x = 1;
                        }

                        if (origin.y <= 0){
                            squash_ball_direction += 90 + 180 * Math.random();
                            squash_ball_speed += 0.1;
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
			    squash_ball_speed += 0.1;
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
        final Text squash_py_score;
        try {
	    squash_py_score = new Text("PY: 0 Player: 0", new Point(10, 10))
                {
                     @Override
                     public void update (long milliseconds){
                         setText("PY: " + py_score + " Player: " + player_score);
                     }
                };
        } catch (IllegalArgumentException ex) {
	    System.err.println("Failed to load score board");
	    return;
        }


	System.err.println("Initializing scenes");
	final GScene scene_1 = new GScene (floor, blackBox, redBox);
        final GScene scene_2 = new GScene (floor, blueBox, greenBox, squashBall, squash_py_score);

	scene_1.addGKeyListener (new GKeyAdapter()
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

	scene_2.addGKeyListener (new GKeyAdapter()
	    {
		@Override
		public void keyPressed (KeyEvent e, GFrame f) {
		    if (!ignoreKeys.get()) {
			switch (e.getKeyCode())
			    {
			    case KeyEvent.VK_A:
				greenBox.translate (-5, 0);
				break;
			    case KeyEvent.VK_D:
				greenBox.translate (5, 0);
				break;
			    case KeyEvent.VK_W:
				greenBox.translate (0, -5);
				break;
			    case KeyEvent.VK_S:
				greenBox.translate (0, 5);
				break;
			    case KeyEvent.VK_Q:
				javax.swing.JOptionPane.showMessageDialog(null, "Don't let the ball be blue when it hits the back wall! First to 5 wins!");
				break;

			    }
		    }
		}

                public void update (long milliseconds){
                    if(py_score == 11){
                            javax.swing.JOptionPane.showMessageDialog(null, "GEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEET DUNKED ON!!");
                    }
                    if(player_score == 11){
                            //FIXME: Says cannot find this var (f)
                            f.jumpToScene (0);
                    }
                }
	    });


	System.err.println("Launching in GUI mode");
	final GFrame app = new GFrame("Prototype", scene_1, scene_2);
	app.setVisible (true);
    }
}
