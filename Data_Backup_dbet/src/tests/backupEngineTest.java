package tests;

import engine.BackupEngine;
import engine.DataDestinationEngine;
import engine.Drive;
import engine.DriveUtils;
import org.junit.Test;

import javax.swing.*;
import java.io.File;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.util.ArrayList;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by kevin on 12/30/14.
 */
public class backupEngineTest {

    @Test
    public void testDrivesForBackup() {
        JFrame jFrame = new JFrame();

        BackupEngine backupEngine = new BackupEngine(jFrame);

        backupEngine.startEngine();

        // Mac tested for proper filtering of unwanted drives. PASSED
        // Windows tested for proper filtering of unwanted drives. PASSED
        // Tested for dynamic list saving, procedure -- PASSED using LM PNP on multiple tries.
            /*
             * Remove (temporarily) one of the dd's default keywords so that a drive is detected as backup-able
             * When prompted by the JOptionPane have it add the keyword to the list and close the program.
             * Rerun the program and see if it is detected (should load the list automatically).
             */
        // Mac Folder Creation (askUserForMkdir in du) should name folders properly NEEDS STRESS TESTING [PASSED]
            /*
             * For some reason you don't need the '\\ ' for syntax it just handles it properly for mkdir.
             *      Otherwise it would\ make\ the\ folders\ like\ this.
             */
        // Tested Rsync w and w/o --progress [PASSED]
            /*
             * Will be using rsync from now on, it is much more robust and faster at transferring everything.
             */
        // CSC from LaCie 2Big NAS mounts once connected and can be accessed like a regular drive on MAC [PASSED]
        // CSC-NAS Windows needs to be tested that it is accessible from [IN TESTING]

    }


    @Test
    public void testContainsWithArrayList() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("first");
        list.add("second with spaces");
        list.add("THird333666999");
        list.add("random1");

        assertTrue(list.contains("first"));
        assertTrue(list.get(0).contains("first"));
        assertTrue(list.get(0).contains("f"));
        assertTrue(list.get(0).contains("firs"));
        assertTrue(list.get(3).contains("random"));
        assertFalse(list.get(0).contains("F"));

        // what happens with ToLowerCase when there is a number
        System.out.println(list.get(2).toLowerCase());

        assertTrue(list.get(1).contains("with"));
        assertTrue(list.get(1).contains("space"));

        assertFalse("users".contains("usr"));
    }

    @Test
    public void testFileSystemsForWindows() {
        for (FileStore f : FileSystems.getDefault().getFileStores()) {
            System.out.println(f.name());
            File file = new File(String.valueOf(f));
            System.out.println(file.getName());
        }
    }

    @Test
    public void testStringBuilderAppend() {

        // Used to have a stringbuilder hold the output string. Found out it outputted the entirety of itself
        // everytime, meaning that we could have a thousand lines outputted and then it would output 1001 lines the next
        // time it was called, reducing overall memory immensely. Will save only important information for the log file.
        StringBuilder sb = new StringBuilder();
        String line = "This is a single line that we will use";
        String two = "THIS IS A SECONDARY LINE THAT WE WILL USE";
        String three = "third line that we will use as an example";
        String[] arr = new String[]{line, two, three};

        for (int i = 0; i < 3; i++) {
            sb.append(line + "\n");
            sb.append(two + "\n");
            sb.append(three + "\n");
            System.out.println(sb.toString());

        }
    }

    @Test
    public void testMkdirCreatesFoldersProperly() {
        DriveUtils du = new DriveUtils();
        JFrame j = new JFrame();

        DataDestinationEngine dde = new DataDestinationEngine();
        ArrayList<Drive> list = dde.getDriveList();

        // Swap the OS for the proper mode filtering and syntax handling for the mkdir command
        String OS = //"Mac";
                "Windows";


        du.askUserForMkdir(list.get(0), j, list.get(0), true, OS);

    }
}
