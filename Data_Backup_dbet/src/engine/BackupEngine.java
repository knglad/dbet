package engine;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Kevin Gladhart
 *
 *         This engine determines the logic on how to backup the given drive in the most effective way
 */
public class BackupEngine {

    public String OS;
    public DisregardDrives dd;
    public ArrayList<Drive> drivesToBackup;

    public BackupEngine(String givenOperatingSystem) {

        OS = givenOperatingSystem;

        // Load in the list of drives to ignore
        dd = dd.loadList(); // Primary engine adds the list prior to this

        // -----------------------------------------------
        // Find the drives to backup
        findDrivesToBackup(dd);

        // Ask which ones to backup, make a list
        // Options: Yes, No, Don't Ask Again
        // Determine best method to back them up
        // Back them up
        // Log everything to text file and GUI element

    }

    /**
     * @param driveList - all the drives we DONT backup, in the form of ArrayList<String>
     * @return A new arrayList with the drives we DO want to backup
     *
     * This will look through the systems entire mount-points and find ones that we may care
     * about and return the list.
     */
    public ArrayList<File> findDrivesToBackup(DisregardDrives driveList) {

        ArrayList<File> currentDrives = null;
        File f = null;

        if (OS.contains("Mac"))
            f = new File("/Volumes/");

        else if (OS.contains("Windows"))
            f = new File("Computer");

        currentDrives = new ArrayList<File>(Arrays.asList(f.listFiles()));

        for (File file : currentDrives) {
            if (dd.contains(file.getName())) {
                currentDrives.remove(file);
            }
        }

        return currentDrives;
    }


} // END OF BACKUP ENGINE
