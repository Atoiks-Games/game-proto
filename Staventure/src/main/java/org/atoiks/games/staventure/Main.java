/**
 *  Staventure
 *  Copyright (C) 2017-2019  Atoiks-Games <atoiks-games@outlook.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.atoiks.games.staventure;

import org.atoiks.games.framework2d.FrameInfo;
import org.atoiks.games.framework2d.SceneManager;

import org.atoiks.games.framework2d.java2d.Frame;

import org.atoiks.games.staventure.scenes.LoadingScene;

public class Main {

    public static final int HEIGHT = 450;

    public static final int WIDTH = 700;

    public static void main(String[] args) {
        final FrameInfo info = new FrameInfo()
                .setTitle("Atoiks Games - staventure")
                .setFps(60)
                .setSize(WIDTH, HEIGHT);

        try (final Frame frame = new Frame(info)) {
            frame.init();
            SceneManager.pushScene(new LoadingScene());
            frame.loop();
        }
    }
}
