package tests;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import engine.PrimaryEngine;

public class engineTest {
	
        @Test
	public void PrimaryEngineTest(){
		PrimaryEngine pe = new PrimaryEngine();
		
		assertEquals("Mac OS X", pe.getOS() ); // This will fail on Windows
		// Test to ensure the list is populating in engine
		assertTrue(pe.isListMade());
		
		// Test to see how many drives are in the array list
		assertEquals(3, pe.getDriveList().size());
		assertEquals("/Volumes/Storage", pe.getDriveList().get(0).getMountPoint()); // It knows its a folder, drops the last '/'
			// Also the getName() returns the folder we are in with this file particularly. getPath returns the entire path for the file.
		
		assertEquals("/Volumes/Storage2", pe.getDriveList().get(1).getMountPoint() );
		
		// Check to ensure they have specific free space remaining
		assertTrue(   pe.getDriveList().get(0).getCapacity("total") != pe.getDriveList().get(1).getCapacity("total")   );
		
		
		// getName is the last folder in the absolute path so in '/Volumes/Storage' it would be storage
		assertEquals("Storage", pe.getDriveList().get(0).getName());
		
		System.out.println(pe.getDriveList().get(0).getCapacity("total") / Math.pow(1000, 3) );
		
	}
}
