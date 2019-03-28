package actions;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.GuiUtils;
import com.intellij.ui.components.JBLabel;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class StartDialog extends DialogWrapper {

    private JProgressBar progressBar;
    private JBLabel messageLabel;

    public StartDialog() {
        super(true); // use current window as parent
        init();
        setOKActionEnabled(false);
        setTitle("Test DialogWrapper");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        messageLabel = new JBLabel("Uploading files...");

        return gui.GuiUtils.elementsInBox(Arrays.asList(messageLabel, progressBar),
                BoxLayout.Y_AXIS);
    }

    public void updateProcessBar(double percent){
        if(progressBar != null) {
            progressBar.setValue((int)(100 * percent));
        }
    }

    public void updateMessage(String message) {
        messageLabel.setText(message);
    }
}
