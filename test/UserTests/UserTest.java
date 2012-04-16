package UserTests;

import controllers.GuestBook;
import models.*;
import org.junit.After;
import org.junit.Test;
import play.test.UnitTest;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: James
 * Date: 10/08/11
 * Time: 20:35
 * To change this template use File | Settings | File Templates.
 */
public class UserTest extends UnitTest {

    //user test data
    private String fullname         = "Bob Mail";
    private String email            = "bob@testmail.com";
    private String password         = UUID.randomUUID().toString();
    private String fullname2        = "Sam Mail";
    private String email2           = "sam@testmail.com";
    private String password2        = UUID.randomUUID().toString();
    private String wrongemail       = "tom@gmail.com";
    private String wrongpassword    = "badpassword" ;

    //playlist test data
    private String spotifyLink      = "spotify:user:jennyhagg10:playlist:1uJ56TghiC9Mn562KsNna7";
    private String spotifyName      = "Test Spotify";
    private String spotifyDesc      = "This is a test spotify link";
    private String spotifyLink2     = "spotify:user:jennyhagg10:playlist:1uJ56TghiC9Mn562KsNna2";
    private String spotifyName2     = "Test Spotify 2";
    private String spotifyDesc2     = "This is another test spotify link";

    //picture album test data
    private String pictureAlbumName     = "Test Picture Album";
    private String pictureAlbumCover    = "/link/to/cover.jpg";
    private String pictureAlbumName2    = "Test Picture Album 2";
    private String pictureAlbumCover2   = "/link/to/cover2.jpg";

    //picture test data
    private String picturePath      = "/path/to/picture.jpg";
    private String pictureDesc      = "This is a test picture";
    private String picturePath2     = "/path/to/picture2.jpg";
    private String pictureDesc2     = "This is test picture 2";

    //comment test data
    private String testComment      = "This is a test comment";

    //rating test data
    private int rating1             = 4;
    private int rating2             = 2;
    private double averageRating    = ( rating1 + rating2 ) / 2;

    //guest book message
    private String message = "This is a test guestbook message";
    private String message2 = "This is a second test guestbook message";

    @After
    public void tearDown(){
        User bob = User.findByEmail( email );
        if( bob != null )
            bob.delete();

        User sam = User.findByEmail( email2 );
        if( sam != null )
            sam.delete();
    }

    @Test
    public void createNewUser(){
        //count how many users we currently have to begin with
        int originalUsers = User.count();

        //load test data
        User bob = new User( fullname, email, password );
        assertNotNull(bob);
        bob.insert();

        // Count things
        assertEquals( originalUsers + 1, User.count() );

        //check connections are made / not made
        assertNotNull( User.connect( email, password ) );
        assertNull( User.connect( email, wrongpassword ) );
        assertNull( User.connect( wrongemail, password ) );

        // Find user and check details
        bob = User.connect( email, password );
        assertNotNull( bob );
        assertEquals( fullname, bob.name);
        assertEquals( email, bob.email );

        //clean up
        bob.delete();
        assertEquals( originalUsers, User.count() );
    }

    @Test
    public void createPlayList(){
        //count how many play lists we currently have to begin with
        int originalPlaylists = Playlist.count();

        //load test data
        User bob = new User( fullname, email, password );
        bob.insert();
        bob = User.connect( email, password );
        assertNotNull( bob );

        User sam = new User( fullname2, email2, password2 );
        sam.insert();
        sam = User.connect( email2, password2 );
        assertNotNull( sam );

        //create new playlist
        Playlist playlist = bob.addPlaylist( spotifyName, spotifyDesc, spotifyLink );
        bob.playlists.asList().add(playlist);
        bob.save();
        assertEquals( originalPlaylists + 1, Playlist.count() );

        //check bob has the play list
        List<Playlist> playlists = bob.playlists.asList();
        assertNotNull( playlists );
        assertEquals(playlists.size(), 1);
        assertEquals( playlists.get( 0 ), playlist );
        assertEquals( playlists.get( 0 ).name, spotifyName );
        assertEquals(playlists.get(0).description, spotifyDesc);
        assertEquals( playlists.get( 0 ).spotify, spotifyLink );

        //check sam does not
        playlists = sam.playlists.asList();
        assertNotNull(playlists);
        assertEquals( playlists.size(), 0 );

        //clean up
        bob.delete();
        sam.delete();
        //check play lists are deleted when user is deleted
        assertEquals( originalPlaylists, Playlist.count() );
    }

    @Test
    public void createPictureAlbum(){
        //count how many picture albums we currently have to begin with
        int originalPictureAlbums = PictureAlbum.count();

        //load test data
        User bob = new User( fullname, email, password );
        bob.insert();
        bob = User.connect( email, password );
        assertNotNull( bob );

        User sam = new User( fullname2, email2, password2 );
        sam.insert();
        sam = User.connect( email2, password2 );
        assertNotNull( sam );

        //create new picture album
        PictureAlbum pictureAlbum = bob.addPictureAlbum(pictureAlbumName, pictureAlbumCover);
        bob.pictureAlbums.asList().add( pictureAlbum );
        bob.save();
        assertEquals( originalPictureAlbums + 1, PictureAlbum.count() );

        //check bob has the picture album
        List<PictureAlbum> pictureAlbums = bob.pictureAlbums.asList();
        assertNotNull(pictureAlbums);
        assertEquals( pictureAlbums.size(), 1 );
        assertEquals( pictureAlbums.get( 0 ), pictureAlbum );
        assertEquals( pictureAlbums.get( 0 ).name, pictureAlbumName );
        assertEquals( pictureAlbums.get( 0 ).coverImage, pictureAlbumCover );

        //check sam does not
        pictureAlbums = sam.pictureAlbums.asList();
        assertNotNull( pictureAlbums );
        assertEquals( pictureAlbums.size(), 0 );

        //clean up
        bob.delete();
        sam.delete();
        //check picture albums are deleted when user is deleted
        assertEquals( originalPictureAlbums, PictureAlbum.count() );
    }

    @Test
    public void addPicturesToAlbum(){
        //count how many pictures we currently have to begin with
        int originalPictures = Picture.count();

        //load test data
        User bob = new User( fullname, email, password );
        bob.insert();
        bob = User.connect( email, password );
        assertNotNull( bob );

        //create two new picture albums
        PictureAlbum pictureAlbum1 = bob.addPictureAlbum(pictureAlbumName, pictureAlbumCover);
        bob.pictureAlbums.asList().add( pictureAlbum1 );
        PictureAlbum pictureAlbum2 = bob.addPictureAlbum(pictureAlbumName2, pictureAlbumCover2);
        bob.pictureAlbums.asList().add( pictureAlbum2 );
        bob.save();
        assertNotNull(pictureAlbum1);
        assertNotNull(pictureAlbum2);

        //add picture to the first picture album
        Picture picture = pictureAlbum1.addPicture( bob, null );
        pictureAlbum1.pictures.asList().add( picture );
        pictureAlbum1.save();
        assertEquals(originalPictures + 1, Picture.count());

        //check the first picture album has the picture
        List<Picture> pictures = pictureAlbum1.pictures.asList();
        assertNotNull(pictures);
        assertEquals( pictures.size(), 1 );
        assertEquals( pictures.get( 0 ), picture );
        assertEquals(pictures.get(0).description, pictureDesc);

        //check the second album does not
        pictures = pictureAlbum2.pictures.asList();
        assertNotNull(pictures);
        assertEquals(pictures.size(), 0);

        //check pictures are deleted when picture album is deleted
        pictureAlbum1.delete();
        assertEquals( originalPictures, Picture.count() );

        //clean up
        bob.delete();
    }

    @Test
    public void commentOnPicture(){
        //count how many comments we currently have to begin with
        int originalComments = Comment.count();

        //load test data
        User bob = new User( fullname, email, password );
        bob.insert();
        bob = User.connect( email, password );
        assertNotNull( bob );

        //create new picture album
        //create two new picture albums
        PictureAlbum pictureAlbum = bob.addPictureAlbum(pictureAlbumName, pictureAlbumCover);
        bob.pictureAlbums.asList().add( pictureAlbum );
        bob.save();
        assertNotNull( pictureAlbum );

        //add pictures to the picture album
        Picture picture1 = pictureAlbum.addPicture( bob, null );
        pictureAlbum.pictures.asList().add( picture1 );
        Picture picture2 = pictureAlbum.addPicture( bob, null );
        pictureAlbum.pictures.asList().add( picture2 );
        pictureAlbum.save();
        assertNotNull(picture1);
        assertNotNull(picture2);

        //add comments to the first picture
        Comment comment = picture1.addComment( bob, testComment );
        picture1.comments.asList().add(comment);
        picture1.save();
        assertNotNull(comment);

        //check the first picture has the comments
        List<Comment> comments = picture1.comments.asList();
        assertNotNull( comments );
        assertEquals( comments.size(), 1 );
        assertEquals(comments.get(0), comment);
        assertEquals( comments.get( 0 ).comment, testComment );
        assertEquals( comments.get( 0 ).user.id, bob.id );

        //check the second picture does not
        comments = picture2.comments.asList();
        assertNotNull( comments );
        assertEquals( comments.size(), 0 );

        //check comments are not added to database and only associated with the object
        assertEquals(originalComments, Comment.count());
        picture1.delete();
        assertEquals(originalComments, Comment.count());

        //clean up
        bob.delete();
    }

    @Test
    public void commentOnPlayList(){
        //count how many comments we currently have to begin with
        int originalComments = Comment.count();

        //load test data
        User bob = new User( fullname, email, password );
        bob.insert();
        bob = User.connect( email, password );
        assertNotNull( bob );

        //create two new playlist
        Playlist playlist1 = bob.addPlaylist(spotifyName, spotifyDesc, spotifyLink);
        bob.playlists.asList().add( playlist1 );
        Playlist playlist2 = bob.addPlaylist(spotifyName2, spotifyDesc2, spotifyLink2);
        bob.playlists.asList().add( playlist2 );
        bob.save();
        assertNotNull( playlist1 );
        assertNotNull( playlist2 );

        //add comments to the first playlist
        Comment comment = playlist1.addComment(bob, testComment);
        playlist1.comments.asList().add( comment );
        playlist1.save();
        assertNotNull(comment);

        //check the first playlist has the comments
        List<Comment> comments = playlist1.comments.asList();
        assertNotNull( comments );
        assertEquals( comments.size(), 1 );
        assertEquals( comments.get( 0 ), comment );
        assertEquals( comments.get( 0 ).comment, testComment );
        assertEquals(comments.get(0).user.id, bob.id);

        //check the second playlist does not
        comments = playlist2.comments.asList();
        assertNotNull(comments);
        assertEquals( comments.size(), 0 );

        //check comments are not added to database - these are only associated with the picture
        assertEquals( originalComments, Comment.count() );
        playlist1.delete();
        assertEquals(originalComments, Comment.count());

        //clean up
        bob.delete();
    }

    @Test
    public void leaveGuestBookMessage(){
        //count how many picture albums we currently have to begin with
        int originalGuestBookMessages = GuestBookMessage.count();

        //load test data
        User bob = new User( fullname, email, password );
        bob.insert();
        bob = User.connect( email, password );
        assertNotNull( bob );

        User sam = new User( fullname2, email2, password2 );
        sam.insert();
        sam.guestBookMessage.get().delete();        //delete Siena OneToOne work around hack
        sam = User.connect( email2, password2 );
        assertNotNull( sam );

        //create new guest book message
        GuestBookMessage guestBookMessage = new GuestBookMessage( message ); // bob.addGuestBookMessage(message);
        assertNotNull( guestBookMessage );
        GuestBookMessage hack = bob.guestBookMessage.get();
        bob.guestBookMessage.set(guestBookMessage);
        bob.save();
        assertNotNull( bob.guestBookMessage.get() );
        assertNotSame( hack, bob.guestBookMessage.get() );
        hack.delete();

        assertNotNull( guestBookMessage );
        assertEquals( originalGuestBookMessages + 1, GuestBookMessage.count() );

        //check bob has the guest book message
        assertNotNull( bob.guestBookMessage.get() );
        assertEquals( bob.guestBookMessage.get(), guestBookMessage );

        //get the guest book message for Bob
        guestBookMessage = bob.guestBookMessage.get();
        assertNotNull( guestBookMessage );
        assertEquals(guestBookMessage.user, bob);
        assertEquals( guestBookMessage.message, message);

        //get the guest book message for Sam
        guestBookMessage = sam.guestBookMessage.get();
        assertNull(guestBookMessage);

        //edit the guest book message and check that it has changed within bob
        guestBookMessage = bob.guestBookMessage.get();
        guestBookMessage.message = message2;
        guestBookMessage.update();
        bob.guestBookMessage.get();
        assertEquals(message2, bob.guestBookMessage.get().message);
        assertEquals(message2, guestBookMessage.message);
        assertEquals(guestBookMessage, bob.guestBookMessage.get());
        assertEquals(bob.guestBookMessage.get().message, guestBookMessage.message);

        //delete the guest book message and ensure bob's guest book message is back to null
        guestBookMessage.delete();
        assertNull( bob.guestBookMessage.forceSync().get() );
        assertEquals( originalGuestBookMessages, GuestBookMessage.count() );

        //add another guest book message
        guestBookMessage = bob.addGuestBookMessage(message);
        bob.guestBookMessage.get();
        bob.guestBookMessage.set(guestBookMessage);
        bob.insert();
        assertEquals( originalGuestBookMessages + 1, GuestBookMessage.count() );
        assertNotNull(guestBookMessage);
        assertNotNull( bob.guestBookMessage.get() );

        //add another guest book message to Bob (the first one should no longer be associated with Bob)
        GuestBookMessage guestBookMessage2 = bob.addGuestBookMessage(message2);
        bob.guestBookMessage.get();
        bob.guestBookMessage.set(guestBookMessage2);
        bob.save();
        //check old guest book message was removed from Bob
        assertNull(guestBookMessage.user);
        guestBookMessage.delete();
        assertNotNull(guestBookMessage2);
        assertNotNull( bob.guestBookMessage.get() );
        assertEquals( guestBookMessage2, bob.guestBookMessage.get() );
        assertEquals( originalGuestBookMessages + 1, GuestBookMessage.count() );

        //clean up
        bob.delete();
        sam.delete();
        //check picture albums are deleted when user is deleted
        assertEquals( originalGuestBookMessages, GuestBookMessage.count() );
    }

    @Test
    public void updateUser(){
        User bob = new User( fullname, email, password );
        bob.insert();
        bob = User.connect( email, password );
        assertNotNull( bob );

        bob.email = email2;
        bob.update();

        bob.get();

        assertEquals( bob.email, email2 );

        //clean up
        bob.delete();
    }

    @Test
    public void validatePlaylist(){
        String spotiUrl = "http://open.spotify.com/user/user10/playlist/1uJ56TghiC9Mn562KsNna7";
        String spotiReg = "http://open.spotify.com/user/\\w*/playlist/\\w*";
        boolean matches = spotiUrl.matches(spotiReg);
        assertTrue(matches);
    }

    @Test
    public void oneToOne(){
        User bob = new User( fullname, email, password );
        bob.insert();
        bob = User.connect( email, password );
        assertNotNull( bob );

        GuestBookMessage guestBookMessage = bob.addGuestBookMessage( message );
        bob.guestBookMessage.get();
        bob.guestBookMessage.set( guestBookMessage );
        bob.update();

        assertNotNull( bob.guestBookMessage.get() );
    }
}
