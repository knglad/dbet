package engine;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
    public JFrame parentWindow;
    public boolean shouldSaveDisregardDrives = false;
    public StringBuilder log;
    private DriveUtils du;
    private PrimaryEngine pe;

    /**
     * @param window - Allows sending of signals to the window and its graphical components
     */

    public BackupEngine(JFrame window) { // THIS IS THE BIG KAHUNA! The whole algo is this constructor.
        parentWindow = window;
        du = new DriveUtils();
        pe = new PrimaryEngine();

        // OS determines where to look for newly mounted drives we may want to backup
        OS = du.getOS();

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

        Object[] options = {"Use as backup drive",
                "No",
                "Never use this drive to backup",
        };

        int counter = 1;

        ArrayList<Drive> drivesToBackup = new ArrayList<Drive>();
        String fileName = "";

        if (listToAsk.isEmpty()) {
            JOptionPane.showMessageDialog(parentWindow, "No Drives were found that were able to be backed up.");
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
        // TODO :: Test this with a drive that simulates a drive containing an OS and has a Users folder + Applications Folder

        for (Drive drive : backupThisList) {
            // The process runtime uses a string array to handle creating a single command
            // I.E. String[]{"cp", "-Rv", "pathToBackup", "destination"};

            // Use ArrayList to avoid extra spaces and keep array size to a minimum
            ArrayList<String> backupCommand = new ArrayList<String>();
            backupCommand.add("cp");

            if (du.getOS().contains("Mac")) {
                backupCommand.add("-Rv");
            }

            ArrayList<String> fullPathFilesList = new ArrayList<String>();
            // Find all the folders we want to backup from the drive
            for (String fileName : findImportantFiles(drive.getFile().listFiles())) {
                String fullPathFile = drive.getFile().getPath() + "/" + fileName;
                fullPathFilesList.add(fullPathFile);
            }

            backupCommand.addAll(fullPathFilesList);


            // Create the destination folder
            String users = du.getUsers(drive);


            String mkdir = JOptionPane.showInputDialog(parentWindow, "Enter the customers Service Invoice Number( i.e 13021)\n\n" +
                            "Potential users:\n" + users,
                    "Make Directory", JOptionPane.QUESTION_MESSAGE);

            if (mkdir == null) {
                boolean response = du.askUserYesNo("No input was detected for the directory, do you wish to proceed?", parentWindow);

                if (!response)
                    break; // Don't backup this drive, move on.
                if (response) {
                    mkdir = JOptionPane.showInputDialog(parentWindow, "Enter the customers Service Invoice Number( i.e 13021)\n\n" +
                                    "Potential users:\n" + users,
                            "Make Directory", JOptionPane.QUESTION_MESSAGE);
                }

                // Asked again, they obviously WANT to backup this drive just didn't input a name or closed the box.
                // Best thing to do is back it up for them, and just have them rename the folder by hand later.
                if (mkdir == null)
                    mkdir = "RENAME THIS FOLDER!_" + drive.toString();
                    /*
                    Without this DBET will replace the last made RENAME THIS FOLDER! with the
                    current one, losing data. The drive.toString();
                    */

            } // End of mkdir == null (the first time)


            // WATCH Mac made the folder 13021\ Kevin\ Tester in the actual folder
            // replace spaces in the mkdir with "\ "
            //mkdir = mkdir.replace(" ", "\\ ");


            // Get exact path to the destination folder, find the best storage option and use that.
            mkdir = du.getHighestStorageDrive(pe.getDriveList()).getMountPoint() + "/" + mkdir + "/";

            // BUG WORKAROUND Storage3 would lose its '/' after its name.
            mkdir = mkdir.replace("//", "/");


            // Add the Destination folder to the command
            backupCommand.add(mkdir);


            // Powershell uses '-recurse' after the command to handle folders and -verbose to get the data
            if (du.getOS().contains("Windows")) {
                backupCommand.add("-recurse");
                backupCommand.add("-verbose");
            }


            // Make the directory actually exist so we can back up to it
            String[] makeDirectoryCommand = new String[]{"mkdir", mkdir};
            try {
                Process p = Runtime.getRuntime().exec(makeDirectoryCommand);
            } catch (IOException ioe) {
                ioe.printStackTrace();
                System.out.println("Could not reach destination folder to create directory.");
            }


            // the command has been built lets make it into an array
            String[] finalCommand = backupCommand.toArray(new String[backupCommand.size()]);

            for (String s : finalCommand) {
                System.out.println(s);
            }
            System.out.println("\n");


            // Now actually back it up.
            // Pump the output to the GUI, which the GUI will save the output into a log file for later examination.

            // Takes a List<String> in the constructor or just a string
            ProcessBuilder processBuilder = new ProcessBuilder(finalCommand);
            // Redirect the error stream to also the inputStream so we see all output text from the command
            processBuilder.redirectErrorStream(true);

            Process process;
            try {
                process = processBuilder.start();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                StringBuilder stringBuilder = new StringBuilder();
                String line; // Something to hold the current string so it can be saved and checked

                // Statistics to help determine if the backup worked or not.
                int totalLineCounter = 0;
                int errorCounter = 0;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                    totalLineCounter++;
                    if (line.contains("error"))
                        errorCounter++;
                    else if (line.contains("usage:")) {
                        errorCounter++;
                        System.out.println("\"cp\" command failed to start.");
                    }
                }

                process.waitFor();
                stringBuilder.append("COPY-ITEM STATISTICS =================================================== \n");
                stringBuilder.append("Total Errors: " + errorCounter + "\nTotal Files Transferred: " + totalLineCounter);
                stringBuilder.append("\n Percent Error: " + Math.round(errorCounter / totalLineCounter) + "%");

                // Do something with the string, like save it to a text file or something. 
                System.out.println(stringBuilder.toString());


            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to start Process in backupData");
            } catch (InterruptedException ie) {
                ie.printStackTrace();
                System.out.println("Interrupted During Out/In/Error stream reading");
            }
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
                } else if (f.getName().toCharArray()[0] == '.') { // periods before filenames shouldn't be backed up.
                    addToList = false;
                    break;
                }
            }

            if (addToList)
                filesWeWantSaved.add(f.getName());

            addToList = true; // reset flag just in case.
        }

        return filesWeWantSaved;
    }
} // END OF BACKUP ENGINE