import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * A Cell is a single unit on the ether.
 */
public class Cell implements Drawable {
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
            StdDraw.filledRectangle(x, y, halfX, halfY);
        } else {
            double colorHeight = halfY / (packetsInCell.size());
            int index = 0;

            for (Packet packet : packetsInCell) {
                StdDraw.setPenColor(packet.getSource().getHostColor());
                StdDraw.filledRectangle(x, y + index * colorHeight, halfX, colorHeight);
                index++;
            }
        }

        StdDraw.setPenColor(old);
    }
}
