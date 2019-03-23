package actions;


public class RefactoringState {
    public boolean isValidateEnalbed() {
        return currentState == State.Ongoing;
    }

    public boolean isStartEnabled() {
        return currentState == State.Init;
    }

    public boolean isRestartEnabled() {
        return currentState == State.Ongoing;
    }

    public enum State{Init, Ongoing};
    private State currentState = State.Init;

    private static RefactoringState INSTANCE;

    private RefactoringState() {
    }

    public static RefactoringState getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new RefactoringState();
        }
        return INSTANCE;
    }

    public void setInit(){
        currentState = State.Init;
    }

    public void setOngoing(){
        currentState = State.Ongoing;
    }

}
