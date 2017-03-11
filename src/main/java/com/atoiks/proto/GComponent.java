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

/**
 * Any component inside a scene.
 */
public interface GComponent {

    /**
     * All the drawing should be done here.
     *
     * @param g The canvas for drawing
     */
    public void render (Graphics g);

    /**
     * Non-drawing changes should be done here.
     *
     * @param millisec The amount of time (in milliseconds) since last update
     * @param f The GFrame the component is in
     */
    public void update (long millisec, GFrame f);

    /**
     * Test to see if the component encapsulates the point.
     *
     * @param p The point that is potentially in the component
     *
     * @return True if the point is contained by the component
     */
    public boolean containsPoint (Point p);

    /**
     * Collision testing should be done here
     *
     * @param other The component that is potentially colliding
     * @param f The GFrame the component is in
     */
    public void testCollision (GComponent other, GFrame f);
}
