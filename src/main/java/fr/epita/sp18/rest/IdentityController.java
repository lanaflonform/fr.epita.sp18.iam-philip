/**
 *
 */
package fr.epita.sp18.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.epita.sp18.model.ApiResponse;
import fr.epita.sp18.model.IdentityRequest;
import fr.epita.sp18.model.IdentityResponse;
import fr.epita.sp18.service.IdentityService;

/**
 * API /api/identities is the interface of IAM-Project with external
 * application.
 * <p>
 * To make calls to this API, you need to send the API token in a Authorization
 * HTTP header using the Bearer authentication scheme.
 *
 * API token can be acquired via sign in thru URL /login
 *
 * @author Philip
 *
 */
@RestController
@RequestMapping("/api/identities")
public class IdentityController
{
    @Autowired
    IdentityService service;

    /**
     * DELETE /api/identities/1531171542822. Delete the identity base on its primary
     * key
     *
     * @param uid
     *            An unique id of the identity. Integer 64 bits
     * @return Delete result.
     *         <p>
     *         Http response sample when delete record successfully: {model: null,
     *         message: "", hasError: false, errorMessage: ""}.
     *         <p>
     *         Http response sample when there is error: {model: null, message: "",
     *         hasError: false, errorMessage: "Error when deleting identity. Please
     *         try again"}
     */
    @DeleteMapping(value = "/{uid}")
    public ApiResponse<IdentityResponse> delete(@PathVariable("uid") Long uid)
    {
        return service.delete(uid);
    }

    /**
     * GET /api/identities/1531171542822. Get information of an identity base on its
     * primary key
     *
     * @param uid
     *            An unique id of the identity. Integer 64 bits
     * @return Search result.
     *         <p>
     *         Http response sample: {model: { email : "hugo@email.com" name : "Hugo
     *         Lloris" uid : 1531171542822}, message: "", hasError: false,
     *         errorMessage: ""}.
     *         <p>
     *         Http response sample when no identity found: {model: null, message:
     *         "", hasError: false, errorMessage: ""}
     */
    @GetMapping(value = "/{uid}")
    public ApiResponse<IdentityResponse> get(@PathVariable("uid") Long uid)
    {
        return service.get(uid);
    }

    /**
     * GET /api/identities?filter=xyz. Search for identities name or email that
     * contain keyword specified in "filter" parameter
     *
     * @param filter
     *            the keyword for searching. Sample API that search for identities
     *            containing the keyword "go": /api/identities?filter=go
     * @return Search result. Http response sample: {model: [{uid: 1531171542822,
     *         name: "Hugo Lloris", email: "hugo@email.com"}], message: "",
     *         hasError: false, errorMessage: ""}.
     *
     *         Http response sample when no identity found: {model: null, message:
     *         "", hasError: false, errorMessage: ""}
     */
    @GetMapping()
    public ApiResponse<List<IdentityResponse>> list(@RequestParam("filter") String filter)
    {
        final String sort = "";
        return service.search(filter == null ? "" : filter, sort);
    }

    /**
     * POST /api/identities. Create a new identity according to the parameter
     * received in post http request body
     *
     * @param request
     *            The identity in JSON format. Sample request body: {"uid": 0,
     *            "name":"Admin", "email":"admin@email.com", "password":"Admin1234"}
     * @return Create result. Http response sample: {model: {uid: 1531171542822,
     *         name: "Hugo Lloris", email: "hugo@email.com"}, message: "", hasError:
     *         false, errorMessage: ""}.
     *
     *         Http response sample when creating identity has error: {model: {uid:
     *         1531171542822, name: "Hugo Lloris", email: "hugo@email.com"},
     *         message: "", hasError: true, errorMessage: "This email is already
     *         used. Choose a new one"}. Detail error can be found in server's log.
     */
    @PostMapping()
    public ApiResponse<IdentityResponse> create(@RequestBody IdentityRequest request)
    {
        return service.create(request);
    }

    /**
     * PUT /api/identities/1531171542822. Update identity according to the data
     * received in http request body
     *
     * @param request
     *            Same as POST request value
     * @return Update result. Http response is same as POST request
     */
    @PutMapping(value = "/{uid}")
    public ApiResponse<IdentityResponse> update(@RequestBody IdentityRequest request)
    {
        return service.update(request);
    }

    /**
     * PATCH /api/identities/1531171542822. Update identity according to the data
     * received in http request body, EXCEPT the password property
     *
     * @param request
     *            Same as POST request value
     * @return Patch result. Http response is same as POST request
     */
    @PatchMapping(value = "/{uid}")
    public ApiResponse<IdentityResponse> patch(@RequestBody IdentityRequest request)
    {
        return service.patch(request);
    }
}
