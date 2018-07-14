package fr.epita.sp18.exception;

/**
 * Thrown when an data integrity violation occurred during DAO's operation. It
 * happens when updating a doubled value, against unique index restriction.
 *
 * @author Philip
 *
 */
public class IamDataIntegrityViolationException extends Exception
{
    private static final long serialVersionUID = 7718828512143293562L;

    private final ErrorCode code;

    public IamDataIntegrityViolationException(ErrorCode code)
    {
        super();
        this.code = code;
    }

    public IamDataIntegrityViolationException(String message, Throwable cause, ErrorCode code)
    {
        super(message, cause);
        this.code = code;
    }

    public IamDataIntegrityViolationException(String message, ErrorCode code)
    {
        super(message);
        this.code = code;
    }

    public IamDataIntegrityViolationException(Throwable cause, ErrorCode code)
    {
        super(cause);
        this.code = code;
    }

    public ErrorCode getCode()
    {
        return this.code;
    }
}
