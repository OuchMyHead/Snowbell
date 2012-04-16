package controllers;

import models.*;
import play.mvc.Controller;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: James
 * Date: 12/11/11
 * Time: 15:43
 * To change this template use File | Settings | File Templates.
 */
public class PictureAlbums extends Controller {

    /**
     * Show all picture albums for this user
     */
    public static void showPictureAlbums(){
        User user = Users.getUser();
        List<PictureAlbum> pictureAlbums = user.pictureAlbums.asList();
        renderTemplate( "pictureAlbums/showPictureAlbums.html", pictureAlbums );
    }

    /**
     * Show template for adding picture album
     */
    public static void addPictureAlbum(){
        renderTemplate( "pictureAlbums/addPictureAlbum.html" );
    }

    /**
     * Add new picture album
     * @param name - Name of album
     * @param coverImage - image used to identify album
     */
    public static void createPictureAlbum( String name, String coverImage ){
        name = name.trim();
        coverImage = coverImage.trim();

        User user = Users.getUser();
        PictureAlbum pictureAlbum = user.addPictureAlbum( name, coverImage );
        validation.valid( pictureAlbum );

        if( validation.hasErrors() ){
            params.flash();
            validation.keep();
            addPictureAlbum();
        }
        else{
            pictureAlbum.insert();
            showPictureAlbums();
        }
    }

    /**
     * Activate picture album and all associated pictures
     * @param id - Picture Album id
     */
    public static void activatePictureAlbum( Long id ){
        User user = Users.getUser();
        PictureAlbum pictureAlbum = PictureAlbum.findById( id );
        if( pictureAlbum != null && pictureAlbum.user.equals(user) ){
            pictureAlbum.activate( true );
        }
        showPictureAlbums();
    }

    /**
     * Deactivate picture album and all associated pictures
     * - safer option than deleting
     * @param id - Picture Album id
     */
    public static void deactivatePictureAlbum( Long id ){
        User user = Users.getUser();
        PictureAlbum pictureAlbum = PictureAlbum.findById( id );
        if( pictureAlbum != null && pictureAlbum.user.equals(user) ){
            pictureAlbum.activate( false );
        }
        showPictureAlbums();
    }

    /**
     * Delete the picture album - associated pictures are deleted by the model
     * @param id - Picture Album id
     */
    public static void deletePictureAlbum( Long id ){
        User user = Users.getUser();
        PictureAlbum pictureAlbum = PictureAlbum.findById( id );
        if( pictureAlbum != null && pictureAlbum.user.equals(user) )
            pictureAlbum.delete();
        showPictureAlbums();
    }

    /**
     * Show update picture album template
     * @param id - Picture Album id
     */
    public static void editPictureAlbum( Long id ){
        PictureAlbum pictureAlbum = PictureAlbum.findById( id );
        renderTemplate( "pictureAlbums/editPictureAlbum.html", pictureAlbum );
    }

    /**
     * Update picture album
     * @param id - Picture Album id
     * @param name - Name of album
     * @param coverImage - image used to identify album
     */
    public static void updatePictureAlbum( Long id, String name, String coverImage ){
        name = name.trim();
        coverImage = coverImage.trim();

        User user = Users.getUser();
        PictureAlbum pictureAlbum = PictureAlbum.findById( id );
        if( pictureAlbum.user.equals( user ) ){
            pictureAlbum.name = name;
            pictureAlbum.coverImage = coverImage;
        }
        validation.valid( pictureAlbum );

        if( validation.hasErrors() ){
            params.flash();
            validation.keep();
            editPictureAlbum( id );
        }
        else{
            pictureAlbum.update();
            showPictureAlbums();
        }
    }

}
