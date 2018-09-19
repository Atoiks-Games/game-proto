package org.atoiks.games.staventure.colliders;

import org.atoiks.games.framework2d.IGraphics;

public interface Collider {

    public void render(IGraphics g);

    public boolean collidesWith(Collider collider);
}