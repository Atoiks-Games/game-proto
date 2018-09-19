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

package org.atoiks.games.staventure;

import java.util.HashMap;

import org.atoiks.games.framework2d.FrameInfo;
import org.atoiks.games.framework2d.GameScene;
import org.atoiks.games.framework2d.swing.Frame;

import org.atoiks.games.staventure.scenes.*;

public class Main {

    public static final int HEIGHT = 450;

    public static final int WIDTH = 700;

    public static void main(String[] args) {
        final HashMap<Class<?>, Integer> sceneMap = new HashMap<>();
        final GameScene[] scenes = { new SquashCourtScene(), new CourtHallwayScene() };
        final FrameInfo info = new FrameInfo()
                .setTitle("Atoiks Games - staventure")
                .setFps(60)
                .setSize(WIDTH, HEIGHT)
                .setLoader(new LoadingScene())
                .setGameScenes(scenes)
                .addResource("scene.map", sceneMap);

        // Iterate the game scenes and map them to their class
        for (int i = 0; i < scenes.length; ++i) {
            final GameScene s = scenes[i];
            sceneMap.put(s.getClass(), i);
        }

        try (final Frame frame = new Frame(info)) {
            frame.init();
            frame.loop();
        }
    }
}
