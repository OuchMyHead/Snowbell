package models;

import play.data.validation.*;
import siena.*;
import siena.Max;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: James
 * Date: 10/08/11
 * Time: 23:40
 * To change this template use File | Settings | File Templates.
 */
@Table("comments")
public class Comment extends Model {

    @Id(Generator.AUTO_INCREMENT)
    public Long id;

    @Column("user")
    @NotNull
    public User user;

    @Column("created")
    @NotNull
    public long created;

    @Column("comment")
    @Max(500)
    @Required
    @MaxSize( value=500, message="validation.maxSize")
    public String comment;

    /*
     Useful Sienna methods for Querying the database
     */
    public static Query<Comment> all() {
        return Model.all(Comment.class);
    }

    public static Comment findById(Long id) {
        return all().filter("id", id).get();
    }

    public static int count(){
        return all().count();
    }

    /**
     * Ye older constructor class
     * @param user
     * @param comment
     */
    public Comment( User user, String comment ){
        this.user     = user;
        this.comment    = comment;
        this.created    = new Date().getTime();
    }
}
