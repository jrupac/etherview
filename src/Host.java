import java.awt.*;
import java.util.*;

/**
 * This class represents a single host on this Ethernet network.
 */
public class Host implements Drawable {
    private static final int MAX_RETRIES = 16;
    private static final double LINE_HEIGHT = Runner.Y_SCALE / 18;

    private enum State {
        IDLE,               // No packets to send
        DEFERRING,          // Waiting for passing packet to complete
        WRITING,            // Writing packet to ether
        BACKED_OFF,         // Waiting after collision before re-sending
        JAMMING,            // Jamming the ether
    }

    private final double x;
    private final double y;
    private final int cellIndex;
    private final Ether ether;
    private final Queue<Packet> queuedPackets;
    private final Color hostColor;
    private final String name;
    private final Map<Host, Integer> receiveLengths;
    private final Map<Host, Boolean> receiveChecksums;
    private final Random rand;

    private int secondsTilNextSend;
    private int backoffFactor;
    private int jamCellsRemaining;
    private State currentState;

    public Host(String name, int cellIndex, Color color, Ether ether) {
        this.name = name;
        this.x = cellIndex * Runner.CELL_WIDTH;
        this.y = 8 * Runner.Y_SCALE / 10;
        this.cellIndex = cellIndex;
        this.ether = ether;

        queuedPackets = new LinkedList<Packet>();
        hostColor = color;
        receiveLengths = new HashMap<Host, Integer>();
        receiveChecksums = new HashMap<Host, Boolean>();
        rand = new Random(System.nanoTime());

        currentState = State.IDLE;
        secondsTilNextSend = 0;
        backoffFactor = 0;
        jamCellsRemaining = 0;

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

    public void sendPacket(Packet packet) {
        packet.setSource(this);
        queuedPackets.add(packet);
    }

    public void receivePacket(Cell cell) {
        for (Packet packet : cell.getPackets()) {
            if (packet.getDestination() == this) {
                if (!receiveLengths.containsKey(packet.getSource())) {
                    receiveLengths.put(packet.getSource(), packet.getLength());
                    receiveChecksums.put(packet.getSource(), true);
                }

                if (cell.isCorrupted()) {
                    receiveChecksums.put(packet.getSource(), false);
                }

                int remaining = receiveLengths.get(packet.getSource());
                if (remaining > 1) {
                    receiveLengths.put(packet.getSource(), remaining - 1);
                } else {
                    if (receiveChecksums.get(packet.getSource()) == true) {
                        System.out.println("HOST " + this + " RECEIVED PACKET FROM HOST " + cell
                                .getPacket().getSource() + " SUCCESSFULLY!");
                    } else {
                        System.out.println("HOST " + this + " RECEIVED PACKET FROM HOST " + cell
                                .getPacket().getSource() + " WITH INVALID CHECKSUM!");
                    }

                    receiveLengths.remove(packet.getSource());
                    receiveChecksums.remove(packet.getSource());
                }
            }
        }

        Set<Host> hosts = new HashSet<Host>(receiveLengths.keySet());
        Set<Host> senders = new HashSet<Host>();
        for (Packet p : cell.getPackets()) {
            senders.add(p.getSource());
        }
        for (Host host : hosts) {
            if (!senders.contains(host)) {
                receiveLengths.remove(host);
                receiveChecksums.remove(host);
            }
        }
    }

    public void update() {
        Cell currentCell = ether.read(cellIndex);

        if (!currentCell.isEmpty()) {
            receivePacket(currentCell);
        }

        switch (currentState) {
            case IDLE:
                if (!queuedPackets.isEmpty()) {
                    if (currentCell.isEmpty()) {
                        ether.write(queuedPackets.peek(), cellIndex);
                        queuedPackets.peek().markCellWritten();
                        currentState = State.WRITING;
                    } else {
                        currentState = State.DEFERRING;
                    }
                }
                break;
            case DEFERRING:
                if (currentCell.isEmpty()) {
                    currentState = State.IDLE;

                    if (!queuedPackets.isEmpty()) {
                        ether.write(queuedPackets.peek(), cellIndex);
                        queuedPackets.peek().markCellWritten();
                        currentState = State.WRITING;
                    }
                }
                break;
            case WRITING:
                if (!currentCell.isEmpty()) {
                    jamCellsRemaining = Ether.MIN_PACKET_LENGTH;
                    queuedPackets.peek().resetCellsLeftToWrite();
                    currentState = State.JAMMING;
                } else {
                    if (queuedPackets.peek().cellsLeftToWrite()) {
                        ether.write(queuedPackets.peek(), cellIndex);
                        queuedPackets.peek().markCellWritten();
                    } else {
                        queuedPackets.poll();
                        secondsTilNextSend = 0;
                        backoffFactor = 0;
                        currentState = State.IDLE;
                    }
                }
                break;
            case BACKED_OFF:
                if (--secondsTilNextSend <= 0) {
                    secondsTilNextSend = 0;
                    if (currentCell.isEmpty()) {
                        currentState = State.WRITING;
                    } else {
                        currentState = State.DEFERRING;
                    }
                }
                break;
            case JAMMING:
                if (--jamCellsRemaining > 0) {
                    ether.write(Ether.JAM_PACKET, cellIndex);
                } else {
                    if (++backoffFactor > MAX_RETRIES) {
                        queuedPackets.poll();
                        System.out.println(this + " retried sending too many times. Dropping packet.");
                        backoffFactor = 0;
                        currentState = State.IDLE;
                    } else {
                        secondsTilNextSend += rand.nextInt(1 + (1 << backoffFactor)) * Ether.SLOT_TIME;
                        currentState = State.BACKED_OFF;
                    }
                }
                break;
        }

        this.draw();
    }

    public void draw() {
        Color oldColor = StdDraw.getPenColor();

        StdDraw.setPenColor(Color.WHITE);
        StdDraw.filledRectangle(x, y,
                Runner.CELL_WIDTH + 20, y - ether.getY() + Runner.CELL_HEIGHT);

        double[] triangleY = new double[3];
        double[] triangleX = new double[3];

        triangleX[0] = this.x;
        triangleY[0] = ether.getY() + Runner.CELL_HEIGHT / 2;

        triangleX[1] = this.x - Runner.CELL_WIDTH / 2;
        triangleY[1] = ether.getY() + Runner.CELL_HEIGHT;

        triangleX[2] = this.x + Runner.CELL_WIDTH / 2;
        triangleY[2] = ether.getY() + Runner.CELL_HEIGHT;

        StdDraw.setPenColor(this.hostColor);
        StdDraw.filledPolygon(triangleX, triangleY);

        StdDraw.setPenColor(Color.BLACK);
        StdDraw.text(x, y, this.toString());

        switch (currentState) {
            case IDLE:
                StdDraw.text(x, y - LINE_HEIGHT, "Idle");
                if (receiveLengths.size() == 1) {
                    StdDraw.text(x, y - 2 * LINE_HEIGHT, "Reading from " + receiveLengths.keySet().iterator().next());
                }
                break;
            case DEFERRING:
                StdDraw.text(x, y - LINE_HEIGHT, "Deferring");
                break;
            case BACKED_OFF:
                StdDraw.text(x, y - LINE_HEIGHT, "Backed Off");
                StdDraw.text(x, y - 2 * LINE_HEIGHT, "Waiting " + String.valueOf(secondsTilNextSend) + " ticks");
                break;
            case WRITING:
                StdDraw.text(x, y - LINE_HEIGHT, "Writing");
                StdDraw.text(x, y - 2 * LINE_HEIGHT, String.valueOf(queuedPackets.peek().numCellsLeftToWrite()) +
                        " segments remaining");
                break;
            case JAMMING:
                StdDraw.text(x, y - LINE_HEIGHT, "Jamming");
                StdDraw.text(x, y - 2 * LINE_HEIGHT, String.valueOf(jamCellsRemaining) + " segments remaining");
                break;
        }

        StdDraw.setPenColor(oldColor);
    }
}
