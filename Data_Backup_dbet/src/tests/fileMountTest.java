package tests;

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
}
