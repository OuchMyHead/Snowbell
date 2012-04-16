package models;

import play.data.Upload;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import siena.*;
import siena.core.Many;
import siena.core.Owned;

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
@Table("pictureAlbums")
public class PictureAlbum extends Model{

    @Id(Generator.AUTO_INCREMENT)
    public Long id;

    @Index("user_index")
    public User user;

    @Column("name")
    @Max(100) @NotNull
    @Required
    @MaxSize( value=100, message="validation.maxSize")
    public String name;

    @Column("cover")
    @NotNull
    @Required
    public String coverImage;

    @Column("created")
    @NotNull
    private long created;

    @Column("active")
    @NotNull
    public boolean active;

    @Owned(mappedBy="pictureAlbum")
    public Many<Picture> pictures;

    /*
     Useful Sienna methods for Querying the database
     */
    public static Query<PictureAlbum> all() {
        return Model.all(PictureAlbum.class);
    }

    public static PictureAlbum findById(Long id) {
        return all().filter("id", id).get();
    }

    public static List<PictureAlbum> fetchAllActive(){
        return all().filter("active", true).fetch();
    }

    public static int count(){
        return all().count();
    }
    /**
     * Ye olde constructor method
     * @param user
     * @param name
     * @param coverImage
     */
    public PictureAlbum( User user, String name, String coverImage ){
        this.user       = user;
        this.name       = name;
        this.coverImage = coverImage;
        this.created    = new Date().getTime();
        this.active     = true;
    }

    public Picture addPicture( User user, Upload data ){
        Picture picture = new Picture( this, user.id, data );
        return picture;
    }

    public void activate( boolean active ){
        for( Picture picture : pictures.asList() ){
            picture.active = active;
            picture.update();
        }
        this.active = active;
        this.update();
    }

    @Override
    public void delete(){
        //delete all the pictures
        for( Picture picture : pictures.asList() ){
            picture.delete();
        }

        super.delete();
    }

}
