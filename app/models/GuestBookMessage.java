package models;

import play.Logger;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import siena.*;

import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: James
 * Date: 10/08/11
 * Time: 23:46
 * To change this template use File | Settings | File Templates.
 */
@Table("guestBookMessages")
public class GuestBookMessage extends Model  {

    @Id(Generator.AUTO_INCREMENT)
    public Long id;

    public User user;

    @Column("created")
    @NotNull
    private long created;

    @Column("message")
    @Max(2500)
    @Required( message="validation.required.message")
    @MaxSize( value=2500, message="validation.maxSize.message" )
    public String message;

    @Column("active")
    @NotNull
    public boolean active;

    /*
     Useful Sienna methods for Querying the database
     */
    public static Query<GuestBookMessage> all() {
        return Model.all(GuestBookMessage.class);
    }

    public static GuestBookMessage findById(Long id) {
        return all().filter("id", id).get();
    }

    public static List<GuestBookMessage> fetchAllActive(){
        return all().filter("active", true).order("-created").fetch();
    }

    public static int count(){
        return all().count();
    }

    /**
     * Ye olde constructor method
     * @param message
     */
    public GuestBookMessage( String message ){
        this.message    = message;
        this.active     = true;
        this.created    = new Date().getTime();
    }

}
