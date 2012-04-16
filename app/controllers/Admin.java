package controllers;

import models.User;
import play.mvc.Controller;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: James
 * Date: 31/08/11
 * Time: 22:22
 * To change this template use File | Settings | File Templates.
 */
public class Admin extends Controller  {

    /**
     * will there be an index page? This could be the users 'profile' page
     */
    public static void index(){
        //todo make this admin profile-style page (where they can manage their account)
        render();
    }

    /**
     * Display all users
     */
    public static void displayUsers(){
        List<User> users;
        users = User.all().fetch();
        renderTemplate( "admin/users/displayUsers.html", users );
    }

    /**
     * Delete user
     * @param id
     */
    public static void deleteUser( long id ){
        User user = User.findById( id );

        if( user != null )
            user.delete();
        //todo:: what if user is null?
        //todo:: render()
    }

}
