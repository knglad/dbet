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
 * 
 * TODO 
 * 	Find Java mount point for windows systems for 'File windows' in primary engine constructor
 * 	Make list of key words to ignore certain drives or images of things that aren't backup drives ( LM PNP for example ). 
 */

public class PrimaryEngine {


	public String OS; // The Operating System type we are working with
	private ArrayList<File> rawDrives = new ArrayList<File>(); // Arraylists make themselves bigger if need be, automatically. 
	private boolean listMadeSuccessfully = false;
	
	/**
	 * MANUAL HARD CODED DRIVE STRINGS ARE HERE! EDIT IF MOUNT POINTS OR NAMES CHANGE!!!!!!!!
	 */
	// I couldn't find a dynamic way to get ALL the drives 
			// NOTE: macVolumes must be the absolute path to EACH PARTITIONS ROOT!! Breaks otherwise and doesn't know what to do.
	private String[] macVolumes = { "/Volumes/Storage/", "/Volumes/Storage2/", "/Volumes/Storage3/"};
	private String[] windowsVolumes = {"D:/"}; // EDIT THIS WHEN WE KNOW WHAT LETTER IS ATTACHED TO THE WINDOWS BACKUP DRIVES!!
	
	
	
	
	
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
	 * @param file 
	 * 		a file that is the mount point for all the drives, /Volumes/ for example
	 * @return
	 * 		false if an empty list or true if something is in the list.
	 */
	public boolean makeDriveList(String[] mountPoint){
		
		for (String s : mountPoint){// Advanced for loop, for each file I'm calling f WITHIN fileArray, do the following.
			
			File f = new File(s); // Make a file out of the string mount point
			rawDrives.add(f); // add this newly created file to the list, the next available position.
		}
		
		if (rawDrives.size() == 0 || rawDrives.size() < mountPoint.length)
			return false; // its empty, it can never be empty
		else 
			return true; // there's something in it, that's all this cares about. 
	}
	
	
	public String getOS(){
		return OS;
	}
	
	public boolean isListMade(){
		return listMadeSuccessfully;
	}
	
	public ArrayList<File> getDriveList(){
		return rawDrives;
	}
	
} // END OF PRIMARY ENGINE
