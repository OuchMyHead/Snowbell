package models;

import play.Play;
import play.data.validation.*;
import siena.*;
import siena.Max;
import java.util.Date;
import play.libs.Codec;
import siena.core.*;

/**
 * Created by IntelliJ IDEA.
 * User: James
 * Date: 10/08/11
 * Time: 19:50
 * To change this template use File | Settings | File Templates.
 */
@Table("users")
public class User extends Model {

    public static final String USER_ID = "user_id";

    @Id(Generator.AUTO_INCREMENT)
    public Long id;

    @Column("name")
    @Max(50) @NotNull
    @Required
    @MaxSize( value=100, message="validation.maxSize.name")
    public String name;

    @Column("email")
    @Max(100) @NotNull
    @Required
    @Email
    @MaxSize( value=100, message="validation.maxSize.email")
    @CheckWith(MyEmailCheck.class)
    public String email;

    @Column("password")
    @NotNull
    @Required
    @CheckWith(MyPasswordCheck.class)
    public String password;

    @Column("active")
    @NotNull
    public boolean active;

    @Column("created")
    @NotNull
    public long created;

    @Column("activated")
    public long firstActivated;

    //todo - Link to facebook some-how or let user upload their own if they do not want to link their account (or don't have one)
    public byte[] photo = null;
    public String photo_link = Play.configuration.getProperty( "default.user.photo", "/public/photos/no.photo.png" );

    @Owned(mappedBy="user")
	public One<GuestBookMessage> guestBookMessage;

    @Owned(mappedBy="user")
    public Many<PictureAlbum> pictureAlbums;

    @Owned(mappedBy="user")
    public Many<Playlist> playlists;

    /*
     Useful Sienna methods for Querying the database
     */
    public static Query<User> all() {
        return Model.all(User.class);
    }

    public static User findById( Long id ) {
        return all().filter( "id", id ).get();
    }

    public static User findByEmail(String email) {
        return all().filter( "email", email ).get();
    }

    public static User connect( String email, String password ){
        return all().filter( "email", email ).filter("password", password).get();
    }

    public static int count(){
        return all().count();
    }

    /**
     * Ye olde constructor method
     * @param fullname
     * @param email
     * @param password
     */
    public User( String fullname, String email, String password ){
        this.name               = fullname;
        this.email              = email;
        this.password           = password;
        this.active             = false;
        this.created            = new Date().getTime();
        this.firstActivated     = 0;
        setUpGuestBookMessage();
    }

    public void firstActivation(){
        if( firstActivated == 0 ){
            firstActivated = new Date().getTime();
            active = true;
            this.save();
        }
    }

    /**
     * A hack method to get past the Siena OneToOne bug
     */
    public void setUpGuestBookMessage(){
        GuestBookMessage guestBookMessage = addGuestBookMessage( null );
        guestBookMessage.active = false;
        this.guestBookMessage.set( guestBookMessage );
    }

    public PictureAlbum addPictureAlbum( String name, String coverImage ){
        PictureAlbum pictureAlbum = new PictureAlbum( this, name, coverImage );
        return pictureAlbum;
    }

    public Playlist addPlaylist( String name, String description, String spotify ){
        Playlist playlist = new Playlist( this, name, description, spotify );
        return playlist;
    }

    public GuestBookMessage addGuestBookMessage( String message ){
        GuestBookMessage guestBookMessage = new GuestBookMessage( message );
        return guestBookMessage;
    }

    @Override
    public void delete(){
        //delete all the play lists
        for( Playlist playlist : this.playlists.asList() )
            playlist.delete();

        //delete all the picture albums
        for( PictureAlbum pictureAlbum : this.pictureAlbums.asList() )
            pictureAlbum.delete();

        //delete the guest book message
        if( guestBookMessage.get() != null )
           guestBookMessage.get().delete();

        super.delete();
    }

    /**
     * Check email is not used by anyone else
     */
    static class MyEmailCheck extends Check {

        public boolean isSatisfied(Object user, Object email) {
            final User found = User.findByEmail( (String)email );
            User given = (User)user;
            setMessage("validation.emailUsed", (String)email);
            return (given.equals(found))?true:found == null;
        }
    }

    /**
     * Check password has not been left blank
     * - blank strings are still encrypted
     */
    static class MyPasswordCheck extends Check {

        public boolean isSatisfied(Object user, Object password) {
            final String blank = Codec.hexSHA1( "" );
            String given = (String)password;
            setMessage("validation.required");
            return !given.equals( blank );
        }
    }

}
