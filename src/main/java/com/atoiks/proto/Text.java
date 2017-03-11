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

import javax.swing.UIManager;

import java.awt.Font;
import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;

/**
 * Renders a non-collidable string.
 */
public class Text implements GComponent {

    /**
     * The string being rendered, supports {@code '\n'} as new line.
     */
    protected String text;

    /**
     * The font of the string.
     */
    protected Font font;

    /**
     * The color of the string.
     */
    protected Color color;

    /**
     * The top-left corner of the string box.
     */
    protected Point origin;

    /**
     * Flag for if text will be rendered.
     */
    protected boolean visible;

    /**
     * Constructs a string with an origin.
     *
     * @param text The string
     * @param origin The origin
     */
    public Text (String text, Point origin) {
	this (text, origin,
	      UIManager.getDefaults ().getFont ("TextPane.font"),
	      Color.black);
    }

    /**
     * Constructs a string with an origin, font, and color.
     *
     * @param text The string
     * @param origin The origin
     * @param font The font of the string
     * @param color The color of the string
     */
    public Text (String text, Point origin, Font font, Color color) {
	this.text = text;
	this.font = font;
	this.color = color;
	this.origin = origin;
	this.visible = true;
    }

    @Override
    public void render (Graphics g) {
	if (visible) {
	    final int pt = font.getSize ();
	    final String[] segs = text.split ("\n");
	    final Color original = g.getColor ();
	    for (int i = 0; i < segs.length; ++i) {
		g.setColor (color);
		g.drawString (segs[i], origin.x, origin.y + pt * i);
	    }
	    g.setColor (original);
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

    public void setFont (Font f) {
	this.font = f;
    }

    public Font getFont () {
	return this.font;
    }

    public void setColor (Color c) {
	this.color = c;
    }

    public Color getColor () {
	return this.color;
    }

    public void setVisible (boolean flag) {
	this.visible = flag;
    }

    public boolean isVisible () {
	return visible;
    }

    /**
     * Sets the origin to (x, y)
     *
     * @param x The new X coordinate
     * @param y The new Y coordinate
     */
    public void move (int x, int y) {
	origin.move (x, y);
    }

    /**
     * Sets the origin to (x+dx, y+dy)
     *
     * @param dx Change in X coordinate
     * @param dy Change in Y coordinate
     */
    public void translate (int dx, int dy) {
	origin.translate (dx, dy);
    }

    public Point getLocation () {
	return origin;
    }

    public void setLocation (Point pt) {
	origin = pt;
    }

    @Override
    public void update (long millisec, GFrame f) {
	// Do nothing
    }

    @Override
    public void testCollision (GComponent comp, GFrame f) {
	// You cannot collide with an text object
    }
}

