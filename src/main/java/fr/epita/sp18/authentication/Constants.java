package fr.epita.sp18.authentication;

/**
 * Constant values for Authentication JWT methods
 *
 */
public class Constants
{
    private Constants()
    {
    }

    public static final String SECRET          = "ThisIsNotASecretKey";
    public static final long   EXPIRATION_TIME = 86_400_000;           // 1 days
    public static final String TOKEN_PREFIX    = "Bearer ";
    public static final String HEADER_STRING   = "Authorization";

    /**
     * Allow h2 console can be accessed under debug mode
     */
    public static final String DEV_PERMIT_URL = "/h2/**";
}
