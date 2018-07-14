/**
 *
 */
package fr.epita.sp18.dao;

import java.util.List;

import fr.epita.sp18.exception.IamDataAccessException;
import fr.epita.sp18.exception.IamDataIntegrityViolationException;
import fr.epita.sp18.exception.IamDuplicateKeyException;

/**
 * DAO interface define common methods (name, parameters, errors thrown) that we
 * usually use for an entity
 *
 * @author Philip
 * @param <T>
 *            Type of the entity
 * @param <E>
 *            Type of the entity's primary key
 *
 */
public interface DAO<T, E>
{
    void create(T entity) throws IamDataAccessException, IamDuplicateKeyException;

    void delete(E uid) throws IamDataAccessException, IamDataIntegrityViolationException;

    T get(E uid) throws IamDataAccessException;

    List<T> search(String filter, String sort) throws IamDataAccessException;

    void update(T entity) throws IamDataAccessException, IamDataIntegrityViolationException;

    void update(T entity, String fields) throws IamDataAccessException, IamDataIntegrityViolationException;
}
