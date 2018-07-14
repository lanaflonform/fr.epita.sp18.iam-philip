package fr.epita.sp18;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import fr.epita.sp18.entity.Identity;
import fr.epita.sp18.model.ApiResponse;
import fr.epita.sp18.model.IdentityRequest;
import fr.epita.sp18.model.IdentityResponse;
import fr.epita.sp18.service.IdentityService;

/**
 * Test IAM-Project by calling the IdentityService to:
 * <p>
 * - Create an identity
 * <p>
 * - Get the newly created identity from database and compare it with the
 * initial data
 * <p>
 * - Search the newly created identity by its email. Compare the result with the
 * initial data
 * <p>
 * - Use BCryptPasswordEncoder check if the raw and hashed password are matched
 * <p>
 * - Change name and email of that identity.
 * <p>
 * - Read it again to test the update result
 * <p>
 * - Delete the identity by it uid
 * <p>
 * - Get it again to test the deleting result
 *
 * @author Philip
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests
{
    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    IdentityService service;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Test
    public void TestDAO()
    {
        System.out.println("");
        System.out.println("----------- STARTING THE TEST -----------");
        System.out.println("");

        boolean testOK = true;

        // Append the Test1 into database
        IdentityRequest request1 = new IdentityRequest(0L, "Test1 Name", "test1.name@email.com", "Test1Pass123");
        ApiResponse<IdentityResponse> create = service.create(request1);

        IdentityResponse source1 = create.getModel();
        String rawPassword = request1.getPassword();

        System.out.println(String.format("Create record: %1$s", source1.toString()));

        // Read the Test1
        ApiResponse<IdentityResponse> get1 = service.get(create.getModel().getUid());
        IdentityResponse read1 = get1.getModel();
        System.out.println(String.format("Read the newly created record: %1$s", read1.toString()));

        // Compare read with the source
        if ((read1.getUid().equals(source1.getUid()))
                && read1.getName().equals(source1.getName())
                && read1.getEmail().equals(source1.getEmail())) {
            System.out.println("Create source == Value read after creating --> good");
        }
        else {
            System.out.println("Create source != Value read after creating --> ERROR");
            testOK = false; // test failed
        }
        System.out.println("");

        // Search Test1 by email
        System.out.println("Search for record that has email = " + read1.getEmail());
        Identity search = service.findByEmail(read1.getEmail());
        System.out.println(String.format("Search result: %1$s",
                search.toString()));

        // Compare search with the source
        if ((search.getUid().equals(source1.getUid()))
                && search.getName().equals(source1.getName())
                && search.getEmail().equals(source1.getEmail())) {
            System.out.println("Search result == Searching source --> good");
        }
        else {
            System.out.println("Search result != Searching source --> ERROR");
            testOK = false; // test failed
        }
        System.out.println("");

        // Check bCrypt password
        System.out.println("Check if assword is hashed and saved correctly");
        if (bCryptPasswordEncoder.matches(rawPassword, search.getPasswordHash())) {
            System.out.println("Raw password matches with the hashed one --> good");
        }
        else {
            System.out.println("Raw password NOT matches with the hashed one --> ERROR");
        }
        System.out.println("");

        // Change name and email of the source to Test2
        IdentityRequest request2 = new IdentityRequest(read1.getUid(), "Test2 Name", "test2.name@email.com", "");

        // Update database again
        System.out.println("Update record with name='Test2 Name', email='test2.name@email.com'");
        ApiResponse<IdentityResponse> update = service.patch(request2);
        System.out.println(String.format("Update result = %1$s", !update.getHasError()));

        // Read again to test the update work
        ApiResponse<IdentityResponse> get2 = service.get(create.getModel().getUid());
        IdentityResponse read2 = get2.getModel();
        System.out.println(String.format("Read the newly updated record: %1$s", read2.toString()));

        // Compare read2 with the source
        if ((read2.getUid().equals(request2.getUid()))
                && read2.getName().equals(request2.getName())
                && read2.getEmail().equals(request2.getEmail())) {
            System.out.println("Update source == Read value after update --> good");
        }
        else {
            System.out.println("Update source != Read value after update --> ERROR");
            testOK = false; // test failed
        }
        System.out.println("");

        // Delete the test record
        ApiResponse<IdentityResponse> delete = service.delete(request2.getUid());
        System.out.println(String.format("Delete record result = %1$s", !delete.getHasError()));

        // Read to test the delete work
        ApiResponse<IdentityResponse> get3 = service.get(create.getModel().getUid());
        IdentityResponse read3 = get3.getModel();

        if (read3 == null) {
            System.out.println("After delete, Read that record again == null --> good");
        }
        else {
            System.out.println("After delete, Read that record again != null --> ERROR");
            testOK = false; // test failed
        }
        System.out.println("");

        // Show the test result
        if (testOK) {
            System.out.println("Well done. All tests PASSED");
        }
        else {
            System.out.println("TEST FAILED");
        }
    }
}
