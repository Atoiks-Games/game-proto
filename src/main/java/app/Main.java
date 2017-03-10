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

package app;

import com.atoiks.proto.*;
import com.atoiks.proto.event.GKeyAdapter;
import com.atoiks.proto.event.GStateAdapter;

import app.entity.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;

import java.io.IOException;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public final class WeightWrapper<T> {

	private T data;

	private double weight;

	public WeightWrapper (T data, double weight) {
	    this.data = data;
	    this.weight = weight;
	}

	public T get () {
	    return data;
	}

	public void set (T data) {
	    this.data = data;
	}

	public double getWeight () {
	    return weight;
	}

	public void setWeight (double weight) {
	    this.weight = weight;
	}
    }

    public static WeightWrapper<?> weightedRandom (WeightWrapper<?>[] array) {
	double totalWeight = 0;
	for (WeightWrapper<?> w : array)
	    totalWeight += w.getWeight();
	double randomVal = Math.random() * totalWeight;
	for (WeightWrapper<?> w : array) {
	    randomVal -= w.getWeight();
	    if (randomVal <= 0)
		return w;
	}
	return null;
    }

    public static void main(String[] args) {
        new Main().run();
    }

    public void run () {
	final SquashCourtScene scene0 = SquashCourtScene.getInstance ();
	final SquashGameScene scene1 = SquashGameScene.getInstance ();

	final GFrame app = new GFrame("Prototype", scene0, scene1);
	app.setVisible (true);
    }
}
