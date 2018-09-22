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

package org.atoiks.games.staventure.scenes.colby;

import java.awt.Color;
import java.awt.Image;

import java.util.Map;

import org.atoiks.games.framework2d.GameScene;
import org.atoiks.games.framework2d.IGraphics;

import org.atoiks.games.staventure.prefabs.Player;
import org.atoiks.games.staventure.prefabs.Direction;

public final class ColbyHallwayScene extends GameScene {

    private static final int DOOR_LIB_X1 = 10;
    private static final int DOOR_LIB_Y1 = 325;
    private static final int DOOR_LIB_X2 = 72;
    private static final int DOOR_LIB_Y2 = 329;

    private static final int DOOR_BO_X1 = 50;
    private static final int DOOR_BO_Y1 = 121;
    private static final int DOOR_BO_X2 = 102;
    private static final int DOOR_BO_Y2 = 125;

    private Image bgImg;

    private Player player;

    private int LIBRARY_SCENE_IDX;
    private int BUSINESS_OFFICE_SCENE_IDX;

    public void init() {
        bgImg = (Image) scene.resources().get("/colby/colby_hallway/floor.png");

        LIBRARY_SCENE_IDX = ((Map<?, Integer>) scene.resources().get("scene.map")).get(LibraryScene.class);
        BUSINESS_OFFICE_SCENE_IDX = ((Map<?, Integer>) scene.resources().get("scene.map")).get(BusinessOfficeScene.class);

        player = new Player();
        player.state = Player.IDLE_FRAME;
        player.speed = 80;
    }

    @Override
    public void enter(int from) {
        if (from == LIBRARY_SCENE_IDX) {
            player.direction = Direction.UP;
            player.move(25, 272);
        } else if (from == BUSINESS_OFFICE_SCENE_IDX) {
            player.direction = Direction.DOWN;
            player.move(68, 130);
        } else {
            player.direction = Direction.RIGHT;
            player.move(25, 218);
        }
    }

    @Override
    public void render(IGraphics g) {
        g.setClearColor(Color.black);
        g.clearGraphics();

        // Draw floor
        g.rotate((float) -Math.PI / 2, 0, 325);
        g.drawImage(bgImg, 0, 325);
        g.rotate((float) +Math.PI / 2, 0, 325);

        player.render(g);

        g.setColor(Color.red);
        g.fillRect(DOOR_LIB_X1, DOOR_LIB_Y1, DOOR_LIB_X2, DOOR_LIB_Y2);
        g.setColor(Color.black);
        g.fillRect(DOOR_LIB_X1, DOOR_LIB_Y2, DOOR_LIB_X2, DOOR_LIB_Y2 + 40);

        g.setColor(Color.red);
        g.fillRect(DOOR_BO_X1, DOOR_BO_Y1, DOOR_BO_X2, DOOR_BO_Y2);
        g.setColor(Color.black);
        g.fillRect(DOOR_BO_X1, DOOR_BO_Y1 - 40, DOOR_BO_X2, DOOR_BO_Y1);
    }

    @Override
    public boolean update(float dt) {
        final float oldX = player.x;
        final float oldY = player.y;
        player.update(dt, scene);

        if (player.x < -6) {
            // TODO: This is the Pierce Hall side of the hallway
            // scene.switchToScene()
            player.x = -6;
        }
        if (player.x > 700 - 26) {
            // TODO: This is the LaBaron side of the hallway
            // scene.switchToScene()
            player.x = 700 - 26;
        }
        if (player.y < 125) {
            if (DOOR_BO_X1 - 1 < player.x && player.x < DOOR_BO_X2 - 26) {
                if (player.y < 125 - 32) {
                    scene.switchToScene(BUSINESS_OFFICE_SCENE_IDX);
                    return true;
                }
            } else {
                player.y = 125;
            }
        }
        if (player.y > 325 - 32) {
            if (DOOR_LIB_X1 - 6 < player.x && player.x < DOOR_LIB_X2 - 26) {
                if (player.y > 325) {
                    scene.switchToScene(LIBRARY_SCENE_IDX);
                    return true;
                }
            } else {
                player.y = 325 - 32;
            }
        }
        return true;
    }

    @Override
    public void resize(int w, int h) {
        // Ignore
    }
}