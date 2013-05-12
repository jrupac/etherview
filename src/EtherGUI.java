import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * The user interface, which allows users to make hosts send messages,
 * and play, pause, or speed up animation.
 */
public class EtherGUI extends JFrame implements ActionListener {

    private Host[] hosts;

    private static final String SEND = "Send";
    private static final String START = "Start";
    private static final String PLAY = "Play";
    private static final String PAUSE = "Pause";
    private static final String STEP = "Step";
    private static final String STOP = "Stop";

    public EtherGUI(Host[] h, int canvasX, int canvasY) {
        hosts = new Host[h.length];
        for (int i = 0; i < hosts.length; i++) {
            hosts[i] = h[i];
        }
        createGUI(canvasX, canvasY);
    }

    private void createGUI(int canvasX, int canvasY) {
        setLayout(new BorderLayout(5, 10));
//        add(Box.createRigidArea(new Dimension(0, 35)));

        // Host actions
        final JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));

        final JComboBox<Host> senderBox = new JComboBox<Host>(hosts);
        senderBox.setSelectedIndex(-1);
        senderBox.setPreferredSize(new Dimension(140, 22));
        senderBox.setMaximumSize(new Dimension(140, 22));
//        senderBox.addItemListener(this);
        actions.add(senderBox);

        final JComboBox<Host> receiverBox = new JComboBox<Host>(hosts);
        receiverBox.setSelectedIndex(-1);
        receiverBox.setPreferredSize(new Dimension(140, 22));
        receiverBox.setMaximumSize(new Dimension(140, 22));
//        receiverBox.addItemListener(this);
        actions.add(receiverBox);

        JButton sendButton = new JButton(SEND);
        sendButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int senderIndex = senderBox.getSelectedIndex();
                int receiverIndex = receiverBox.getSelectedIndex();

                if (senderIndex < 0 || senderIndex >= hosts.length) {
                    return;
                }

                if (receiverIndex < 0 || receiverIndex >= hosts.length) {
                    return;
                }

                Host sender = senderBox.getItemAt(senderIndex);
                Host receiver = receiverBox.getItemAt(receiverIndex);
                sender.sendPacket(new Packet(5, receiver));
            }
        });
        actions.add(sendButton);

        // Animation controls
        final JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));

        final JButton startButton = new JButton(START);
        final JButton playPauseButton = new JButton(PAUSE);
        final JButton stepButton = new JButton(STEP);
        final JButton stopButton = new JButton(STOP);

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
        controls.add(stopButton);

        final JPanel canvas = new JPanel();

        add(actions, BorderLayout.CENTER);
        add(controls, BorderLayout.SOUTH);
        StdDraw.init(canvas, canvasX, canvasY);

        setTitle("Ethernet Viewer");
        setSize(canvasX + 100, canvasY + 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void actionPerformed(ActionEvent e) {
        System.exit(0);
    }

//    public void itemStateChanged(ItemEvent e) {
//
//        if (e.getStateChange() == ItemEvent.SELECTED) {
//            JComboBox combo = (JComboBox) e.getSource();
//            int index = combo.getSelectedIndex();
//            display.setIcon(new ImageIcon(
//                    ClassLoader.getSystemResource(images[index])));
//        }
//
//    }

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
