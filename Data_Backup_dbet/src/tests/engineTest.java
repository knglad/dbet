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
		assertEquals("/Volumes/Storage", pe.getDriveList().get(0).getPath() ); // It knows its a folder, drops the last '/'
			// Also the getName() returns the folder we are in with this file particularly. getPath returns the entire path for the file.
		
		assertEquals("/Volumes/Storage2", pe.getDriveList().get(1).getPath() );
		
		// Check to ensure they have specific free space remaining
		assertTrue(   pe.getDriveList().get(0).getFreeSpace() != pe.getDriveList().get(1).getFreeSpace()   );

	}
}
