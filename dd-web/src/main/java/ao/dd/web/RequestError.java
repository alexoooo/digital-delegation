package ao.dd.web;

/**
 *
 */
public class RequestError extends Error
{
	private static final long serialVersionUID = -4380783420726140169L;

	public RequestError(String message)
    {
        super( message );
    }

    public RequestError(Throwable cause)
    {
        super( cause );
    }
}
