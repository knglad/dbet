package engine;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by CSC on 7/30/2014.
 *
 * This class uses the Singleton design principal, and allows us to store a list of
 * strings that are names of drives we want dbet to disregard or DONT backup. This
 * allows us to have users add or modify the list easily.
 */
public class DisregardDrives extends ArrayList<String> implements Serializable {

    public String fileName = "disregardList"; // The file to be read and loaded
    public DisregardDrives dd;
    private boolean similar_name_dont_backup = false;
    // any drive matching these patterns wont be backed up
    private String[] disregardKeywords = {"LM PNP", "WD SmartWare", "gimp", "Storage", "Adobe", "KeePass", ".DS", "Data Drive"};




    public DisregardDrives() {}


    public boolean addWithoutDuplicates(String keyword) {
        if (this.contains(keyword)) {
            System.out.println("Failed to add " + keyword + "DUPLICATE ERROR!");
            return false;
        } else {
            this.add(keyword);
            return true;
        }
    }

    /**
     * @return true if successfully saved, false if not
     *
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
     *
     * Return the disregardDrives object from the serialized file, then return the object
     * so it can be saved into the program. Will create a default list from the array disregard
     * keywords if nothing is found.
     * @author Kevin Gladhart
     */
    public void loadList() {

        try {
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            dd = (DisregardDrives) ois.readObject();
            this.addAll(dd); // Takes the contents of dd and adds it to this single object.
            ois.close();
            fis.close();

        } catch (FileNotFoundException fnfe){
            // Can't find the file? Make a default!
             this.createDefaultList();

        } catch (IOException ioe) {
            ioe.printStackTrace();

        } catch (ClassNotFoundException c) {
            System.out.println("Class not found");
            c.printStackTrace();

        }

    }

    /**
     * @param newFileName Change the file that will be saved and loaded for this instance
     *
     *                    Used for testing purposes to create a new file and try to load it.
     */
    public void changeFileName(String newFileName) {
        fileName = newFileName;
    }

    /**
     * @return A disregardDrives object that is the default object with no customizations.
     *
     * Should loadList() fail to find a file (it was deleted or otherwise moved out of scope),
     * this method uses a default hard coded array to create a new one.
     */
    public void createDefaultList(){

        for(String s : disregardKeywords){
            this.addWithoutDuplicates(s);
        }

    }


    /**
     * @param fi - a single file containing a drives information
     * @return true if it should be backed up, false if not
     *
     * Compares the getName() string of the file to our dd list.
     */
    public boolean shouldBeBackedup(File fi) {
        String name = fi.getName();
        name = name.toLowerCase();


        for (String keyword : this) {
            keyword = keyword.toLowerCase();
            if (name.contains(keyword)) {
                return false;
            }
        }
        if (similar_name_dont_backup == false) {
            if (name.contains("macintosh hd")) { // EVERYTHING MUST BE LOWERCASE!!!!
                similar_name_dont_backup = true;
                return false;
            }
        }
        // The drive does NOT contain any of the keywords in this list, it can be used to backup.
        return true;

    }

    // TODO : Debug mode to go through list of drives we've disregarded, give the option to possibly remove from that list (human error)

} // END OF DISREGARD LISTS


