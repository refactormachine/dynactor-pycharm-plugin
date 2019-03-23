package gui;

import logic.FileDiff;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SillyDiffsViewer implements DiffViewCreator{
    @Override
    public List<JComponent> createDiffViews(Map<String, FileDiff> filesDiff) {
        List<JComponent> l = new ArrayList<>();
        for(String name: filesDiff.keySet()){
            FileDiff diff = filesDiff.get(name);
            JLabel label = new JLabel(diff.oldContent() + "\n\tvs \n" + diff.newContent());
            label.setName(name);
            l.add(label);
        }
        return l;
    }
}
