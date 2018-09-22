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

package org.atoiks.games.staventure.scenes;

import java.awt.Color;
import java.awt.Image;

import java.util.Map;
import java.util.Random;

import javax.swing.ImageIcon;

import org.atoiks.games.framework2d.GameScene;
import org.atoiks.games.framework2d.IGraphics;

import org.atoiks.games.staventure.prefabs.Player;
import org.atoiks.games.staventure.prefabs.Direction;

import org.atoiks.games.staventure.colliders.RectangleCollider;

public final class SquashCourtScene extends GameScene {

    public enum ID {
        // There are two squash courts
        TOP, BOTTOM;
    }

    public static final String KEY_ID = "scene.squash_court.id";
    public static final String KEY_WIN = "scene.squash_court.win";

    private static final int X1 = 155;
    private static final int Y1 = 4;
    private static final int X2 = 515;
    private static final int Y2 = 448;

    private static final int DOOR_X1 = 300;
    private static final int DOOR_X2 = 360;

    private final Random rnd = new Random();

    private Image bg;
    private Player player;

    private int COURT_HALLWAY_SCENE_IDX;
    private int SQUASH_GAME_SCENE_IDX;

    private ID id;

    private ImageIcon pyIcon;
    private Image pyImg;
    private float pyX;
    private float pyY;
    private final RectangleCollider pyCollider = new RectangleCollider();

    private boolean winAgainstPY;

    @Override
    public void init() {
        bg = (Image) scene.resources().get("/squash_court/floor.png");
        pyImg = (Image) scene.resources().get("/py/spr_2.png");
        pyIcon = new ImageIcon(pyImg);

        COURT_HALLWAY_SCENE_IDX = ((Map<?, Integer>) scene.resources().get("scene.map")).get(CourtHallwayScene.class);
        SQUASH_GAME_SCENE_IDX   = ((Map<?, Integer>) scene.resources().get("scene.map")).get(SquashGameScene.class);

        player = new Player();
        player.state = Player.IDLE_FRAME;
        player.direction = Direction.DOWN;
        player.move((DOOR_X1 + DOOR_X2) / 2 - 16, 180);
        player.speed = 50;

        // PY's collider will never change width and height
        pyCollider.w = 32 - 12;
        pyCollider.h = 32;

        // Define this value
        scene.resources().put(SquashCourtScene.KEY_ID, (id = ID.TOP));
        scene.resources().put(SquashCourtScene.KEY_WIN, (winAgainstPY = false));
    }

    @Override
    public void enter(int from) {
        if (from == SQUASH_GAME_SCENE_IDX) {
            winAgainstPY = (boolean) scene.resources().get(SquashCourtScene.KEY_WIN);
        } else if (from == COURT_HALLWAY_SCENE_IDX) {
            this.id = (ID) scene.resources().get(KEY_ID);
            player.direction = Direction.UP;
            player.move((DOOR_X1 + DOOR_X2) / 2 - 16, 420);
        }

        // Recompute PY's location
        pyX = rnd.nextFloat() * (X2 - X1 - 20) + X1 + 5;
        pyY = rnd.nextFloat() * (Y2 - Y1 - 20) + Y1 + 5;
        pyCollider.x = pyX + 6; // +6 due to padding
        pyCollider.y = pyY;
    }

    @Override
    public void render(final IGraphics g) {
        g.setClearColor(Color.black);
        g.clearGraphics();

        g.drawImage(bg, 0, 0);

        /*
        // Just for debugging purposes. These are the *walls*
        g.setColor(Color.red);
        g.drawLine(X1, Y1, X1, Y2);  // left
        g.drawLine(X2, Y1, X2, Y2);  // right
        g.drawLine(X1, Y1, X2, Y1);  // top
        g.drawLine(X1, Y2, X2, Y2);  // bottom
        */

        player.render(g);

        if (id == ID.TOP && !winAgainstPY) {
            // PY only stays in the top court
            g.drawImage(pyImg, (int) pyX, (int) pyY);
        }

        g.setColor(Color.lightGray);
        g.fillRect(DOOR_X1, Y2 - 1, DOOR_X2, Y2 + 4);
    }

    @Override
    public boolean update(final float dt) {
        player.update(dt, scene);

        // Restrict player in boundary
        // player is 32 * 32, but x axis actually has a 6 px padding
        // on each side
        if (player.x < X1 - 6) player.x = X1 - 6;
        if (player.x > X2 - 26) player.x = X2 - 26;
        if (player.y < Y1) player.y = Y1;
        if (player.y > Y2 - 32) {
            // There is a door at the very bottom
            // and if the player is within the door's range,
            // the player is allowed to go beyond the bottom of the screen
            if (DOOR_X1 - 6 < player.x && player.x < DOOR_X2 - 26) {
                if (player.y > Y2) {
                    // When we get here, we switch scenes
                    scene.switchToScene(COURT_HALLWAY_SCENE_IDX);
                    return true;
                }
            } else {
                player.y = Y2 - 32;
            }
        }

        if (id == ID.TOP && !winAgainstPY) {
            // See if we bump into PY
            if (pyCollider.collidesWith(player.collider)) {
                scene.switchToScene(SQUASH_GAME_SCENE_IDX);
                return true;
            }
        }

        return true;
    }

    @Override
    public void resize(int w, int h) {
        // Ignore
    }
}