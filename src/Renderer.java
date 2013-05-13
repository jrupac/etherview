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
    private static volatile boolean isStepping = false;

    public synchronized static void registerDrawable(Drawable drawable) {
        children.add(drawable);
    }

    public synchronized static void unregisterDrawable(Drawable drawable) {
        children.remove(drawable);
    }

    public static void stop() {
        if (drawThread != null) {
            isPaused = false;
            isStepping = false;
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

    public synchronized static void step() {
        isPaused = false;
        isStepping = true;

        if (runnable != null) {
            synchronized (runnable) {
                runnable.notify();
            }
        }
    }

    public synchronized static void pause() {
        isPaused = true;
        isStepping = false;
    }

    public synchronized static void resume() {
        isPaused = false;
        isStepping = false;

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
                            StdDraw.show(1000 / Runner.getFps());

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

                            if (Renderer.isStepping) {
                                Renderer.pause();
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
