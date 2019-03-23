package gui;

import logic.FileDiff;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public interface DiffViewCreator {
    List<JComponent> createDiffViews(Map<String, FileDiff> filesDiff);
}
