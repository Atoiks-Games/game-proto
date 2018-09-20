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
            return Math.hypot(x - circ.x, y - circ.y) < r + circ.r;
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

    private static boolean intersectSegmentCircle(float startX, float startY, float endX, float endY,
                                                  float centerX, float centerY, float r) {
        // Based on https://www.gamedevelopment.blog/collision-detection-circles-rectangles-and-polygons/
        final float t0X = endX - startX;
        final float t0Y = endX - startY;
        final float t1X = centerX - startX;
        final float t1Y = centerY - startY;

        final float l = (float) Math.hypot(t0X, t0Y);
        final float u = t1X * t0X / l + t1Y * t0Y / l;

        final float t2X, t2Y;
        if (u <= 0) {
            t2X = startX; t2Y = startY;
        } else {
            t2X = endX; t2Y = endY;
        }

        final float x = centerX - t2X;
        final float y = centerY - t2Y;
        return x * x + y * y <= r * r;
    }
}