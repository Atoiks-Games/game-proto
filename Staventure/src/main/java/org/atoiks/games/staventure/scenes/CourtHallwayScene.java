package org.atoiks.games.staventure.scenes;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.KeyEvent;

import java.util.Map;

import org.atoiks.games.framework2d.GameScene;
import org.atoiks.games.framework2d.IGraphics;

import org.atoiks.games.staventure.prefabs.Player;

import org.atoiks.games.staventure.colliders.RectangleCollider;

public final class CourtHallwayScene extends GameScene {

    private static final RectangleCollider LEFT_COLLIDER = new RectangleCollider(0, 49, 106 - 0, 397 - 49);
    private static final RectangleCollider TOP_COLLIDER = new RectangleCollider(162, 0, 700 - 162, 155 - 0);
    private static final RectangleCollider BOTTOM_COLLIDER = new RectangleCollider(162, 316, 700 - 162, 450 - 316);

    private static final int LEFT_DOOR_X1 = 25;
    private static final int LEFT_DOOR_X2 = 60;

    private static final int RIGHT_DOOR_Y1 = 200;
    private static final int RIGHT_DOOR_Y2 = 250;

    private Image bg;
    private Player player;

    private int SQUASH_COURT_SCENE_IDX;

    private float oldX;
    private float oldY;

    @Override
    public void init() {
        bg = (Image) scene.resources().get("/court_hallway/floor.png");

        SQUASH_COURT_SCENE_IDX = ((Map<?, Integer>) scene.resources().get("scene.map")).get(SquashCourtScene.class);

        player = new Player();
        player.direction = Player.Direction.DOWN;
        player.state = Player.IDLE_FRAME;
        player.speed = 80;
    }

    @Override
    public void enter(int from) {
        if (from == SQUASH_COURT_SCENE_IDX) {
            final SquashCourtScene.ID id = (SquashCourtScene.ID) scene.resources().get(SquashCourtScene.KEY_ID);

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

        g.drawImage(bg, 0, 0);

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

        player.update(dt, scene);

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
                    scene.gotoNextScene();
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
                    scene.resources().put(SquashCourtScene.KEY_ID, SquashCourtScene.ID.TOP);
                    scene.switchToScene(SQUASH_COURT_SCENE_IDX);
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
                    scene.resources().put(SquashCourtScene.KEY_ID, SquashCourtScene.ID.BOTTOM);
                    scene.switchToScene(SQUASH_COURT_SCENE_IDX);
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