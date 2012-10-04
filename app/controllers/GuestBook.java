package controllers;

import models.GuestBookMessage;
import models.User;
import play.mvc.Before;
import play.mvc.Controller;

/**
 * Created by IntelliJ IDEA.
 * User: James
 * Date: 13/11/11
 * Time: 17:30
 * To change this template use File | Settings | File Templates.
 */
public class GuestBook extends Controller {

    @Before
    static void setPageMarker(){
        Application.setMarker( "share" );
    }

    @Before
    static void setOption(){
        Users.setOption( "message" );
    }

    @Before
    static void checkAccess(){
        Users.checkAccess();
    }

    /**
     * Show user's guest book message
     */
    public static void showGuestBookMessage(){
        User user = Users.getUser();
        GuestBookMessage guestBookMessage = user.guestBookMessage.get();
        if( guestBookMessage == null || guestBookMessage.message == null )
            addGuestBookMessage();
        renderTemplate("application/account/message.html", guestBookMessage);
    }

    /**
     * Show template for adding guest book message
     */
    public static void addGuestBookMessage(){
        renderTemplate( "application/account/create/addMessage.html" );
    }

    /**
     * Add guest book message
     * @param message - Guest book message
     */
    public static void createGuestBookMessage( String message  ){
        User user = Users.getUser();
        message = message.trim();
        GuestBookMessage guestBookMessage = user.addGuestBookMessage( message );
        validation.valid( guestBookMessage );

        if( validation.hasErrors() ){
            params.flash();
            validation.keep();
            addGuestBookMessage();
        }
        else{
            //tidy up from our Siena OneToOne hack
            GuestBookMessage checkMessage = user.guestBookMessage.get();
            user.guestBookMessage.set( guestBookMessage );
            if( checkMessage != null )
                checkMessage.delete();

            user.save();
            showGuestBookMessage();
        }
    }

    /**
     * Activate guest book message
     */
    public static void activateGuestBookMessage(){
        User user = Users.getUser();
        GuestBookMessage guestBookMessage = user.guestBookMessage.get();
        if( guestBookMessage != null){
            guestBookMessage.active = true;
            guestBookMessage.update();
        }
        showGuestBookMessage();
    }

    /**
     * Deactivate guest book message
     * - safer option than deleting
     */
    public static void deactivateGuestBookMessage(){
        User user = Users.getUser();
        GuestBookMessage guestBookMessage = user.guestBookMessage.get();
        if( guestBookMessage != null){
            guestBookMessage.active = false;
            guestBookMessage.update();
        }
        showGuestBookMessage();
    }

    /**
     * Delete the guest book message
     */
    public static void deleteGuestBookMessage(){
        User user = Users.getUser();
        GuestBookMessage guestBookMessage = user.guestBookMessage.get();
        if( guestBookMessage != null){
            //we can't delete due to Siena's OneToOne bug - so set message to null
            guestBookMessage.message = null;
            guestBookMessage.update();
        }

        showGuestBookMessage();
    }

    /**
     * Show update guest book message template
     */
    public static void editGuestBookMessage(){
        User user = Users.getUser();
        GuestBookMessage guestBookMessage = user.guestBookMessage.get();
        renderTemplate( "guestBook/editGuestBookMessage.html", guestBookMessage );
    }

    /**
     * Update user's guest book message
     * @param message - Guest book message
     */
    public static void updateGuestBookMessage( String message ){
        User user = Users.getUser();
        message = message.trim();
        GuestBookMessage guestBookMessage = user.guestBookMessage.get();
        guestBookMessage.message = message;
        validation.valid( guestBookMessage );

        if( validation.hasErrors() ){
            params.flash();
            validation.keep();
            addGuestBookMessage();
        }
        else{
            guestBookMessage.update();
            showGuestBookMessage();
        }
    }
}
