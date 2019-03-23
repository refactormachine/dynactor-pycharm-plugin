package actions;

import com.intellij.openapi.ui.DialogWrapper;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;

public class StartDialog extends DialogWrapper {

    private JProgressBar progressBar;

    public StartDialog() {
        super(true); // use current window as parent
        init();
        setTitle("Test DialogWrapper");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        JLabel label = new JLabel("testing");
        label.setPreferredSize(new Dimension(100, 100));
        dialogPanel.add(label, BorderLayout.CENTER);
        dialogPanel.add(progressBar);
        return dialogPanel;
    }

    public void updateProcessBar(double percent){
        if(progressBar != null) {
            progressBar.setValue((int)(100 * percent));
        }
    }
}
