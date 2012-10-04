package controllers;

import models.*;
import notifiers.Mails;
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
        setMarker( "wedding" );
        renderTemplate( "application/main/wedding.html" );
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
     * Display registration page
     */
    public static void share(){
        setMarker( "share" );
        renderTemplate( "application/main/share.html" );
    }

}