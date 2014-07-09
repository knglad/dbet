package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import engine.PrimaryEngine;

public class engineTest {
	
	@Test
	public void PrimaryEngineTest(){
		PrimaryEngine pe = new PrimaryEngine();
		
		assertEquals("Mac OS X", pe.getOS() ); // This will fail on Windows
		
	}
}
