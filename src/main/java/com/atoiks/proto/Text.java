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

package com.atoiks.proto;

import java.awt.Font;
import java.awt.Point;
import java.awt.Graphics;

public class Text implements GComponent {

    protected String text;

    protected Point origin;

    protected boolean visible;

    public Text (String text, Point origin) {
	this.text = text;
	this.origin = origin;
	this.visible = true;
    }

    @Override
    public void render (Graphics g) {
	if (visible) {
		final Font f = g.getFont ();
		final int pt = f.getSize ();
		final String[] segs = text.split ("\n");
		for (int i = 0; i < segs.length; ++i) {
			g.drawString (segs[i], origin.x, origin.y + pt * i);
		}
	}
    }

    @Override
    public boolean containsPoint (Point p) {
        return false;
    }

    public void setText (String text) {
	this.text = text;
    }

    public String getText () {
	return this.text;
    }

    public void setVisible (boolean flag) {
	this.visible = flag;
    }

    public boolean isVisible () {
	return visible;
    }

    public void move (int x, int y) {
	origin.move (x, y);
    }

    public void translate (int x, int y) {
	origin.translate (x, y);
    }

    public Point getLocation () {
	return origin;
    }

    @Override
    public void update (long millisec, GFrame f) {
    }

    @Override
    public void testCollision (GComponent comp, GFrame f) {
    }
}

