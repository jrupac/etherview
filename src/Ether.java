import java.awt.*;
import java.util.*;

/**
 * A class to represent the ether, i.e., the cable
 * that hosts on the network are connected to.
 */
public class Ether implements Drawable {

    // The division by 1 is because packets travel at 1 cell per frame
    public static final int MAX_ETHER_LENGTH = 25;
    public static final int MIN_PACKET_LENGTH = (2 * MAX_ETHER_LENGTH) / 1;
    public static final int SLOT_TIME = MAX_ETHER_LENGTH / 1;

    // Used to make all jam packets have the same color
    private static final Host JAM_HOST = new Host("JAM", 0, Color.BLACK, null);
    public static final Packet JAM_PACKET = new Packet(MIN_PACKET_LENGTH, JAM_HOST);

    static {
        JAM_PACKET.setSource(JAM_HOST);
    }

    private static enum Direction {
        LEFT,
        RIGHT,
        BOTH    // needed for initial cell
    }

    private int frame;
    private final double startX;
    private final double startY;
    private final double cellWidth;
    private final double cellHeight;
    private Cell[] ether;
    private final Set<Host> hosts;
    private final Map<Packet, Direction> directions;


    public Ether(double startX, double startY, double cellWidth, double cellHeight, int numCells) {
        if (numCells > MAX_ETHER_LENGTH) {
            throw new IllegalArgumentException("Maximum length of an Ether is " + MAX_ETHER_LENGTH + " cells.\n" +
                    "(Each cell represents " + 500 / MAX_ETHER_LENGTH + " meters.)");
        }
        frame = 0;
        this.startX = startX;
        this.startY = startY;
        this.cellHeight = cellHeight;
        this.cellWidth = cellWidth;

        ether = new Cell[numCells];
        hosts = new HashSet<Host>();
        directions = new HashMap<Packet, Direction>();

        for (int i = 0; i < ether.length; i++) {
            ether[i] = new Cell(startX + i * cellWidth, startY,
                                cellWidth / 2, cellHeight / 2);
        }
    }

    public double getY() {
        return startY;
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
        if (Runner.commands.containsKey(frame)) {
            for (Command c : Runner.commands.get(frame)) {
                c.sender.sendPacket(new Packet(c.length, c.receiver));
            }
        }

        Color oldColor = StdDraw.getPenColor();
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.filledRectangle(Runner.CELL_WIDTH * (ether.length - 1), 5,
                Runner.CELL_WIDTH, Runner.CELL_HEIGHT);
        StdDraw.setPenColor(oldColor);
        StdDraw.text(Runner.CELL_WIDTH * (ether.length - 1), 5, String.valueOf(++frame));

        for (Host host : hosts) {
            host.update();
        }

        for (Cell cell : ether) {
            cell.draw();
        }

        Cell[] newEther = new Cell[ether.length];

        for (int i = 0; i < newEther.length; i++) {
            newEther[i] = new Cell(startX + i * cellWidth, startY,
                                   cellWidth / 2, cellHeight / 2);
        }

        for (int i = 0; i < ether.length; i++) {
            for (Packet packet : ether[i].getPackets()) {
                if (directions.get(packet) == Direction.BOTH) {
                    Packet rightPacket = new Packet(packet);
                    Packet leftPacket = new Packet(packet);
                    directions.put(rightPacket, Direction.RIGHT);
                    directions.put(leftPacket, Direction.LEFT);

                    if (i < ether.length - 1) newEther[i + 1].addPacket(rightPacket);
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
    }
}
