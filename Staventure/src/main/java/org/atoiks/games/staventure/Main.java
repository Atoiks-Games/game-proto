/**
 *  Staventure
 *  Copyright (C) 2017-2018  Atoiks-Games <atoiks-games@outlook.com>
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

import java.io.IOException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import java.util.HashMap;

import org.atoiks.games.framework2d.FrameInfo;
import org.atoiks.games.framework2d.GameScene;
import org.atoiks.games.framework2d.swing.Frame;

import org.atoiks.games.staventure.scenes.*;
import org.atoiks.games.staventure.scenes.gym.*;
import org.atoiks.games.staventure.scenes.colby.*;

public class Main {

    public static final int HEIGHT = 450;

    public static final int WIDTH = 700;

    public static void main(String[] args) {
        final HashMap<Class<?>, Integer> sceneMap = new HashMap<>();
        final GameScene[] scenes = { new SquashCourtScene(), new SquashGameScene(), new CourtHallwayScene(), new LibraryScene(), new ColbyHallwayScene(), new BusinessOfficeScene() };
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

        final Frame frame = new Frame(info);
        try {
            frame.init();
            frame.loop();
        } finally {
            // Access game data before game disposes
            final GameData data = (GameData) frame.getSceneManager().resources().get("save.dat");
            frame.close();

            // Save the game data
            try (final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./save.dat"))) {
                oos.writeObject(data);
            } catch (IOException ex) {
                // Well, the game could not be saved, no big deal lol
            }
        }
    }
}
