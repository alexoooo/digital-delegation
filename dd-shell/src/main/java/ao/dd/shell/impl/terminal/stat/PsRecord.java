package ao.dd.shell.impl.terminal.stat;

import org.joda.time.LocalTime;

import java.io.Serializable;

/**
 * User: Mable
 * Date: May 17, 2010
 * Time: 10:21:54 PM
 *
 * ps -ef -o pid,ppid,pcpu,vsz,comm
 * while true ; do (ps -ef -o pid,ppid,pcpu,vsz,comm) ; sleep 10 ; done
 */
public class PsRecord
        implements Serializable,
                   Comparable<PsRecord>
{
    //----------------------------------------------------------------------
    private final LocalTime timestamp;
    private final int       pid;
    private final String    ppid;
    private final double    cpu;
    private final long      memory;
    private final String    command;


    //----------------------------------------------------------------------
    public PsRecord(
            String pid,
            String ppid,
            double cpu,
            long   memory,
            String command)
    {
        this.pid     = Integer.valueOf(pid);
        this.ppid    = ppid;
        this.cpu     = cpu;
        this.memory  = memory;
        this.command = command;

        timestamp = new LocalTime();
    }


    //----------------------------------------------------------------------
    public double cpu() {
        return cpu;
    }

    public String command() {
        return command;
    }

    public int pid() {
        return pid;
    }


    //----------------------------------------------------------------------
    public int compareTo(PsRecord o) {
        return -Double.compare(cpu, o.cpu);
    }


    //----------------------------------------------------------------------
    public String toCsv() {
        return timestamp + "," +
               pid       + "," +
               ppid      + "," +
               cpu       + "," +
               memory    + "," +
               command;
    }
}
