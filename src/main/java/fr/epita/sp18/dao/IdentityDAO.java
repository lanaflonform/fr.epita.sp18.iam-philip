/**
 *
 */
package fr.epita.sp18.dao;

import org.springframework.stereotype.Component;

import fr.epita.sp18.entity.Identity;

/**
 * This class implements DAO interface by extending the BaseJdbcDAO, so that it
 * can perform CRUD operation on Identity table
 *
 * @author Philip
 *
 */
@Component
public class IdentityDAO extends BaseJdbcDAO<Identity, Long> implements DAO<Identity, Long>
{
    /**
     * Construct IdentityDAO class with table name is "Identities" and primary key
     * is "uid"
     */
    public IdentityDAO()
    {
        super("Identities", "uid", Identity.class);
    }
}
