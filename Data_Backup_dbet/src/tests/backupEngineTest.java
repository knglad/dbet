package tests;

import engine.BackupEngine;
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

        // Mac tested for proper filtering of unwanted drives. PASSED
        // Windows tested for proper filtering of unwanted drives. PASSED
        // Tested for dynamic list saving, procedure -- PASSED using LM PNP on multiple tries.
            /*
             * Remove (temporarily) one of the dd's default keywords so that a drive is detected as backup-able
             * When prompted by the JOptionPane have it add the keyword to the list and close the program.
             * Rerun the program and see if it is detected (should load the list automatically).
             */
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
        // time it was called, reducing overall memory.
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
}
