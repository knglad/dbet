package engine;

import java.io.File;
import java.util.ArrayList;

/**
 * 
 * @author kevin
 *
 * The Data Destination Engine handles the interactions for the drives that we will backup TO, that is to say the destination for
 * the data we want to backup. Things like creating Drive objects from the drives we want to store data to, reducing JRE time to
 * access the OS, which reduces clashes from other OS processes.
 *
 * DDE stores the hard coded list of drives that we KNOW are suitable for back ups (through enough size or dedicated to just customer data for safety).
 *
 */

public class DataDestinationEngine {



    private ArrayList<Drive> rawDrives = new ArrayList<Drive>(); // Arraylists make themselves bigger if need be, automatically.
    private boolean listMadeSuccessfully = false;
	private DriveUtils du = new DriveUtils();
	/**
	 * MANUAL HARD CODED DRIVE STRINGS ARE HERE! EDIT IF MOUNT POINTS OR NAMES CHANGE!!!!!!!!
	 */

			// NOTE: macVolumes must be the absolute path to EACH PARTITIONS ROOT!! Breaks otherwise and doesn't know what to do.
	private String[] macVolumes = {"/Volumes/Storage/", "/Volumes/Storage 2 (500)/", "/Volumes/Storage3/"};
	private String[] windowsVolumes = {"F:\\CustBackup\\"};


	public DataDestinationEngine() {
		// Determine which OS we are working with


		if (du.getOS().equals("Mac OS X"))
			listMadeSuccessfully = makeDriveList(macVolumes);

		else if (du.getOS().contains("Windows"))
			listMadeSuccessfully = makeDriveList(windowsVolumes);



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

			if (f.getTotalSpace() != 0.0) // pointless if it doesn't exist.
				// add this newly created file to the list, the next available position.
				rawDrives.add(du.mountPointToDrive(f));

		}
		
		if (rawDrives.size() == 0 || rawDrives.size() < mountPoint.length)
			return false; // its empty, it can never be empty
		else 
			return true; // there's something in it, that's all this cares about. 
	}







	//////// GETTERS AND SETTERS //////////////////////////////

	public boolean isListMade(){
		return listMadeSuccessfully;
	}
	
	public ArrayList<Drive> getDriveList(){
		return rawDrives;
	}


	
	
} // END OF PRIMARY ENGINE
