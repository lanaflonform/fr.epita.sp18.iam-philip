package fr.epita.sp18.exception;

/**
 * Thrown when an data integrity violation occurred during DAO's operation. It
 * happens when inserting a doubled value, against unique index restriction.
 * 
 * @author Philip
 *
 */
public class IamDuplicateKeyException extends Exception
{
    private static final long serialVersionUID = 7718828512143293561L;

    private final ErrorCode code;

    public IamDuplicateKeyException(ErrorCode code)
    {
        super();
        this.code = code;
    }

    public IamDuplicateKeyException(String message, Throwable cause, ErrorCode code)
    {
        super(message, cause);
        this.code = code;
    }

    public IamDuplicateKeyException(String message, ErrorCode code)
    {
        super(message);
        this.code = code;
    }

    public IamDuplicateKeyException(Throwable cause, ErrorCode code)
    {
        super(cause);
        this.code = code;
    }

    public ErrorCode getCode()
    {
        return this.code;
    }
}
