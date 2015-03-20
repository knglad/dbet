package engine;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;

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
    public long hoursItTook;
    public long minutesRemaining;
    public LocalTime startTime;
    public LocalDate startOfBackupDate;



    /**
     * sans-argument constructor so the class that is trying to get the log can
     * decide how it works. Currently we inititate it at the time just before
     * we begin backup, and set the initial time. Then as the backup is running
     * we append the error'd files' paths to the errorFiles string. Then once
     * showStats runs we can create the log and complete the object. Once thats done
     * we can save the obj to a file in the same destination as the data. We can also create
     * a text file for users to read manually.
     *
     */
    public Log() {
        logText = new StringBuilder();
        errorFiles = new StringBuilder();
        startOfBackupDate = LocalDate.now();
    }


    /**
     * @param backupEngine -- all stat variables are public in this, allows ad hoc saving of
     *                     those variables into this log, before they get changed with the
     *                     next backup
     * @return boolean, true if completed successfully and false if something went wrong.
     */
    public void createLog(BackupEngine backupEngine) {
        dataDestination = backupEngine.destination;
        errors = backupEngine.errorCounter;
        totalBackupSize = backupEngine.totalBackupSize;
        percentError = backupEngine.percentError;
        hoursItTook = backupEngine.hoursItTook;
        minutesRemaining = backupEngine.minutesRemaining;
    }



    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void addToErrorFiles(String errorFilePath) {
        errorFiles.append(errorFilePath + "\n");

    }

    public boolean saveObjectToDestination() {

        try {
            FileOutputStream fos = new FileOutputStream(dataDestination + File.separator + fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            fos.close();
            return true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
