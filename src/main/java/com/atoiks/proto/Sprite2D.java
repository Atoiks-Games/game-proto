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

public class Sprite2D extends Sprite {

    protected Image[] frames;

    protected int frameIdx;

    protected double millisecPerFrame;

    private long elapsed;

    public Sprite2D (int currentFrame, Point origin, int fps, Image... frames) {
	super (frames[currentFrame], origin);
	this.frames = frames;
	this.frameIdx = currentFrame;
	this.millisecPerFrame = 1000.0 / fps;
	this.elapsed = 0;
    }

    public void jumpToFrame (int frameIdx) {
	this.frameIdx = frameIdx % this.frames.length;
	this.image = this.frames[this.frameIdx];
    }

    @Override
    public void update (long millisec, GFrame f) {
	if (this.millisecPerFrame < 0) return;
        elapsed += millisec;
	final double deltaFrames = elapsed / this.millisecPerFrame;
	if (deltaFrames >= 1) {
	    long lastFrameChange = (long) (this.millisecPerFrame * deltaFrames);
	    elapsed -= lastFrameChange;
	    this.jumpToFrame (this.frameIdx + (int) deltaFrames);
	}
    }
}
