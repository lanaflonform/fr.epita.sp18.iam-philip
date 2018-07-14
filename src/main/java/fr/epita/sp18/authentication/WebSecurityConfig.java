package fr.epita.sp18.authentication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Define filtered routes and password encoding method
 *
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{

    private static final Log logger = LogFactory.getLog(WebSecurityConfig.class);

    private UserDetailsService    userDetailsService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public WebSecurityConfig(UserDetailsService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder)
    {
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {

        boolean debug = logger.isDebugEnabled();

        if (debug) {
            // permit H2 database console during development
            http.headers().frameOptions().disable();

            http.csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/index.html", "/", "/favicon.ico", "/*.js", "/*.css").permitAll()
                    .antMatchers(Constants.DEV_PERMIT_URL).permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .addFilter(new JwtUserFilter(authenticationManager()))
                    .addFilter(new JwtAuthenticationFilter(authenticationManager()));
        }
        else {
            http.csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/index.html", "/", "/favicon.ico", "/*.js", "/*.css").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .addFilter(new JwtUserFilter(authenticationManager()))
                    .addFilter(new JwtAuthenticationFilter(authenticationManager()));
        }
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception
    {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }
}
