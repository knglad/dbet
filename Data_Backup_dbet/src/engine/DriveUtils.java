package engine;

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
     * @param rawDrives - ArrayList<Drive>
     * @return the Drive object with the highest freeCapacity
     */
    public Drive getHighestStorageDrive(ArrayList<Drive> rawDrives) {
        Drive highest = rawDrives.get(0);

        for (int i = 1; i < rawDrives.size(); i++) { // i = 1 because highest starts out as the first drive.
            if (rawDrives.get(i).getCapacity("free") > highest.getCapacity("free")) {
                highest = rawDrives.get(i);
            }
        }

        if (highest.getName().equals("Storage")) { // TODO BUG WORKAROUND: Sun bug where getRuntime().exec() can't handle "\\ "
            // Had to rename folder without spaces, works fine otherwise.
            highest.mountPoint = highest.getMountPoint() + "/customer_backup/";
        }
        return highest;
    }

    /**
     * @param file -- when given a file creates a Drive object so we can auto calculate storage and avoid IO errors
     * @return The converted Drive object
     * <p/>
     * When we have the file but want to use the Drive object
     */
    public Drive mountPointToDrive(File file) {
        String name = file.getName(); // /path/to/file/ThisIsTheName
        String path = file.getAbsolutePath();
        double capac = byteToGigabyte(file.getTotalSpace());
        double free = byteToGigabyte(file.getFreeSpace());
        double used = capac - free;
        String fileSystem = OS;

        Drive drive = new Drive(name, path, capac, free, used, fileSystem, file);
        return drive;
    }


    public String getOS() {
        return OS;
    }

}
