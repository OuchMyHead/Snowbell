package tools;

import models.User;
import org.apache.commons.codec.binary.Hex;
import play.Logger;
import play.libs.Crypto;
import play.libs.Codec;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: James
 * Date: 28/09/11
 * Time: 22:52
 * To change this template use File | Settings | File Templates.
 */
public class Codes {

    /**
     * Create activation code for user
     * @param user
     * @return
     */
    public static String getActivationCode( User user ){
        return Crypto.encryptAES( user.email);
    }

    /**
     * Check activation code and return user associated with code
     * @param code
     * @return
     */
    public static User checkActivationCode( String code ){
        String email;
        try{
            email = Crypto.decryptAES( code );
        }
        catch(Exception e){
            return null;
        }
        return User.findByEmail( email );
    }

    /**
     * Generate reset code for user
     * @param user
     * @return
     */
    public static String getResetCode( User user ) {
        String resetCode = String.format( "%s&%s&%s", user.password, new Date().getTime(), user.email );
        resetCode = Crypto.encryptAES( resetCode );
        return resetCode;
    }

    /**
     * Check reset code is valid and return user associated with code
     * @param code
     * @return
     * @throws NumberFormatException
     */
    public static User checkResetCode( String code ) throws NumberFormatException{
        String time;
        String password;
        String email;
        String resetCode = "";
        try{
            resetCode = Crypto.decryptAES( code );
        }
        catch(Exception e){
            return null;
        }
        String[] fields = resetCode.split( "&");

        if( fields.length != 3 )
            return null;

        //get user info from token
        password = fields[0];
        time = fields[1];
        email = fields[2];

        User user = User.findByEmail( email );

        //check email matches and that the code has not expired (has 6 hour life)
        if( user!=null && !hasExpired( time, 21600000L ) && user.password.equals( password ) ){
            return user;
        }
        else{
            return null;
        }
    }

    /**
     * Check if a timestamp has expired
     * @param time
     * @param timeLimit
     * @return
     */
    public static boolean hasExpired( long time, long timeLimit ){
        //if current time is larger than (time + time limit) then it has expired
        return (time + timeLimit) < new Date().getTime();
    }

    /**
     * Check if a time stamp has expired
     * @param s_time
     * @param timeLimit
     * @return
     */
    public static boolean hasExpired( String s_time, long timeLimit ){
        try{
            long l_time = Long.parseLong( s_time );
            return hasExpired( l_time, timeLimit );
        }
        catch( NumberFormatException nfe ){
            return true;
        }
    }

    /**
     * Encrypt user email with their current IP as salt
     * @param user - user intending to upload
     * @param salt - user IP address
     * @return Upload code
     */
    public static String getUploadCode( User user, String salt ) throws NoSuchAlgorithmException, InvalidKeyException {
        String salted = salt( user.password, salt );
        String key = String.format( "%s,%s", user.email, salted );
        return Crypto.encryptAES( key );
    }

    /**
     * Get user from the upload code
     * @param code - upload code
     * @param salt - user IP address
     * @return User as determined by upload code
     */
    public static User getUploadUser( String code, String salt ) throws NoSuchAlgorithmException, InvalidKeyException {
        String decrypted = Crypto.decryptAES( code );
        String[] details = decrypted.split( "," );
        User user = User.findByEmail( details[0] );
        String salted = salt( user.password, salt );
        if( !salted.equals( details[1] )  )
            user = null;
        return user;
    }

    /**
     * Salt a String using SHA1
     * @param string - String to be salted
     * @param salt - salt
     * @return Salted version of String
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static String salt( String string, String salt ) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance( "HmacSHA1" );
        SecretKeySpec secret = new SecretKeySpec( salt.getBytes(), mac.getAlgorithm() );
        mac.init( secret );
        byte[] digest = mac.doFinal( string.getBytes() );
        return new String(Hex.encodeHex(digest));
    }

}
