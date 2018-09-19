package org.atoiks.games.staventure.scenes;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.KeyEvent;

import java.util.Map;

import org.atoiks.games.framework2d.GameScene;
import org.atoiks.games.framework2d.IGraphics;

import org.atoiks.games.staventure.prefabs.Player;

public final class CourtHallwayScene extends GameScene {

    private static final int LEFT_X1 = 0;
    private static final int LEFT_Y1 = 49;
    private static final int LEFT_X2 = 106;
    private static final int LEFT_Y2 = 397;

    private static final int TOP_X1 = 162;
    private static final int TOP_Y1 = 0;
    private static final int TOP_X2 = 700;
    private static final int TOP_Y2 = 154;

    private static final int BOTTOM_X1 = 162;
    private static final int BOTTOM_Y1 = 316;
    private static final int BOTTOM_X2 = 700;
    private static final int BOTTOM_Y2 = 450;

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
        bg = (Image) scene.resources().get("/court_hallway/court_hallway.png");

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
        g.drawRect(LEFT_X1, LEFT_Y1, LEFT_X2, LEFT_Y2);
        g.drawRect(TOP_X1, TOP_Y1, TOP_X2, TOP_Y2);
        g.drawRect(BOTTOM_X1, BOTTOM_Y1, BOTTOM_X2, BOTTOM_Y2);
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

        if (oldX != player.x) {
            // Only test X coordinate stuff
            if (player.x < -6) {
                player.x = -6;
            }
            if (player.x > 700 - 26) {
                // If player is headed for right door, its fine
                if (RIGHT_DOOR_Y1 < player.y && player.y < RIGHT_DOOR_Y2) {
                    if (player.x > 700) {
                        // When we get there, switch scenes
                        scene.gotoNextScene();
                        return true;
                    }
                } else {
                    // Restrict bounds otherwise
                    player.x = 700 - 26;
                }
            }
            if (player.y > LEFT_Y1 - 32 && player.y < LEFT_Y2 && player.x < LEFT_X2 - 6) {
                player.x = LEFT_X2 - 6;
            }
            if (player.y < TOP_Y2 && player.x > TOP_X1 - 26) {
                player.x = TOP_X1 - 26;
            }
            if (player.y > BOTTOM_Y1 - 32 && player.x > BOTTOM_X1 - 26) {
                player.x = BOTTOM_X1 - 26;
            }
        }

        if (oldY != player.y) {
            // Only test Y coordinate stuff
            if (player.y < 0) {
                // If player is headed for top-left door, its fine
                if (LEFT_DOOR_X1 < player.x && player.x < LEFT_DOOR_X2) {
                    if (player.y < -26) {
                        // This one jumps back to SquashCourtScene
                        scene.resources().put(SquashCourtScene.KEY_ID, SquashCourtScene.ID.TOP);
                        scene.switchToScene(SQUASH_COURT_SCENE_IDX);
                        return true;
                    }
                } else {
                    player.y = 0;
                }
            }
            if (player.y > 450 - 32) {
                // If player is headed for top-left door, its fine
                if (LEFT_DOOR_X1 < player.x && player.x < LEFT_DOOR_X2) {
                    if (player.y > 450) {
                        // This one also jumps back to SquashCourtScene
                        scene.resources().put(SquashCourtScene.KEY_ID, SquashCourtScene.ID.BOTTOM);
                        scene.switchToScene(SQUASH_COURT_SCENE_IDX);
                        return true;
                    }
                } else {
                    player.y = 450 - 32;
                }
            }
            if (player.x < LEFT_X2 - 26 && player.y > LEFT_Y1 - 32 && player.y < LEFT_Y2) {
                // If oldY > newY, player was moving upwards
                player.y = oldY > player.y ? LEFT_Y2 : LEFT_Y1 - 32;
            }
            if (player.x > TOP_X1 - 26 && player.y < TOP_Y2) {
                player.y = TOP_Y2;
            }
            if (player.x > BOTTOM_X1 - 26 && player.y > BOTTOM_Y1 - 32) {
                player.y = BOTTOM_Y1 - 32;
            }
        }
        return false;
    }

    @Override
    public void resize(int w, int h) {
        // Ignore
    }
}