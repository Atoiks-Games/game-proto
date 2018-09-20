package org.atoiks.games.staventure.prefabs;

import java.awt.Color;
import java.awt.Image;

import org.atoiks.games.framework2d.IGraphics;

import org.atoiks.games.staventure.colliders.RectangleCollider;

public class SquashPlayer{

    public static final Image[] SPRITE_SHEET = new Image[12];

    public static final int IDLE_FRAME   = -1;
    public static final int MOVING_DOWN  = 0;
    public static final int MOVING_RIGHT = 3;
    public static final int MOVING_LEFT  = 6;
    public static final int MOVING_UP    = 9;

    public final RectangleCollider collider = new RectangleCollider();

    public Direction direction;
    public int state;

    public float x;
    public float y;
    public float speed;

    public float elapsed;

    public SquashPlayer() {
        // Collider's size does not change
        collider.w = 32 - 12;
        collider.h = 32;
    }

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

        /*
        // Debug use: Displays the collider
        g.setColor(Color.black);
        collider.render(g);
        */
    }

    public void update(final float dt, final Direction nextMove) {
        elapsed += dt;
        boolean updateState = true;
        if (nextMove == null) {
            updateState = false;
        } else {
            direction = nextMove;
        }

        if (!updateState) {
            state = IDLE_FRAME;
            elapsed = 0;
        } else if (elapsed > speed / 125) {
            elapsed = 0;
            if (++state > 2) state = 0;
        }

        // Update collider location
        // x coordinates have 6px padding
        collider.x = this.x + 6;
        collider.y = this.y;
    }

    public void move(final float x, final float y) {
        setX(x);
        setY(y);
    }

    public void translate(final float dx, final float dy) {
        setX(x + dx);
        setY(y + dy);
    }

    public void setX(final float x) {
        this.x = x;
        collider.x = x + 6;
    }

    public void setY(final float y) {
        this.y = y;
        collider.y = y;
    }
}