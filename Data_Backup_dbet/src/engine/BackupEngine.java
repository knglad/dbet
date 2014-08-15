package engine;

import java.io.File;
import java.util.ArrayList;

/**
 * @author Kevin Gladhart
 *         <p/>
 *         This engine determines the logic on how to backup the given drive in the most effective way
 */
public class BackupEngine {

    public String OS;

    public BackupEngine(String givenOperatingSystem) {

        OS = givenOperatingSystem;

        // Load in the list of drives to ignore
        DisregardDrives dd = new DisregardDrives();
        dd = dd.loadList(); // Primary engine adds the list prior to this

        // -----------------------------------------------
        // Find the drives to backup
        findDrivesToBackup(dd);

        // Determine best method to back them up
        // Back them up
        // Log everything to text file and GUI element

    }

    /**
     * @param driveList - all the drives we DONT backup, in the form of ArrayList<String>
     * @return A new arrayList with the drives we DO want to backup
     * <p/>
     * This will look through the systems entire mount-points and find ones that we may care
     * about and return the list.
     */
    public ArrayList<Drive> findDrivesToBackup(DisregardDrives driveList) {

        File[] currentDrives;

        if (OS.contains("Mac")) {
            File f = new File("/Volumes");
            currentDrives = f.listFiles();
        }


        return null;
    }


}
