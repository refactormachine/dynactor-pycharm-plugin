package gui;

import logic.RefactoringApplier;
import logic.RefactoringSuggestion;

import javax.swing.*;

public class PanelPresentor {
    private static JPanel createSampleMenuPanel() {
        RefactoringSuggestion r = RefactoringSuggestion.exampleRefactoringSuggestion("someRefactoring");
        RefactoringView demo = new RefactoringView(
                r,
                new SillyDiffsViewer());
        String root = "/tmp";
        final int[] i = {0};
        demo.setOperations(new RefactoringApplier(r, root),
                ()->{
                    System.out.println("next");
                    i[0]++;
                    RefactoringSuggestion newR = RefactoringSuggestion.exampleRefactoringSuggestion(String.valueOf(i[0]));
                    demo.setCurrentRefactoring(newR);
                },
                ()->{System.out.println("previous");},
                ()->{System.out.println("reject");});
        return demo.getPanel();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("JPanel Demo");

            f.add(createSampleMenuPanel());

            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
