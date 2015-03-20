package tests;

import filter.BackupFileFilter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by kevin on 1/15/15.
 */
public class testFilters {

    @Test
    public void testFilterListIsCreated() {
        BackupFileFilter bdff = new BackupFileFilter();

        assertEquals("Yose Life Image", bdff.get(3));
        assertEquals("private", bdff.get(5));
        assertEquals("net", bdff.get(7));
    }

    @Test
    public void testListBehavior() {

    }
}
