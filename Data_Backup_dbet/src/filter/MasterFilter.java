package filter;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by kevin on 1/15/15.
 * <p/>
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
     * @param data -- a String that contains some data that we want to see if it is valuable or not
     * @return -- true if the filter is contained within data, false if no filters were found within the data String.
     */
    public boolean filterSelection(String data) {
        for (String filter : this) {
            if (data.contains(filter))
                return true;
        }

        return false; // nothing stopped the method from reaching this point.

    }

    /**
     * Every child of this class must actually create the list so it CANNOT be empty. There is no point in having an
     * empty filter list.
     */
    public void setupFilterList(String[] array) {
        this.addAll(Arrays.asList(array));
    }
}
