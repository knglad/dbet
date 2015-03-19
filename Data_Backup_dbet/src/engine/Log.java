package engine;

/**
 * This object controls all the data we want to store about a single backup. It will allow itself to be saved to both
 * a text file for operator reading and an object to be used by DBET for data backup analysis.
 * <p/>
 * Created by kevin on 3/18/15.
 */
public class Log {

    public String fileName; // The current file we are / will be working with
    public StringBuilder logText; // The text of the log itself, this is for the text file
    public String dataDestination; // where the data was sent to originally

    // Errors and size
    public StringBuilder errorFiles; // When a file errors, we save its path from the backup command into this.
    public int errors; // total number of errors that occurred in the backup
    public double totalBackupSize;
    public double percentError;

    // Time reporting
    public int hoursItTook;
    public int minutesRemaining;


    /**
     *
     */
    public Log() {
        // TODO :: Make Log amazingly easy to use
    }


}
