package fr.epita.sp18.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Defined values needed for authentication process:
 * <p>
 * String email - Email of the identity
 * <p>
 * String password - Identity's password
 * <p>
 *
 * @author Philip
 *
 */
public class LoginRequest
{
    private String email;

    private String password;

    @JsonCreator
    public LoginRequest(@JsonProperty("email") String email, @JsonProperty("password") String password)
    {
        this.email = email;
        this.password = password;
    }

    public String getEmail()
    {
        return email;
    }

    @JsonSetter
    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }

    @JsonSetter
    public void setPassword(String password)
    {
        this.password = password;
    }
}
