/**
 * This class represents an Ethernet packet.
 */
public class Packet {
    private final int length;
    private Host source;
    private final Host destination;

    private int numCellsWritten;

    public Packet(int length, Host destination) {
        if (length < Ether.MIN_PACKET_LENGTH) {
            throw new IllegalArgumentException("Minimum length of a packet is " + Ether.MIN_PACKET_LENGTH + " cells.\n" +
                    "(Each cell represents " + 500 / Ether.MAX_ETHER_LENGTH + " meters.)");
        }
        this.length = length;
        this.destination = destination;
        numCellsWritten = 0;
    }

    // copy constructor
    public Packet(Packet copy) {
        this.length = copy.length;
        this.source = copy.source;
        this.destination = copy.destination;
        this.numCellsWritten = copy.numCellsWritten;
    }

    public void setSource(Host source) {
        this.source = source;
    }

    public void markCellWritten() {
        if (numCellsWritten >= length) {
            throw new IllegalStateException("No more data can be written");
        } else {
            numCellsWritten++;
        }
    }

    public boolean cellsLeftToWrite() {
        return numCellsWritten < length;
    }

    public Host getDestination() {
        return destination;
    }

    public Host getSource() {
        return source;
    }
}
