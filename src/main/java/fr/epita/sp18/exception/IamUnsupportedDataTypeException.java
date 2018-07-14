package fr.epita.sp18.exception;

/**
 * Thrown when a newly generated primary key can not be casted to table's
 * primary key object type. Currently IAM DAO support all Java's primitives data
 * type and String
 *
 * @author Philip
 *
 */
public class IamUnsupportedDataTypeException extends Exception
{
    private static final long serialVersionUID = 7718828512143293560L;

    private final ErrorCode code;

    public IamUnsupportedDataTypeException(ErrorCode code)
    {
        super();
        this.code = code;
    }

    public IamUnsupportedDataTypeException(String message, Throwable cause, ErrorCode code)
    {
        super(message, cause);
        this.code = code;
    }

    public IamUnsupportedDataTypeException(String message, ErrorCode code)
    {
        super(message);
        this.code = code;
    }

    public IamUnsupportedDataTypeException(Throwable cause, ErrorCode code)
    {
        super(cause);
        this.code = code;
    }

    public ErrorCode getCode()
    {
        return this.code;
    }
}
