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

import java.awt.Image;
import java.awt.Point;
import java.awt.Graphics;

public class Sprite implements GComponent {

    protected Image image;

    protected Point origin;

    protected boolean visible;

    protected boolean collidable;

    public Sprite (Image image, Point origin) {
	this.image = image;
	this.origin = origin;
	this.visible = true;
	this.collidable = false;
    }

    @Override
    public void render (Graphics g) {
	if (visible)
	    g.drawImage (image, origin.x, origin.y, null);
    }

    @Override
    public boolean containsPoint (Point p) {
	if (p.x < origin.x) return false;
	if (p.x > image.getWidth(null)) return false;
	if (p.y < origin.y) return false;
	if (p.y > image.getHeight(null)) return false;
	return true;
    }

    public void setImage (Image img) {
	this.image = img;
    }

    public Image getImage () {
	return this.image;
    }

    public void setCollidable (boolean flag) {
	this.collidable = flag;
    }

    public boolean isCollidable () {
	return this.collidable;
    }

    public void setVisible (boolean flag) {
	this.visible = flag;
    }

    public boolean isVisible () {
	return visible;
    }

    public void disable () {
	this.collidable = false;
	this.visible = false;
    }

    public void enable () {
	this.collidable = true;
	this.visible = true;
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
    public void update (long millisec) {
    }

    @Override
    public void testCollision (GComponent comp) {
	if (!collidable) return;
	// Floor is always the base layer (not above the obj)
	if (comp instanceof Floor) return;

	final Sprite sp2 = (Sprite) comp;
	if ((origin.x < sp2.origin.x + sp2.image.getWidth(null))
	    && (origin.x + image.getWidth(null) > sp2.origin.x)
	    && (origin.y < sp2.origin.y + sp2.image.getHeight(null))
	    && (origin.y + image.getHeight(null) > sp2.origin.y)) {
	    onCollision (sp2);
	}
    }

    public void onCollision (Sprite other) {
    }
}
