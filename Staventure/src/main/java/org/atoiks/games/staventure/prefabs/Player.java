package org.atoiks.games.staventure.prefabs;

import java.awt.Image;
import java.awt.event.KeyEvent;

import org.atoiks.games.framework2d.IGraphics;
import org.atoiks.games.framework2d.SceneManager;

public class Player {

    public enum Direction {
        UP, DOWN, LEFT, RIGHT;
    }

    public final static Image[] SPRITE_SHEET = new Image[12];

    public static final int IDLE_FRAME   = -1;
    public static final int MOVING_DOWN  = 0;
    public static final int MOVING_RIGHT = 3;
    public static final int MOVING_LEFT  = 6;
    public static final int MOVING_UP    = 9;

    public Direction direction;
    public int state;

    public float x;
    public float y;
    public float speed;

    public float elapsed;

    public void render(final IGraphics g) {
        final int offset;
        switch (direction) {
            case UP:    offset = MOVING_UP; break;
            case DOWN:  offset = MOVING_DOWN; break;
            case LEFT:  offset = MOVING_LEFT; break;
            case RIGHT: offset = MOVING_RIGHT; break;
            default:    throw new AssertionError("Wtf dir " + direction);
        }

        if (state == IDLE_FRAME) {
            g.drawImage(SPRITE_SHEET[offset + 1], (int) x, (int) y);
        } else {
            g.drawImage(SPRITE_SHEET[offset + state], (int) x, (int) y);
        }
    }

    public void update(final float dt, final SceneManager scene) {
        elapsed += dt;
        boolean updateState = true;
        final float dsp = speed * dt;
        if (scene.keyboard().isKeyDown(KeyEvent.VK_W)) {
            this.y -= dsp;
            direction = Direction.UP;
        } else if (scene.keyboard().isKeyDown(KeyEvent.VK_S)) {
            this.y += dsp;
            direction = Direction.DOWN;
        } else if (scene.keyboard().isKeyDown(KeyEvent.VK_A)) {
            this.x -= dsp;
            direction = Direction.LEFT;
        } else if (scene.keyboard().isKeyDown(KeyEvent.VK_D)) {
            this.x += dsp;
            direction = Direction.RIGHT;
        } else {
            updateState = false;
        }

        if (!updateState) {
            state = IDLE_FRAME;
            elapsed = 0;
        } else if (elapsed > speed / 125) {
            elapsed = 0;
            if (++state > 2) state = 0;
        }
    }

    public boolean overlapsRect(final float x1, final float y1, final float x2, final float y2) {
        return x < x2 && x + 32 > x1 && y < y2 && y + 32 > y1;
    }
}