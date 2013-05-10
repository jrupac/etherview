import java.awt.Color;

/**
 * This class represents an Ethernet packet.
 */
public class Packet {
    private final int length;
    private final Color color;

    public Packet(int length, Color color) {
        this.length = length;
        this.color = color;
    }

    public Host getDestination() {
        return null;
    }
}
