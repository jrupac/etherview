import java.awt.*;

/**
 * A Cell is a single unit on the ether.
 */
public class Cell implements Drawable {
    private final double x;
    private final double y;
    private final double halfX;
    private final double halfY;
    private Color color;

    public Cell(double x, double y, double halfX, double halfY) {
        this.x = x;
        this.y = y;
        this.halfX = halfX;
        this.halfY = halfY;
        color = Color.YELLOW;
    }

    public Packet getPacket() {
        return null;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isEmpty() {
        return color.equals(Color.YELLOW);
    }

    @Override
    public void draw() {
        Color old = StdDraw.getPenColor();
        StdDraw.setPenColor(color);
        StdDraw.filledRectangle(x, y, halfX, halfY);
        StdDraw.setPenColor(old);
    }
}
