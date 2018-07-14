package fr.epita.sp18.model;

/**
 * Define a wrapper that can cover both data and message that need to be sent to
 * API's caller via http response body. ApiResponse's properties are:
 * <p>
 * T model - Response's data
 * <p>
 * String message - Message sending from server to API's caller
 * <p>
 * boolean hasError - There is error when processing the API request
 * <p>
 * String errorMessage - Explanation of the error
 * <p>
 *
 * @author Philip
 *
 * @param <T>
 *            Type of replied data
 */
public class ApiResponse<T>
{
    private T       model;
    private String  message;
    private boolean hasError;
    private String  errorMessage;

    public ApiResponse()
    {
        model = null;
        message = "";
        hasError = false;
        errorMessage = "";
    }

    public T getModel()
    {
        return model;
    }

    public void setModel(T model)
    {
        this.model = model;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public boolean getHasError()
    {
        return hasError;
    }

    public void setHasError(boolean hasError)
    {
        this.hasError = hasError;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }
}
