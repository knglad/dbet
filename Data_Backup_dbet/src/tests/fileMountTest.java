package tests;

import engine.DataDestinationEngine;
import engine.Drive;
import engine.DriveUtils;
import org.junit.Test;

import java.io.File;

/**
 * Created by kevin on 8/15/14.
 */
public class fileMountTest {


    // Used to test to make sure that listFiles() was working, how it worked and to see what getPath returns.
    @Test
    public void testMacMountPointsTotal() {
        File f = new File("/Volumes/");
        File[] mountPoints = f.listFiles();

        for (File file : mountPoints)
            System.out.println(file.getPath());
    }

    // Test the primaryEngines rawDrives variable to see what happens if a drive we EXPECT
    // to be there, isn't
    @Test
    public void testRawDrivesWhenNotPresent() {
        DataDestinationEngine pe = new DataDestinationEngine();
        DriveUtils du = new DriveUtils();

        for (Drive d : pe.getDriveList()) {
            System.out.println(d.getMountPoint() + "  " + d.printCapacity("free", du));
        }
    }
}
