/**
 * A Cell is a single unit on the ether.
 */
public class Cell implements Drawable {
    private final int x;
    private final int y;
    private final int halfX;
    private final int halfY;

    public Cell(int x, int y, int halfX, int halfY) {
        this.x = x;
        this.y = y;
        this.halfX = halfX;
        this.halfY = halfY;
    }

    public Packet getPacket() {
        return null;
    }

    public boolean isEmpty() {
        return true;
    }

    @Override
    public void draw() {
        StdDraw.rectangle(x, y, halfX, halfY);
    }
}
