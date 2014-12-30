package engine;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Kevin Gladhart
 *
 * This is the class that handles all the data backup logic. Figures out which drives need to be
 * backed up, asks the user which drives they want us to backup and allows certain drives to not be backed up.
 *
 *
 */
public class BackupEngine {

    public String OS;
    public DisregardDrives dd;
    public PrimaryEngine primaryEngine;
    public JFrame parentWindow;

    /**
     * @param pe     - Allows data transfer with the central object, and access to useful methods
     * @param window - Allows sending of signals to the window and its graphical components
     */

    public BackupEngine(PrimaryEngine pe, JFrame window) {

        primaryEngine = pe;
        parentWindow = window;

        // OS determines where to look for newly mounted drives we may want to backup
        OS = primaryEngine.getOS();

        // Drives we KNOW we don't want to backup, as users use the program it will learn what it shouldn't look for.
        dd = new DisregardDrives();

        // Load in the list of drives to ignore
        dd.loadList();

        // -----------------------------------------------
        // Find the drives to backup
        ArrayList<File> drivesWeMayWantToBackup;
        drivesWeMayWantToBackup = findDrivesToBackup(dd);
        // TODO : Test on Windows machine to see if code breaks

        // Tests the drives AFTER the filtering.
        for (File f : drivesWeMayWantToBackup) {
            System.out.println(f.getName());
        }

        // Ask which ones to backup, make a list
        // Options: Yes, No, Don't Ask For This Drive Ever Again
        // askWhichDrivesToBackup(drivesWeMayWantToBackup);


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

        ArrayList<File> currentDrives;
        ArrayList<File> temp = new ArrayList<File>();
        File f = null;

        if (OS.contains("Mac"))
            f = new File("/Volumes/");

        else if (OS.contains("Windows"))
            f = new File("/");
        // TODO : Find Windows path that contains all mounted volumes


        // Make a TOTAL list of ALL drives on the computer
        currentDrives = new ArrayList<File>(Arrays.asList(f.listFiles()));


        // Remove the ones we KNOW we don't want to backup
        for (int i = 0; i < currentDrives.size(); i++) {

            if (dd.shouldBeBackedup(currentDrives.get(i))) {
                temp.add(currentDrives.get(i));
            }
        }


        return temp;
    }


    /**
     *
     * @param listToAsk - List of all drives minus the ones that we know we don't want to backup, handled by
     *               findDrivesToBackup
     * @return - An ArrayList<Drive> that gets created using primary engine's method to create the drive list
     *               this is to make it seem cleaner and easier as expansion happens.
     */
    public ArrayList<Drive> askWhichDrivesToBackup(ArrayList<File> listToAsk) {

        Object[] options = {"Use as backup drive",
                "No",
                "Never use this drive to backup",
        };

        ArrayList<Drive> drivesToBackup = new ArrayList<Drive>();

        for (File file : listToAsk) {

            int n = JOptionPane.showOptionDialog(parentWindow,
                    "Would you like to backup files from " + file.getName() + "?",
                    "DBET - Drive Selection",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[1]);

        }

        return drivesToBackup; // TODO: Test how the JOptionPane works and then test to make sure its behaving.

    }


    /**
     * Grabs the string that represents the drive name and backs it up.
     *
     * @param backupThisList - List of 1 or more Drive objects that are to be backed up.
     */
    public void backupData(ArrayList<File> backupThisList) {

    }
} // END OF BACKUP ENGINE
