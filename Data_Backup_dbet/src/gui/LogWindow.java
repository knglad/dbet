package gui;

import javax.swing.*;

/**
 * @author Kevin Gladhart
 *         <p/>
 *         This window will show text as files are backed up or as
 *         errors occur will show them. Several panels are held within that store
 *         the current file we are working on, the progress of that file, the files remaining
 *         and the last error the backup encountered
 */
public class LogWindow extends JPanel {

    // Dimensions in pixels
    public int height = 120;
    public int width = 240;

    // Position within the parent JFrame (created in main method)
    public int positionX = 0;
    public int positionY = 0;

    // The data for this window

    // Current file we are working on
    public String currentFile = "";
    // rsync has a progress line to show us how its going, this will be a single line updated every few seconds.
    public String fileProgressLine = "";
    // if an error is reported, show the text to the user
    public String latestError = ""; // This contains any files that may have failed to transfer


    public LogWindow() {
        // Make the overall window
        makeOverallPanel();
        // Make each internal text area that fits within the panel

    }

    protected void clearText() {
        currentFile = "";
        fileProgressLine = "";
        latestError = "";
    }


    public void makeOverallPanel() {
        this.setSize(width, height);
        this.setLocation(positionX, positionY);
    }
}
