package gui;


import com.intellij.icons.AllIcons;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.labels.LinkLabel;
import logic.MyAction;
import logic.RefactoringSuggestion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

/**
 * This program demonstrates how to use JPanel in Swing.
 * @author www.codejava.net
 */
public class RefactoringView {
    public static final Dimension PREFERRED_SIZE = new Dimension(800, 600);
    public static final int BUTTONS_MARGIN = 20;
    private static final int SCORE_MARGIN = 15;
    private RefactoringSuggestion refactoring;
    private final DiffViewCreator creator;
    private JComponent diffComponent = null;
    private LinkLabel<Integer> seeDrawingLink = new LinkLabel<>("graphic", null);
    private JButton buttonRefactor = new JButton(Strings.ACCEPT_REFACTOR);
    private JButton buttonNext = new JButton(Strings.NEXT,
            AllIcons.Actions.Forward);
    private JButton buttonPrevious = new JButton(Strings.PREVIOUS,
            AllIcons.Actions.Back);
    private JButton buttonReject = new JButton(Strings.REJECT);

    private JBLabel scoreLabel = new JBLabel();
    private JBLabel oneLineDescription = new JBLabel();
    private JBTextArea description = new JBTextArea(5, 20);
    private JPanel panel;

    public RefactoringView(DiffViewCreator creator){
        this.creator = creator;
        panel = new JPanel(){
            @Override
            public void invalidate() {
                super.invalidate();
                GuiUtils.setDefaultButton(buttonRefactor);
            }
        };

        buttonRefactor.setMnemonic(KeyEvent.VK_R);
        buttonPrevious.setMnemonic(KeyEvent.VK_P);
        buttonNext.setMnemonic(KeyEvent.VK_N);
        buttonReject.setMnemonic(KeyEvent.VK_J);
    }

    public RefactoringView(RefactoringSuggestion refactoring,
                           DiffViewCreator creator){
        this(creator);
        setCurrentRefactoring(refactoring);
    }

    public void setCurrentRefactoring(RefactoringSuggestion refactoring){
        this.refactoring = refactoring;
        scoreLabel.setText(String.format("score: %.1f", refactoring.score()));
        oneLineDescription.setText(refactoring.oneLineDescription());
        description.setText(refactoring.fullDescription());
        description.setEditable(false);
        diffComponent = createDiffComponent();
        setupLayout();
    }

    public JPanel getPanel(){
        return panel;
    }



    private void setupLayout() {
        assert panel != null;
        JPanel buttonsBox = GuiUtils.spacedElementsInBox(Arrays.asList(
                buttonRefactor, buttonPrevious, buttonNext, buttonReject),
                BoxLayout.X_AXIS, new Dimension(BUTTONS_MARGIN, 0));
        diffComponent.setBorder(BorderFactory.createTitledBorder(Strings.CODE_DIFF));
//        GuiUtils.setDefaultButton(buttonRefactor);

        JPanel top = GuiUtils.spacedElementsInBox(Arrays.asList(
                scoreLabel, oneLineDescription, Box.createHorizontalGlue()),
                BoxLayout.X_AXIS, new Dimension(SCORE_MARGIN, 0));
        GuiUtils.setBoxLayout(Arrays.asList(
                top, description, seeDrawingLink, buttonsBox,
                diffComponent),
                BoxLayout.Y_AXIS, panel);
        panel.setPreferredSize(PREFERRED_SIZE);
    }

    private JComponent createDiffComponent() {
        List<JComponent> diffViews = creator.createDiffViews(refactoring.filesDiff());
        if(diffViews.size() == 0){
            return new JLabel("No diff to preview at this time");
        }
        if(diffViews.size() == 1){
            return diffViews.get(0);
        }

        return GuiUtils.createCardsPanel(diffViews);
    }

    public void setOperations(MyAction apply,
                              MyAction next,
                              MyAction previous,
                              MyAction reject) {
        buttonRefactor.addActionListener(e -> apply.run());
        buttonPrevious.addActionListener(e->previous.run());
        buttonNext.addActionListener(e-> next.run());
        buttonReject.addActionListener(e->reject.run());
    }

    public void setNextEnabled(Boolean enabled) {
        buttonPrevious.setEnabled(enabled);
    }

    public void setPrevEnabled(Boolean enabled) {
        buttonNext.setEnabled(enabled);
    }
}
