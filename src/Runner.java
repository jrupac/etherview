import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Main entry point into the program.
 */
public class Runner {
    private static Ether ether;

    public static final double X_SCALE = 200;
    public static final double Y_SCALE = 200;
    private static final int NUM_CELLS = 25;

    public static final double CELL_WIDTH = X_SCALE / (NUM_CELLS - 1);
    public static final double CELL_HEIGHT = Y_SCALE / 4;

    private static int fps = 10;
    public static final Map<Integer, List<Command>> commands = new TreeMap<Integer, List<Command>>();

    public static void main(String args[]) throws InterruptedException {
        ether = new Ether(0, 3 * Y_SCALE / 10, CELL_WIDTH, CELL_HEIGHT, NUM_CELLS);

        final Host[] hosts = new Host[3];
        hosts[0] = new Host("Alice", 2, new Color(182, 223, 225), ether);
        hosts[1] = new Host("Bob", 12, new Color(237, 110, 100), ether);
        hosts[2] = new Host("Charlie", 22, new Color(78, 139, 97), ether);

        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                EtherGUI gui = new EtherGUI(hosts, 1000, 300);
                gui.pack();
                gui.setVisible(true);

                // TODO: make drawing canvas a subcomponent in the main GUI window
                StdDraw.setXscale(0, X_SCALE);
                StdDraw.setYscale(0, Y_SCALE);

                Renderer.registerDrawable(ether);
            }
        });

        Scanner scan = new Scanner(System.in);
        while (scan.hasNextInt()) {
            int frame = scan.nextInt();
            int senderIndex = scan.nextInt();
            int receiverIndex = scan.nextInt();
            int length = scan.nextInt();

            if (frame < 0) {
                System.err.println("Frame must be greater than 0. Ignoring...");
            } else if (senderIndex < 0 || senderIndex >= hosts.length) {
                System.err.println("Sender must be between 0 and " + (hosts.length - 1) + ". Ignoring...");
            } else if (receiverIndex < 0 || receiverIndex >= hosts.length) {
                System.err.println("Receiver must be between 0 and " + (hosts.length - 1) + ". Ignoring...");
            } else if (length < Ether.MIN_PACKET_LENGTH) {
                System.err.println("Length must be at least " + Ether.MIN_PACKET_LENGTH + ". Ignoring...");
            } else {
                if (!commands.containsKey(frame)) {
                    commands.put(frame, new ArrayList<Command>());
                }

                commands.get(frame).add(new Command(hosts[senderIndex], hosts[receiverIndex], length));
            }
        }
    }

    public static int getFps() {
        return fps;
    }

    public static void setFps(int f) {
        fps = f;
    }
}
