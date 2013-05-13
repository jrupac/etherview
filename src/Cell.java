import java.awt.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A Cell is a single unit on the ether.
 */
public class Cell implements Drawable {
    // Fudge factor to prevent color bleeds
    private static final double EPS = 1;

    // determines about how many striped should be made in collisions
    private static final int NUM_STRIPES = 2;

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
        emptyColor = new Color(220, 220, 200);
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
        return packetsInCell;
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
        } else if (packetsInCell.size() == 1) {
            StdDraw.setPenColor(getPacket().getSource().getHostColor());
            StdDraw.filledRectangle(x - EPS, y - EPS, halfX + EPS / 2, halfY + EPS / 2);
        } else {
            double splitHalfY = halfY / packetsInCell.size() / NUM_STRIPES;
            double splitY = y - halfY + splitHalfY;

            for (int i = 0; i < NUM_STRIPES; i++) {
                for (Packet packet : packetsInCell) {
                    StdDraw.setPenColor(packet.getSource().getHostColor());
                    StdDraw.filledRectangle(x - EPS, splitY - EPS, halfX + EPS / 2, splitHalfY + EPS / 2);
                    splitY += 2 * splitHalfY;
                }
            }
        }

        StdDraw.setPenColor(old);
    }
}
