package tests;

import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by CSC on 1/12/2015.
 */
public class windowsRuntimeExecTest {

    @Test
    public void testCP() {
        String[] command = new String[]{"powershell.exe", "/C", "cp", "C:\\Users\\CSC\\Contacts", "C:\\Users\\CSC\\Favorites", "G:\\", "-recurse"};
        try {
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
            assertTrue(p.isAlive());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
