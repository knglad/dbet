package tests;

import engine.DataDestinationEngine;
import engine.Drive;
import engine.DriveUtils;
import org.junit.Test;

import javax.swing.*;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by kevin on 2/26/15.
 */
public class driveUtilsTest {

    @Test
    public void testSufficientSpaceMethod() {
        JFrame j = new JFrame();
        DataDestinationEngine dde = new DataDestinationEngine();
        ArrayList<Drive> list = dde.getDriveList();

        DriveUtils du = new DriveUtils();

        assertTrue(du.getSystemHasSufficientStorage(list.get(0), list.get(1), j));

    }

    @Test
    public void testDivisorForByteToGigabyte() {
        DriveUtils du = new DriveUtils();
        DataDestinationEngine dde = new DataDestinationEngine();
        ArrayList<Drive> list = dde.getDriveList();

        // If we divide it properly it should come up with a near even number
        assertEquals(list.get(0).getCapacity("free"), 1500.0);


    }
}
