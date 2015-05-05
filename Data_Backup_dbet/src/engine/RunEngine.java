package engine;

import gui.LogWindow;
import gui.MainWindow;

/**
 * Created on : 5/4/15 for engine
 * by Kevin Gladhart
 * <p/>
 * This class creates all the necessary objects and controls them from here. This class uses the
 * singleton OOD principal so there cannot be more than one made.
 */
public class RunEngine {

    public RunEngine() {
        LogWindow logWindow = new LogWindow();
        MainWindow mainWindow = new MainWindow(logWindow);

        BackupEngine backupEngine = new BackupEngine(mainWindow);
        backupEngine.startEngine();


    }
}
