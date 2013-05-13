import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This class represents a single host on this Ethernet network.
 */
public class Host {
    private enum State {
        IDLE,               // No packets to send
        DEFERRING,          // Waiting for passing packet to complete
        WRITING,            // Writing packet to ether
        BACKED_OFF,         // Waiting after collision before re-sending
    }

    private final double x;
    private final double y;
    private final int cellIndex;
    private final Ether ether;
    private final Queue<Packet> queuedPackets;
    private final Color hostColor;
    private final String name;

    private int secondsTilNextSend;
    private int backoffFactor;
    private State currentState;

    public Host(String name, int x, int y, int cellIndex, Color color, Ether ether) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.cellIndex = cellIndex;
        this.ether = ether;
        queuedPackets = new LinkedList<Packet>();
        hostColor = color;

        currentState = State.IDLE;
        secondsTilNextSend = 0;
        backoffFactor = 1;

        if (ether != null) {
            ether.registerHost(this);
        }
    }

    public Color getHostColor() {
        return hostColor;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public synchronized void sendPacket(Packet packet) {
        packet.setSource(this);
        queuedPackets.add(packet);
    }

    public void update() {
        Cell currentCell = ether.read(cellIndex);

        if (!currentCell.isEmpty() &&
            !currentCell.isCorrupted() &&
            currentCell.getPacket().getDestination() == this) {
            System.out.println("HOST " + hostColor + " RECEIVED PACKET FROM HOST " + currentCell
                    .getPacket().getSource().getHostColor() + "!");
        }

        switch (currentState) {
            case IDLE:
                if (currentCell.isEmpty()) {
                    if (!queuedPackets.isEmpty()) {
                        if (queuedPackets.peek().cellsLeftToWrite()) {
                            ether.write(queuedPackets.peek(), cellIndex);
                            queuedPackets.peek().markCellWritten();
                        }

                        if (queuedPackets.peek().cellsLeftToWrite()) {
                            currentState = State.WRITING;
                        }
                    }
                }
                break;
            case DEFERRING:
                if (currentCell.isEmpty()) {
                    currentState = State.IDLE;

                    if (!queuedPackets.isEmpty()) {
                        if (queuedPackets.peek().cellsLeftToWrite()) {
                            ether.write(queuedPackets.peek(), cellIndex);
                            queuedPackets.peek().markCellWritten();
                        }

                        if (queuedPackets.peek().cellsLeftToWrite()) {
                            currentState = State.WRITING;
                        }
                    }
                }
                break;
            case WRITING:
                if (currentCell.isCorrupted()) {
                    ether.write(Ether.JAM_PACKET, cellIndex);
                    secondsTilNextSend += (int) (Math.random() * (1 << ++backoffFactor));
                    currentState = State.BACKED_OFF;
                } else {
                    if (queuedPackets.peek().cellsLeftToWrite()) {
                        ether.write(queuedPackets.peek(), cellIndex);
                        queuedPackets.peek().markCellWritten();
                    } else {
                        queuedPackets.poll();
                        secondsTilNextSend = 0;
                        currentState = State.IDLE;
                    }
                }
                break;
            case BACKED_OFF:
                if (--secondsTilNextSend == 0) {
                    backoffFactor = 1;
                    currentState = State.WRITING;
                }
                break;
        }
    }
}
