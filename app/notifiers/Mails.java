package notifiers;

import models.User;
import play.*;
import play.mvc.*;
import java.util.*;


/**
 * Created by IntelliJ IDEA.
 * User: James
 * Date: 20/08/11
 * Time: 18:26
 * To change this template use File | Settings | File Templates.
 */
public class Mails extends Mailer {

    /**
     * Send welcome message to new user
     * @param user
     * @param activationCode
     */
    public static void welcome ( User user, String activationCode ) {
        setSubject( "Confirm your account, %s", user.name );
        addRecipient( user.email );
        setFrom( "Claire & Colin Bell<notifier@snowbells.co.uk>" );
        send( user, activationCode );
    }

    /**
     * Send user link to reset their password
     * @param user
     * @param resetcode
     */
    public static void resetPassword( User user, String resetcode ) {
        setFrom("<notifier@snowbells.co.uk>");
        setSubject("Password Reset");
        addRecipient(user.email);
        send(user, resetcode);
    }

}

