/**
 * Main entry point into the program.
 */
public class Runner {
    private static Ether ether;

    public static void main(String args[]) throws InterruptedException {
        ether = new Ether(10, 10, 15);

        //StdDraw.setCanvasSize(200, 50);

        Renderer.registerDrawable(ether);
        Renderer.start();
    }
}
