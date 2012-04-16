package MailTests;

import models.User;
import notifiers.Mails;
import org.junit.Before;
import org.junit.Test;
import play.libs.Crypto;
import play.test.UnitTest;

import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: James
 * Date: 21/08/11
 * Time: 16:13
 * To change this template use File | Settings | File Templates.
 */
public class MailTest extends UnitTest {

    //user test data
    private String fullname         = "Bob Mail";
    private String email            = "mathieson10@hotmail.com";
    private String password         = UUID.randomUUID().toString();
    private String fullname2        = "Sam Mail";
    private String email2           = "james.mathieson@live.co.uk";
    private String password2        = UUID.randomUUID().toString();

    @Before
    public void setup(){
        User bob = User.findByEmail( email );
        User sam = User.findByEmail( email2 );
        if( bob != null )
            bob.delete();
        if( sam != null )
            sam.delete();
    }

    @Test
    public void sendSubscriptionEmailTest(){
        //count how many users we currently have to begin with
        int originalUsers = User.count();
        //load test data
        new User( fullname, email, password );
        assertEquals( originalUsers + 1, User.count() );

        //get bob from the database
        User bob = User.connect( email, password );
        assertNotNull( bob );

        //send email to bob
        String activationCode = Crypto.encryptAES( bob.email );
        Mails.welcome( bob, activationCode );

        //clean up
        bob.delete();
        assertEquals( originalUsers, User.count() );
    }

}
