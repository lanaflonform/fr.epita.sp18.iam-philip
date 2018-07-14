/**
 *
 */
package fr.epita.sp18.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import fr.epita.sp18.dao.IdentityDAO;
import fr.epita.sp18.entity.Identity;
import fr.epita.sp18.exception.IamDataIntegrityViolationException;
import fr.epita.sp18.exception.IamDuplicateKeyException;
import fr.epita.sp18.model.ApiResponse;
import fr.epita.sp18.model.IdentityRequest;
import fr.epita.sp18.model.IdentityResponse;

/**
 * IdentityService is a layer between the API controller and IdentityDAO
 * service. It convert API's request to DAO's method parameter and translate
 * DAO's reply into API's response format. The service also translate DAO's
 * errors into user's readable text messages.
 * <p>
 * This service return an ApiReponse object with:
 * <p>
 * - ApiReponse.model = DAO's result.
 * <p>
 * - ApiResponse.hasError = true when any Exceptional error occurred.
 * <p>
 * - ApiResponse.errorMessage = error explanation if any
 *
 * @author Philip
 *
 */
@Service
public class IdentityService implements BaseService
{
    private static final Logger   logger = LogManager.getLogger("IdentityService");
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Construct the IdentityService with an BCryptPasswordEncoder to hash
     * identity's raw password receiving from controller
     *
     * @param bCryptPasswordEncoder
     *            The encoder for raw password
     */
    public IdentityService(BCryptPasswordEncoder bCryptPasswordEncoder)
    {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Autowired
    IdentityDAO dao;

    /**
     * Query identity by primary key. Example when calling it from the controller:
     *
     * <pre>
     * &#64;GetMapping(value = "/{uid}")
     * public ApiResponse&lt;IdentityResponse&gt; get(@PathVariable("uid") Long uid)
     * {
     *     return service.get(uid);
     * }
     * </pre>
     *
     * @param uid
     *            Primary key of the expected identity
     * @return ApiResponse object that has ApiResponse.model = found identity or
     *         null when not found.
     */
    public ApiResponse<IdentityResponse> get(Long uid)
    {
        ApiResponse<IdentityResponse> resp = new ApiResponse<>();
        if (uid <= 0) {
            resp.setErrorMessage("Invalid identity uid");
            resp.setHasError(true);
            return resp; // The request is not valid
        }

        try {
            Identity search = dao.get(uid);

            if (search != null) {
                resp.setModel(new IdentityResponse(search.getUid(), search.getName(), search.getEmail()));
            }
        }
        catch (final Exception ex) {
            logger.error("Identity get() error", ex);
            resp.setErrorMessage("Error when geting identity. Please try again");
            resp.setHasError(true);
        }

        return resp;
    }

    /**
     * Search for identities that name or email contain keyword
     *
     * @param filter
     *            Searching keyword in String format
     * @param sort
     *            Sort order String format. Controller does not use this parameter
     *            at the moment
     *
     * @return ApiResponse object that has ApiResponse.model = List&lt;Identity&gt;
     *         when found or null when not found
     */
    public ApiResponse<List<IdentityResponse>> search(String filter, String sort)
    {
        ApiResponse<List<IdentityResponse>> resp = new ApiResponse<>();

        List<IdentityResponse> result = Collections.emptyList();
        List<Identity> search = Collections.emptyList();

        try {
            if (filter != null && !filter.isEmpty()) {
                filter = String.format("LOWER(name) LIKE '%%%1$s%%' OR LOWER(email) LIKE '%%%1$s%%'",
                        filter.toLowerCase());
            }

            search = dao.search(filter, sort);

            if (search != null) {
                result = search
                        .stream()
                        .map(x -> new IdentityResponse(x.getUid(), x.getName(), x.getEmail()))
                        .collect(Collectors.toList());
                resp.setModel(result);
            }
        }
        catch (final Exception ex) {
            logger.error("Identity search() error", ex);
            resp.setErrorMessage("Error when searching for identities. Please try again");
            resp.setHasError(true);
        }

        return resp;
    }

    /**
     * Find the identity by its email address. Email is an unique index of Identity
     * entity
     *
     * @param email
     *            Email address of the expected identity
     * @return Identity object of the expected identity or null when not found
     */
    public Identity findByEmail(String email)
    {
        final String filter = String.format("normalizedEmail='%1$s'", email.toUpperCase());
        final String sort = "";

        Identity result = null;
        List<Identity> search = Collections.emptyList();

        try {
            search = dao.search(filter, sort);

            if (!search.isEmpty()) {
                result = search.get(0);
            }
        }
        catch (final Exception ex) {
            logger.error("Identity search() error", ex);
        }

        return result;
    }

    /**
     * Delete the identity base on its primary key
     *
     * @param uid
     *            Primary key of the to-be-deleted identity. Integer 64 bits
     * @return ApiResponse object that has ApiResponse.hasError = false when
     *         deleting successfully. Otherwise, the ApiResponse.errorMessage will
     *         have the explanation for error
     */
    public ApiResponse<IdentityResponse> delete(Long uid)
    {
        ApiResponse<IdentityResponse> resp = new ApiResponse<>();
        if (uid <= 0) {
            resp.setErrorMessage("Invalid identity uid");
            resp.setHasError(true);
            return resp; // The request is not valid
        }

        try {
            dao.delete(uid);
        }
        catch (final Exception ex) {
            logger.error("Identity delete() error", ex);
            resp.setErrorMessage("Error when deleting identity. Please try again");
            resp.setHasError(true);
        }

        return resp;
    }

    /**
     * Insert a new identity into the database. A new identity primary key will be
     * generated regardless existing uid value of identity object in the parameter
     *
     * @param request
     *            An IdentityRequest object, carries identity's value to be inserted
     * @return ApiResponse object that has ApiResponse.model = newly inserted
     *         identity (without password property) when inserting successfully.
     *         Otherwise, the ApiResponse.errorMessage will have the explanation for
     *         error and ApiResponse.hasError = true
     */
    public ApiResponse<IdentityResponse> create(IdentityRequest request)
    {
        return save(request, "create");
    }

    /**
     * Update current identity in the database with new value from the parameter,
     * including identity's password
     *
     * @param request
     *            An IdentityRequest object, carries identity's value to be updated
     * @return ApiResponse object that has ApiResponse.model = newly updated
     *         identity (without password property) when inserting successfully.
     *         Otherwise, the ApiResponse.errorMessage will have the explanation for
     *         error and ApiResponse.hasError = true
     */
    public ApiResponse<IdentityResponse> update(IdentityRequest request)
    {
        return save(request, "update");
    }

    /**
     * Update current identity in the database with new value from the parameter,
     * excluding identity's password. Identity's password will remain intact.
     *
     * @param request
     *            An IdentityRequest object, carries identity's value to be updated.
     *            IdentityRequest.password will be ignored
     * @return ApiResponse object that has ApiResponse.model = newly updated
     *         identity (without password property) when inserting successfully.
     *         Otherwise, the ApiResponse.errorMessage will have the explanation for
     *         error and ApiResponse.hasError = true
     */
    public ApiResponse<IdentityResponse> patch(IdentityRequest request)
    {
        return save(request, "patch");
    }

    private ApiResponse<IdentityResponse> save(IdentityRequest request, String method)
    {
        ApiResponse<IdentityResponse> resp = new ApiResponse<>();

        resp.setErrorMessage(validateIdentityRequest(request));
        if (resp.getErrorMessage().length() > 0) {
            resp.setHasError(true);
            return resp; // The request is not valid
        }

        Identity identity = null;

        try {
            identity = new Identity(
                    request.getUid(),
                    request.getName(),
                    request.getEmail(),
                    request.getEmail().toUpperCase(),
                    bCryptPasswordEncoder.encode(request.getPassword()));

            switch (method) {
            case "create":
                dao.create(identity);
                break;
            case "update":
                dao.update(identity);
                break;
            default:
                dao.update(identity, "name, email, normalizedEmail");
                break;
            }
        }
        catch (final IamDuplicateKeyException | IamDataIntegrityViolationException ex) {
            logger.error("Identity save() error", ex);
            resp.setErrorMessage("This email is already used. Choose a new one");
            resp.setHasError(true);
        }
        catch (final Exception ex) {
            logger.error("Identity save() error", ex);
            resp.setErrorMessage("Error when creating a new identity. Please try again");
            resp.setHasError(true);
        }

        if (identity != null) {
            resp.setModel(new IdentityResponse(identity.getUid(), identity.getName(), identity.getEmail()));
        }

        return resp;
    }

    private String validateIdentityRequest(IdentityRequest request)
    {
        List<String> err = new ArrayList<>();

        if (request.getName().length() == 0) {
            err.add("Required name but missing");
        }

        if (request.getEmail().length() == 0) {
            err.add("Required email but missing");
        }

        if (request.getName().length() == 0) {
            err.add("Required password but missing");
        }

        return String.join(". ", err);
    }
}
