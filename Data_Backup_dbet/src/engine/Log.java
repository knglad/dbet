package engine;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * This object controls all the data we want to store about a single backup. It will allow itself to be saved to both
 * a text file for operator reading and an object to be used by DBET for data backup analysis.
 * <p/>
 * Created by kevin on 3/18/15.
 */
public class Log implements Serializable {

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
     * SERIAL UID HERE!
     */
    final static long serialVersionUID = 1223334444;


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

    public void addToErrorFiles(String errorFilePath) {
        errorFiles.append(errorFilePath + "\n");

    }

    public boolean saveObjectToDestination() {

        try {
            FileOutputStream fos = new FileOutputStream(dataDestination + File.separator + "log.ser");
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

    public void setTextLog(StringBuilder sb) {
        logText = sb;
    }

    public boolean saveTextLog() {

        try {
            PrintWriter pw = new PrintWriter(dataDestination + File.separator + "log.txt");
            pw.println(logText.toString());
            pw.println("Date Started: " + startOfBackupDate);
            pw.println("Time Started: " + startTime);

            if (errorFiles.length() != 0) {
                pw.println("\n\n======== BACKUP ERROR FILES, THESE WERE NOT BACKED UP OR ARE DAMAGED ========");
                pw.println(errorFiles.toString());
            }

            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File not found in saveTextLog");
            return false;
        }


        return true;
    }

    /**
     * Looks through all the drives in DisregardDrives for a log.ser object and grabs it. Loads a copy of the
     * DataDestinationEngine so it doesn't rely on anything to run. This allows us to check all logs without needing
     * a backupEngine object.
     *
     * @return list of all logs found in all storage drives
     */
    public ArrayList<Log> loadAllLogs() {
        // returned, new data structure
        ArrayList<Log> allFoundLogs = new ArrayList<Log>();

        // Maintains the drives that we want to store data to.
        DataDestinationEngine dde = new DataDestinationEngine();

        for (Drive drive : dde.getDriveList()) {

            // Attempt to load all the logs from the various storage positions
            String pathToData = drive.getDataDestination();


            File[] foldersToCheckForLogs = new File(pathToData).listFiles();

            // Now that we have the files for this drives path to the customers data, we need to check
            // each one for a log object
            for (File customerFolder : foldersToCheckForLogs) {

                // Check this customers folder, this array is all the files / folders within.
                File[] innerCustomerFolders = customerFolder.listFiles();


                if (innerCustomerFolders != null) { // makes sure its a folder not a file


                    for (File f : innerCustomerFolders) {

                        if (f.getName().contains("log.ser")) { // This folder contains a lob object
                            Log loadedLog = loadLog(f.getAbsolutePath());

                            if (loadedLog != null) // double check to be sure we actually got it.
                                allFoundLogs.add(loadedLog);
                        }

                    }

                }
            } // END OF CUSTOMERFOLDER LOOP


            // After all that insanity we return what we got. Null if nothing was found.
            return allFoundLogs;
        }


        return null;
    }


    public Log loadLog(String pathToLog) {

        Log loadedLog = null;

        try {
            FileInputStream fis = new FileInputStream(pathToLog);
            ObjectInputStream ois = new ObjectInputStream(fis);
            loadedLog = (Log) ois.readObject();
            ois.close();
            fis.close();

        } catch (FileNotFoundException fnfe) {
            // Can't find the file? Make a default!
            System.out.println("Path did not allow us to load a log!");
            System.out.println("Path given loadLog: " + pathToLog);
            return null;

        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.out.println("IOException on loadLog, could not reach the destination.");
            System.out.println("Path given loadLog: " + pathToLog);

        } catch (ClassNotFoundException c) {
            System.out.println("Class not found exception in loadLog(Path)");
            c.printStackTrace();

        }

        return loadedLog;
    }

}
