package fr.epita.sp18.model;

/**
 * Define properties that are sharing by both IdentityRequest and
 * IdentityResponse. They are:
 * <p>
 * Long uid - Primary key of the identity
 * <p>
 * String name - Display name of the identity
 * <p>
 * String email - Email of the identity
 * <p>
 *
 * @author Philip
 *
 */
public abstract class IdentityAbstract
{
    public IdentityAbstract(Long uid, String name, String email)
    {
        super();
        this.uid = uid;
        this.name = name;
        this.email = email;
    }

    private Long   uid;
    private String name;
    private String email;

    public Long getUid()
    {
        return uid;
    }

    public void setUid(Long uid)
    {
        this.uid = uid;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    @Override
    public String toString()
    {
        return String.format("uid = %1$s, name = %2$s, email = %3$s",
                this.uid.toString(), this.name, this.email);
    }
}
