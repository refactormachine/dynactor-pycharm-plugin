package gui;

import com.intellij.openapi.ui.ComboBox;
import util.Utils;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GuiUtils {
    // direction is one of BoxLayout.X_AXIS, BoxLayout.Y_AXIS BoxLayout.LINE_AXIS, BoxLayout.PAGE_AXIS
    public static JPanel spacedElementsInBox(Iterable<Component> elements, int direction, Dimension margin){
        JPanel panel = new JPanel();
        setSpacedBoxLayout(elements, direction, margin, panel);
        return panel;
    }

    public static void setSpacedBoxLayout(Iterable<Component> elements, int direction, Dimension margin, JPanel panel) {
        panel.removeAll();
        BoxLayout layout = new BoxLayout(panel, direction);
        panel.setLayout(layout);
        for(Component component: elements) {
            panel.add(component);
            if(margin.width > 0 || margin.height > 0) {
                panel.add(Box.createRigidArea(margin));
            }
        }
    }

    public static void setBoxLayout(Iterable<Component> elements, int direction, JPanel panel) {
        setSpacedBoxLayout(elements, direction, new Dimension(0, 0), panel);
    }

    public static JPanel elementsInBox(Iterable<Component> elements, int direction) {
        return spacedElementsInBox(elements, direction, new Dimension(0, 0));
    }

    public static JComponent createCardsPanel(java.util.List<JComponent> components) {
        CardLayout layout = new CardLayout();
        JPanel cards = new JPanel(layout);
        List<String> names = components.stream().map(Component::getName).collect(Collectors.toList());
        components.forEach(view->cards.add(view, view.getName()));

        ComboBox<String> cb = new ComboBox<>(Utils.toArrayNoPolymorphism(names));
        cb.addItemListener(e -> layout.show(cards, (String)e.getItem()));

        return elementsInBox(Arrays.asList(cb, cards), BoxLayout.Y_AXIS);
    }

    public static void setDefaultButton(JButton button) {
        JRootPane rootPane = SwingUtilities.getRootPane(button);
        if(rootPane == null){
            System.err.println("button must be added to pane that was added to a frame " +
                    "before setting default");
        }else {
            System.out.println("setting default !");
            rootPane.setDefaultButton(button);
        }
    }
}
