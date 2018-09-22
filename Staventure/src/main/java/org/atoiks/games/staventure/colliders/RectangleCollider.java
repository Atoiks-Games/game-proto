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

package org.atoiks.games.staventure.colliders;

import org.atoiks.games.framework2d.IGraphics;

public final class RectangleCollider implements Collider {

    public float x;
    public float y;
    public float w;
    public float h;

    public RectangleCollider() {
        //
    }

    public RectangleCollider(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        if (w < 0) {
            this.x += w;
            this.w *= -1;
        }
        if (h < 0) {
            this.y += h;
            this.h *= -1;
        }
    }

    @Override
    public void render(IGraphics g) {
        g.drawRect(x, y, x + w, y + h);
    }

    @Override
    public boolean collidesWith(Collider other) {
        if (other.getClass() == RectangleCollider.class) {
            final RectangleCollider rect = (RectangleCollider) other;
            return x < rect.x + rect.w && x + w > rect.x
                && y < rect.y + rect.h && y + h > rect.y;
        }
        if (other.getClass() == CircleCollider.class) {
            return other.collidesWith(this);
        }
        return false;
    }

    public float[] toPolygon() {
        return new float[] {
            x,     y,
            x + w, y,
            x + w, y + h,
            x,     y + h,
        };
    }
}