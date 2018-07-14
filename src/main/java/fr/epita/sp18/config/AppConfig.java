/**
 *
 */
package fr.epita.sp18.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * App's dynamic configuration
 *
 * @author Philip
 *
 */
@Configuration
@PropertySource(value = { "classpath:application.properties" })
public class AppConfig
{
}
