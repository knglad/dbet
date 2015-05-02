package tests;

import engine.DataDestinationEngine;
import engine.Drive;
import engine.DriveUtils;
import engine.Log;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

/**
 * Created on : 8/15/14 for ${PACKAGE_NAME}
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


    @Test
    public void testFileGetFilesWhenNotAFolder() {
        File f = new File("/Users/Kevin/Desktop/Gladhart_HarassmentTraining.png");

        // RETURNS NULL IF THERE ISNT A FILE LIST INSIDE.
        File[] files = f.listFiles();
        System.out.println(f.getName());
        assertTrue(files == null);
    }


    @Test
    public void testLoadAllLogsWorks() {
        Log l = new Log();
        ArrayList<Log> allLogsOnSystem = l.loadAllLogs();

        for (Log log : allLogsOnSystem) {
            System.out.println(log.logText);
        }


    }
}
