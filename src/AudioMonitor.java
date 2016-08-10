import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class AudioMonitor extends JFrame {

    public AudioMonitor(String name) {
        super(name);
        setResizable(false);
    }

    public static void main(String args[]) throws Exception {

        AudioMonitor f = new AudioMonitor("Audio Monitor");

        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        //Set up the content pane.
        f.addComponentsToPane(f.getContentPane());
        //Display the window.
        f.pack();
        f.setVisible(true);
    }

    public void addComponentsToPane(final Container pane) {
        final JPanel jp = new JPanel();


        //Set up components preferred size
        jp.add(new ServerUI(9990));
        //jp.add(new ServerUI(8293));

        jp.setPreferredSize(new Dimension(200, 120));

        pane.add(jp, BorderLayout.NORTH);
        pane.add(new JSeparator(), BorderLayout.SOUTH);
    }
}