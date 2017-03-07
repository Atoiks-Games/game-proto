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

import java.awt.Point;
import java.awt.Graphics;

public class Group implements GComponent {

    private final GComponent[] children;

    public Group (GComponent... subcmp) {
	this.children = subcmp;
    }

    public GComponent getChild (int idx) {
	return children[idx];
    }

    public void setChild (int idx, GComponent comp) {
	children[idx] = comp;
    }

    @Override
    public void render (Graphics g) {
	for (GComponent child : children) {
	    child.render (g);
	}
    }

    @Override
    public void update (long mills, GFrame f) {
	for (GComponent child : children) {
	    child.update (mills, f);
	}
    }

    @Override
    public boolean containsPoint (Point p) {
	for (GComponent child : children) {
	    if (child.containsPoint (p)) return true;
	}
	return false;
    }

    @Override
    public void testCollision (GComponent comp, GFrame f) {
	for (GComponent child : children) {
	    if (comp instanceof Group) {
		final Group rhs = (Group) comp;
		for (GComponent rhsChild : rhs.children) {
		    child.testCollision (rhsChild, f);
		}
	    } else {
		child.testCollision (comp, f);
	    }
	}
    }
}
