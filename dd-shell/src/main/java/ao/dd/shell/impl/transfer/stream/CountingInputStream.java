package ao.dd.shell.impl.transfer.stream;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CountingInputStream
		extends FilterInputStream
{
	//--------------------------------------------------------------------
	private long totalRead;
	
	
	//--------------------------------------------------------------------
	public CountingInputStream(InputStream in) {
		super(in);
	}

	
	//--------------------------------------------------------------------
	@Override public int read() throws IOException {
		int byteVal = super.read();
		if (byteVal != -1) {
			totalRead++;
		}
		return byteVal;
    }
	
	@Override public int read(byte b[])
			throws IOException {
		int nRead = super.read(b);
		if (nRead != -1) {
			totalRead += nRead;
		}
		return nRead;
    }
	
	@Override public int read(byte b[], int off, int len)
			throws IOException {
		int nRead = super.read(b, off, len);
		if (nRead != -1) {
			totalRead += nRead;
		}
		return nRead;
    }
	
	
	//--------------------------------------------------------------------
	public long totalRead() {
		return totalRead;
	}
}
