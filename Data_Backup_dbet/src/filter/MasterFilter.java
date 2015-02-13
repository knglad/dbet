package filter;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by kevin on 1/15/15.
 *
 * A Filter is essentially an arrayList of String that will be used to filter various things and make updating
 * and or maintaining the filters for all the processes DBET needs.
 */
public abstract class MasterFilter extends ArrayList<String> {

    public MasterFilter() {
    }

    public String[] getFilterArray() {
        return this.toArray(new String[this.size()]);
    }


    /**
     * For every filter contained in this list, check if the data contains any of the filters patterns. In example,
     * we don't want the always there folder 'Library' to be backed up so we have a string that matches its name EXACTLY.
     *
     * @param data -- a String that contains some data that we want to see if it is valuable or not
     * @return -- false if the filter is contained within data, true if no filter patterns were found within the data String.
     */
    public boolean filterSelection(String data) {
        for (String filter : this) {
            if (!specialRules(data))
                return false;
            if (data.contains(filter))
                return false;
        }

        return true; // nothing stopped the method from reaching this point.

    }

    /**
     * Every child of this class must actually create the list so it CANNOT be empty. There is no point in having an
     * empty filter list.
     */
    public void setupFilterList(String[] array) {
        this.addAll(Arrays.asList(array));
    }

    /**
     * Allows each filter to have special rules to apply when filtering, should return TRUE if there are NO special
     * rules to be applied for that filter. In example, we want to see if a file has '.' before it, special rules
     * can then take the string, turn it into a char array and check the first spot if its a '.' without breaking the
     * dynamics of the filter itself.
     *
     * @return false means it fails and should not be used, true means it passes and can be used.
     */
    public abstract boolean specialRules(String data);
}
