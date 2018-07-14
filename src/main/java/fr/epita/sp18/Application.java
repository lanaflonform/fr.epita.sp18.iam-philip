package fr.epita.sp18;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Starting point of IAM-Project application.
 * <p>
 * Initialized by Spring Boot 2.0.3
 *
 * @author Philip
 *
 */
@EnableAutoConfiguration
@ComponentScan({
        "fr.epita.sp18",
        "fr.epita.sp18.config",
        "fr.epita.sp18.dao",
        "fr.epita.sp18.entity",
        "fr.epita.sp18.rest",
        "fr.epita.sp18.service"
})
@SpringBootApplication
public class Application
{

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }
}
