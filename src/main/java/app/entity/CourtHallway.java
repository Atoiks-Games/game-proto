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

package app.entity;

import com.atoiks.proto.*;
import com.atoiks.proto.event.GKeyListener;
import com.atoiks.proto.event.GStateListener;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

public class CourtHallway
    extends GScene
    implements GKeyListener, GStateListener {

    private static CourtHallway instance;

    public static final Image HALLWAY = Utils.loadImage ("/court_hallway/spr1.png");

    public static final Image TOP_RIGHT = Utils.loadImage ("/court_hallway/top_right.png");

    public static final Image BOT_RIGHT = Utils.loadImage ("/court_hallway/bot_right.png");

    public static final Image LEFT = Utils.loadImage ("/court_hallway/left.png");

    public static final Image HORIZ = Utils.loadImage ("/court_hallway/horiz.png");

    public static final Image VERT = Utils.loadImage ("/court_hallway/vert.png");

    public static final Point TOP_SPAWN = new Point (64, 12);

    public static final Point BOT_SPAWN = new Point (64, GFrame.HEIGHT - 12);

    public static final Point RIGHT_SPAWN = new Point (600, GFrame.HEIGHT / 2 - 16);

    private MainCharacter player;

    private boolean ignoreKeys;

    public Point playerSpawn;

    public static CourtHallway getInstance () {
	if (instance == null) {
	    synchronized (CourtHallway.class) {
		if (instance == null) {
		    instance = new CourtHallway ();
		}
	    }
	}
	return instance;
    }

    private void initComponents () {
	player = new MainCharacter (new Point (300, 380), 8);
	player.enable ();
	this.instances.add (player);

	final Sprite topDoor = new Sprite (HORIZ, new Point (56, 0))
	    {
		@Override
		public void onCollision (Sprite other, GFrame f) {
		    if (other == player) {
			f.jumpToScene (0);
		    }
		}
	    };
	topDoor.enable ();
	this.instances.add (topDoor);

	final Sprite botDoor = new Sprite (HORIZ, new Point (56, GFrame.HEIGHT - 1))
	    {
		@Override
		public void onCollision (Sprite other, GFrame f) {
		    if (other == player) {
			f.jumpToScene (2);
		    }
		}
	    };
	botDoor.enable ();
	this.instances.add (botDoor);

	final Sprite rightDoor = new Sprite (VERT, new Point (GFrame.WIDTH - VERT.getWidth (null), 181))
	    {
		@Override
		public void onCollision (Sprite other, GFrame f) {
		    if (other == player) {
			System.err.println ("Leaving hallway");
		    }
		}
	    };
	rightDoor.enable ();
	this.instances.add (rightDoor);
    }

    private CourtHallway () {
	super (null);

	final Sprite topRight = new Sprite (TOP_RIGHT, new Point (GFrame.WIDTH - TOP_RIGHT.getWidth (null), 0))
	    {
		@Override
		public void onCollision (Sprite other, GFrame f) {
		    if (other == player) {
			if (player.dy == 0) {
			    player.translate (-player.dx - 2, 0);
			} else {
			    player.translate (0, -player.dy + 4);
			}
		    }
		}
	    };
	topRight.enable ();

	final Sprite botRight = new Sprite (BOT_RIGHT, new Point (GFrame.WIDTH - BOT_RIGHT.getWidth (null), GFrame.HEIGHT - BOT_RIGHT.getHeight (null)))
	    {
		@Override
		public void onCollision (Sprite other, GFrame f) {
		    if (other == player) {
			if (player.dy == 0) {
			    player.translate (-player.dx - 2, 0);
			} else {
			    player.translate (0, -player.dy - 1);
			}
		    }
		}
	    };
	botRight.enable ();

	final Sprite left = new Sprite (LEFT, new Point (0, 47))
	    {
		@Override
		public void onCollision (Sprite other, GFrame f) {
		    if (other == player) {
			if (player.dy == 0) {
			    player.translate (-player.dx + 2, 0);
			} else if (player.dy > 0) {
			    // Player was trying to move down
			    player.translate (0, -player.dy - 1);
			} else {
			    // Player was trying to move up
			    player.translate (0, -player.dy + 1);
			}
		    }
		}
	    };
	left.enable ();

	this.instances.set (0, new Group (new Floor (HALLWAY),
					  topRight, botRight, left));

	initComponents ();

	addGKeyListener (this);
	addGStateListener (this);
    }

    @Override
    public void keyTyped (KeyEvent evt, GFrame f) {
    }

    @Override
    public void keyPressed (KeyEvent evt, GFrame f) {
	if (!ignoreKeys) {
	    switch (evt.getKeyCode ())
		{
		case KeyEvent.VK_A:
		    player.dx = -3;
		    player.dy = 0;

		    player.directionLeft ();
		    player.setActiveFrame ();
		    break;
		case KeyEvent.VK_D:
		    player.dx = 3;
		    player.dy = 0;

		    player.directionRight ();
		    player.setActiveFrame ();
		    break;
		case KeyEvent.VK_W:
		    player.dx = 0;
		    player.dy = -3;

		    player.directionUp ();
		    player.setActiveFrame ();
		    break;
		case KeyEvent.VK_S:
		    player.dx = 0;
		    player.dy = 3;

		    player.directionDown ();
		    player.setActiveFrame ();
		    break;
		case KeyEvent.VK_Q:
		    JOptionPane.showMessageDialog(null, "You are in a hallway, move around!");
		    break;
		default:
		    break;
		}
	    boundCheck ();
	}
    }

    @Override
    public void keyReleased (KeyEvent evt, GFrame f) {
	if (evt.getKeyCode () == KeyEvent.VK_ESCAPE) {
	    if (f.isPaused ()) f.resume ();
	    else f.pause ();
	    return;
	}
        player.setIdleFrame ();
	player.dx = 0;
	player.dy = 0;
	boundCheck ();
    }

    private void boundCheck () {
	ignoreKeys = true;
	final Point loc = player.getLocation ();
	if (loc.x < 0) {
	    player.move (0, loc.y);
	}
	if (loc.x > GFrame.WIDTH - 32) {
	    player.move (GFrame.WIDTH - 32, loc.y);
	}
	if (loc.y < 0) {
	    player.move (loc.x, 0);
	}
	if (loc.y > GFrame.HEIGHT - 32) {
	    player.move (loc.x, GFrame.HEIGHT - 32);
	}
	ignoreKeys = false;
    }

    @Override
    public void onEnter () {
	if (playerSpawn != null) player.setLocation (playerSpawn);
	player.dx = 0;
	player.dy = 0;
    }

    @Override
    public void onLeave () {
    }

    @Override
    public void onPause () {
	ignoreKeys = true;
    }

    @Override
    public void onResume () {
	ignoreKeys = false;
    }
}
