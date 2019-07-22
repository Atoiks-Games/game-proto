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

package org.atoiks.games.staventure.prefabs;

import org.atoiks.games.framework2d.Input;
import org.atoiks.games.framework2d.KeyCode;
import org.atoiks.games.framework2d.IGraphics;

import org.atoiks.games.framework2d.resource.Texture;

import org.atoiks.games.staventure.colliders.RectangleCollider;

public class Player {

    public static final Texture[] SPRITE_SHEET = new Texture[12];

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

    public Player() {
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
            g.drawTexture(SPRITE_SHEET[offset + 1], x, y);
        } else {
            g.drawTexture(SPRITE_SHEET[offset + state], x, y);
        }

        /*
        // Debug use: Displays the collider
        g.setColor(Color.black);
        collider.render(g);
        */
    }

    public void update(final float dt) {
        elapsed += dt;
        boolean updateState = true;
        final float dsp = speed * dt;
        if (Input.isKeyDown(KeyCode.KEY_W)) {
            this.y -= dsp;
            direction = Direction.UP;
        } else if (Input.isKeyDown(KeyCode.KEY_S)) {
            this.y += dsp;
            direction = Direction.DOWN;
        } else if (Input.isKeyDown(KeyCode.KEY_A)) {
            this.x -= dsp;
            direction = Direction.LEFT;
        } else if (Input.isKeyDown(KeyCode.KEY_D)) {
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

        // Update collider location
        // x coordinates have 6px padding
        collider.x = this.x + 6;
        collider.y = this.y;
    }

    public void move(final float x, final float y) {
        setX(x);
        setY(y);
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
