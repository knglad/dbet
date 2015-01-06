package engine;

import javax.swing.*;
import java.io.File;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
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
    public boolean shouldSaveDisregardDrives = false;

    /**
     * @param pe     - Allows data transfer with the central object, and access to useful methods
     * @param window - Allows sending of signals to the window and its graphical components
     */

    public BackupEngine(PrimaryEngine pe, JFrame window) { // THIS IS THE BIG KAHUNA! The whole algo is this constructor.

        primaryEngine = pe;
        parentWindow = window;

        // OS determines where to look for newly mounted drives we may want to backup
        OS = primaryEngine.getOS();

        // Drives we KNOW we don't want to backup, as users use the program it will learn what it shouldn't look for.
        dd = new DisregardDrives();

        // Load in the list of drives to ignore, if it's not found it uses the default.
        dd.loadList();

        // -----------------------------------------------
        // Find the drives to backup
        ArrayList<File> drivesWeMayWantToBackup;
        drivesWeMayWantToBackup = findDrivesToBackup(dd);



        // Ask which ones to backup, make a list
        // Options: Yes, No, Don't Ask For This Drive Ever Again
        askWhichDrivesToBackup(drivesWeMayWantToBackup);

        // If something was added to dd, save the list now.
        if (shouldSaveDisregardDrives) {
            dd.saveList();
            shouldSaveDisregardDrives = false; // TODO This flag might not be needed, remove for performance once tested.
        }
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

        if (OS.contains("Mac")) {
            f = new File("/Volumes/");
            // Make a TOTAL list of ALL drives on the computer
            currentDrives = new ArrayList<File>(Arrays.asList(f.listFiles()));

            // Remove the ones we KNOW we don't want to backup
            for (int i = 0; i < currentDrives.size(); i++) {

                if (dd.shouldBeBackedup(currentDrives.get(i))) {
                    temp.add(currentDrives.get(i));
                }
            }
        }

        else if (OS.contains("Windows")) {
            //Use the Filestore object from FileSystems in java.nio to get ALL possible mounted volumes
            for (FileStore fileStore : FileSystems.getDefault().getFileStores()) {
                File current = new File(String.valueOf(fileStore));

                // Filter out the ones we DONT care about
                if (dd.shouldBeBackedup(current)) {
                    temp.add(current);
                }
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

        int counter = 1;

        ArrayList<Drive> drivesToBackup = new ArrayList<Drive>();
        String fileName = "";

        if (listToAsk.isEmpty()) {
            JOptionPane.showMessageDialog(parentWindow, "No Drives were found that were able to be backed up.");
        }
        for (File file : listToAsk) {
            if (primaryEngine.getOS().contains("Mac"))
                fileName = file.getName();
            else if (primaryEngine.getOS().contains("Windows"))
                fileName = file.getPath();

            int n = JOptionPane.showOptionDialog(parentWindow,
                    "Would you like to backup files from " + fileName + "?  (" + counter + " / " + listToAsk.size() + ")",
                    "DBET - Drive Selection",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[1]);

            // n is the index of the answer -1 is returned if they exit out of it
            // JOptionPane logic tree that handles ALL required responses given by the user.

            if (n == -1) // Early Cancellation of program (avoids situations where theres HUGE amounts of drives
                System.exit(0);
            if (n == 0) // BACKUP THE DRIVE
                drivesToBackup.add(primaryEngine.mountPointToDrive(file));
            if (n == 2) { // Never backup this drive!
                dd.addWithoutDuplicates(file.getName());
                shouldSaveDisregardDrives = true; // Tells the program to save the new DD as something was added
            }

            // Update counter for the overall count, user friendly GUI element.
            counter++;
        }

        return drivesToBackup;

    }


    /**
     * Using the Drive object we've created we tell the system to transfer specific folders it finds. It does this by
     * first generating the String[] that contains the backup command, using 'cd' to get to the drives mount point location,
     * then backing up the files it finds that aren't system files or folders using the 'cp' command with the recurse flag.
     *
     * @param backupThisList - List of 1 or more Drive objects that are to be backed up.
     * @param mode - Optional string to thread each backup individually or not. 'Threaded' will create a
     *             new thread for each drive it backs up and then closes the thread upon completion of that task.
     */
    public void backupData(ArrayList<Drive> backupThisList, String[]... mode) {

        for (Drive drive : backupThisList) {
            // The process runtine uses a string array to handle creating a single command
            // I.E. String[]{"cp", "-Rv", "pathToBackup", "destination"};

            // Create the String[] for the backup based on the OS of this drive
            String[] backupCommand = new String[25]; // TODO Ensure the size does not present an issue.
            backupCommand[0] = "cp";
            int startAddingFilesIntoCommand = 1;

            if (drive.getFileSystem().contains("Mac")) {
                backupCommand[1] = "-Rv";
                startAddingFilesIntoCommand = 2;
            }
            // Find all the folders we want to backup from the drive


            ArrayList<String> childrenToBackup = findImportantFiles(File.listRoots());

            for (String s : childrenToBackup) {
                // Dynamic array index insertion to handle the case if its a Mac system or not
                // Also takes the arraylist and turns it into the array format we need for runtime.exec()
                backupCommand[startAddingFilesIntoCommand] = s;
                startAddingFilesIntoCommand++;
            }

            // Create the destination folder TODO Use JOptionPane to ask the user for input and mkdir
            // Add the Destination folder to the command

            // Powershell uses '-recurse' after the command to handle folders

            if (drive.getFileSystem().contains("Windows"))
                backupCommand[startAddingFilesIntoCommand] = "-recurse";



        }

    }


    public ArrayList<String> findImportantFiles(File[] files) {
        // Look for anything that isn't a system file
        ArrayList<String> filesWeWantSaved = new ArrayList<String>();
        boolean addToList = true;
        // Contains both Mac and PC files in the root folders of most drives that
        // are system and we don't need to save.
        String[] foldersAndFilesWeDontWant = new String[]{
                "Library",
                "System",
                "Volumes",
                "Yose Life Image",
                "Incompatible Software",
        };

        for (File f : files) {
            for (String s : foldersAndFilesWeDontWant) {
                if (f.getName().toLowerCase().contains(s.toLowerCase())) {
                    addToList = false;
                    break; // it wont be anything else, why keep looping?
                }
            }

            if (addToList)
                filesWeWantSaved.add(f.getName());

            addToList = true; // reset flag just in case.
        }

        return filesWeWantSaved;
    }
} // END OF BACKUP ENGINE
