package tests;

import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by CSC on 1/12/2015.
 */
public class windowsRuntimeExecTest {

    /**
     * Failed on 3/19/15 to copy the folders. It got hung up, I'm not sure if this was because of the code or something with the machine. more testing needed.
     * <p/>
     * TODO :: Test windows runtime is working in general
     */

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
