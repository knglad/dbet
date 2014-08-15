package engine;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by CSC on 7/30/2014.
 * <p/>
 * This class uses the Singleton design principal, and allows us to store a list of
 * strings that are names of drives we want dbet to disregard or DONT backup. This
 * allows us to have users add or modify the list easily.
 */
public class DisregardDrives extends ArrayList<String> implements Serializable {

    public String fileName = "disregardList"; // The file to be read and loaded

    public DisregardDrives() {
    }

    public boolean addWithoutDuplicates(String keyword) {
        if (this.contains(keyword)) {
            return false;
        } else {
            this.add(keyword);
            return true;
        }
    }

    /**
     * @return true if successfully saved, false if not
     * <p/>
     * When this method is called a file will be saved to the engine folder
     * this file will contain the byte stream of this array list
     * @author Kevin Gladhart
     */
    public boolean saveList() {

        try {
            FileOutputStream fos = new FileOutputStream(fileName);
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

    /**
     * @return returns the object that is read from the file so it can be used by primary engine.
     * <p/>
     * Return the disregardDrives object from the serialized file, then return the object
     * so it can be saved into the program
     * @author Kevin Gladhart
     */
    public DisregardDrives loadList() {

        try {
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            DisregardDrives dd;
            dd = (DisregardDrives) ois.readObject();
            ois.close();
            fis.close();
            return dd;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            System.out.println("Class not found");
            c.printStackTrace();
            return null;
        }

    }

    /**
     * @param newFileName Change the file that will be saved and loaded for this instance
     *                    <p/>
     *                    Used for testing purposes to create a new file and try to load it.
     */
    public void changeFileName(String newFileName) {
        fileName = newFileName;
    }


} // END OF DISREGARD LISTS


