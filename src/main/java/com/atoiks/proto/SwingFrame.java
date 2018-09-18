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

import javax.swing.JPanel;
import javax.swing.JFrame;

import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An GFrame implementation backed up by the java swing UI.
 */
public class SwingFrame
    extends JFrame
    implements GFrame, KeyListener, Runnable {

    private ExecutorService threads;

    private GScene[] scenes;

    private int sceneIdx;

    private boolean first;

    private AtomicBoolean pauseFlag;

    private int width;

    private int height;

    public SwingFrame (String title, int width, int height,
		       final GScene... scenes) {
	super (title);

	if (width < 1) {
	    throw new IllegalArgumentException (width + "[width] < 1");
	}
	if (height < 1) {
	    throw new IllegalArgumentException (height + "[height] < 1");
	}
	
	setSize (width, height + 22);
	setResizable (false);
	setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

        final JPanel panel = new JPanel ()
	    {
	        protected void paintComponent (Graphics g) {
		    scenes[sceneIdx].renderStep (g);
		}
	    };
	this.add (panel, BorderLayout.CENTER);

	this.first = true;
	this.pauseFlag = new AtomicBoolean (false);
	this.scenes = scenes;
	this.width = width;
	this.height = height;
	this.addKeyListener (this);
	scenes[this.sceneIdx = 0].onEnterTrigger ();
    }

    @Override
    public void keyTyped (KeyEvent e) {
	scenes[sceneIdx].onKeyTypedTrigger (e, this);
    }

    @Override
    public void keyPressed (KeyEvent e) {
	scenes[sceneIdx].onKeyPressedTrigger (e, this);
    }

    @Override
    public void keyReleased (KeyEvent e) {
	scenes[sceneIdx].onKeyReleasedTrigger (e, this);
    }

    @Override
    public void jumpToScene (int idx) {
	scenes[sceneIdx].onLeaveTrigger ();
	scenes[sceneIdx = idx].onEnterTrigger ();
    }

    @Override
    public void pause () {
	if (pauseFlag.compareAndSet (false, true)) {
	    scenes[sceneIdx].onPauseTrigger ();
	}
    }

    @Override
    public void resume () {
	if (pauseFlag.compareAndSet (true, false)) {
	    scenes[sceneIdx].onResumeTrigger ();
	}
    }

    @Override
    public boolean isPaused () {
	return pauseFlag.get();
    }

    @Override
    public void toggleState () {
	if (pauseFlag.compareAndSet (true, false)) {
	    scenes[sceneIdx].onResumeTrigger ();
	} else {
	    pauseFlag.set (true);
	    scenes[sceneIdx].onPauseTrigger ();
	}
    }

    @Override
    public void setVisible (boolean f) {
	super.setVisible (f);
	if (first && f) {
	    first = false;
	    if (threads != null) {
		threads.shutdownNow();
	    }
	    threads = Executors.newFixedThreadPool (1);
	    threads.execute(this);
	}
	if (!f) {
	    first = true;
	    threads.shutdownNow();
	}
    }

    @Override
    public int getFrameWidth () {
	return width;
    }

    @Override
    public int getFrameHeight () {
	return height;
    }

    @Override
    public Dimension getFrameSize () {
	return new Dimension (width, height);
    }

    @Override
    public void run () {
	long lastTime = System.currentTimeMillis ();
	while (threads != null && !threads.isShutdown ()) {
	    final long current = System.currentTimeMillis ();
	    if (!pauseFlag.get()) {
		scenes[sceneIdx].collisionStep (this);
		scenes[sceneIdx].updateStep (current - lastTime, this);
		repaint ();
	    }
	    lastTime = current;
	    try {
		// This line is just here so your youtube
		// videos don't start glitching because of
		// excessive lagggggggg. :-)
		Thread.sleep(15);
	    } catch (java.lang.InterruptedException ex) {
	    }
	}
	threads = null;
    }
}
