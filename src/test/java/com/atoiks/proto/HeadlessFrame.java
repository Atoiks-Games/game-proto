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

import java.awt.Dimension;

public class HeadlessFrame implements GFrame {

    private int width;

    private int height;

    private GScene[] scenes;

    private int sceneIdx;

    private boolean pauseFlag;

    private boolean visibleFlag;
    
    public HeadlessFrame (int width, int height, GScene... scenes) {
	this.width = width;
	this.height = height;
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
}
