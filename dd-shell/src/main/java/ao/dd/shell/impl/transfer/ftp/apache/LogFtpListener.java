package ao.dd.shell.impl.transfer.ftp.apache;

import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.log4j.Logger;

public class LogFtpListener implements ProtocolCommandListener
{
	//--------------------------------------------------------------------
	private final Logger log;
	
	
	//--------------------------------------------------------------------
	public LogFtpListener(Logger onBehalfOf)
	{
		log = onBehalfOf;
	}
	
	
	//--------------------------------------------------------------------
	@Override
	public void protocolCommandSent(
			ProtocolCommandEvent event) {
		log.trace("SENT: " + event.getMessage());
	}

	@Override
	public void protocolReplyReceived(
			ProtocolCommandEvent event) {
		log.trace("RECEIVED: " + event.getMessage());
	}
}
