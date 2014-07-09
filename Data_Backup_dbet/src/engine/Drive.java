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
	
	public String name;
	public String mountPoint;
	public double totalCapacity;
	public double freeCapacity;
	public double usedCapacity;
	public String fileSystem;
	
	// This is just a way for us to work with the drive and not keep the resource tied up so it can be unmounted etc. 
	public Drive(String namee, String mount_point, double total_capac, double free_capac, double used_capac, String fs){

		name = namee;
		mountPoint = mount_point;
		totalCapacity = total_capac;
		freeCapacity = free_capac;
		usedCapacity = used_capac;
		fileSystem = fs;
	}
	
	/**
	 * Getters and Setters
	 */

	public String getName(){
		return name;
	}
	
	public String getMountPoint(){
		return mountPoint;
	}
	
	public double getCapacity(String type){
	/**
	 * @returnType double
	 * 		pass in 'total', 'free', or 'used' otherwise will return 0.0	
	 */
		
		if (type.equals("total") )
			return totalCapacity;
		else if (type.equals("free") )
			return freeCapacity;
		else if (type.equals("used"))
			return usedCapacity;
		else
			return 0.0;
	}
	
	public String getFileSystem(){
		return fileSystem;
	}
	
	public boolean setCapacity(String type, double amt){
		/**
		 * @arg type
		 * 		pass in 'total', 'free', or 'used' otherwise will return false	
		 * 
		 * @arg amt
		 * 		the amount of GB ( rounded to two decimal places ) that the drive contains in that area.
		 * 
		 * @returnType 
		 * 		returns true if it set properly, false otherwise.
		 */
			
			if (type.equals("total") )
			{	 totalCapacity = amt; return true; }
			
			else if (type.equals("free") )
			{	 freeCapacity = amt; return true; }
			
			else if (type.equals("used"))
			{	 usedCapacity = amt; return true; }
			
			else
				return false;
	}
	
	
	
} // End class DRIVE
