import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * A Cell is a single unit on the ether.
 */
public class Cell implements Drawable {
    // Fudge factor to prevent color bleeds
    private static final double EPS = 1;

    private final double x;
    private final double y;
    private final double halfX;
    private final double halfY;
    private Color emptyColor;
    private Set<Packet> packetsInCell;

    public Cell(double x, double y, double halfX, double halfY) {
        this.x = x;
        this.y = y;
        this.halfX = halfX;
        this.halfY = halfY;
        emptyColor = Color.YELLOW;
        packetsInCell = new HashSet<Packet>();
    }

    public Packet getPacket() {
        if (isEmpty() || isCorrupted()) {
            return null;
        } else {
            return packetsInCell.iterator().next();
        }
    }

    public Iterable<Packet> getPackets() {
        return new HashSet<Packet>(packetsInCell);
    }

    public void addPacket(Packet packet) {
        packetsInCell.add(packet);
    }

    public void removePacket(Packet packet) {
        packetsInCell.remove(packet);
    }

    public boolean isEmpty() {
        return packetsInCell.isEmpty();
    }

    public boolean isCorrupted() {
        return packetsInCell.size() > 1;
    }


    @Override
    public void draw() {
        Color old = StdDraw.getPenColor();

        if (isEmpty()) {
            StdDraw.setPenColor(emptyColor);
            StdDraw.filledRectangle(x - EPS, y - EPS, halfX + EPS, halfY + EPS);
        } else {
            int avgRgb = 0;

            for (Packet packet : packetsInCell) {
                avgRgb += packet.getSource().getHostColor().getRGB();
            }

            StdDraw.setPenColor(new Color(avgRgb / packetsInCell.size()));
            StdDraw.filledRectangle(x - EPS, y - EPS, halfX + EPS / 2, halfY + EPS / 2);
        }

        StdDraw.setPenColor(old);
    }
}
