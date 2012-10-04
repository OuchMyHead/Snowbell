package controllers;

import play.mvc.Controller;

/**
 * Created by IntelliJ IDEA.
 * User: James
 * Date: 16/09/12
 * Time: 16:42
 * To change this template use File | Settings | File Templates.
 */
public class Feedback extends Controller {

    public static void contactUs(){
        //todo show feedback interface
        renderTemplate( "application/feedback/feedback.html" );
    }
    public static void feedback(){
        //todo process feedback
        renderTemplate( "application/feedback/feedback.html" );
    }

}
