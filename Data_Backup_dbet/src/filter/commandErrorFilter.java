package filter;

/**
 * Created by kevin on 2/13/15.
 * <p>
 * Filter that returns true if an error pattern is recognized and should help reduce the amount of redudant code
 * in runCommand in backupEngine.
 */
public class CommandErrorFilter extends MasterFilter {

    String[] error_patterns = new String[]{
            "error",
            "Operation not supported"
    };


    public CommandErrorFilter() {
        super.setupFilterList(error_patterns);
    }

    @Override
    public boolean specialRules(String data) {
        if (data.contains("usage:")) {
            System.out.println("\"rsync\" command failed to start, improper syntax is likely.");
            return false;
        }

        return true;

    }
}
