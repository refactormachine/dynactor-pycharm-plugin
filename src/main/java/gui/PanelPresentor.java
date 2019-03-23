package gui;

import javax.swing.*;

public class PanelPresentor {


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("JPanel Demo");

//            f.add();

            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
