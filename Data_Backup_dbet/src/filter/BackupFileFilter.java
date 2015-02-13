package filter;

/**
 * Created by kevin on 1/15/15.
 */
public class BackupFileFilter extends MasterFilter {

    private String[] foldersAndFilesWeDontWant = new String[]{
            "Library",
            "System",
            "Volumes",
            "Yose Life Image",
            "Incompatible Software",
            "private",
            "sbin",
            "net",
            "usr",
            "var",
            "tmp",
            "cores",
            "bin",
            "Network",
            "dev",
            "etc",
            "home",
            "mach_kernel",
            "Applications" // Applications mostly fail when backed up.
    };


    public BackupFileFilter() {
        super.setupFilterList(foldersAndFilesWeDontWant);
    }


    /**
     * @param data
     * @return Checks to see if a file contains '.' before the name, such as '.vol' or '.Trashes'
     */
    public boolean specialRules(String data) {
        if (data.toCharArray()[0] == '.')
            return false;
        else if (data.contains(".failurerequests"))
            return false;
        else
            return true;
    }


}
