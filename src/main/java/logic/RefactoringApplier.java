package logic;

import util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RefactoringApplier implements MyAction{
    private final RefactoringSuggestion refactoring;
    private final String projectRoot;
    List<String> modifeidFilenames = new ArrayList<>();

    public RefactoringApplier(RefactoringSuggestion refactoring, String projectRoot){
        this.refactoring = refactoring;
        this.projectRoot = projectRoot;
    }

    void applyWithoutPriorValidation() throws InvalidRefactoringSuggestion, IOException {
        modifeidFilenames.clear();
        for(String relativePath: Utils.sorted(refactoring.filesDiff().keySet())){
            String path = Paths.get(projectRoot, relativePath).toString();
            System.out.println("path = " + path);
            String currentContent = null;
            try {
                currentContent = Utils.readFileContent(path);
            } catch (IOException e) {
                e.printStackTrace();
                throw new InvalidRefactoringSuggestion();
            }
            FileDiff diff = refactoring.filesDiff().get(relativePath);
            if(!diff.isDiffValid(currentContent)){
                throw new InvalidRefactoringSuggestion();
            }
            try {
                Utils.writeFile(path, diff.newContent());
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
                throw new InvalidRefactoringSuggestion();
            }
            modifeidFilenames.add(relativePath);
        }
    }

    public void apply() throws InvalidRefactoringSuggestion, IOException {
        System.out.println("apply " + refactoring);
        modifeidFilenames.clear();
        if(!refactoring.isRefactoringValid(projectRoot)){
            throw new InvalidRefactoringSuggestion();
        }
        applyWithoutPriorValidation();
    }

    public void undoApply(){
        for(String modifiedRelative: modifeidFilenames){
            FileDiff diff = refactoring.filesDiff().get(modifiedRelative);
            String path = Paths.get(projectRoot, modifiedRelative).toString();
            if(diff.oldContent().isEmpty()){
                if(new File(path).exists()){
                    new File(path).delete();
                }
            }else{
                try {
                    Utils.writeFile(path, diff.oldContent());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run() {
        try {
            apply();
        } catch (InvalidRefactoringSuggestion invalidRefactoringSuggestion) {
            invalidRefactoringSuggestion.printStackTrace();
            System.out.println("Should opurpose the user to abort here");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
