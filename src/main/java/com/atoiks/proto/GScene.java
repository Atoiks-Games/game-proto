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

import com.atoiks.proto.event.GKeyListener;
import com.atoiks.proto.event.GStateListener;

import java.awt.Graphics;
import java.awt.event.KeyEvent;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class GScene {

    protected List<GKeyListener> keyEvts;

    protected List<GStateListener> listeners;

    protected List<GComponent> instances;

    public GScene (GComponent floor, GComponent... insts) {
	this.instances = new ArrayList<GComponent>();
	this.instances.add (floor);
	this.instances.addAll (Arrays.asList (insts));
	this.listeners = new ArrayList<GStateListener>();
	this.keyEvts = new ArrayList<GKeyListener>();
    }

    public void setFloor (GComponent floor) {
        this.instances.set (0, floor);
    }

    public GComponent getFloor () {
	return this.instances.get (0);
    }

    public void pushBackGComp (GComponent comp) {
	instances.add (comp);
    }

    public GComponent popBackGComp () {
	return instances.remove (instances.size() - 1);
    }

    public void pushFrontGComp (GComponent comp) {
        instances.add (1, comp);
    }

    public GComponent popFrontGComp (GComponent comp) {
	return instances.remove (1);
    }

    public GComponent getGComp (int idx) {
	return instances.get (idx);
    }

    public GComponent setGComp (int idx, GComponent el) {
	return instances.set (idx, el);
    }

    public GComponent removeGComp (int idx) {
	return instances.remove (idx);
    }

    public boolean removeGComp (GComponent comp) {
	return instances.remove (comp);
    }

    public void renderStep (Graphics g) {
	for (GComponent el : instances) {
	    if (el != null) el.render (g);
	}
    }

    public void collisionStep (GFrame f) {
	for (GComponent sp1 : instances) {
	    for (GComponent sp2 : instances) {
                if (sp1 != sp2) sp1.testCollision (sp2, f);
	    }
	}
    }

    public void updateStep (long elapsed, GFrame f) {
	for (GComponent el : instances) {
	    if (el != null) el.update (elapsed, f);
	}
    }

    public void addGStateListener (GStateListener lis) {
	this.listeners.add (lis);
    }

    public void removeGStateListener (GStateListener lis) {
	this.listeners.remove (lis);
    }

    public void addGKeyListener (GKeyListener lis) {
	this.keyEvts.add (lis);
    }

    public void removeGKeyListener (GKeyListener lis) {
	this.keyEvts.remove (lis);
    }

    public final void onEnterTrigger () {
	for (GStateListener lis : listeners) {
	    lis.onEnter ();
	}
    }

    public final void onLeaveTrigger () {
	for (GStateListener lis : listeners) {
	    lis.onLeave ();
	}
    }

    public final void onPauseTrigger () {
	for (GStateListener lis : listeners) {
	    lis.onPause ();
	}
    }

    public final void onResumeTrigger () {
	for (GStateListener lis : listeners) {
	    lis.onResume ();
	}
    }

    public final void onKeyTypedTrigger (KeyEvent e, GFrame f) {
	for (GKeyListener lis : keyEvts) {
	    lis.keyTyped (e, f);
	}
    }

    public final void onKeyPressedTrigger (KeyEvent e, GFrame f) {
	for (GKeyListener lis : keyEvts) {
	    lis.keyPressed (e, f);
	}
    }

    public final void onKeyReleasedTrigger (KeyEvent e, GFrame f) {
	for (GKeyListener lis : keyEvts) {
	    lis.keyReleased (e, f);
	}
    }
}
