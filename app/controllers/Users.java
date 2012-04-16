package controllers;

import com.google.apphosting.runtime.security.WhiteList;
import models.*;
import play.Logger;
import play.libs.Codec;
import play.mvc.*;
import tools.Grammer;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: James
 * Date: 31/08/11
 * Time: 22:21
 * To change this template use File | Settings | File Templates.
 */
public class Users extends Controller {

    /**
     * Check user access
     */
    @Before
    static void checkAccess(){
        //check user is logged in
        User user = getUser();
        //if we cannot find user, direct to login
        if( user == null ){
            Application.userSignIn();
        }
    }

    /**
     * Log user out
     */
    public static void userLogout(){
        session.remove(User.USER_ID);
        //todo:: confirm logout at top of screen -> like twitter
        renderTemplate( "users/signout.html" );
    }

    /**
     * Get user from session (this is used a lot so may as well put it in its own method)
     *
     * @return User object
     */
    protected static User getUser(){
        User user = null;
        String user_id = session.get( User.USER_ID );
        if( user_id != null && !user_id.isEmpty())
            user = User.findById( Long.valueOf( user_id ) );

        return user;
    }

    /**
     * Display users account page (their personal details and what they have uploaded)
     */
    public static void account() {
        User user = getUser();
        //check if last letter is s
        boolean s = Grammer.shouldPluralize( user.name );
        renderTemplate( "users/account.html", user, s );
    }

    /**
     * Display user details - account settings
     */
    public static void accountSettings(){
        User user = getUser();
        renderTemplate( "users/accountSettings.html", user );
    }

    /**
     * Display form for changing name
     */
    public static void editName(){
        User user = getUser();
        renderTemplate( "users/editName.html", user );
    }

    /**
     * Update name
     */
    public static void updateName( String name, String password ){
        //prepare data
        name = name.trim();
        password = password.trim();
        password = Codec.hexSHA1( password );

        //assign new details and validate
        User user = getUser();
        user.name = name;
        validation.valid(user);
        validation.match( password, user.password )
                .message("validation.match.passwords");

        //Save or return errors
        if( validation.hasErrors() ){
            params.flash();
            validation.keep();
            editName();
        }
        else{
            user.save();
            accountSettings();
        }
    }

    /**
     * Display form for changing email
     */
    public static void editEmail(){
        User user = getUser();
        renderTemplate( "users/editEmail.html", user );
    }

    /**
     * Update email
     */
    public static void updateEmail( String email, String password ){
        //prepare data
        email = email.trim();
        password = password.trim();
        password = Codec.hexSHA1( password );

        //assign new details and validate
        User user = getUser();
        user.email = email;
        validation.valid(user);
        validation.match( password, user.password )
                .message("validation.match.passwords");

        //Save or return errors
        if( validation.hasErrors() ){
            params.flash();
            validation.keep();
            editEmail();
        }
        else{
            user.save();
            accountSettings();
        }
    }

    /**
     * Display form for changing password
     */
    public static void editPassword(){
        renderTemplate( "users/editPassword.html" );
    }

    /**
     * Update password
     */
    public static void updatePassword( String password, String new_password, String pass_conf ){
        User user = getUser();
        //prepare data
        password = password.trim();
        new_password = new_password.trim();
        pass_conf = pass_conf.trim();

        //check everything that is required is present
        validation.required( password );
        validation.required( pass_conf ).message("validation.required.confirm");

        //check everything matches that should
        password = Codec.hexSHA1(password);
        validation.match(pass_conf, new_password).message("validation.match.details");
        validation.match(password, user.password).message("validation.match.passwords");

        new_password = Codec.hexSHA1( new_password );
        user.password = new_password;
        validation.valid(user);
        if( validation.hasErrors() ){
            params.flash();
            validation.keep();
            editPassword();
        }
        else{
            user.save();
            accountSettings();
        }
    }

    /**
     * Deactivate users account - safe alternative to deleting them
     */
    public static void deactivateUser( ){
        renderTemplate( "users/deactivateAccount.html" );
    }

    /**
     * Confirm account deactivation
     * @param password
     */
    public static void confirmDeactivateUser( String password ){
        User user = getUser();
        //check password matches current password
        password = password.trim();
        password = Codec.hexSHA1( password );
        validation.match( password, user.password )
                .message("validation.match.passwords");

        //deactivate user or return errors
        if( validation.hasErrors() ){
            params.flash();
            validation.keep();
            deactivateUser();
        }
        else{
            user.active = false;
            user.save();
            accountSettings();
        }
    }

    /**
     * Reactivate users account
     */
    public static void reactivateUser(){
        renderTemplate( "users/reactivateAccount.html" );
    }

    /**
     * Confirm account reactivation
     * @param password
     */
    public static void confirmReactivateUser( String password ){
        User user = getUser();
        //check password matches current password
        password = password.trim();
        password = Codec.hexSHA1( password );
        validation.match( password, user.password )
                .message("validation.match.passwords");

        //deactivate user or return errors
        if( validation.hasErrors() ){
            params.flash();
            validation.keep();
            reactivateUser();
        }
        else{
            user.active = true;
            user.save();
            accountSettings();
        }
    }

    /**
     * Delete user
     */
    public static void deleteUser(){
        renderTemplate( "users/deleteAccount.html" );
    }

    /**
     * Confirm account deletion
     * @param password
     */
    public static void confirmDeleteUser( String email, String password ){
        //process input data
        User user = getUser();
        password = Codec.hexSHA1(password);
        email = email.trim();

        //get user from database
        User checkUser = User.connect( email, password );
        validation.required(checkUser).message( "validation.required.login" );
        validation.equals(checkUser, user).message( "validation.required.login" );

        //delete user or return errors
        if( validation.hasErrors() ){
            params.flash();
            validation.keep();
            deleteUser();
        }
        else{
            user = getUser();
            user.delete();
            renderTemplate( "users/confirmAccountDeletion.html" );
        }
    }

}
