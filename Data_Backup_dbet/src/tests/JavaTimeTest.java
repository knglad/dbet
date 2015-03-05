package tests;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Created by kevin on 2/27/15.
 */
public class JavaTimeTest {

    @Test
    public void getCurrentTime() {
        LocalDateTime ldt = LocalDateTime.now();
        System.out.println(ldt.toLocalTime());
    }


    @Test
    public void testDifferenceInTime() {
        LocalTime ltCurrent = LocalTime.now();

        // Add some time dilation using the user
    }
}
