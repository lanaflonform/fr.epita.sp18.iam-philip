package fr.epita.sp18.authentication;

import static java.util.Collections.emptyList;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import fr.epita.sp18.entity.Identity;
import fr.epita.sp18.service.IdentityService;

/**
 * Get identity by email to support Spring's authentication method
 *
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService
{
    private IdentityService identityService;

    public UserDetailsServiceImpl(IdentityService identityService)
    {
        this.identityService = identityService;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
    {
        Identity identity = identityService.findByEmail(username);

        if (identity == null) throw new UsernameNotFoundException(username);

        return new User(identity.getEmail(), identity.getPasswordHash(), emptyList());
    }
}
