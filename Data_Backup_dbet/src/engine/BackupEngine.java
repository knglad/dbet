package engine;

import filter.CommandErrorFilter;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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
    public JFrame parentWindow;
    public boolean shouldSaveDisregardDrives = false;
    public StringBuilder log;
    // Statistical variables
    public int errorCounter;
    public long hoursItTook;
    public long minutesRemaining;
    public float percentError;
    public double totalBackupSize;
    // Show where the data ended up.
    public String destination;
    private boolean DEBUG = false;
    private DriveUtils du;
    private DataDestinationEngine dde;
    private int totalLineCounter;
    private double preBackupFreeSpace;
    private double postBackupFreeSpace;
    private LocalTime preBackupTime;

    /**
     * @param window - Allows sending of signals to the window and its graphical components
     */

    public BackupEngine(JFrame window, boolean... debugMode) { // THIS IS THE BIG KAHUNA! The whole algo is this constructor.
        parentWindow = window;

        // set debug if given, allows debug behavior post jar creation
        if (debugMode.length != 0)
            DEBUG = debugMode[0];

        du = new DriveUtils();
        dde = new DataDestinationEngine();

        // OS determines where to look for newly mounted drives we may want to backup
        OS = du.getOS();

        // Drives we KNOW we don't want to backup, as users use the program it will learn what it shouldn't look for.
        dd = new DisregardDrives();

        // Load in the list of drives to ignore, if it's not found it uses the default.
        dd.loadList();

        if (DEBUG)
            System.out.println("Debugging has been enabled, no commands will be run!");
        // -----------------------------------------------

        // Find the drives to backup
        ArrayList<File> drivesWeMayWantToBackup = findDrivesToBackup(dd);


        // Ask which ones to backup, make a list
        // Options: Yes, No, Don't Ask For This Drive Ever Again
        // The list that the user wants us to backup, back it up.
        backupData(askWhichDrivesToBackup(drivesWeMayWantToBackup));

        // If something was added to dd, save the list now.
        if (shouldSaveDisregardDrives) {
            dd.saveList();
            shouldSaveDisregardDrives = false; // WATCH This flag might not be needed, remove for performance once tested.
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
     *
     * FILE SYSTEM SENSITIVE METHOD (Does NOT support Linux at this time)
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

        Object[] options = {"Backup this drive",
                "No",
                "Never backup this drive (permanent)",
        };

        int counter = 1;

        ArrayList<Drive> drivesToBackup = new ArrayList<Drive>();
        String fileName = "";

        if (listToAsk.isEmpty()) {
            JOptionPane.showMessageDialog(parentWindow, "No drives were found that were able to be backed up.");
        } else {
            for (File file : listToAsk) {
                if (du.getOS().contains("Mac"))
                    fileName = file.getName();
                else if (du.getOS().contains("Windows"))
                    fileName = file.getPath(); // Name could be "" or some random string, path for windows gave the proper name.

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
                    drivesToBackup.add(du.mountPointToDrive(file));
                if (n == 2) { // Never backup this drive!
                    dd.addWithoutDuplicates(file.getName());
                    shouldSaveDisregardDrives = true; // Tells the program to save the new DD as something was added
                }

                // Update counter for the overall count, user friendly GUI element.
                counter++;
            }
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
            preBackupTime = LocalTime.now();

            totalLineCounter = 1;
            errorCounter = 0;

            // Determine which OS it is and call that backup method
            if (OS.contains("Mac")) {
                macBackup(drive);
            } else if (OS.contains("Windows")) {
                windowsBackup(drive);
            }


        }
    }


    public void runCommand(String[] command) {

        ProcessBuilder processBuilder = new ProcessBuilder(command);

        // Redirect the error stream to also the inputStream so we see all output text from the command
        processBuilder.redirectErrorStream(true);

        Process process;
        try {
            process = processBuilder.start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));



            // Statistics to help determine if the backup worked or not.
            CommandErrorFilter commandFilter = new CommandErrorFilter();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                totalLineCounter++;

                if (!commandFilter.filterSelection(line)) {
                    errorCounter++;
                    // TODO :: Send error line to LOG.
                }
            }


            process.waitFor();


        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to start Process in backupData");
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            System.out.println("Interrupted During Out/In/Error stream reading");
        }


    }


    public void showCopyStatistics(Drive justForStatsDrive) {
        StringBuilder stringBuilder = new StringBuilder();
        // Need to check the backup with a new drive, the previous one is static and wont change dynamically.
        Drive updatePostBackupDrive = du.updateDriveInformation(justForStatsDrive);
        postBackupFreeSpace = updatePostBackupDrive.getCapacity("free");
        totalBackupSize = DriveUtils.round(preBackupFreeSpace - postBackupFreeSpace, 4);
        percentError = DriveUtils.round((errorCounter / totalLineCounter), 2);

        // Find the time difference
        LocalTime postBackupTime = LocalTime.now();
        long minutes = ChronoUnit.MINUTES.between(preBackupTime, postBackupTime);
        hoursItTook = minutes / 60;
        minutesRemaining = minutes % 60;
        String time = hoursItTook + "h - " + minutesRemaining + "m";

        // Create the actual text to show the user
        stringBuilder.append("\n");
        stringBuilder.append("COPY-ITEM STATISTICS =================================================== \n");
        stringBuilder.append("Data backed up to: " + destination + "\n");
        stringBuilder.append("Total time elapsed: " + time + "\n");
        stringBuilder.append("Total Errors: " + errorCounter + "\nTotal Files Transferred: " + (totalLineCounter - 1));
        stringBuilder.append("\nPercent Error: " + percentError + "%");
        stringBuilder.append("\nTotal Backup Size: " + totalBackupSize + "GB");

        // Do something with the string, like save it to a text file or something.
        System.out.println(stringBuilder.toString());

    }




    /**
     * @return a string array that consists of the complete command for Macintosh systems (Mac OS X)
     */
    public String[] macBackup(Drive drive) {
        ArrayList<String> backupCommand = new ArrayList<String>();

        // Initial command and its flags in UNIX style command line
        backupCommand.add("rsync");
        backupCommand.add("-a");
        backupCommand.add("-v");
        backupCommand.add("--progress");

        // Add to the list all the files (filtered) and their full paths to the command
        backupCommand.addAll(du.getFullPathForFiles(drive));


        // Get exact path to the destination folder, find the best storage option and use that.
        // This drive object will help us know about the total amount backed up,
        Drive currentHighestStorageDrive = du.getHighestStorageDrive(dde.getDriveList());

        if (!du.getSystemHasSufficientStorage(drive, currentHighestStorageDrive, parentWindow))
            return null; // They didn't want to continue, we can stop here and keep backing up.

        preBackupFreeSpace = currentHighestStorageDrive.getCapacity("free");

        // Create the destination folder
        String mac_filtering = "Mac";

        // mkdir[1] is the actual destination
        String[] mkdir = du.askUserForMkdir(drive, parentWindow, currentHighestStorageDrive, DEBUG, mac_filtering);
        destination = mkdir[1];

        // Add the Destination folder to the command
        backupCommand.add(mkdir[1]);

        String[] finalCommand = backupCommand.toArray(new String[backupCommand.size()]);


        if (DEBUG) {
            for (int i = 0; i < finalCommand.length; i++) {
                String s = finalCommand[i];
                System.out.println(s);
            }
        } else {
            // Now actually back it up.
            System.out.println("Running Mac Backup");
            runCommand(finalCommand);
        }

        // After the command has run show us what happened.
        showCopyStatistics(currentHighestStorageDrive);

        return finalCommand;

    }


    public String[] windowsBackup(Drive drive) {


        // Thanks Powershell..for not doing multiple files in one command

        // Ask the user what they want to name the folder, create it and return the mkdir complete command in string form
        String windows_filtering = "Windows";
        // Pull this drive out so we can see if we even have enough room first.
        Drive highestStorage = du.getHighestStorageDrive(dde.getDriveList());


        if (!du.getSystemHasSufficientStorage(drive, highestStorage, parentWindow))
            return null; // They didn't want to continue, we can stop here and keep backing up other drives.

        String[] mkdir = du.askUserForMkdir(drive, parentWindow, highestStorage, DEBUG, windows_filtering);
        destination = mkdir[1];

        // Get all the files/folders that we want in a full paths list for powershell
        ArrayList<String> fullPathFilesList = du.getFullPathForFiles(drive);


        // We've made the directory, now we need to do the backup.
        for (String fileToBackup : fullPathFilesList) { // Loops through ALL available files / folders for each command.

            // Create the command
            ArrayList<String> windowsCommand = new ArrayList<String>();
            windowsCommand.add("powershell.exe");
            windowsCommand.add("/C");
            windowsCommand.add("cp");
            windowsCommand.add(fileToBackup); // total path to the file / folder
            windowsCommand.add(mkdir[1]); // total path to the destination
            windowsCommand.add("-recurse");
            windowsCommand.add("-verbose");

            String[] finalWindowsCommand = windowsCommand.toArray(new String[windowsCommand.size()]);


            if (DEBUG) {
                for (int i = 0; i < finalWindowsCommand.length; i++) {
                    String s = finalWindowsCommand[i];
                    System.out.println(s);
                }
            } else // Actually do something.
                runCommand(finalWindowsCommand);

        }


        showCopyStatistics(drive);
        return null; // return is pointless, except that it allows us to stop the method earlier.
    }


} // END OF BACKUP ENGINE