package ao.dd.shell.impl.terminal.file;

import ao.dd.shell.def.ShellFile;
import ao.util.data.Arrs;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

/**
 * User: aostrovsky
 * Date: 9-Feb-2010
 * Time: 10:46:49 AM
 */
public class LsRecord
        extends ShellFile
{
    //-------------------------------------------------------------------------
    private static final String[] MONTHS = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};


    //-------------------------------------------------------------------------
    private final String permissions;
    private final int    nHardLinks;
    private final String owner;
    private final String group;


    //-------------------------------------------------------------------------
    public LsRecord(
            String filePermissions,
            String hardLinks,
            String fileOwner,
            String fileGroup,
            String fileSize,
            String month,
            String day,
            String timeOrYear,
            String fileName)
    {
        super(fileName,
              Long.parseLong(fileSize),
              filePermissions.startsWith("d"),
              parseModified(month, day, timeOrYear));
        
        permissions = filePermissions.trim();
        nHardLinks  = Integer.parseInt(hardLinks.trim());
        owner       = fileOwner.trim();
        group       = fileGroup.trim();
    }


    //-------------------------------------------------------------------------
    public String permissions() {
        return permissions;
    }

    public String owner() {
        return owner;
    }

    public String group() {
        return group;
    }

    public int hardLinks() {
        return nHardLinks;
    }


    //-------------------------------------------------------------------------
    private static DateTime parseModified(
            String month, String day, String timeOrYear)
    {
        boolean hasTime  = timeOrYear.contains(":");
        int     yearNum  = (hasTime
                           ? new LocalDate().getYear()
                           : Integer.parseInt(timeOrYear));
        int     monthNum = Arrs.indexOf(MONTHS, month) + 1;
        int     dayNum   = Integer.parseInt(day);
        int     hour     = Integer.parseInt(hasTime
                           ? timeOrYear.substring(0, 2) : "0");
        int     minute   = Integer.parseInt(hasTime
                           ? timeOrYear.substring(3)    : "0");
        return new DateTime(
                yearNum, monthNum, dayNum, hour, minute, 0, 0);
    }
}
