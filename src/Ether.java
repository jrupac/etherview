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

    private final List<Packet> packets;
    private final Cell[] ether;

    public Ether(double startX, double startY, double cellWidth, double cellHeight, int numCells) {
        ether = new Cell[numCells];
        RTT = numCells * 2;

        for (int i = 0; i < ether.length; i++) {
            ether[i] = new Cell(startX + i * cellWidth , startY,
                                cellWidth / 2, cellHeight / 2);
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
