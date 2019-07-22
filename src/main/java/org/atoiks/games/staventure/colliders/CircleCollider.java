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

package org.atoiks.games.staventure.colliders;

import org.atoiks.games.framework2d.IGraphics;

public final class CircleCollider implements Collider {

    public float x;
    public float y;
    public float r;

    public CircleCollider() {
        //
    }

    public CircleCollider(float x, float y, float r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }

    @Override
    public void render(IGraphics g) {
        g.drawCircle((int) x, (int) y, (int) r);
    }

    @Override
    public boolean collidesWith(Collider other) {
        if (other.getClass() == CircleCollider.class) {
            final CircleCollider circ = (CircleCollider) other;
            final float dx = x - circ.x;
            final float dy = y - circ.y;
            final float dr = r + circ.r;
            return dx * dx + dy * dy < dr * dr;
        }
        if (other.getClass() == RectangleCollider.class) {
            return collidesWithPolygon(((RectangleCollider) other).toPolygon());
        }
        return false;
    }

    private boolean collidesWithPolygon(float[] coords) {
        for (int i = 0; i < coords.length; i += 2) {
            final float startX = coords[i];
            final float startY = coords[i + 1];
            final float endX   = coords[(i + 2) % coords.length];
            final float endY   = coords[(i + 3) % coords.length];
            if (intersectSegmentCircle(startX, startY, endX, endY, x, y, r)) {
                return true;
            }
        }
        return false;
    }

    private static boolean intersectSegmentCircle(float x1, float y1, float x2, float y2,
                                                  float cx, float cy, float cr) {
        // Taken from https://stackoverflow.com/a/10392860
        final float acx = cx - x1;
        final float acy = cy - y1;

        final float abx = x2 - x1;
        final float aby = y2 - y1;

        final float ab2 = abx * abx + aby * aby;
        final float acab = acx * abx + acy * aby;
        float t = acab / ab2;

        if (t < 0) {
            t = 0;
        } else if (t > 1) {
            t = 1;
        }

        final float hx = (abx * t) + x1 - cx;
        final float hy = (aby * t) + y1 - cy;

        return hx * hx + hy * hy <= cr * cr;
    }
}
