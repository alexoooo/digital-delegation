package ao.dd.shell.impl.transfer.stream;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CountingOutputStream
		extends FilterOutputStream
{
	//--------------------------------------------------------------------
	private long totalWritten;


    //--------------------------------------------------------------------
	public CountingOutputStream(OutputStream in) {
		super(in);
	}


    //--------------------------------------------------------------------
    @Override public void write(
            byte[] b) throws IOException
    {
        super.write(b);
        totalWritten += b.length;
    }

    @Override public void write(
            byte[] b, int off, int len) throws IOException
    {
        super.write(b, off, len);
        totalWritten += len;
    }

    @Override public void write(
            int b) throws IOException
    {
        super.write( b );
        totalWritten++;
    }


	//--------------------------------------------------------------------
	public long totalWritten() {
		return totalWritten;
	}
}