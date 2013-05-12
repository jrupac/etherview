import java.awt.*;

/**
 * Main entry point into the program.
 */
public class Runner {
    private static Ether ether;

    private static final double X_SCALE = 200;
    private static final double Y_SCALE = 200;
    private static final int NUM_CELLS = 21;

    private static final double CELL_WIDTH = X_SCALE / (NUM_CELLS - 1);
    private static final double CELL_HEIGHT = Y_SCALE / 2;

    public static void main(String args[]) throws InterruptedException {
        ether = new Ether(0, 3 * Y_SCALE / 10, CELL_WIDTH, CELL_HEIGHT, NUM_CELLS);

        final Host[] hosts = new Host[3];
        hosts[0] = new Host("Alice", 0, 0, 5, Color.CYAN, ether);
        hosts[1] = new Host("Bob", 0, 0, 10, Color.MAGENTA, ether);
        hosts[2] = new Host("trOOOdy", 0, 0, 2, Color.YELLOW, ether);

        ether.registerHost(hosts[0]);
        ether.registerHost(hosts[1]);
        ether.registerHost(hosts[2]);

        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                EtherGUI gui = new EtherGUI(hosts, 400, 100);
                gui.pack();
                gui.setVisible(true);

                // TODO: make drawing canvas a subcomponent in the main GUI window
                StdDraw.setXscale(0, X_SCALE);
                StdDraw.setYscale(0, Y_SCALE);

                Renderer.registerDrawable(ether);
//                Renderer.start();
            }
        });




        hosts[2].sendPacket(new Packet(6, hosts[0]));
        hosts[0].sendPacket(new Packet(4, hosts[1]));
        // TODO: fix bug that occurs when there are 3 packets
        hosts[1].sendPacket(new Packet(10, hosts[0]));
    }
}
