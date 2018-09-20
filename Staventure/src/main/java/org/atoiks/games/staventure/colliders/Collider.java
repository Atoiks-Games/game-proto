package org.atoiks.games.staventure.colliders;

import org.atoiks.games.framework2d.IGraphics;

public interface Collider {

    public void render(IGraphics g);

    public boolean collidesWith(Collider collider);

    public default boolean collidesWithAny(final Collider... colliders) {
        for (final Collider col : colliders) {
            if (collidesWith(col)) return true;
        }
        return false;
    }

    public default boolean collidesWithAll(final Collider... colliders) {
        for (final Collider col : colliders) {
            if (!collidesWith(col)) return false;
        }
        return true;
    }
}