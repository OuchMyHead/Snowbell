package models;

import play.Logger;
import play.data.validation.Match;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.data.validation.URL;
import siena.*;
import siena.core.Aggregated;
import siena.core.Many;

import javax.persistence.Transient;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: James
 * Date: 10/08/11
 * Time: 19:51
 * To change this template use File | Settings | File Templates.
 */
@Table("playlists")
public class Playlist extends Model {

    @Id(Generator.AUTO_INCREMENT)
    public Long id;

    @Index("user_index")
    public User user;

    @Column("name")
    @Max(100) @NotNull
    @Required
    @MaxSize( value=100, message="validation.maxSize")
    public String name;

    @Column("description")
    @Max(200)
    @Required
    @MaxSize( value=200, message="validation.maxSize")
    public String description;

    @Column("spotify")
    @Max(200) @NotNull
    @Required
    @URL
    @MaxSize( value=200, message="validation.maxSize")
    @Match( value="http://open.spotify.com/user/\\w*/playlist/\\w*", message="validation.match.spotify" )
    public String spotify;

    @Column("created")
    @NotNull
    public long created;

    @Column("active")
    @NotNull
    public boolean active;

    @Aggregated
	public Many<Comment> comments;

    /*
     Useful Sienna methods for Querying the database
     */
    public static Query<Playlist> all() {
        return Model.all(Playlist.class);
    }

    public static Playlist findById(Long id) {
        return all().filter("id", id).get();
    }

    public static List<Playlist> fetchAllActive(){
        return all().filter("active", true).fetch();
    }

    public static int count(){
        return all().count();
    }

    /**
     * Ye olde constructor method
     * @param user
     * @param name
     * @param description
     * @param spotify
     */
    public Playlist( User user, String name, String description, String spotify ){
        this.user           = user;
        this.name           = name;
        this.description    = description;
        this.spotify        = spotify;
        this.active         = true;
        this.created        = new Date().getTime();
    }

    public Comment addComment( User user, String s_comment ){
        Comment comment = new Comment( user, s_comment );
        return comment;
    }
}
