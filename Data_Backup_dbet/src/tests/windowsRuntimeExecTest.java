package tests;

import org.junit.Test;

import java.io.IOException;

/**
 * Created by CSC on 1/12/2015.
 */
public class windowsRuntimeExecTest {

    @Test
    public void testCP() {
        String[] command = new String[]{"powershell.exe", "/C", "cp", "C:\\Users\\CSC\\Contacts", "C:\\Users\\CSC\\Favorites", "H:\\", "-recurse"};
        try {
            Process p = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
