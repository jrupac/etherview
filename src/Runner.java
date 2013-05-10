/**
 * Main entry point into the program.
 */
public class Runner {
    private static Ether ether;

    private static final double X_SCALE = 200;
    private static final double Y_SCALE = 200;
    private static final int NUM_CELLS = 21;

    private static final double CELL_WIDTH = X_SCALE / (NUM_CELLS - 1);
    private static final double CELL_HEIGHT = Y_SCALE / 3;

    public static void main(String args[]) throws InterruptedException {
        ether = new Ether(0, 0, CELL_WIDTH, CELL_HEIGHT, NUM_CELLS);

        StdDraw.setCanvasSize(400, 100);
        StdDraw.setTitle("Etherview");

        StdDraw.setXscale(0, X_SCALE);
        StdDraw.setYscale(0, Y_SCALE);

        Renderer.registerDrawable(ether);
        Renderer.start();
    }
}
