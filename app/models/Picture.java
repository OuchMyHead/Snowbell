package models;

import play.libs.IO;
import play.Logger;
import play.data.Upload;
import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.libs.Codec;
import siena.*;
import siena.core.Aggregated;
import siena.core.Many;
import tools.ImageHost;

import javax.persistence.Transient;
import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: James
 * Date: 10/08/11
 * Time: 19:52
 * To change this template use File | Settings | File Templates.
 */
@Table("pictures")
public class Picture extends Model {

    @Transient
    private static final String JPG = "jpg";
    @Transient
    private static final String JPEG = "jpeg";
    @Transient
    private static final String PNG = "png";
    @Transient
    private static final String GIF = "gif";
    @Transient
    private static final String CONTENT_TYPE = "image/%s";

    @Id(Generator.AUTO_INCREMENT)
    public Long id;

    @Index("pictureAlbum_index")
    @CheckWith(PermissionCheck.class)
    public PictureAlbum pictureAlbum;

    @Column("user_id")
    @NotNull
    public Long userid;

    //basic image data - kept for when on GAE domain
    public byte[] file;
    public String fileName;
    public String contentType;

    @Column("image_link")
    public String image_link;

    @Column("thumb_link")
    public String thumb_link;

    @Column("description")
    @Max(100)
    @MaxSize( value=100, message="validation.maxSize")
    public String description;

    @Column("uploaded")
    @NotNull
    private long uploaded;

    @Column("active")
    @NotNull
    public boolean active;

    @Aggregated
	public Many<Comment> comments;

    /*
     Useful Sienna methods for Querying the database
     */
    public static Query<Picture> all() {
        return Model.all(Picture.class);
    }

    public static Picture findById(Long id) {
        return all().filter("id", id).get();
    }

    public static List<Picture> fetchAllActive(){
        return all().filter("active", true).fetch();
    }

    public static int count(){
        return all().count();
    }

    /**
     *
     * @param pictureAlbum
     * @param userid
     * @param fileupload
     */
    public Picture( PictureAlbum pictureAlbum, long userid, Upload fileupload ) {
        this.pictureAlbum   = pictureAlbum;
        this.userid         = userid;
        this.contentType    = suggestedContentType( fileupload );
        this.fileName       = fileupload.getFileName();
        this.file           = fileupload.asBytes();
        this.uploaded       = new Date().getTime();
        this.active         = true;
        this.image_link     = null;
        this.thumb_link     = null;
    }

    public Comment addComment( User user, String s_comment ){
        Comment comment = new Comment( user, s_comment );
        return comment;
    }

    /**
     * Because flash / flex uploads all images with the content-type octet-stream
     * we have to determine to correct type through the image name
     * @param data - Upload object
     * @return Suggested content type
     */
    public String suggestedContentType( Upload data ){
        String extension = "";
        String split[] = data.getFileName().split( "\\." );
        String fileType = split[ split.length - 1 ].toLowerCase();
        //determine the suggested content type by extension
        if( fileType.equals( JPG ) || fileType.equals( JPEG ) )
            extension = JPEG;
        else if( fileType.equals( PNG ) )
            extension = PNG;
        else if( fileType.equals( GIF ) )
            extension = GIF;
        else
            return data.getContentType();
        //return the suggested content type
        return String.format( CONTENT_TYPE, extension);
    }

    /**
     * Check user has permission to add to this album
     */
    static class PermissionCheck extends Check {

        public boolean isSatisfied(Object picture, Object pictureAlbum) {
            PictureAlbum check_album = (PictureAlbum) pictureAlbum;
            check_album.get();
            check_album.user.get();
            Picture check_picture = (Picture) picture;
            setMessage("validation.permission.pictures", (String)check_album.name );
            return check_album.user.id.equals( check_picture.userid );
        }
    }
}