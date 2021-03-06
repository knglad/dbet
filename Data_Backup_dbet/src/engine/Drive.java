package engine;

import java.io.File;

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
	public File file;
    public String dataDestination = null;

    // This is just a way for us to work with the drive and not keep the resource tied up so it can be unmounted etc.
	public Drive(String namee, String mount_point, double total_capac, double free_capac, double used_capac, String fs, File f) {

		name = namee;
		mountPoint = mount_point;
		totalCapacity = total_capac;
		freeCapacity = free_capac;
		usedCapacity = used_capac;
		file = f;
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
			return -1.0;
	}

	public File getFile() {
		return file;
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
			
			else if (type.equals("used") )
			{	 usedCapacity = amt; return true; }
			
			else
				return false;
	}

    public String printCapacity(String type, DriveUtils du) {
        if (type.equals("total"))
            return DriveUtils.round(totalCapacity, 2) + "GB";
        else if (type.equals("free"))
            return DriveUtils.round(freeCapacity, 2) + "GB";
        else if (type.equals("used"))
            return DriveUtils.round(usedCapacity, 2) + "GB";
        else
            return "Improper argument 'type of capacity' for Drive object";
    }

    /**
     * Should there be a specific folder you want to save customer data to this will give you
     * the full path to the folder.
     *
     * @return the path to the customers data beyond the mount point
     */
    public String getDataDestination() {
        if (dataDestination == null)
            return this.getMountPoint() + File.separator;
        else
            return this.getMountPoint() + File.separator + dataDestination + File.separator;
    }

    /**
     * Allows certain drives to have any type of folder structure within the root to store data,
     * i.e Storage/cust_backup/ instead of just Storage/
     *
     * @param s -- The string representing the folder starting passed the root.
     */
    public void setDataDestination(String s) {
        // getDataDestination puts the file.separators in for you
        dataDestination = s;
    }

} // End class DRIVE
