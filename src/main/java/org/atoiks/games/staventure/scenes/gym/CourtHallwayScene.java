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

package org.atoiks.games.staventure.scenes.gym;

import java.awt.Color;

import org.atoiks.games.framework2d.Scene;
import org.atoiks.games.framework2d.IGraphics;
import org.atoiks.games.framework2d.SceneManager;
import org.atoiks.games.framework2d.ResourceManager;

import org.atoiks.games.framework2d.resource.Texture;

import org.atoiks.games.staventure.prefabs.Player;
import org.atoiks.games.staventure.prefabs.Direction;

import org.atoiks.games.staventure.colliders.RectangleCollider;

import org.atoiks.games.staventure.scenes.colby.LibraryScene;

public final class CourtHallwayScene implements Scene {

    private static final RectangleCollider LEFT_COLLIDER = new RectangleCollider(0, 49, 106 - 0, 397 - 49);
    private static final RectangleCollider TOP_COLLIDER = new RectangleCollider(162, 0, 700 - 162, 155 - 0);
    private static final RectangleCollider BOTTOM_COLLIDER = new RectangleCollider(162, 316, 700 - 162, 450 - 316);

    private static final int LEFT_DOOR_X1 = 25;
    private static final int LEFT_DOOR_X2 = 60;

    private static final int RIGHT_DOOR_Y1 = 200;
    private static final int RIGHT_DOOR_Y2 = 250;

    private final Texture bg;
    private final Player player;

    private float oldX;
    private float oldY;

    public CourtHallwayScene() {
        this.bg = ResourceManager.get("/gym/court_hallway/floor.png");

        this.player = new Player();
        this.player.direction = Direction.DOWN;
        this.player.state = Player.IDLE_FRAME;
        this.player.speed = 80;
    }

    @Override
    public void enter(Scene from) {
        if (from instanceof SquashCourtScene) {
            final SquashCourtScene.ID id = ((SquashCourtScene) from).id;

            player.x = (LEFT_DOOR_X1 + LEFT_DOOR_X2) / 2 - 16;
            switch (id) {
                case TOP:
                    player.y = 10;
                    break;
                case BOTTOM:
                    player.y = 408;
                    break;
            }
        }
    }

    @Override
    public void render(final IGraphics g) {
        g.setClearColor(Color.black);
        g.clearGraphics();

        g.drawTexture(bg, 0, 0);

        /*
        // Just for debugging purposes. These are the *walls*
        g.setColor(Color.red);
        LEFT_COLLIDER.render(g);
        TOP_COLLIDER.render(g);
        BOTTOM_COLLIDER.render(g);
        */

        player.render(g);

        g.setColor(Color.lightGray);
        g.fillRect(LEFT_DOOR_X1, 0, LEFT_DOOR_X2, 4);
        g.fillRect(LEFT_DOOR_X1, 446, LEFT_DOOR_X2, 450);
        g.fillRect(696, RIGHT_DOOR_Y1, 700, RIGHT_DOOR_Y2);
    }

    @Override
    public boolean update(final float dt) {
        // Save player coordinates, used in boundCheck
        oldX = player.x;
        oldY = player.y;

        player.update(dt);

        if (boundCheck()) {
            // If boundCheck returns true, that means
            // someone issued a scene trasition,
            // which we exit early
            return true;
        }
        return true;
    }

    private boolean boundCheck() {
        // Restrict player in boundary
        // player is 32 * 32, but x axis actually has a 6 px padding
        // on each side

        if (player.collider.collidesWithAny(LEFT_COLLIDER, TOP_COLLIDER, BOTTOM_COLLIDER)) {
            player.move(oldX, oldY);
            return false;
        }

        if (player.x < -6) {
            player.x = oldX;
        }
        if (player.x > 700 - 26) {
            // If player is headed for right door, its fine
            if (RIGHT_DOOR_Y1 < player.y && player.y < RIGHT_DOOR_Y2 - 32) {
                if (player.x > 700) {
                    // When we get there, switch scenes
                    SceneManager.swapScene(new LibraryScene());
                    return true;
                }
            } else {
                // Restrict bounds otherwise
                player.x = oldX;
            }
        }

        if (player.y < 0) {
            // If player is headed for top-left door, its fine
            if (LEFT_DOOR_X1 - 6 < player.x && player.x < LEFT_DOOR_X2 - 26) {
                if (player.y < -26) {
                    // This one jumps back to SquashCourtScene
                    SceneManager.swapScene(new SquashCourtScene(SquashCourtScene.ID.TOP));
                    return true;
                }
            } else {
                player.y = oldY;
            }
        }
        if (player.y > 450 - 32) {
            // If player is headed for top-left door, its fine
            if (LEFT_DOOR_X1 - 6 < player.x && player.x < LEFT_DOOR_X2 - 26) {
                if (player.y > 450) {
                    // This one also jumps back to SquashCourtScene
                    SceneManager.swapScene(new SquashCourtScene(SquashCourtScene.ID.BOTTOM));
                    return true;
                }
            } else {
                player.y = oldY;
            }
        }
        return false;
    }

    @Override
    public void resize(int w, int h) {
        // Ignore
    }
}
