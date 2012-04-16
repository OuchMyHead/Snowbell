package controllers;

import models.*;
import play.Logger;
import play.data.Upload;
import play.libs.Codec;
import play.mvc.*;
import tools.Codes;
import tools.ImageHost;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: James
 * Date: 12/11/11
 * Time: 16:59
 * To change this template use File | Settings | File Templates.
 */
public class Pictures extends Controller {

    /**
     * Show all pictures for a picture album
     * @param id - Picture Album id
     */
    public static void showPictures( Long id ){
        PictureAlbum pictureAlbum = PictureAlbum.findById(id);
        List<Picture> pictures = pictureAlbum.pictures.asList();
        renderTemplate( "pictures/showPictures.html", pictures, pictureAlbum );
    }

    /**
     * Show template for adding picture
     * @param id - Picture Album id
     */
    public static void addPicture( Long id ) throws Exception{
        User user = Users.getUser();
        String uploadCode = Codes.getUploadCode(user, request.remoteAddress);
        PictureAlbum pictureAlbum = PictureAlbum.findById(id);
        renderTemplate( "pictures/addPicture.html", pictureAlbum, uploadCode );
    }

    /**
     * Add new picture to picture album
     * @param id - picture album id
     */
    public static void createPicture( Long id, Upload data )throws Exception{
        if( data != null ){
            String uploadCode = params.get("upload");
            User user = Codes.getUploadUser(uploadCode, request.remoteAddress);
            PictureAlbum pictureAlbum = PictureAlbum.findById( id );
            Picture picture = pictureAlbum.addPicture( user, data );
            validation.valid( picture );

            if( validation.hasErrors() ){
                params.flash();
                validation.keep();
                addPicture( id );
            }
            else{
                picture.insert();
                ImageHost.uploadGet( picture );
            }
        }
    }

    /**
     * Load picture from binary
     * @param id - picture id
     */
    public static void showPicture( Long id ) {
        Picture picture = Picture.findById(id);
        response.setContentTypeIfNotSet(picture.contentType);
        renderBinary(new ByteArrayInputStream(picture.file), picture.file.length);
    }

    /**
     * Activate picture
     * @param id - Picture Album id
     * @param pictureId - Picture id
     */
    public static void activatePicture( Long id, Long pictureId ){
        User user = Users.getUser();
        Picture picture = Picture.findById( pictureId );
        if( picture != null && picture.userid.equals(user.id) ){
            picture.active = true;
            picture.save();
        }
        showPictures( id );
    }

    /**
     * Deactivate picture
     * @param id - Picture Album id
     * @param pictureId - Picture id
     */
    public static void deactivatePicture( Long id, Long pictureId ){
        User user = Users.getUser();
        Picture picture = Picture.findById( pictureId );
        if( picture != null && picture.userid.equals(user.id) ){
            picture.active = false;
            picture.save();
        }
        showPictures( id );
    }

    /**
     * Delete the picture
     * @param id - Picture Album id
     * @param pictureId - Picture id
     */
    public static void deletePicture( Long id, Long pictureId ){
        User user = Users.getUser();
        Picture picture = Picture.findById( pictureId );
        if( picture != null && picture.userid.equals(user.id) )
            picture.delete();
        showPictures( id );
    }

    /**
     * Show update picture template
     * @param id - Picture album id
     * @param pictureId - Picture id
     */
    public static void editPicture( Long id, Long pictureId ){
        PictureAlbum pictureAlbum = PictureAlbum.findById( id );
        Picture picture = Picture.findById( pictureId );
        renderTemplate( "pictures/editPicture.html", picture, pictureAlbum );
    }

    /**
     * Update picture - only allow for description update (not change picture path)
     * @param id - Picture album id
     * @param pictureId - Picture id
     * @param description - image description
     */
    public static void updatePicture( Long id, Long pictureId, String description ){
        description = description.trim();

        User user = Users.getUser();
        Picture picture = Picture.findById( pictureId );
        if( picture.userid.equals( user.id ) )
            picture.description = description;

        validation.valid( picture );

        if( validation.hasErrors() ){
            params.flash();
            validation.keep();
            editPicture( id, pictureId );
        }
        else{
            picture.update();
            showPictures( id );
        }
    }

    /**
     * Show comments for picture
     * @param id - Picture id
     */
    public static void showComments( Long id ){
        Picture picture = Picture.findById( id );
        List<Comment> comments = picture.comments.asList();
        renderTemplate( "pictures/showComments.html", comments, id );
    }

    /**
     * Show add comment template
     * @param id - Picture id
     */
    public static void addComment( Long id ){
        renderTemplate( "pictures/addComment.html", id);
    }

    /**
     * Add new comment to picture
     * @param id - Picture album id
     * @param comment - comment text
     */
    public static void createComment(  Long id, String comment ){
        User user = Users.getUser();
        Picture picture = Picture.findById( id );

        comment = comment.trim();
        Comment new_comment = picture.addComment( user, comment );
        validation.valid(new_comment);

        if( validation.hasErrors() ){
            params.flash();
            validation.keep();
            addComment( id );
        }
        else{
            picture.comments.asList().add( new_comment );
            picture.save();
            showComments( id );
        }
    }

    /**
     * Delete comment from picture
     * @param id - picture id
     * @param commentId
     */
    public static void deleteComment( Long id, Long commentId ){
        User user = Users.getUser();
        Picture picture = Picture.findById( id );

        for( Comment comment : picture.comments.asList() ){
            if( comment.id.equals(commentId) ) {
                //only delete if owner is the one trying to delete the comment
                if( comment.user.equals(user) ){
                    picture.comments.asList().remove( comment );
                    picture.update();
                }
                break;
            }
        }
        showComments( id );
    }

}
