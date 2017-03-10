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

import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;

import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class SquashCourtScene
    extends GScene
    implements GKeyListener, GStateListener {

    private static SquashCourtScene instance;

    public static final Image RED_BOX = Utils.loadImage ("/red_box.bmp");

    public static final Image GREEN_BOX = Utils.loadImage ("/green_box.png");

    public static final Image SQUASH_COURT = Utils.loadImage ("/squash_court/spr1.png");

    public static final Image BORDER_RIGHT = Utils.loadImage ("/squash_court/border_right.png");

    public static final Image BORDER_LEFT = Utils.loadImage ("/squash_court/border_left.png");

    private Group court;

    private MainCharacter player;

    private Sprite redBox;

    private Sprite greenBox;

    private boolean ignoreKeys;

    private AtomicInteger playerSpeedX;

    private AtomicInteger playerSpeedY;

    public static SquashCourtScene getInstance () {
	if (instance == null) {
	    synchronized (SquashCourtScene.class) {
		if (instance == null) {
		    instance = new SquashCourtScene ();
		}
	    }
	}
	return instance;
    }

    private void initComponents () {
	playerSpeedX = new AtomicInteger (0);
	playerSpeedY = new AtomicInteger (0);
	player = new MainCharacter (new Point (300, 380), 8,
				    playerSpeedX, playerSpeedY);
	player.enable ();
	this.instances.add (player);

	redBox = new Sprite (RED_BOX, new Point (50, 75))
	    {
		@Override
		public void onCollision (Sprite other, GFrame f) {
		    if (other == player) {
			final SquashGameScene locScene = SquashGameScene.getInstance ();
			if (locScene.getPlayerScore() == 0
			    && locScene.getPyScore() == 0) {
			    JOptionPane.showMessageDialog (null, "You found me! NOW PLAY AGAINST ME!!!", "PY", JOptionPane.WARNING_MESSAGE, new ImageIcon (PyCharacter.DIRECTION_SHEET[1][0][0]));
			    f.jumpToScene (1);
			}
		    }
		}
	    };
	redBox.disable ();
	this.instances.add (redBox);

	greenBox = new Sprite (GREEN_BOX, new Point (0, 0))
	    {
		@Override
		public void onCollision (Sprite other, GFrame f) {
		    if (other == player) {
			setCollidable (false);
			ignoreKeys = true;
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
				    ignoreKeys = false;
				    onEnter ();
				}
			    }).start ();
		    }
		}
	    };
	greenBox.setVisible (false);
	greenBox.setCollidable (true);
	this.instances.add (greenBox);
    }

    private SquashCourtScene () {
	super (null);

	final Floor base = new Floor (SQUASH_COURT);
	final Sprite rightBorder = new Sprite (BORDER_RIGHT,
					       new Point (700 - 186, 0))
	    {
		@Override
		public void onCollision (Sprite other, GFrame f) {
		    if (other == player) {
			player.translate (-playerSpeedX.get() - 2, 0);
		    }
		}
	    };
	rightBorder.enable ();

	final Sprite leftBorder = new Sprite (BORDER_LEFT,
					      new Point (0, 52))
	    {
		@Override
		public void onCollision (Sprite other, GFrame f) {
		    if (other == player) {
			if (playerSpeedY.get() != 0) {
			    player.translate (0, -playerSpeedY.get() - 2);
			} else {
			    player.translate (-playerSpeedX.get() + 2, 0);
			}
		    }
		}
	    };
	leftBorder.enable ();
	court = new Group (base, rightBorder, leftBorder);
	// Set court as floor!
	this.instances.set (0, court);

	initComponents ();

	addGKeyListener (this);
	addGStateListener (this);
    }

    @Override
    public void keyTyped (KeyEvent e, GFrame f) {
    }

    @Override
    public void keyReleased (KeyEvent e, GFrame f) {
	if (e.getKeyCode () == KeyEvent.VK_ESCAPE) {
	    if (f.isPaused ()) f.resume ();
	    else f.pause ();
	    return;
	}
        player.setIdleFrame ();
	playerSpeedX.set(0);
	playerSpeedY.set(0);
    }

    @Override
    public void keyPressed (KeyEvent e, GFrame f) {
	if (!ignoreKeys) {
	    switch (e.getKeyCode())
		{
		case KeyEvent.VK_A:
		    playerSpeedX.set(-5);
		    playerSpeedY.set(0);

		    player.directionLeft ();
		    player.setActiveFrame ();
		    redTileConditions ();
		    break;
		case KeyEvent.VK_D:
		    playerSpeedX.set(5);
		    playerSpeedY.set(0);

		    player.directionRight ();
		    player.setActiveFrame ();
		    redTileConditions ();
		    break;
		case KeyEvent.VK_W:
		    playerSpeedX.set(0);
		    playerSpeedY.set(-5);

		    player.directionUp ();
		    player.setActiveFrame ();
		    redTileConditions ();
		    break;
		case KeyEvent.VK_S:
		    playerSpeedX.set(0);
		    playerSpeedY.set(5);

		    player.directionDown ();
		    player.setActiveFrame ();
		    redTileConditions ();
		    break;
		case KeyEvent.VK_Q:
		    JOptionPane.showMessageDialog(null, "Move around!");
		    break;
		default:
		    break;
		}

	    Point loc = player.getLocation ();
	    if (loc.y < 0) {
		player.move (loc.x, 0);
	    }
	    if (loc.y > 380) {
		player.move (loc.x, 380);
	    }

	    loc = player.getLocation ();
	    if (loc.x > 300 && loc.x < 333 && loc.y > 370) {
		System.err.println ("Leaving squash court");
		return;
	    }
	}
    }

    private void redTileConditions () {
	ignoreKeys = true;
	if (Math.random() > 0.8) {
	    // Domain: [150, 500]
	    // Range: [72, 420]
	    final int newX = (int) (Math.random() * (500 - 150) + 150);
	    final int newY = (int) (Math.random() * (420 - 72) + 72);
	    redBox.move (newX, newY);
	    redBox.setCollidable (true);
	    System.err.println ("X: " + newX + ", Y: " + newY);
	}
	ignoreKeys = false;
    }

    @Override
    public void onEnter () {
	player.setIdleFrame ();
	playerSpeedX.set(0);
	playerSpeedY.set(0);
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
