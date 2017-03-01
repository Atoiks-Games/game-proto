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

import javax.swing.JPanel;
import javax.swing.JFrame;

import java.awt.Graphics;
import java.awt.event.KeyListener;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class GFrame extends JFrame {

    public static final int HEIGHT = 450;
    public static final int WIDTH = 700;

    private final JPanel panel;

    private ExecutorService threads;

    private ArrayList<GComponent> renderStack;

    private Floor floor;

    private boolean first;

    public GFrame (String title) {
	super (title);
	setSize (WIDTH, HEIGHT);
	setResizable (false);
	setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

	renderStack = new ArrayList<GComponent>();
	
        panel = new JPanel ()
	    {
	        protected void paintComponent (Graphics g) {
		    // Draw the floor before everything
		    if (floor != null) floor.render (g);
		    for (GComponent el : renderStack) {
			if (el != null) el.render (g);
		    }
		}
	    };
	this.add (panel);
	first = true;
    }

    @Override
    public void setVisible (boolean f) {
	super.setVisible(f);
	if (first && f) {
	    first = false;
	    threads = Executors.newFixedThreadPool(1);
	    // Rendering thread
	    threads.execute(() -> {
		    long lastTime = System.currentTimeMillis ();
		    while (!threads.isShutdown ()) {
			for (GComponent sp1 : renderStack) {
			    for (GComponent sp2 : renderStack) {
				sp1.testCollision (sp2);
			    }
			}

			final long current = System.currentTimeMillis ();
			final long elapsed = current - lastTime;
			for (GComponent el : renderStack) {
			    if (el != null) el.update (elapsed);
			}
			repaint ();
			lastTime = current;
			try {
			    // This line is just here so your youtube
			    // videos don't start glitching because of
			    // excessive lagggggggg. :-)
			    Thread.sleep(15);
			} catch (java.lang.InterruptedException ex) {
			}
		    }
		});
	}
	if (!f) {
	    first = true;
	    threads.shutdownNow();
	}
    }

    public void setFloor (Floor floor) {
	this.floor = floor;
    }

    public Floor getFloor () {
	return floor;
    }

    public void pushBackGComp (GComponent comp) {
	renderStack.add (comp);
    }

    public GComponent popBackGComp () {
	return renderStack.remove (renderStack.size() - 1);
    }

    public void pushFrontGComp (GComponent comp) {
	renderStack.add (0, comp);
    }

    public GComponent popFrontGComp (GComponent comp) {
	return renderStack.remove (0);
    }

    public GComponent getGComp (int idx) {
	return renderStack.get (idx);
    }

    public GComponent setGComp (int idx, GComponent el) {
	return renderStack.set (idx, el);
    }

    public GComponent removeGComp (int idx) {
	return renderStack.remove (idx);
    }

    public boolean removeGComp (GComponent comp) {
	return renderStack.remove (comp);
    }
}
