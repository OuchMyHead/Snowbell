package controllers;

import models.*;
import notifiers.Mails;
import play.cache.Cache;
import play.libs.Crypto;
import play.libs.Images;
import play.libs.Codec;
import play.mvc.*;
import tools.Codes;
import ugot.recaptcha.Recaptcha;

import java.util.List;

public class Application extends Controller {

    @Before
    static void getUser(){
        User user = Users.getUser();
        renderArgs.put( "user", user );
    }

    @Before
    static void setMarker(){
        //always default to index page
        setMarker( "welcome" );
    }

    static void setMarker( String page ){
        renderArgs.put( "page", page );
    }

    /**
     * landing page
     */
    public static void holding(){
        renderTemplate("holding.html");
    }

    /**
     * landing page
     */
    public static void index(){
        renderTemplate("application/main/welcome.html");
    }

    /**
     * Show login template
     */
    public static void userSignIn(){
        if( renderArgs.get( "user" ) != null )
            index();
        else
            renderTemplate("application/users/login.html");
    }

    /**
     * Log user in
     * @param email - user email
     * @param password - user password
     */
    public static void userLogIn( String email, String password ){
        //process input data
        User user = null;
        password = Codec.hexSHA1(password);
        email = email.trim();

        //get user from database
        user = User.connect( email, password );
        validation.required(user).message( "validation.required.login" );

        //put user in cache or show errors
        if( validation.hasErrors() ){
            params.flash();
            validation.keep();
            userSignIn();
        }
        else{
            session.put( User.USER_ID, user.id );
            Users.account();
        }
    }

    /**
     * Add user - display page for visitor to create new account
     */
    public static void addUser(){
        renderTemplate("application/users/addUser.html");
    }

    /**
     * Create new user
     * @param name - user name
     * @param email - user contact email
     * @param password - user password
     * @param email_conf - confirmation of email address
     * @param pass_conf - confirmation of password
     * @param captcha - CAPTCHA code
     */
    public static void createUser( String name, String email, String password, String email_conf, String pass_conf, @Recaptcha String captcha ){
        //prepare user input
        name = name.trim();
        email = email.trim();
        email_conf = email_conf.trim();
        password = password.trim();
        pass_conf = pass_conf.trim();

        //check everything that is required is present
        validation.required( email_conf ).message("validation.required.confirm");
        validation.required( pass_conf ).message("validation.required.confirm");
        validation.match(email_conf, email).message("validation.match.details");
        validation.match(pass_conf, password).message("validation.match.details");

        //encrypt password sha1 and create / validate user
        password = Codec.hexSHA1(password);
        User user = new User( name, email, password );
        validation.valid( user );

        //insert user or return errors
        if( validation.hasErrors() ){
            params.flash();
            validation.keep();
            addUser();
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
        renderTemplate("application/users/confirmRegistration.html");
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
        renderTemplate( "application/users/activateAccount.html", user );
    }

    /**
     * Cancel account request
     * @param activationCode - Code sent to user to activate account
     */
    public static void cancelAccount( String activationCode ){
        User user = Codes.checkActivationCode(activationCode);
        if( user != null)
            user.delete();
        renderTemplate("application/users/cancelAccount.html", user);
    }

    /**
     * Show forgot password page
     */
    public static void forgotPassword(){
        renderTemplate("application/users/forgottenPassword.html");
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
        renderTemplate("application/users/sentPasswordReminder.html");
    }

    /**
     * Allow user to reset their password
     * @param resetCode - unique reset code that allows user to reset password
     */
    public static void createNewPassword( String resetCode ){
        User user = Codes.checkResetCode(resetCode);
        renderTemplate("application/users/createNewPassword.html", user, resetCode);
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
        renderTemplate("application/users/confirmPasswordReset.html");
    }

    public static void ourStory(){
        setMarker( "story" );
        renderTemplate( "application/main/ourStory.html" );
    }

    /**
     * Display 'the albums' page
     */
    public static void theAlbum(){
        renderTemplate( "application/main/ourAlbums.html" );
    }

    /**
     * Display all active play lists
     */
    public static void thePlaylists(){
        List<Playlist> playlists = Playlist.fetchAllActive();
        for( Playlist playlist : playlists ){
            playlist.user.get();
            for( Comment comment : playlist.comments.asList() ){
                comment.user.get();
            }
        }
        renderTemplate( "application/main/playlists.html", playlists );
    }

    /**
     * Display all active picture albums
     */
    public static void thePictureAlbums(){
        List<PictureAlbum> pictureAlbums = PictureAlbum.fetchAllActive();
        for( PictureAlbum pictureAlbum : pictureAlbums ){
            pictureAlbum.user.get();
        }
        renderTemplate( "application/main/pictureAlbums.html", pictureAlbums );
    }

    /**
     * Display all active pictures within picture album
     * @param id - picture album id
     */
    public static void thePictures( Long id ){
        PictureAlbum pictureAlbum = PictureAlbum.findById( id );
        pictureAlbum.user.get();
        List<Picture> pictures = Picture.fetchAllActive();
        for( Picture picture : pictures )
            for( Comment comment : picture.comments.asList() )
                comment.user.get();
        renderTemplate( "application/main/pictures.html", pictureAlbum, pictures );
    }

    public static void theWedding(){

    }

    /**
     * Display all active guest book messages
     */
    public static void guestBook(){
        setMarker( "guestbook" );
        List<GuestBookMessage> guestBookMessages = GuestBookMessage.fetchAllActive();
        for( GuestBookMessage guestBookMessage : guestBookMessages )
            guestBookMessage.user.get();
        renderTemplate( "application/main/guestBook.html", guestBookMessages );
    }

    /**
     * Display contact us form
     */
    public static void contactUs(){
        renderTemplate( "application/feedback/feedback.html" );
    }

    /**
     * Process the feedback
     */
    public static void feedback(){

    }

}