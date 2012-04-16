package controllers;

import models.Comment;
import models.Playlist;
import models.User;
import play.Logger;
import play.mvc.Controller;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: James
 * Date: 12/11/11
 * Time: 14:03
 * To change this template use File | Settings | File Templates.
 */
public class Playlists extends Controller {

    /**
     * Show all playlist for logged in user
     */
    public static void showPlaylists(){
        User user = Users.getUser();
        List<Playlist> playlists = user.playlists.asList();
        renderTemplate( "playlists/showPlaylists.html", playlists );
    }

    /**
     * Show add playlist template
     */
    public static void addPlaylist(){
        renderTemplate( "playlists/addPlaylist.html" );
    }

    /**
     * Create new Playlist for logged in user
     * @param name - playlist name
     * @param description - playlist description
     * @param spotify - HTTP link to Spotify playlist
     */
    public static void createPlaylist( String name, String description, String spotify ){
        //process user input
        name = name.trim();
        description = description.trim();
        spotify = spotify.trim();

        //create new playlist and validate
        User user = Users.getUser();
        Playlist playlist = user.addPlaylist( name, description, spotify );
        validation.valid( playlist );

        //either save playlist or return errors
        if( validation.hasErrors() ){
            params.flash();
            validation.keep();
            addPlaylist();
        }
        else{
            playlist.insert();
            showPlaylists();
        }
    }

    /**
     * Activate playlist
     * @param id - Playlist id
     */
    public static void activatePlaylist( Long id ){
        User user = Users.getUser();
        Playlist playlist = Playlist.findById( id );
        if( playlist != null && playlist.user.equals(user) ){
            playlist.active = true;
            playlist.save();
        }
        showPlaylists();
    }

    /**
     * Deactivate playlist - safer alternative to deleting
     * @param id - Playlist id
     */
    public static void deactivatePlaylist( Long id ){
        User user = Users.getUser();
        Playlist playlist = Playlist.findById( id );
        if( playlist != null && playlist.user.equals(user) ){
            playlist.active = false;
            playlist.save();
        }
        showPlaylists();
    }

    /**
     * Delete playlist
     * @param id - Playlist id
     */
    public static void deletePlaylist( Long id ){
        User user = Users.getUser();
        Playlist playlist = Playlist.findById( id );
        if( playlist != null && playlist.user.equals(user) )
            playlist.delete();
        showPlaylists();
    }

    /**
     * Show the update playlist template
     * @param id - Playlist id
     */
    public static void editPlaylist( Long id ){
        Playlist playlist = Playlist.findById( id );
        renderTemplate( "playlists/editPlaylist.html", playlist );
    }

    /**
     * Update playlist details
     * @param id - Playlist id
     * @param name - Playlist name
     * @param description - Playlist description
     * @param spotify - HTTP link to Spotify playlist
     */
    public static void updatePlaylist( Long id, String name, String description, String spotify  ){
        //process user input
        name = name.trim();
        description = description.trim();
        spotify = spotify.trim();

        //check playlist belongs to user and update then validate
        User user = Users.getUser();
        Playlist playlist = Playlist.findById( id );
        if( playlist.user.equals( user ) ){
            playlist.name = name;
            playlist.description = description;
            playlist.spotify = spotify;
        }
        validation.valid( playlist );

        //either save playlist or return errors
        if( validation.hasErrors() ){
            params.flash();
            validation.keep();
            editPlaylist( id );
        }
        else{
            playlist.save();
            showPlaylists();
        }
    }

    /**
     * Show comments for playlist
     * @param id - Playlist id
     */
    public static void showComments( Long id ){
        Playlist playlist = Playlist.findById( id );
        List<Comment> comments = playlist.comments.asList();
        renderTemplate( "playlists/showComments.html", comments, id );
    }

    /**
     * Show add comment template
     * @param id - Playlist id
     */
    public static void addComment( Long id ){
        renderTemplate( "playlists/addComment.html", id );
    }

    /**
     * Add new comment to playlist
     * @param id - Playlist id
     * @param comment - comment text
     */
    public static void createComment( Long id, String comment ){
        User user = Users.getUser();
        Playlist playlist = Playlist.findById( id );

        comment = comment.trim();
        Comment new_comment = playlist.addComment( user, comment );
        validation.valid(new_comment);

        if( validation.hasErrors() ){
            params.flash();
            validation.keep();
            addComment( id );
        }
        else{
            playlist.comments.asList().add( new_comment );
            playlist.save();
            showComments( id );
        }
    }

    /**
     * Delete comment from Playlist
     * @param id - Playlist id
     */
    public static void deleteComment( Long id, Long commentId ){
        User user = Users.getUser();
        Playlist playlist = Playlist.findById( id );

        for( Comment comment : playlist.comments.asList() ){
            if( comment.id.equals(commentId) ) {
                //only delete if owner is the one trying to delete the comment
                if( comment.user.equals(user) ){
                    playlist.comments.asList().remove( comment );
                    playlist.update();
                }
                break;
            }
        }
        showComments( id );
    }
}
