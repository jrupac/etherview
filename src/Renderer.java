import java.util.HashSet;
import java.util.Set;

/**
 * This class implements the main draw loop.
 */
public class Renderer {
    private static final Set<Drawable> children;

    static {
        children = new HashSet<Drawable>();
    }

    private static Thread drawThread = null;
    private static Runnable runnable = null;

    private static volatile boolean isPaused = false;
    private static volatile boolean isStopped = false;

    public synchronized static void registerDrawable(Drawable drawable) {
        children.add(drawable);
    }

    public synchronized static void unregisterDrawable(Drawable drawable) {
        children.remove(drawable);
    }

    public static void stop() {
        if (drawThread != null) {
            isStopped = true;

            if (runnable != null) {
                synchronized (runnable) {
                    runnable.notify();
                }
            }

            try {
                drawThread.join();
            } catch (InterruptedException e) {
                System.err.println("Interrupted while waiting for thread to join: " + e.getMessage());
            }
        }
    }

    public static void pause() {
        isPaused = true;
    }

    public static void resume() {
        isPaused = false;

        if (runnable != null) {
            synchronized (runnable) {
                runnable.notify();
            }
        }
    }

    public static void start() {
        if (drawThread != null) {
            return;
        }

        synchronized (Renderer.class) {
            runnable = new Runnable() {
                public void run() {
                    while (!Renderer.isStopped) {
                        try {
                            Thread.sleep(100);

                            if (Renderer.isPaused) {
                                synchronized (this) {
                                    while (Renderer.isPaused) {
                                        wait();
                                    }
                                }
                            }
                        } catch (InterruptedException expected) {
                        }

                        if (!Renderer.isStopped) {
                            for (Drawable child : Renderer.children) {
                                child.draw();
                            }
                        }
                    }
                }
            };

            drawThread = new Thread(runnable);
            drawThread.start();
        }
    }
}
