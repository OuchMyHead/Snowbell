package controllers;

import models.User;
import notifiers.Mails;
import play.libs.Codec;
import play.mvc.Before;
import play.mvc.Controller;
import tools.Codes;
import ugot.recaptcha.Recaptcha;

/**
 * Created by IntelliJ IDEA.
 * User: James
 * Date: 16/09/12
 * Time: 17:04
 * To change this template use File | Settings | File Templates.
 */
public class Registration extends Controller {

    @Before
    static void setMarker(){
        Application.setMarker( "share" );
    }

    /**
     * Create new user
     * @param name - user name
     * @param reg_email - user contact email
     * @param password - user password
     * @param captcha - CAPTCHA code
     */
    public static void createUser( String name, String reg_email, String password, @Recaptcha String captcha ){
        //prepare user input
        name = name.trim();
        reg_email = reg_email.trim();
        password = password.trim();

        //encrypt password sha1 and create / validate user
        password = Codec.hexSHA1(password);
        User user = new User( name, reg_email, password );
        validation.valid( user );

        //insert user or return errors
        if( validation.hasErrors() ){
            params.flash();
            validation.keep();
            Application.share();
        }
        else{
            //todo:: destroy unconfirmed users after 48hrs -> look up CRON (http://code.google.com/appengine/docs/java/config/cron.html)
            //todo:: will Play make a cron file for you? http://www.playframework.org/documentation/1.2.3/jobs - upload to gae and do a job in 5 mins
            user.insert();
            //create activation code - user must confirm their registration before they are activated
            String activationCode = Codes.getActivationCode(user);
            Mails.welcome(user, activationCode);

            //display confirmation page
            confirmRegistrationSent();
        }
    }

    /**
     * Confirm registration email has been sent
     */
    public static void confirmRegistrationSent( ){
        Application.setMarker( "share" );
        renderTemplate("application/registration/confirmRegistration.html");
    }

    /**
     * Activate users account
     * @param activationCode - Code sent to user to activate account
     */
    public static void activateAccount( String activationCode ){
        User user = Codes.checkActivationCode(activationCode);
        if( user != null){
            user.firstActivation();
        }
        renderTemplate( "application/registration/activateAccount.html", user );
    }

    /**
     * Cancel account request
     * @param activationCode - Code sent to user to activate account
     */
    public static void cancelAccount( String activationCode ){
        User user = Codes.checkActivationCode(activationCode);
        if( user != null)
            user.delete();
        renderTemplate("application/registration/cancelAccount.html", user);
    }

}
