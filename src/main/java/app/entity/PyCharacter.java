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

import app.Main;

import com.atoiks.proto.GFrame;
import com.atoiks.proto.Sprite;
import com.atoiks.proto.Sprite2D;

import javax.imageio.ImageIO;

import java.awt.Image;
import java.awt.Point;

import java.io.IOException;

public class PyCharacter extends Sprite2D {

    private static final Image[][][] DIRECTION_SHEET = new Image[4][2][];

    static {
	try {
	    // 0 = up, 1 = down, 2 = left, 3 = right
	    // 0 = idle, 1 = move
	    DIRECTION_SHEET[0][0] = new Image[] { ImageIO.read(PyCharacter.class.getResourceAsStream("/py/spr_11.png")) };
	    DIRECTION_SHEET[0][1] = new Image[] { ImageIO.read(PyCharacter.class.getResourceAsStream("/py/spr_10.png")), ImageIO.read(PyCharacter.class.getResourceAsStream("/py/spr_12.png")) };

	    DIRECTION_SHEET[1][0] = new Image[] { ImageIO.read(PyCharacter.class.getResourceAsStream("/py/spr_2.png")) };
	    DIRECTION_SHEET[1][1] = new Image[] { ImageIO.read(PyCharacter.class.getResourceAsStream("/py/spr_1.png")), ImageIO.read(PyCharacter.class.getResourceAsStream("/py/spr_3.png")) };

	    DIRECTION_SHEET[2][0] = new Image[] { ImageIO.read(PyCharacter.class.getResourceAsStream("/py/spr_8.png")) };
	    DIRECTION_SHEET[2][1] = new Image[] { ImageIO.read(PyCharacter.class.getResourceAsStream("/py/spr_7.png")), ImageIO.read(PyCharacter.class.getResourceAsStream("/py/spr_9.png")) };

	    DIRECTION_SHEET[3][0] = new Image[] { ImageIO.read(PyCharacter.class.getResourceAsStream("/py/spr_5.png")) };
	    DIRECTION_SHEET[3][1] = new Image[] { ImageIO.read(PyCharacter.class.getResourceAsStream("/py/spr_4.png")), ImageIO.read(PyCharacter.class.getResourceAsStream("/py/spr_6.png")) };
	} catch (IOException | IllegalArgumentException ex) {
	    System.err.println ("Failed to load py spr_1..3");
	    System.exit (1);
	}
    }

    private int idx = 1;

    private Main session;

    public PyCharacter (Point pt, int fps, Main main) {
	super (0, pt, fps, DIRECTION_SHEET[1][0]);
	this.session = main;
    }

    @Override
    public void update (long mills, GFrame f) {
        super.update (mills, f);
	if (session.squashBall.getImage() != session.squash_ball_blue) {
	    final double dx = session.squashBall.getLocation().x - origin.x;
	    final double dy = session.squashBall.getLocation().y - origin.y;
	    if(dx == 0) {
		return;
	    }
	    final double target_angle = Math.atan2(dy, dx);
	    double speed = 4;
	    if (session.player_score == 10) {
		speed = 10;
	    }
	    translate ((int) (speed * Math.cos(target_angle)),
		       (int) (speed * Math.sin(target_angle)));
	    if (Math.abs (dx) > Math.abs (dy)) {
		if (dx > 0) directionRight ();
		else directionLeft ();
	    } else {
		if (dy > 0) directionDown ();
		else directionUp ();
	    }
	    setActiveFrame ();
	} else {
	    setIdleFrame ();
	}
    }

    public void directionUp () {
	idx = 0;
    }

    public void directionDown () {
	idx = 1;
    }

    public void directionLeft () {
	idx = 2;
    }

    public void directionRight () {
	idx = 3;
    }

    public void setIdleFrame () {
	setFrames (DIRECTION_SHEET[idx][0]);
    }

    public void setActiveFrame () {
	setFrames (DIRECTION_SHEET[idx][1]);
    }
}
