package ao.dd.shell.impl.terminal.data;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;

/**
 * User: aostrovsky
 * Date: 27-Jan-2010
 * Time: 12:18:54 PM
 *
 * See http://www.cs.utsa.edu/~wagner/CS2213/queue/queue.html
 */
public class FiniteQueue
{
    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        FiniteQueue buff = new FiniteQueue(3);

        System.out.println(buff);
        for (byte i = 0; i < 10; i++) {
            buff.add( i );
            System.out.println(buff);

            if (buff.equals(new byte[]{4, 5, 6})) {
                System.out.println("!!!");
            }
        }
    }


    //--------------------------------------------------------------------
    private final int    maxSize;
    private final byte[] queue;

    private int size  = 0;
    private int front = 0;
    private int rear  = 0;


    //--------------------------------------------------------------------
    public FiniteQueue(int size)
    {
        queue = new byte[ size ];
        maxSize = size;
    }


    //--------------------------------------------------------------------
    public boolean equals(byte[] values)
    {
        if (size != values.length) return false;

        for (int i = 0, p = front;
                 i < size;
                 i++, p = advanceIndex(p))
        {
            if (queue[p] != values[ i ]) {
                return false;
            }
        }

        return true;
    }

    public boolean endsWith(byte[] values)
    {
        if (size < values.length) return false;

        for (int i = 0, p = advanceIndex(
                              front, size - values.length);
                 i < values.length;
                 i++, p = advanceIndex(p))
        {
            if (queue[p] != values[ i ]) {
                return false;
            }
        }

        return true;
    }


    //--------------------------------------------------------------------
    public int addDestructive(byte value)
    {
        int val = -1;
        if (isFull())
        {
            val = removeEldest();
        }
        add(value);
        return val;
    }

    public void add(byte value)
    {
        if (isFull())
        {
            throw new BufferOverflowException();
        }

        size++;
        queue[rear] = value;
        rear = advanceIndex(rear);
    }

    public byte removeEldest()
    {
        if (! isEmpty())
        {
            size--;
            byte val = queue[front];
            front = advanceIndex(front);
            return val;
        }
        else
        {
            throw new BufferUnderflowException();
        }
    }
    

    //--------------------------------------------------------------------
    public boolean isFull()
    {
        return size == maxSize;
    }

    public boolean isEmpty()
    {
        return size == 0;
    }

    public int size()
    {
        return size;
    }


    //--------------------------------------------------------------------
    private int advanceIndex(int index)
    {
        return (index + 1) % maxSize;
    }

    private int advanceIndex(
            int index, int nTimes)
    {
        return (index + nTimes) % maxSize;
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();

        for (int i = 0, p = front;
                 i < size;
                 i++, p = advanceIndex(p))
        {
            str.append( (char) queue[p] );
        }

        return str.toString();
    }
}
