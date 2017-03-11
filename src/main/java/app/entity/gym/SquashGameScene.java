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

package app.entity.gym;

import app.entity.Utils;
import app.entity.MainCharacter;

import com.atoiks.proto.*;
import com.atoiks.proto.event.GKeyListener;
import com.atoiks.proto.event.GStateListener;

import java.awt.Point;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

public class SquashGameScene
    extends GScene
    implements GKeyListener, GStateListener {

    private static SquashGameScene instance;

    public static final Floor SQUASH_COURT_SIDE = new Floor (Utils.loadImage ("/squash_court_side.png"));

    private PyCharacter py;

    private MainCharacter player;

    private SquashBall ball;

    private int pyScore;

    private int playerScore;

    private boolean ignoreKeys;

    public static SquashGameScene getInstance () {
	if (instance == null) {
	    synchronized (SquashGameScene.class) {
		if (instance == null) {
		    instance = new SquashGameScene ();
		}
	    }
	}
	return instance;
    }

    private void initComponents () {
	Sprite dummy = new Sprite (null, new Point (0, 0))
	    {
		@Override
		public void update (long mills, GFrame f) {
		    if (pyScore >= 11) {
			f.dispose ();
			JOptionPane.showMessageDialog(null, "You're trash");
		    }
		    if (playerScore >= 11) {
			f.jumpToScene (0);
		    }
		}
	    };
	this.instances.add (dummy);

	py = new PyCharacter (new Point (342, 317), 8);
	py.setCollidable (true);
	this.instances.add (py);

	player = new MainCharacter (new Point (300, 380), 8);
	player.enable ();
	this.instances.add (player);

	ball = new SquashBall (new Point (342, 217))
	    {
		@Override
		public void onCollision (Sprite other, GFrame f) {
		    super.onCollision (other, f);

		    if (other == player) setImage (SquashBall.GREEN_BALL_IMG);
		    if (other == py) setImage (SquashBall.BLUE_BALL_IMG);
		}

		@Override
		public void onTakePoint () {
		    if (image == SquashBall.BLUE_BALL_IMG) ++pyScore;
		    if (image == SquashBall.GREEN_BALL_IMG) ++playerScore;
		}
	    };
	ball.enable ();
	this.instances.add (ball);

	final Text scoreBoard = new Text ("PY: 0\nPlayer: 0", new Point (10, 15))
	    {
		@Override
		public void update (long mills, GFrame f) {
		    setText ("PY: " + pyScore + "\nPlayer: " + playerScore);
		}
	    };
	this.instances.add (scoreBoard);
    }

    private SquashGameScene () {
	super (SQUASH_COURT_SIDE);
	initComponents ();

	addGKeyListener (this);
	addGStateListener (this);
    }

    private void playerBoundCheck () {
	final Point loc = player.getLocation ();
	if (loc.y < 0) {
	    player.move (loc.x, 0);
	}
	if (loc.y > GFrame.HEIGHT - 32) {
	    player.move (loc.x, GFrame.HEIGHT - 32);
	}
	if (loc.x < 0) {
	    player.move (0, loc.y);
	}
	if (loc.x > GFrame.WIDTH - 32) {
	    player.move (GFrame.WIDTH - 32, loc.y);
	}
    }

    public int getPyScore () {
	return pyScore;
    }

    public int getPlayerScore () {
	return playerScore;
    }

    public Point getBallLocation () {
	return ball.getLocation ();
    }

    public boolean isBallBlue () {
	return ball.isBlue ();
    }

    @Override
    public void keyTyped (KeyEvent e, GFrame f) {
	// Do nothing
    }

    @Override
    public void keyReleased (KeyEvent e, GFrame f) {
	if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
	    if (f.isPaused ()) f.resume ();
	    else f.pause ();
	    return;
	}
	player.setIdleFrame ();
        player.dx = 0;
	player.dy = 0;
    }

    @Override
    public void keyPressed (KeyEvent e, GFrame f) {
	if (!ignoreKeys) {
	    switch (e.getKeyCode())
		{
		case KeyEvent.VK_A:
		    player.dx = -5;
		    player.directionLeft ();
		    player.setActiveFrame ();
		    break;
		case KeyEvent.VK_D:
		    player.dx = 5;
		    player.directionRight ();
		    player.setActiveFrame ();
		    break;
		case KeyEvent.VK_W:
		    player.dy = -5;
		    player.directionUp ();
		    player.setActiveFrame ();
		    break;
		case KeyEvent.VK_S:
		    player.dy = 5;
		    player.directionDown ();
		    player.setActiveFrame ();
		    break;
		case KeyEvent.VK_Q:
		    JOptionPane.showMessageDialog(null, "Don't let the ball be blue when it hits the back wall! First to 11 wins!");
		    break;
		default:
		    break;
		}
	}
	playerBoundCheck ();
    }

    @Override
    public void onEnter () {
	player.dx = 0;
	player.dy = 0;
	player.move (342, 117);
	ball.resetAngleAndSpeed ();
    }

    @Override
    public void onLeave () {
	// Do nothing
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
