import java.util.LinkedList;
import java.util.Queue;

/**
 * This class represents a single host on this Ethernet network.
 */
public class Host {
    private enum State {
        IDLE,       // No packets to send
        DEFERRING,  // Waiting for passing packet to complete
        WRITING,    // Writing packet to ether
        BACKED_OFF, // Waiting after collision before re-sending
        WAITING_ON_SEND     // Waiting to ensure sent packet does not get corrupted
    }

    private final double x;
    private final double y;
    private final int cellIndex;
    private final Ether ether;
    private final Queue<Packet> queuedPackets;

    private int secondsTilNextSend;
    private int backoffFactor;
    private int secondsTilSure;
    private State currentState;

    public Host(int x, int y, int cellIndex, Ether ether) {
        this.x = x;
        this.y = y;
        this.cellIndex = cellIndex;
        this.ether = ether;

        queuedPackets = new LinkedList<Packet>();

        currentState = State.IDLE;
        secondsTilNextSend = 0;
        backoffFactor = 1;
        secondsTilSure = 0;
    }

    public synchronized void sendPacket(Packet packet) {
        queuedPackets.add(packet);
    }

    public void update() {
        Cell currentCell = ether.read(cellIndex);

        if (!isCorrupted(currentCell) && currentCell.getPacket().getDestination() == this) {
            // do something with this!!
        }

        switch (currentState) {
            case IDLE:
                if (currentCell.isEmpty()) {
                    if (!queuedPackets.isEmpty()) {
                        // send first cell

                        // if packet > 1 cell
                        currentState = State.WRITING;
                    }
                }
                break;
            case DEFERRING:
                if (currentCell.isEmpty()) {
                    currentState = State.IDLE;

                    if (!queuedPackets.isEmpty()) {
                        // send first cell

                        // if packet > 1 cell
                        currentState = State.WRITING;
                    }
                }
                break;
            case WRITING:
                if (isCorrupted(currentCell)) {
                    ether.write(Ether.JAM_PACKET, cellIndex);
                    secondsTilNextSend += (int) (Math.random() * (1 << ++backoffFactor));
                    currentState = State.BACKED_OFF;
                } else {
                    // if something left to write
                        // keep writing
                    // else
                        secondsTilSure = ether.RTT;
                        currentState = State.WAITING_ON_SEND;

                }
                break;
            case BACKED_OFF:
                if (--secondsTilNextSend == 0) {
                    backoffFactor = 1;
                    currentState = State.WRITING;
                }
                break;
            case WAITING_ON_SEND:
                if (isCorrupted(currentCell)) {
                    ether.write(Ether.JAM_PACKET, cellIndex);
                    secondsTilNextSend += (int) (Math.random() * (1 << ++backoffFactor));
                    secondsTilSure = Integer.MAX_VALUE;
                    currentState = State.BACKED_OFF;
                } else {
                    if (--secondsTilSure == 0) {
                        secondsTilNextSend = 0;
                        currentState = State.IDLE;
                    }
                }
                break;
        }
    }

    public boolean isCorrupted(Cell cell) {
        return false;
    }
}
