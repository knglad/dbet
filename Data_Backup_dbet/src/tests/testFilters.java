package tests;

import filter.BackupFileFilter;
import filter.CommandErrorFilter;
import org.junit.Test;

import static org.junit.Assert.*;

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
        CommandErrorFilter cef = new CommandErrorFilter();

        // FALSE as in, it FAILED the filter test. and DONT use it.
        assertFalse(cef.filterSelection("error"));
        assertTrue(cef.filterSelection("/test/path/awesomeness.jpg"));
    }
}
