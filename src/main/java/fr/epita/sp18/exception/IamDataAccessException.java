package fr.epita.sp18.exception;

/**
 * Thrown when an runtime exception occurred during DAO's operation
 * 
 * @author Philip
 *
 */
public class IamDataAccessException extends Exception
{
    private static final long serialVersionUID = 7718828512143293559L;

    private final ErrorCode code;

    public IamDataAccessException(ErrorCode code)
    {
        super();
        this.code = code;
    }

    public IamDataAccessException(String message, Throwable cause, ErrorCode code)
    {
        super(message, cause);
        this.code = code;
    }

    public IamDataAccessException(String message, ErrorCode code)
    {
        super(message);
        this.code = code;
    }

    public IamDataAccessException(Throwable cause, ErrorCode code)
    {
        super(cause);
        this.code = code;
    }

    public ErrorCode getCode()
    {
        return this.code;
    }
}
