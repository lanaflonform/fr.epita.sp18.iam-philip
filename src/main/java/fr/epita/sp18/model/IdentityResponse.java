package fr.epita.sp18.model;

/**
 * Define which identity's data can be sent to API caller.
 *
 * @author Philip
 *
 */
public class IdentityResponse extends IdentityAbstract
{
    public IdentityResponse(Long uid, String name, String email)
    {
        super(uid, name, email);
    }
}
