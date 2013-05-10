import com.sun.xml.internal.ws.api.message.Packet;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to represent the ether, i.e., the cable
 * that hosts on the network are connected to.
 */
public class Ether implements Drawable {
    public static Packet JAM_PACKET = null;

    public final int RTT;

    private static final int CELL_WIDTH = 15;
    private static final int CELL_HEIGHT = 20;

    private final List<Packet> packets;
    private final Cell[] ether;

    public Ether(int startX, int startY, int numCells) {
        ether = new Cell[numCells];
        RTT = numCells * 2;

        for (int i = 0; i < ether.length; i++) {
            ether[i] = new Cell(startX + i * CELL_WIDTH , startY,
                                CELL_WIDTH / 2, CELL_HEIGHT / 2);
        }

        packets = new ArrayList<Packet>();
    }

    public Cell read(int index) {
        return ether[index];
    }

    public void write(Packet packet, int index) {
        // Apply packet to cell
    }

    @Override
    public void draw() {
        for (Packet packet : packets) {
            // Apply packets to cells
        }

        for (Cell cell : ether) {
            cell.draw();
        }
    }
}
