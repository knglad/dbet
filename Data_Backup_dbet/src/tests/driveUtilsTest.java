package tests;

import engine.DataDestinationEngine;
import engine.Drive;
import engine.DriveUtils;
import org.junit.Test;

import javax.swing.*;
import java.util.ArrayList;

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
}
