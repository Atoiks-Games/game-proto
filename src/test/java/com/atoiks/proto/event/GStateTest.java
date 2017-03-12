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

package com.atoiks.proto.event;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

import com.atoiks.proto.GScene;
import com.atoiks.proto.HeadlessFrame;

public class GStateTest implements GStateListener {

    public static final int FOCUSED = 1;

    public static final int NOT_FOCUSED = 0;

    public static final int PAUSED = 1;

    public static final int RUNNING = 0;

    private int state;

    private int pstate;

    private GScene scene0;

    private GScene scene1;

    private HeadlessFrame f;

    @Before
    public void initComponent () {
	scene0 = new GScene (null);
	scene0.addGStateListener (this);
	scene1 = new GScene (null);
	f = new HeadlessFrame (100, 100, scene0, scene1);
    }

    @Test
    public void testSceneChange () {
	f.jumpToScene (1);
	assertEquals ("Expected state to be NOT FOCUSED", NOT_FOCUSED, state);
	f.jumpToScene (0);
	assertEquals ("Expected state to be FOCUSED", FOCUSED, state);
    }

    @Test
    public void testPause () {
	f.pause ();
	assertEquals ("Expected pstate to be PAUSE", PAUSED, pstate);
    }

    @Test
    public void testResume () {
	f.resume ();
	assertEquals ("Expected pstate to be RUNNING", RUNNING, pstate);
    }

    @Override
    public void onEnter () {
	state = FOCUSED;
    }

    @Override
    public void onLeave () {
	state = NOT_FOCUSED;
    }

    @Override
    public void onPause () {
	pstate = PAUSED;
    }

    @Override
    public void onResume () {
	pstate = RUNNING;
    }
}
