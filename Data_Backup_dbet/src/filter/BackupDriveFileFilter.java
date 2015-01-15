package filter;

/**
 * Created by kevin on 1/15/15.
 */
public class BackupDriveFileFilter extends MasterFilter {

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
            "mach_kernel"
    };


    public BackupDriveFileFilter() {
        super.setupFilterList(foldersAndFilesWeDontWant);
    }


}
