import java.awt.Point;
import java.awt.Graphics;

/**
 * Graphics component
 */
public interface GComponent {

    public void render (Graphics g);

    public void update (long millisec);

    public boolean containsPoint (Point p);

    public void testCollision (GComponent comp);
}
