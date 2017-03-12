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
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

/**
 * A frame that does not actually create a window.
 *
 * TODO: Add support for key events
 */
public class HeadlessFrame implements GFrame {

    private BufferedImage img;

    private int width;

    private int height;

    private GScene[] scenes;

    private int sceneIdx;

    private boolean pauseFlag;

    private boolean visibleFlag;

    private long lastTime;
    
    public HeadlessFrame (int width, int height, GScene... scenes) {
	this.width = width;
	this.height = height;
	this.img = new BufferedImage (width, height, BufferedImage.TYPE_INT_ARGB);
	this.scenes = scenes;
	scenes[this.sceneIdx = 0].onEnterTrigger ();
    }

    @Override
    public void jumpToScene (int idx) {
	scenes[sceneIdx].onLeaveTrigger ();
	scenes[sceneIdx = idx].onEnterTrigger ();
    }

    @Override
    public void pause () {
	if (!pauseFlag) {
	    pauseFlag = true;
	    scenes[sceneIdx].onPauseTrigger ();
	}
    }
    
    @Override
    public void resume () {
	if (pauseFlag) {
	    pauseFlag = false;
	    scenes[sceneIdx].onResumeTrigger ();
	}
    }

    @Override
    public boolean isPaused () {
	return pauseFlag;
    }

    @Override
    public void toggleState () {
	pauseFlag = !pauseFlag;
	// Inverted
	if (pauseFlag) {
	    scenes[sceneIdx].onResumeTrigger ();
	} else {
	    scenes[sceneIdx].onPauseTrigger ();
	}
    }

    @Override
    public void setVisible (boolean f) {
	visibleFlag = f;
    }

    @Override
    public boolean isVisible () {
	return visibleFlag;
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
    public void dispose () {
	// Do nothing
    }

    public void setLastTimeToNow () {
	lastTime = System.currentTimeMillis ();
    }

    public void nextStep () {
	final long current = System.currentTimeMillis ();
	if (!pauseFlag) {
	    scenes[sceneIdx].collisionStep (this);
	    scenes[sceneIdx].updateStep (current - lastTime, this);

	    final Graphics g = img.createGraphics ();
	    scenes[sceneIdx].renderStep (g);
	    g.dispose ();
	}
	lastTime = current;
    }

    public Image getImage () {
	return img;
    }
}
