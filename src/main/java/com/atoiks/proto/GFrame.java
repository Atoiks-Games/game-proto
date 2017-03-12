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

/**
 * A frame that can host and process GScenes.
 */
public interface GFrame {

    /**
     * Changes the scene being displayed into another one.
     *
     * @param idx The index number of the scene
     */
    public void jumpToScene (int idx);

    /**
     * Pauses the scene. If scene was already paused, nothing happens.
     */
    public void pause ();

    /**
     * Resumes the scene. If scene was already running, nothing happens.
     */
    public void resume ();

    /**
     * Checks if scene is paused.
     *
     * @return True if paused, false otherwise.
     */
    public boolean isPaused ();

    /**
     * Alters between pause and resume.
     */
    public void toggleState ();

    /**
     * Sets visibility of scene.
     *
     * @param f visibility
     */
    public void setVisible (boolean f);

    /**
     * @return Width of the scene
     */
    public int getFrameWidth ();

    /**
     * @return Height of the scene
     */
    public int getFrameHeight ();

    /**
     * @return Dimension of the scene
     */
    public Dimension getFrameSize ();

    /**
     * Destroys the frame and release resources.
     */
    public void dispose ();
}
