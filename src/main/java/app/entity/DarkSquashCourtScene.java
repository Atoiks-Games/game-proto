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
import java.awt.Point;
import java.awt.event.KeyEvent;

import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JOptionPane;

public class DarkSquashCourtScene
    extends GScene
    implements GKeyListener, GStateListener {

    private static DarkSquashCourtScene instance;

    private final Shader greyShader = new Shader (new Color (0x2A, 0x2A, 0x2A, (int) (255 * 0.75)));

    private MainCharacter player;

    private AtomicInteger playerSpeedX;

    private AtomicInteger playerSpeedY;

    private boolean ignoreKeys;

    public static DarkSquashCourtScene getInstance () {
	if (instance == null) {
	    synchronized (DarkSquashCourtScene.class) {
		if (instance == null) {
		    instance = new DarkSquashCourtScene ();
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

	Sprite light = new Sprite (SquashCourtScene.GREEN_BOX,
				   new Point (GFrame.WIDTH - 15, 0))
	    {
		@Override
		public void onCollision (Sprite other, GFrame f) {
		    if (other == player) {
			player.translate (-playerSpeedX.get() - 2, 0);
			toggleLight ();
		    }
		}
	    };
	light.setVisible (false);
	light.setCollidable (true);
	this.instances.add (light);

	// This has to be the last item added
	this.instances.add (greyShader);
    }

    private DarkSquashCourtScene () {
	super (SquashGameScene.SQUASH_COURT_SIDE);
	initComponents ();

	addGKeyListener (this);
	addGStateListener (this);
    }

    public boolean isLightOff () {
	return greyShader.isVisible ();
    }

    public void toggleLight () {
        greyShader.setVisible (!greyShader.isVisible ());
    }

    private void playerBoundCheck () {
	final Point loc = player.getLocation ();
	if (loc.y < 0) {
	    player.move (loc.x, 0);
	}
	if (loc.y > GFrame.HEIGHT - 64) {
	    player.move (loc.x, GFrame.HEIGHT - 64);
	}
	if (loc.x < 0) {
	    player.move (0, loc.y);
	}
	if (loc.x > GFrame.WIDTH - 32) {
	    player.move (GFrame.WIDTH - 32, loc.y);
	}
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
		    player.directionLeft ();
		    player.setActiveFrame ();
		    break;
		case KeyEvent.VK_D:
		    playerSpeedX.set(5);
		    player.directionRight ();
		    player.setActiveFrame ();
		    break;
		case KeyEvent.VK_W:
		    playerSpeedY.set(-5);
		    player.directionUp ();
		    player.setActiveFrame ();
		    break;
		case KeyEvent.VK_S:
		    playerSpeedY.set(5);
		    player.directionDown ();
		    player.setActiveFrame ();
		    break;
		case KeyEvent.VK_Q:
		    JOptionPane.showMessageDialog(null, "??? ??? ??? ??? ???");
		    break;
		default:
		    break;
		}
	}
	playerBoundCheck ();
    }

    @Override
    public void onEnter () {
	playerSpeedX.set(0);
	playerSpeedY.set(0);
	player.move (300, 380);
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
