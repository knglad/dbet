package engine;

import java.io.File;
import java.util.ArrayList;

/**
 * 
 * @author kevin
 *
 *	This class is used to determine what subclasses are needed to be ran.
 * Things like determining which OS we are using and if we want a FRONT or BACK
 * room MODE.
 */

public class PrimaryEngine {


	public String OS; // The Operating System type we are working with
    private ArrayList<Drive> rawDrives = new ArrayList<Drive>(); // Arraylists make themselves bigger if need be, automatically.
    private boolean listMadeSuccessfully = false;
    /**
	 * MANUAL HARD CODED DRIVE STRINGS ARE HERE! EDIT IF MOUNT POINTS OR NAMES CHANGE!!!!!!!!
	 */
	// I couldn't find a dynamic way to get ALL the drives 
	
			// NOTE: macVolumes must be the absolute path to EACH PARTITIONS ROOT!! Breaks otherwise and doesn't know what to do.
	private String[] macVolumes = { "/Volumes/Storage/", "/Volumes/Storage2/", "/Volumes/Storage3/"};
    private String[] windowsVolumes = {"F:/CustBackup/"};



    public PrimaryEngine(){
		// Determine which OS we are working with
	 OS = System.getProperty("os.name");

        if ( OS.equals("Mac OS X") ){
		 
		listMadeSuccessfully =  makeDriveList(macVolumes);
	}
	 else if ( OS.contains("Windows") ){ // CONFIRM THIS ON A WINDOWS MACHINE!!!
		 
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
			
			Drive drive = new Drive(name, path, capac, free, used, fileSystem);
			
			rawDrives.add(drive); // add this newly created file to the list, the next available position.
		}
		
		if (rawDrives.size() == 0 || rawDrives.size() < mountPoint.length)
			return false; // its empty, it can never be empty
		else 
			return true; // there's something in it, that's all this cares about. 
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
