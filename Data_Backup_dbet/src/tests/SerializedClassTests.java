package tests;

import engine.DisregardDrives;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by kevin on 8/15/14.
 */
public class SerializedClassTests {

    @Test
    public void testSerializedDisregardDrives() {
        DisregardDrives dd = new DisregardDrives();
        dd.addWithoutDuplicates("This is the first string");
        dd.addWithoutDuplicates("Second String time!!!");
        dd.addWithoutDuplicates("Third!!!!");
        dd.changeFileName("test081514");
        dd.saveList();
    }

    @Test
    public void loadDisregardDrives() {
        DisregardDrives dd = new DisregardDrives();
        dd.changeFileName("test081514");
        assertEquals(0, dd.size());
        dd.loadList();

        assertEquals("This is the first string", dd.get(0));
        assertEquals("Second String time!!!", dd.get(1));
        assertEquals("Third!!!!", dd.get(2));

    }

    @Test
    public void testDefaultListCreation() {
        DisregardDrives dd = new DisregardDrives();
        dd.loadList(); // should fail, and create a default
        assertEquals(11, dd.size());
        assertEquals("LM PNP", dd.get(0));
        assertEquals("WD SmartWare", dd.get(1));
        assertEquals("gimp", dd.get(2));
    }
}
