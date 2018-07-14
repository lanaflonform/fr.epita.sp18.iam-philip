/**
 *
 */
package fr.epita.sp18.dao;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.epita.sp18.exception.ErrorCode;
import fr.epita.sp18.exception.IamDataAccessException;
import fr.epita.sp18.exception.IamDataIntegrityViolationException;
import fr.epita.sp18.exception.IamDuplicateKeyException;
import fr.epita.sp18.exception.IamUnsupportedDataTypeException;

/**
 * BaseJdbcDAO is an abstract class with ready-to-used methods that implements
 * DAO interface An entity that implements DAO interface just simply extends
 * this class as DAO's implementation
 *
 * @author Philip
 *
 */
@Repository
public abstract class BaseJdbcDAO<T, E>
{
    private static AtomicLong nextUID = new AtomicLong(System.currentTimeMillis());

    private final String   table;
    private final String   primaryKey;
    private final Class<T> classT;

    private static final String DATA_ACCESS_ERROR_MESSAGE = "SQL execution error: %1$s";

    @Autowired
    JdbcTemplate jdbc;

    /**
     * This constructor helps define the table that the DAO is working on
     *
     * @param tableName
     *            Name of the table that is stored in database
     * @param pkName
     *            Name of the table's primary key. Primary key type can be String or
     *            any Java's primitives data type
     * @param clazz
     *            Definition of the table
     */
    public BaseJdbcDAO(String tableName, String pkName, Class<T> clazz)
    {
        table = tableName;
        primaryKey = pkName;
        classT = clazz;
    }

    /**
     * This method query the table for a unique record that has primary key equal to
     * the parameter value
     *
     * @param uid
     *            Primary key of the expecting record
     * @return the record if found or null when not found
     * @throws IamDataAccessException
     *             when there is any exception occurs during the DAO's execution
     */
    public T get(E uid) throws IamDataAccessException
    {
        T result = null;
        String sql = "";

        try {
            sql = String.format("SELECT * FROM %1$s WHERE %2$s = %3$s;", table, primaryKey, uid);

            result = jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(classT));
        }
        catch (final EmptyResultDataAccessException ex) {
            // No record found. Return null
        }
        catch (final DataAccessException ex) {
            // Read database error. Throw exception
            throw new IamDataAccessException(String.format(DATA_ACCESS_ERROR_MESSAGE, sql), ex,
                    ErrorCode.DATA_ACCESS_ERROR);
        }

        return result;
    }

    /**
     * This method generate a new primary key for the inserting record and insert it
     * into the table
     *
     * @param entity
     *            The inserting record
     * @throws IamDuplicateKeyException
     *             when the inserting record has field(s) with duplicated value,
     *             against the table's unique index restrictions
     * @throws IamDataAccessException
     *             when an exception occurred during the insertion
     */
    public void create(T entity) throws IamDuplicateKeyException, IamDataAccessException
    {
        String sql = "";

        try {
            // Generate an UID if it's not ready yet
            setPrimaryKeyValue(entity);

            String[] list = getFieldList(entity, true);
            String[] values = new String[list.length];
            Arrays.fill(values, "?");

            // "INSERT INTO table_name(column_name, ...) VALUES(value1, ...);"
            sql = String.format("INSERT INTO %1$s(%2$s) VALUES(%3$s);", table,
                    String.join(", ", list),
                    String.join(", ", values));

            runJdbcUpdate(entity, sql, list);
        }
        catch (final DuplicateKeyException ex) {
            // Duplicated record
            throw new IamDuplicateKeyException(String.format(DATA_ACCESS_ERROR_MESSAGE, sql), ex,
                    ErrorCode.DUPLICATE_KEY_ERROR);
        }
        catch (final DataAccessException ex) {
            // Read database error
            throw new IamDataAccessException(String.format(DATA_ACCESS_ERROR_MESSAGE, sql), ex,
                    ErrorCode.DATA_ACCESS_ERROR);
        }
    }

    private void runJdbcUpdate(T entity, String sql, String[] fields)
    {
        jdbc.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException
            {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.NO_GENERATED_KEYS);

                // Inject the entity's value into the SQL statement
                int i = 1;
                final Map<String, Object> map = getMap(entity);

                for (String field : fields) {
                    ps.setObject(i++, map.get(field));
                }

                return ps;
            }
        });
    }

    // Convert the object to Map<String, Object> by Jacson library
    private Map<String, Object> getMap(T entity)
    {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(entity, new TypeReference<Map<String, Object>>() {
        });
    }

    private String getPrimaryKeyStringValue(T entity) throws IamDataAccessException
    {
        String result = "";

        try {
            final Method fieldGetter = entity.getClass()
                    .getMethod("get" + primaryKey.substring(0, 1).toUpperCase() + primaryKey.substring(1));
            result = fieldGetter.invoke(entity).toString();
        }
        catch (final Exception ex) {
            throw new IamDataAccessException("BaseJdbcDAO.getPrimaryKeyValue error", ex,
                    ErrorCode.GET_PRIMARY_KEY_ERROR);
        }

        return result;
    }

    private void setPrimaryKeyValue(T entity) throws IamDataAccessException
    {
        try {
            final Field field = entity.getClass().getDeclaredField(primaryKey);
            final Object id = transformLongToE(nextUID.getAndIncrement(), field.getType());
            field.setAccessible(true);
            field.set(entity, id);
        }
        catch (final Exception ex) {
            throw new IamDataAccessException("BaseJdbcDAO.setPrimaryKeyValue error", ex,
                    ErrorCode.SET_PRIMARY_KEY_ERROR);
        }
    }

    private Object transformLongToE(Long value, Class<?> toClass) throws IamUnsupportedDataTypeException
    {
        if (toClass.isAssignableFrom(value.getClass()))
            return toClass.cast(value);
        else if (toClass.isAssignableFrom(String.class)) return value.toString();

        throw new IamUnsupportedDataTypeException(
                String.format("BaseJdbcDAO.transformLongToE data type %1$s is unsupported", toClass.getName()),
                ErrorCode.UNSUPPORTED_DATA_TYPE);
    }

    private String[] getFieldList(T entity, boolean includePK)
    {
        final Map<String, Object> map = getMap(entity);

        String[] list = new String[map.size() + (includePK ? 0 : -1)];
        int i = 0;

        for (final Map.Entry<String, Object> entry : map.entrySet()) {
            if (!includePK && primaryKey.equals(entry.getKey())) {
                continue;
            }
            list[i++] = entry.getKey();
        }

        return list;
    }

    /**
     * This method update a record with new value, basing on its primary key
     *
     * @param entity
     *            The updating record
     * @throws IamDataIntegrityViolationException
     *             when the updating record has field(s) with duplicated value,
     *             against the table's unique index restrictions
     * @throws IamDataAccessException
     *             when an exception occurred during the updating
     */
    public void update(T entity) throws IamDataIntegrityViolationException, IamDataAccessException
    {
        String[] list = getFieldList(entity, false);

        updateEntity(entity, list);
    }

    /**
     * This method update some fields of a record with new value, basing on its
     * primary key and field list defined in parameter
     *
     * @param entity
     *            The updating record
     * @param fields
     *            The field list that need to be updated, in String format. Example:
     *            "name, email"
     * @throws IamDataIntegrityViolationException
     *             when the updating record has field(s) with duplicated value,
     *             against the table's unique index restrictions
     * @throws IamDataAccessException
     *             when an exception occurred during the updating
     */
    public void update(T entity, String fields) throws IamDataIntegrityViolationException, IamDataAccessException
    {
        String[] list = fields.trim().split("\\s*,\\s*");

        updateEntity(entity, list);
    }

    private void updateEntity(T entity, String[] fields)
            throws IamDataAccessException, IamDataIntegrityViolationException
    {
        String sql = "";
        try {
            final String whereClause = String.format("%1$s = %2$s", primaryKey, getPrimaryKeyStringValue(entity));

            // "UPDATE table_name SET column1 = ?, column2 = ? ... WHERE primary_key = id;"
            sql = String.format("UPDATE %1$s SET %2$s WHERE %3$s;", table, String.join(" = ?, ", fields) + " =? ",
                    whereClause);

            runJdbcUpdate(entity, sql, fields);
        }
        catch (final DataIntegrityViolationException ex) {
            // No record found. Return null
            throw new IamDataIntegrityViolationException(String.format(DATA_ACCESS_ERROR_MESSAGE, sql), ex,
                    ErrorCode.DATA_INTEGRITY_VIOLATION);
        }
        catch (final DataAccessException ex) {
            // Read database error. Throw exception
            throw new IamDataAccessException(String.format(DATA_ACCESS_ERROR_MESSAGE, sql), ex,
                    ErrorCode.DATA_ACCESS_ERROR);
        }
    }

    /**
     * This method query the table with "WHERE clause" and "ORDER BY clause" defined
     * by parameters
     *
     * @param filter
     *            The WHERE clause in String format
     * @param sort
     *            The ORDER BY clause in String format
     *
     * @return List of found object or null when not found
     *
     * @throws IamDataAccessException
     *             when an exception occurred during the search
     */
    public List<T> search(String filter, String sort) throws IamDataAccessException
    {
        List<T> result = null;
        String sql = "";

        try {
            filter = ((filter != null) && !filter.isEmpty()) ? "WHERE " + filter : "";
            sort = ((sort != null) && !sort.isEmpty()) ? "ORDER BY " + sort : "";

            sql = String.format("SELECT * FROM %1$s %2$s %3$s;", table, filter, sort);

            result = jdbc.query(sql, new BeanPropertyRowMapper<>(classT));
        }
        catch (final EmptyResultDataAccessException ex) {
            // No record found. Return null
        }
        catch (final DataAccessException ex) {
            // Read database error. Throw exception
            throw new IamDataAccessException(String.format(DATA_ACCESS_ERROR_MESSAGE, sql), ex,
                    ErrorCode.DATA_ACCESS_ERROR);
        }

        return result;
    }

    /**
     * This method delete a record in table, basing on its primary key defined by
     * parameter
     *
     * @param uid
     *            Primary key of the to be deleted record
     * @throws IamDataAccessException
     *             when an exception occurred during the deletion
     */
    public void delete(E uid) throws IamDataAccessException
    {
        String sql = "";

        try {
            sql = String.format("DELETE FROM %1$s WHERE %2$s = %3$s;", table, primaryKey, uid);

            jdbc.execute(sql);
        }
        catch (final EmptyResultDataAccessException ex) {
            // No record found
        }
        catch (final DataAccessException ex) {
            // Delete error. Throw exception
            throw new IamDataAccessException(String.format(DATA_ACCESS_ERROR_MESSAGE, sql), ex,
                    ErrorCode.DATA_ACCESS_ERROR);
        }
    }
}
