package tests;

import filter.BackupDriveFileFilter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by kevin on 1/15/15.
 */
public class testFilterChildren {

    @Test
    public void testFilterListIsCreated() {
        BackupDriveFileFilter bdff = new BackupDriveFileFilter();

        assertEquals("Yose Life Image", bdff.get(3));
        assertEquals("private", bdff.get(5));
        assertEquals("net", bdff.get(7));
    }
}
