package engine;

/**
 * 
 * @author kevin
 *
 *
 *	This class will allow us to create a Drive object that is able to read a single drive and save its information
 *	in variables, things like Name, mount point, overall storage capacity, free storage and used storage, even file system. 
 */

public class Drive {
	
	
	// This is just a way for us to work with the drive and not keep the resource tied up so it can be unmounted etc. 
	public Drive(String name, String mount_point, double total_capac, double free_capac, double used_capac, String fs){
		// Might need to add an object that references the object that ties to the actual drive itself, then call the methods to set each one or calculate it
		//	 so that its in human readable GB.  
		
	}

}
