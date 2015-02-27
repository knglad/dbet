package tests;

import org.junit.Test;

import java.time.LocalDateTime;

/**
 * Created by kevin on 2/27/15.
 */
public class JavaTimeTest {

    @Test
    public void getCurrentTime() {
        LocalDateTime ldt = LocalDateTime.now();
        System.out.println(ldt.toLocalTime());
    }
}
