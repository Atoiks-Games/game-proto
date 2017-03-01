import java.awt.Image;
import java.awt.Point;
import java.awt.Graphics;

/**
 * Essentially the background
 */
public class Floor implements GComponent {

    protected Image image;

    public Floor (Image img) {
	this.image = img;
    }

    @Override
    public void render (Graphics g) {
	g.drawImage (image, 0, 0, null);
    }

    @Override
    public boolean containsPoint (Point p) {
	// It is the background so guaranteed contains the point
	return true;
    }

    @Override
    public void update (long millisec) {
    }

    @Override
    public void testCollision (GComponent comp) {
    }
}
