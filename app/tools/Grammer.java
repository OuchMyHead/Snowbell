package tools;

/**
 * Created by IntelliJ IDEA.
 * User: James
 * Date: 08/10/11
 * Time: 22:28
 * To change this template use File | Settings | File Templates.
 */
public class Grammer {

    /**
     * Returns true or false depending whether the word needs to be pluralized (ie. should add an s)
     * @param word
     * @return
     */
    public static boolean shouldPluralize( String word ){
        //check if word ends with an 's' - if not then it should be pluralized
        boolean s = word.toLowerCase()
                .substring(word.length()-1)
                .equals( "s" );
        return !s;
    }
}
