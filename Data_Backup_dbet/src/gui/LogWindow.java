package gui;

import javax.swing.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Kevin Gladhart
 *         <p/>
 *         This window will show text as files are backed up or as
 *         errors occur will show them, this also creates a log text file
 *         for each event and saves it in the users folder that is being backed up.
 */
class LogWindow extends JPanel {

    // Dimensions in pixels
    public int height = 120;
    public int width = 240;

    // Position within the parent JFrame (created in main method)
    public int positionX = 0;
    public int positionY = 0;

    // The data for this window
    String logText = "";

    public LogWindow() {

    }


    /**
     * @param str - The string to add to the window
     *            <p/>
     *            Takes the login window and adds some text without deleting what was there prior,
     *            adds a new line to each argument.
     */
    public void appendText(String str) {
        logText += "\n" + str;
    }

    public void clearText() {
        logText = "";
    }


    public String saveText() {
        String fileName = "Backup ";

        // Format for the date string
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        // The actual date object
        Date date = new Date();
        fileName += dateFormat.format(date);


        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(logText);
            oos.close();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return logText;
    }


}
