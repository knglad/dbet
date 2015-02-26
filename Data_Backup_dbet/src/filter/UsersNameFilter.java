package filter;

/**
 * Created by kevin on 2/26/15.
 */
public class UsersNameFilter extends MasterFilter {

    private String[] userNamesWeNeverCareAbout = new String[]{
            "Shared",
            "Applications",
    };

    public UsersNameFilter() {
        super.setupFilterList(userNamesWeNeverCareAbout);
    }

    @Override
    public boolean specialRules(String data) {
        if (data.toCharArray()[0] == '.')
            return false;
        if (data.equals(""))
            return false;
        else
            return true;

    }
}
