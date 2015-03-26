package engine;

import filter.BackupFileFilter;
import filter.UsersNameFilter;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * Created by kevin on 1/12/15.
 *
 * Maintains the utilities to be used on Drive objects, where both engines use the same methods without code duplication.
 */
public class DriveUtils {

    public String OS;
    public String backupFolderName;

    public DriveUtils() {
        OS = System.getProperty("os.name");
    }

    /**
     * Round used from StackOverflow, created by Louis Wasserman and Sean Owen, adapted by user Jonik
     *
     * @param value  -- the value we want to round up
     * @param places -- the number of places we want to go before rounding up
     * @return the double that contains only up to 'places' number of precision
     */
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static float round(float value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    public float byteToGigabyte(float num) {

        int divisor = 1000; // definition of when to change names in byte size kilo -> mega etc
        // On Mac OS X, 1000 is the appropriate one.

        //TODO :: Test divisor on Windows to see if they use 1024 instead

        return round((((num / divisor) / divisor) / divisor), 1);

    }

    public double byteToGigabyte(Double num) {
        int divisor = 1000; // definition of when to change names in byte size kilo -> mega etc
        // On Mac OS X, 1000 is the appropriate one.

        return round((((num / divisor) / divisor) / divisor), 1);

    }

    /**
     * @param rawDrives - ArrayList<Drive> that is all the current drives as they were PRIOR to backing up.
     * @return the Drive object with the highest freeCapacity
     */
    public Drive getHighestStorageDrive(ArrayList<Drive> rawDrives) {
        Drive highest = rawDrives.get(0);

        for (int i = 1; i < rawDrives.size(); i++) { // i = 1 because highest starts out as the first drive.
            if (rawDrives.get(i).getCapacity("free") > highest.getCapacity("free")) {
                highest = rawDrives.get(i);
            }
        }

        /** Special Rules here, Storage actually saves things to /customer_backup/ , and not its root folder. **/
        if (highest.getName().equals("Storage")) { // BUG WORKAROUND: Sun bug where getRuntime().exec() can't handle "\\ "
            // Had to rename folder without spaces, works fine otherwise.
            highest.mountPoint = highest.getMountPoint() + "/customer_backup/";
        }


        return highest;
    }

    /**
     * @param file -- when given a file creates a Drive object so we can auto calculate storage and avoid IO errors
     * @return The converted Drive object
     *
     * When we have the file but want to use the Drive object
     */
    public Drive mountPointToDrive(File file) {

        if (OS.contains("Windows")) {
            // Windows doesn't have a place where all filesystems are mounted, so we look for char:
            char[] name = file.getPath().toCharArray();
            String mountPoint = "";
            for (int i = 0; i < name.length; i++) {
                if (name[i] == ':') {
                    mountPoint = String.valueOf(name[i - 1]) + ":";
                    break;
                }
            }
            file = new File(mountPoint);
        }
        String name = file.getName(); // /path/to/file/ThisIsTheName
        String path = file.getAbsolutePath();
        double capac = byteToGigabyte(file.getTotalSpace());
        double free = byteToGigabyte(file.getFreeSpace());
        double used = round(capac - free, 2);


        Drive drive = new Drive(name, path, capac, free, used, OS, file);
        return drive;
    }

    public String getUsers(Drive drive) {
        String users = "";
        try {
            File getUsers = new File(drive.getMountPoint() + "/Users/");
            File[] allUsers = getUsers.listFiles();
            int counter = 0;
            for (File f : allUsers) {

                /*
                    Filter the Users folders, if it passes all the filters it is added in a single logic tree
                 */

                UsersNameFilter usersNameFilter = new UsersNameFilter();


                if (usersNameFilter.filterSelection(f.getName())) {

                    if (counter == 0) {
                        users += f.getName();
                        counter++;
                    } else
                        users += "\n" + f.getName();

                }
            } // End for loop of files


        } catch (NullPointerException npe) {
            users = "No Users Detected";
        }

        return users;

    }

    public String getOS() {
        return OS;
    }

    /**
     * @param message      -- what to tell the user
     * @param parentWindow -- JFrame to make the JOptionPane happy
     * @return boolean, true means the user said YES to your request, false means NO and closing the option pane closes
     * the program (to avoid flooding).
     */
    public boolean askUserYesNo(String message, JFrame parentWindow) {
        int n = JOptionPane.showConfirmDialog(parentWindow, message + "\n\nClosing this dialog will close DBET", "", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);

        // 0 is YES
        if (n == 0)
            return true;
        // 1 is NO...not entirely sure why.
        if (n == 1)
            return false;
        if (n == -1) {
            System.out.println("\nClosing DBET per user, no further drives will be backed up.");
            System.exit(0);
        }

        return false;

    }

    public ArrayList<String> findImportantFiles(File[] files) {
        // Look for anything that isn't a system file
        ArrayList<String> filesWeWantSaved = new ArrayList<String>();
        boolean addToList = true;
        // Contains both Mac and PC files in the root folders of most drives that
        // are system and we don't need to save.

        BackupFileFilter backupDriveFileFilter = new BackupFileFilter();

        for (File f : files) {
            addToList = backupDriveFileFilter.filterSelection(f.getName());

            if (addToList)
                filesWeWantSaved.add(f.getName());
        }

        return filesWeWantSaved;
    }

    /**
     * We have a list of the files we want, but we need the ENTIRE path to do the transfer command, this will
     * automatically give us the full path easily of any file given.
     *
     * @return list of full paths from the files given
     */
    public ArrayList<String> getFullPathForFiles(Drive drive) {
        ArrayList<String> fullPathFilesList = new ArrayList<String>();

        for (String fileName : findImportantFiles(drive.getFile().listFiles())) {
            String fullPathFile = drive.getFile().getPath() + File.separator + fileName;
            fullPathFilesList.add(fullPathFile);
        }

        return fullPathFilesList;

    }

    /**
     * Creates a JOptionPane to ask the user what they want to call the folder for all the data to be transferred to.
     * This method is OS sensitive and has explicit filtering for either Windows or Mac (because of syntatical differences). This does NOT run the command when debug
     * is set to true.
     *
     * @param drive                      -- Drive we are backing up (to show the users for the operator)
     * @param parentWindow               -- Used for the JOptionPane to actually have the popup exist
     * @param currentHighestStorageDrive -- Used for the destination of the mkdir command
     * @param mode                       -- Allows string filtering for proper syntax based on OS, "Mac" or "Windows" are accepted
     * @return A String[] containing the command issued to the system. [0] is "mkdir" [1] is the formatted destination path string
     */
    public String[] askUserForMkdir(Drive drive, JFrame parentWindow, Drive currentHighestStorageDrive, boolean debug, String... mode) {

        String[] commandToGiveToUser = new String[2];
        commandToGiveToUser[0] = "mkdir";
        String topLine = "Enter the customers Service Invoice Number( i.e 13021) for drive: "
                + drive.getName() + "\n\n";

        String mkdir = JOptionPane.showInputDialog(parentWindow, topLine +
                        "Potential users:\n" + this.getUsers(drive),
                "Make Directory", JOptionPane.QUESTION_MESSAGE);




        if (mkdir == null) {
            boolean response = this.askUserYesNo("No input was detected for the directory, do you wish to proceed?\n\nPressing 'No' will auto-generate a folder and backup the drive.", parentWindow);

            if (response) {
                mkdir = JOptionPane.showInputDialog(parentWindow, "Enter the customers Service Invoice Number( i.e 13021)\n\n" +
                                "Potential users:\n" + this.getUsers(drive),
                        "Make Directory", JOptionPane.QUESTION_MESSAGE);
            }

            // Asked again, they obviously WANT to backup this drive just didn't input a name or closed the box.
            // Best thing to do is back it up for them, and just have them rename the folder by hand later.
            if (mkdir == null)
                mkdir = "RENAME THIS FOLDER!_" + drive.toString();
                    /*
                    Without drive.toString() DBET will replace the last made RENAME THIS FOLDER! with the
                    current one, losing data. The drive.toString();
                    */

        } // End of mkdir == null (the first time)

        // This is JUST the name the user gave us, this is used for the LOG object
        backupFolderName = mkdir;



        // FILTER MKDIR HERE!
        if (mode.length != 0) {
            String[] mode_os = mode;

            if (mode_os[0].contains("Mac")) {

                // MAC FILTERING RULES GO HERE


                // BUG WORKAROUND Storage3 would lose its '/' after its name.


                mkdir = currentHighestStorageDrive.getMountPoint() + File.separator + mkdir + File.separator;

                mkdir = mkdir.replace("//", "/");

                // replace spaces in the mkdir with "\ "
                //mkdir = mkdir.replace(" ", "\\ ");




            } else if (mode_os[0].contains("Window")) {

                // WINDOWS FILTERING HERE

                mkdir = mkdir.replace("\\\\", "\\");


                String custBackup = currentHighestStorageDrive.getMountPoint();

                // F:\ would not go to custBackup without this
                if (custBackup.contains("F:")) {
                    custBackup = custBackup + "CustBackup\\";
                }


                // Windows usees strings in powershell for spaces
                char[] mkdir_as_chars = (custBackup + mkdir + File.separator).toCharArray();

                // Make new array that allows me to add a ' before and after the folder location.
                char[] mkdir_char_to_string = new char[mkdir_as_chars.length + 2];

                mkdir_char_to_string[0] = '\'';

                for (int i = 0; i < mkdir_as_chars.length; i++) {
                    mkdir_char_to_string[i + 1] = mkdir_as_chars[i];
                }

                mkdir_char_to_string[mkdir_char_to_string.length - 1] = '\'';

                String charArrayToString = new String(mkdir_char_to_string);

                mkdir = charArrayToString;


            }
        }


        commandToGiveToUser[1] = mkdir;


        if (!debug) {

            try {
                Process p = Runtime.getRuntime().exec(commandToGiveToUser);
            } catch (IOException ioe) {
                ioe.printStackTrace();
                System.out.println("Could not reach destination folder to create directory.");
            }

        } else {
            for (String s : commandToGiveToUser)
                System.out.println(s);
        }


        return commandToGiveToUser;
    }

    /**
     * There is a posibility that the user has more data than we can backup, this method allows us to deal with this
     * quickly
     *
     * @param driveToPossiblyBackup
     * @param highestCapacityStorageDrive
     * @param parentWindow                -- Allows us to ask the user if there is NOT enough space, to continue anyway with a JOptionPane
     * @return true if we can back up the drive, false if we cannot due to space restrictions
     */
    public boolean getSystemHasSufficientStorage(Drive driveToPossiblyBackup, Drive highestCapacityStorageDrive, JFrame parentWindow) {
        if (driveToPossiblyBackup.getCapacity("used") < highestCapacityStorageDrive.getCapacity("free"))
            return true;

        else {
            double used = driveToPossiblyBackup.getCapacity("used");
            double highestFreeStorage = highestCapacityStorageDrive.getCapacity("free");
            double percentWeCanGetRoundedDown = round((highestFreeStorage / used) * 100, 2);


            boolean response = this.askUserYesNo("The drive " + driveToPossiblyBackup.getName() + " you are attempting to back up is too large for this systems free capacities.\n" +
                    "\nTotal Size of Backup: " + used + "GB" +
                    "\nTotal Size of Largest Drive on System: " + highestFreeStorage + "GB" +
                    "\nTotal Percent we can backup with current system: " + percentWeCanGetRoundedDown + "%" +
                    "\nWould you like to proceed anyway?", parentWindow);

            return response;
        }
    }


    public Drive updateDriveInformation(Drive drive) {
        File f = drive.getFile();
        Drive d = this.mountPointToDrive(f);
        return d;
    }


    public String getBackupFolderName() {
        return backupFolderName;
    }
}// END DRIVE UTILS
