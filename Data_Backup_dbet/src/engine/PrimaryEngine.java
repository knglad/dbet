package engine;

import java.io.File;
import java.util.ArrayList;

/**
 * 
 * @author kevin
 *
 * Primary Engine handles the interactions for the drives that we will backup TO, that is to say the destination for
 * the data we want to backup.
 *
 */

public class PrimaryEngine {


	public String OS; // The Operating System type we are working with
    private ArrayList<Drive> rawDrives = new ArrayList<Drive>(); // Arraylists make themselves bigger if need be, automatically.
    private boolean listMadeSuccessfully = false;
    /**
	 * MANUAL HARD CODED DRIVE STRINGS ARE HERE! EDIT IF MOUNT POINTS OR NAMES CHANGE!!!!!!!! TODO Ensure this is updated
	 */

			// NOTE: macVolumes must be the absolute path to EACH PARTITIONS ROOT!! Breaks otherwise and doesn't know what to do.
	private String[] macVolumes = { "/Volumes/Storage/", "/Volumes/Storage2/", "/Volumes/Storage3/"};
    private String[] windowsVolumes = {"F:/CustBackup/"};



    public PrimaryEngine(){
		// Determine which OS we are working with
		OS = System.getProperty("os.name");

        if ( OS.equals("Mac OS X") ){
		listMadeSuccessfully =  makeDriveList(macVolumes);
		} else if (OS.contains("Windows")) {
		 listMadeSuccessfully = makeDriveList(windowsVolumes);
	 }


    }

	/**
	 *
     * @param mountPoint
     * 		Array of strings that is the exact mount point for the root of the device
	 * @return
	 * 		false if an empty list or true if the list was made successfully
	 */
	public boolean makeDriveList(String[] mountPoint){
		
		for (String s : mountPoint){// Advanced for loop, for each String I'm calling s WITHIN mountPoint array, do the following.
			
			File f = new File(s); // Make a file out of the string mount point
			
			String name = f.getName(); // Name
			String path = f.getAbsolutePath();
			double capac = f.getTotalSpace();
			double free = f.getFreeSpace();
			double used = capac - free;
			String fileSystem = OS;

			Drive drive = new Drive(name, path, capac, free, used, fileSystem, f);
			
			rawDrives.add(drive); // add this newly created file to the list, the next available position.
		}
		
		if (rawDrives.size() == 0 || rawDrives.size() < mountPoint.length)
			return false; // its empty, it can never be empty
		else 
			return true; // there's something in it, that's all this cares about. 
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
		double capac = file.getTotalSpace();
		double free = file.getFreeSpace();
		double used = capac - free;
		String fileSystem = OS;

		Drive drive = new Drive(name, path, capac, free, used, fileSystem, file);
		return drive;
	}

	public Drive getHighestStorageDrive() {
		Drive highest = rawDrives.get(0);

		for (int i = 1; i < rawDrives.size(); i++) { // i = 1 because highest starts out as the first drive.
			if (rawDrives.get(i).freeCapacity > highest.freeCapacity) {
				highest = rawDrives.get(i);
			}
		}

		return highest;
	}


	//////// GETTERS AND SETTERS //////////////////////////////
	
	public String getOS(){
		return OS;
	}
	
	public boolean isListMade(){
		return listMadeSuccessfully;
	}
	
	public ArrayList<Drive> getDriveList(){
		return rawDrives;
	}


	
	
} // END OF PRIMARY ENGINE
