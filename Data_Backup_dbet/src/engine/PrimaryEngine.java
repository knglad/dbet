package engine;

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
	
	
	
	public PrimaryEngine(){
		// Determine which OS we are working with
	 OS = System.getProperty("os.name");
	 
	 if (OS.equals("Mac OS X")){
		 macSystem();
	}
	 
}
	public String getOS(){
		System.out.println(OS);
		return OS;
	}
	
	public void macSystem(){
		// If its a Mac do this
	}
	
	public void WindowsSystem(){
		// If its Windows do this
	}
	
}
