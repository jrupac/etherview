import java.awt.*;
import java.util.*;

/**
 * A class to represent the ether, i.e., the cable
 * that hosts on the network are connected to.
 */
public class Ether implements Drawable {

    // Used to make all jam packets have the same color
    private static final Host JAM_HOST = new Host("JAM", 0, 0, 0, Color.BLACK, null);
    public static final Packet JAM_PACKET = new Packet(1, JAM_HOST);

    static {
        JAM_PACKET.setSource(JAM_HOST);
    }

    private static enum Direction {
        LEFT,
        RIGHT,
        BOTH    // needed for initial cell
    }

    public final int RTT;

    private final double startX;
    private final double startY;
    private final double cellWidth;
    private final double cellHeight;
    private Cell[] ether;
    private final Set<Host> hosts;
    private final Map<Packet, Direction> directions;


    public Ether(double startX, double startY, double cellWidth, double cellHeight, int numCells) {
        this.startX = startX;
        this.startY = startY;
        this.cellHeight = cellHeight;
        this.cellWidth = cellWidth;

        ether = new Cell[numCells];
        hosts = new HashSet<Host>();
        directions = new HashMap<Packet, Direction>();
        RTT = numCells * 2;

        for (int i = 0; i < ether.length; i++) {
            ether[i] = new Cell(startX + i * cellWidth, startY,
                                cellWidth / 2, cellHeight / 2);
        }
    }

    public synchronized void registerHost(Host host) {
        hosts.add(host);
    }

    public Cell read(int index) {
        return ether[index];
    }

    public synchronized void write(Packet packet, int index) {
        ether[index].addPacket(packet);
        directions.put(packet, Direction.BOTH);
    }

    @Override
    public void draw() {
        for (Host host : hosts) {
            host.update();
        }

        Cell[] newEther = new Cell[ether.length];

        for (int i = 0; i < newEther.length; i++) {
            newEther[i] = new Cell(startX + i * cellWidth, startY,
                                   cellWidth / 2, cellHeight / 2);
        }

        for (int i = 0; i < ether.length; i++) {
            for (Packet packet : ether[i].getPackets()) {
                if (directions.get(packet) == Direction.BOTH) {
                    Packet leftPacket = new Packet(packet);
                    directions.put(packet, Direction.RIGHT);
                    directions.put(leftPacket, Direction.LEFT);

                    if (i < ether.length - 1) newEther[i + 1].addPacket(packet);
                    if (i > 0) newEther[i - 1].addPacket(leftPacket);
                } else if (directions.get(packet) == Direction.LEFT) {
                    if (i > 0) newEther[i - 1].addPacket(packet);
                } else if (directions.get(packet) == Direction.RIGHT) {
                    if (i < ether.length - 1) newEther[i + 1].addPacket(packet);
                } else {
                    throw new RuntimeException("Packet did not have associated direction.");
                }
            }
        }

        ether = newEther;

        for (Cell cell : ether) {
            cell.draw();
        }
    }
}
