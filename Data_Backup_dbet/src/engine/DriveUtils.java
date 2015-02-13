package engine;

import filter.BackupFileFilter;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by kevin on 1/12/15.
 * <p/>
 * Maintains the utilities to be used on Drive objects, where both engines use the same methods without code duplication.
 */
public class DriveUtils {

    public String OS;

    public DriveUtils() {
        OS = System.getProperty("os.name");
    }


    public float byteToGigabyte(float num) {

        int divisor = 1000; // definition of when to change names in byte size kilo -> mega etc
        // On Mac OS X, 1000 is the appropriate one.

        //TODO :: Test divisor on Windows to see if they use 1024 instead

        return (((num / divisor) / divisor) / divisor);

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
        double used = capac - free;


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

                if (f.getName().toCharArray()[0] == '.') {
                } // '.DS_Store' is not a user

                else if (f.getName().contains("Shared")) {
                } // Shared is pointless to show

                else if (f.getName().equals("")) {
                } // Had some random empty named folders

                else if (f.getName().equals("Applications")) {
                } // Applications folder in the Users shouldn't be visible.

                else if (counter == 0) {
                    users += f.getName();
                    counter++;
                } else {
                    users += "\n" + f.getName();
                }
            }
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
        int n = JOptionPane.showConfirmDialog(parentWindow, message, "", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);

        // 0 is YES
        if (n == 0)
            return true;
        // 1 is NO...not entirely sure why.
        if (n == 1)
            return false;
        if (n == -1) {
            System.out.println("Closing DBET per user, no further drives will be backed up.");
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

    public String askUserForMkdir(Drive drive, JFrame parentWindow, Drive currentHighestStorageDrive, String[]... mode) {

        String mkdir = JOptionPane.showInputDialog(parentWindow, "Enter the customers Service Invoice Number( i.e 13021)\n\n" +
                        "Potential users:\n" + this.getUsers(drive),
                "Make Directory", JOptionPane.QUESTION_MESSAGE);


        if (mkdir == null) {
            boolean response = this.askUserYesNo("No input was detected for the directory, do you wish to proceed?", parentWindow);

            if (!response)

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
                    Without this DBET will replace the last made RENAME THIS FOLDER! with the
                    current one, losing data. The drive.toString();
                    */

        } // End of mkdir == null (the first time)

        // FILTER MKDIR HERE!
        if (mode.length != 0) {
            String[] mode_os = mode[0];

            if (mode_os[0].contains("Mac")) {

                // MAC FILTERING RULES GO HERE

                // WATCH Mac made the folder 13021\ Kevin\ Tester in the actual folder
                // replace spaces in the mkdir with "\ "
                mkdir = mkdir.replace(" ", "\\ ");

                // BUG WORKAROUND Storage3 would lose its '/' after its name.
                mkdir = mkdir.replace("//", "/");

            } else if (mode_os[0].contains("Window")) {

                // WINDOWS FILTERING HERE


            }
        }

        // Finalize the mkdir command with the total path
        mkdir = currentHighestStorageDrive.getMountPoint() + File.separator + mkdir + File.separator;
        return mkdir;
    }

}
