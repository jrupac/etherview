import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * The user interface, which allows users to make hosts send messages,
 * and play, pause, or speed up animation.
 */
public class EtherGUI extends JFrame {

    private Host[] senderHosts, receiverHosts;

    private static final String SEND = "Send";
    private static final String START = "Start";
    private static final String PLAY = "Play";
    private static final String PAUSE = "Pause";
    private static final String STEP = "Step";
    private static final String STOP = "Stop";

    public EtherGUI(Host[] h, int canvasX, int canvasY) {
        senderHosts = new Host[h.length + 1];
        System.arraycopy(h, 0, senderHosts, 1, h.length);
        senderHosts[0] = new Host("Sender", 0, null, null);

        receiverHosts = new Host[h.length + 1];
        System.arraycopy(h, 0, receiverHosts, 1, h.length);
        receiverHosts[0] = new Host("Receiver", 0, null, null);

        createGUI(canvasX, canvasY);
    }

    private void createGUI(int canvasX, int canvasY) {
//        setLayout(new BorderLayout(5, 10));
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
//        add(Box.createRigidArea(new Dimension(0, 35)));

        // Host actions, packet slider, controls
        final JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        final JPanel slider = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        final JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        final JPanel speed = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));

        final JComboBox<Host> senderBox = new JComboBox<Host>(senderHosts);
        final JComboBox<Host> receiverBox = new JComboBox<Host>(receiverHosts);
        final JButton sendButton = new JButton(SEND);

        senderBox.setSelectedIndex(0);
        senderBox.setPreferredSize(new Dimension(140, 22));
        senderBox.setMaximumSize(new Dimension(140, 22));
        actions.add(senderBox);

        receiverBox.setSelectedIndex(0);
        receiverBox.setPreferredSize(new Dimension(140, 22));
        receiverBox.setMaximumSize(new Dimension(140, 22));
        actions.add(receiverBox);

        sendButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        final JLabel sliderLabel = new JLabel("Packet Length:");
        final JSlider packetSize = new JSlider(JSlider.HORIZONTAL, Ether.MIN_PACKET_LENGTH,
                Ether.MIN_PACKET_LENGTH + 100, Ether.MIN_PACKET_LENGTH);

        slider.add(sliderLabel);

        packetSize.setMajorTickSpacing(10);
        packetSize.setPreferredSize(new Dimension(280, 40));
        packetSize.setPaintTicks(true);
        packetSize.setPaintLabels(true);
        slider.add(packetSize);

        final JButton startButton = new JButton(START);
        final JButton playPauseButton = new JButton(PAUSE);
        final JButton stepButton = new JButton(STEP);
        final JButton stopButton = new JButton(STOP);
        final JLabel fpsLabel = new JLabel("Animation Speed:");
        final JSlider fpsSlider = new JSlider(JSlider.HORIZONTAL, 10, 80, Runner.getFps());

        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Renderer.start();
                startButton.setEnabled(false);
                playPauseButton.setEnabled(true);
                stopButton.setEnabled(true);
            }
        });
        controls.add(startButton);

        playPauseButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        playPauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (playPauseButton.getText().equals(PAUSE)) {
                    Renderer.pause();
                    playPauseButton.setText(PLAY);
                    stepButton.setEnabled(true);
                } else {
                    Renderer.resume();
                    playPauseButton.setText(PAUSE);
                    stepButton.setEnabled(false);
                }
            }
        });
        playPauseButton.setEnabled(false);
        controls.add(playPauseButton);

        stepButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        stepButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Renderer.step();
            }
        });
        stepButton.setEnabled(false);
        controls.add(stepButton);

        stopButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Renderer.stop();
                startButton.setEnabled(true);
                playPauseButton.setEnabled(false);
                stepButton.setEnabled(false);
            }
        });
        stopButton.setEnabled(false);
//        controls.add(stopButton);

        speed.add(fpsLabel);

        fpsSlider.setMajorTickSpacing(10);
        fpsSlider.setPreferredSize(new Dimension(200, 40));
        fpsSlider.setPaintTicks(true);
        fpsSlider.setPaintLabels(true);
        fpsSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                int fps = fpsSlider.getValue();
                Runner.setFps(fps);
            }
        });
        speed.add(fpsSlider);

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int senderIndex = senderBox.getSelectedIndex();
                int receiverIndex = receiverBox.getSelectedIndex();

                if (senderIndex < 1 || senderIndex >= senderHosts.length) {
                    return;
                }

                if (receiverIndex < 1 || receiverIndex >= receiverHosts.length) {
                    return;
                }

                int packetLength = packetSize.getValue();

                Host sender = senderBox.getItemAt(senderIndex);
                Host receiver = receiverBox.getItemAt(receiverIndex);
                sender.sendPacket(new Packet(packetLength, receiver));
            }
        });
        actions.add(sendButton);

        final JPanel canvas = new JPanel();

        add(actions);
        add(slider);
        add(new JSeparator());
        add(controls);
        add(speed);
        StdDraw.init(canvas, canvasX, canvasY);
        StdDraw.setTitle("Etherview Visualizer");

        setTitle("Etherview Controls");
        setSize(canvasX + 100, canvasY + 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        try {
            // Set System L&F
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
            // handle exception
        } catch (ClassNotFoundException e) {
            // handle exception
        } catch (InstantiationException e) {
            // handle exception
        } catch (IllegalAccessException e) {
            // handle exception
        }

        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                EtherGUI gui = new EtherGUI(null, 400, 100);
                gui.pack();
                gui.setVisible(true);
            }
        });
    }
}
