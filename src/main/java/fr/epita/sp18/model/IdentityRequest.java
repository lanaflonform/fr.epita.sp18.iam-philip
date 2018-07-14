package fr.epita.sp18.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Define structure of http request body of Identity's API service.
 * <p>
 * A password can be send to the Identity's API server via IdentityRequest
 * object but API server never sends password back to the caller because
 * IdentityResponse does not has Password property. This is the key difference
 * between IdentityRequest and IdentityResponse
 *
 * @author Philip
 *
 */
public class IdentityRequest extends IdentityAbstract
{
    @JsonCreator
    public IdentityRequest(
            @JsonProperty("uid") Long uid,
            @JsonProperty("name") String name,
            @JsonProperty("email") String email,
            @JsonProperty("password") String password)
    {
        super(uid, name, email);
        this.password = password;
    }

    private String password;

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
