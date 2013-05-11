import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A class to represent the ether, i.e., the cable
 * that hosts on the network are connected to.
 */
public class Ether implements Drawable {
    public static Packet JAM_PACKET = null;

    private static enum Direction {
        LEFT,
        RIGHT
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

        // TODO: Support both left and right
        for (int i = 1; i < ether.length - 1; i++) {
            for (Packet packet : ether[i].getPackets()) {
                newEther[i + 1].addPacket(packet);
                ether[i].removePacket(packet);
            }
        }

        ether = newEther;

        for (Cell cell : ether) {
            cell.draw();
        }
    }
}
