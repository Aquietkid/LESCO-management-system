import View.LoginScreen;

public class Main {

    public static void main(String[] args) {
        new LoginScreen();

        // TODO: Go back to login screen after exiting menu

        // TODO: This writes to the files immediately after creating the LoginScreen. Fix this so that all files are written just before program termination
//        MasterPersistence.getInstance().writeToFiles();


    }

}