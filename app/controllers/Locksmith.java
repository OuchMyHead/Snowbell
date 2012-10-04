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
 * Time: 17:24
 * To change this template use File | Settings | File Templates.
 */
public class Locksmith extends Controller {

    @Before
    static void setMarker(){
        Application.setMarker( "share" );
    }

    /**
     * Show forgot password page
     */
    public static void forgotPassword(){
        renderTemplate("application/locksmith/forgottenPassword.html");
    }

    /**
     * Process users request for new password
     * @param email - email address for account
     */
    public static void requestPassword( String email ){
        email = email.trim();
        User user = User.findByEmail(email);
        validation.required(user).message( "validation.required.requestPassword" );
        if( validation.hasErrors() ){
            params.flash();
            validation.keep();
            forgotPassword();
        }
        else{
            //create resetCode and send user email
            String resetCode = Codes.getResetCode(user);
            Mails.resetPassword(user, resetCode);
            confirmPasswordSent();
        }
    }

    /**
     * Confirm password has been sent
     */
    public static void confirmPasswordSent(){
        renderTemplate("application/locksmith/sentPasswordReminder.html");
    }

    /**
     * Allow user to reset their password
     * @param resetCode - unique reset code that allows user to reset password
     */
    public static void createNewPassword( String resetCode ){
        User user = Codes.checkResetCode(resetCode);
        renderTemplate("application/locksmith/createNewPassword.html", user, resetCode);
    }

    /**
     * Update users password
     * @param resetCode - Password reset code
     * @param password - new password
     * @param pass_conf - new password confirmation
     * @param captcha - CAPTCHA code
     */
    public static void submitNewPassword( String resetCode, String password, String pass_conf, @Recaptcha String captcha ){
        //get user back from the code
        User user = Codes.checkResetCode(resetCode);
        if( user == null )
            createNewPassword( resetCode );

        //prepare received data
        password = password.trim();
        pass_conf = pass_conf.trim();

        //check everything that is required is present
        validation.required( pass_conf ).message("validation.required.confirm");
        validation.match(pass_conf, password).message("validation.match.details");

        password = Codec.hexSHA1(password);
        user.password = password;
        validation.valid(user);

        //if there are errors then return to the addUser page
        if( validation.hasErrors()){
            params.flash();                         //keep user input within the flash scope
            validation.keep();                      //keep the errors for next request
            createNewPassword( resetCode );         //show the create new password page again
        }
        else{
            user.save();
            confirmPasswordUpdated();
        }
    }

    /**
     * Confirm password has been updated
     */
    public static void confirmPasswordUpdated(){
        renderTemplate("application/locksmith/confirmPasswordReset.html");
    }
}
