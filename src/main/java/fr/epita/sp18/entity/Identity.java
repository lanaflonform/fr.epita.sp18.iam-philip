package fr.epita.sp18.entity;

/**
 * Define the Identity class, that keep all identity's information of the
 * IAM-Project. The Identity properties are:
 * <p>
 * Long uid - Primary key of the identity
 * <p>
 * String name - Display name of the identity
 * <p>
 * String email - Email of the identity. Unique value, used for authentication
 * service
 * <p>
 * String normalizedEmail - Email of the identity in upper case. Unique value,
 * used for searching the identity by email
 * <p>
 * String passwordHash - Password of the identity, encode by
 * BCryptPasswordEncoder
 *
 * @author Philip
 *
 */
public class Identity
{
    private Long   uid;
    private String name;
    private String email;
    private String normalizedEmail;
    private String passwordHash;

    public Identity()
    {
    }

    /**
     * Initialize an identity
     *
     * @param uid
     *            Identity's unique id
     * @param name
     *            Identity's display name
     * @param email
     *            Identity's email
     * @param normalizedEmail
     *            Identity's email in upper case
     * @param passwordHash
     *            The password that already encoded
     */
    public Identity(Long uid, String name, String email, String normalizedEmail, String passwordHash)
    {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.normalizedEmail = normalizedEmail;
        this.passwordHash = passwordHash;
    }

    /**
     * @return the email
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the normalizedEmail
     */
    public String getNormalizedEmail()
    {
        return normalizedEmail;
    }

    /**
     * @return the passwordHash
     */
    public String getPasswordHash()
    {
        return passwordHash;
    }

    /**
     * @return the uid
     */
    public Long getUid()
    {
        return uid;
    }

    /**
     * @param email
     *            the email to set
     */
    public void setEmail(String email)
    {
        this.email = email != null ? email.trim() : null;
        normalizedEmail = email != null ? email.trim().toUpperCase() : null;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @param normalizedEmail
     *            the normalizedEmail to set
     */
    public void setNormalizedEmail(String normalizedEmail)
    {
        this.normalizedEmail = normalizedEmail;
    }

    /**
     * @param passwordHash
     *            the passwordHash to set
     */
    public void setPasswordHash(String passwordHash)
    {
        this.passwordHash = passwordHash;
    }

    /**
     * @param uid
     *            the uid to set
     */
    public void setUid(Long uid)
    {
        this.uid = uid;
    }

    /**
     * Customized toString() method
     */
    @Override
    public String toString()
    {
        return String.format("uid = %1$s, name = %2$s, email = %3$s, passwordHash = %4$s",
                this.uid.toString(), this.name, this.email, this.passwordHash);
    }
}
