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

import com.atoiks.proto.GFrame;
import com.atoiks.proto.Sprite2D;

import javax.imageio.ImageIO;

import java.awt.Image;
import java.awt.Point;

import java.io.IOException;

import java.util.concurrent.atomic.AtomicInteger;

public class MainCharacter extends Sprite2D {

    private static final Image[][][] DIRECTION_SHEET = new Image[4][2][];

    static {
	try {
	    // 0 = up, 1 = down, 2 = left, 3 = right
	    // 0 = idle, 1 = move
	    DIRECTION_SHEET[0][0] = new Image[] { ImageIO.read(MainCharacter.class.getResourceAsStream("/main_char/spr_11.png")) };
	    DIRECTION_SHEET[0][1] = new Image[] { ImageIO.read(MainCharacter.class.getResourceAsStream("/main_char/spr_10.png")), ImageIO.read(MainCharacter.class.getResourceAsStream("/main_char/spr_12.png")) };

	    DIRECTION_SHEET[1][0] = new Image[] { ImageIO.read(MainCharacter.class.getResourceAsStream("/main_char/spr_2.png")) };
	    DIRECTION_SHEET[1][1] = new Image[] { ImageIO.read(MainCharacter.class.getResourceAsStream("/main_char/spr_1.png")), ImageIO.read(MainCharacter.class.getResourceAsStream("/main_char/spr_3.png")) };

	    DIRECTION_SHEET[2][0] = new Image[] { ImageIO.read(MainCharacter.class.getResourceAsStream("/main_char/spr_8.png")) };
	    DIRECTION_SHEET[2][1] = new Image[] { ImageIO.read(MainCharacter.class.getResourceAsStream("/main_char/spr_7.png")), ImageIO.read(MainCharacter.class.getResourceAsStream("/main_char/spr_9.png")) };

	    DIRECTION_SHEET[3][0] = new Image[] { ImageIO.read(MainCharacter.class.getResourceAsStream("/main_char/spr_5.png")) };
	    DIRECTION_SHEET[3][1] = new Image[] { ImageIO.read(MainCharacter.class.getResourceAsStream("/main_char/spr_4.png")), ImageIO.read(MainCharacter.class.getResourceAsStream("/main_char/spr_6.png")) };
	} catch (IOException | IllegalArgumentException ex) {
	    System.err.println ("Failed to load main_char spr_1..3");
	    System.exit (1);
	}
    }
    
    private AtomicInteger dx;

    private AtomicInteger dy;

    private int idx = 1;

    public MainCharacter (Point pt, int fps,
			  AtomicInteger dx, AtomicInteger dy) {
	super (0, pt, fps, DIRECTION_SHEET[1][0]);
	this.dx = dx;
	this.dy = dy;
    }

    @Override
    public void update (long mills, GFrame f) {
	super.update (mills, f);
	translate (dx.get(), dy.get());
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
