package engine;

import javax.naming.Context;
import javax.naming.NamingException;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by CSC on 7/30/2014.
 * <p/>
 * TODO : In the constructor check if the object already exists, if not create a new one.
 * TODO : Allow updating of the list and saving it to the entire project.
 * TODO : Test serializable to see if it does save
 * TODO : Getters and Setters for this
 */
public class DisregardDrives extends ArrayList<String> implements Serializable {


    public DisregardDrives() {


    }

    public boolean arrayToArrayList(String[] listOfKeywords) {
        for (String s : listOfKeywords) {
            if (this.contains(s)) {
            } // Don't add something that's already there
            else
                this.add(s);
        }
        if (this.size() != 0)
            return true;
        else
            return false;
    }
}
