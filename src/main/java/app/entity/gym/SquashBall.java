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

import com.atoiks.proto.GFrame;
import com.atoiks.proto.Sprite;

import java.awt.Image;
import java.awt.Point;

public abstract class SquashBall extends Sprite {

    public static final Image GREEN_BALL_IMG = Utils.loadImage ("/squash_ball_green.png");

    public static final Image BLUE_BALL_IMG = Utils.loadImage ("/squash_ball_blue.png");

    public static final Image DEFAULT_BALL_IMG = Utils.loadImage ("/squash_ball.png");

    private final Point spawn;

    protected double angle;

    protected double speed;

    public SquashBall (Point pt) {
	super (DEFAULT_BALL_IMG, new Point (pt));
	spawn = pt;
	resetAngleAndSpeed ();
    }

    public void resetAngleAndSpeed () {
	angle = 180;
	speed = 5;
    }

    public boolean isBlue () {
	return getImage () == BLUE_BALL_IMG;
    }

    public boolean isGreen () {
	return getImage () == GREEN_BALL_IMG;
    }

    @Override
    public void onCollision (Sprite other, GFrame f) {
	angle += 90 + 180 * Math.random ();
	speed += 0.3;
    }

    @Override
    public void update (long mills, GFrame f) {
	final double radAngle = Math.toRadians (angle);
	translate ((int) (Math.cos (radAngle) * speed),
		   (int) (Math.sin (radAngle) * speed));

	if (origin.x < 1) {
	    angle = 180 - angle;
	    speed -= 0.1;
	    origin.x = 1;
	}

	if (origin.y < 1) {
	    angle *= -1;
	    speed -= 0.1;
	    origin.y = 1;
	}

	if (origin.x > GFrame.WIDTH - image.getWidth (null) - 10) {
	    onTakePoint ();

	    image = DEFAULT_BALL_IMG;
	    origin = new Point (spawn);
	    resetAngleAndSpeed ();
	}

	if (origin.y > GFrame.HEIGHT - image.getHeight (null)) {
	    angle *= -1;
	    speed -= 0.1;
	    origin.y = GFrame.HEIGHT - image.getHeight (null);
	}
    }

    public abstract void onTakePoint ();
}
