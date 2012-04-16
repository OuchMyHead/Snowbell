package tools;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import controllers.Pictures;
import models.Picture;
import org.h2.util.New;
import org.w3c.dom.Document;
import play.Logger;
import play.libs.WS;
import play.libs.XPath;
import play.mvc.Router;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: James
 * Date: 01/01/12
 * Time: 16:04
 * To change this template use File | Settings | File Templates.
 */
public class ImageHost {

    /**
     * Upload picture to image sharing site
     * @param image
     * @param picture
     * @throws UnsupportedEncodingException
     * @throws MalformedURLException
     * @throws IOException
     *
    public static void uploadPost( File image, Picture picture ) throws UnsupportedEncodingException, MalformedURLException, IOException {
        WS.FileParam fp = new WS.FileParam( image, "fileupload");
        Map<String,Object> params = new HashMap<String, Object>();
        params.put( "fileupload", new ByteArrayInputStream(picture.file) );
        params.put( "optsize", "resample" );
        params.put( "rembar", "yes" );
        params.put( "public", "no" );
        params.put( "a_username", "sn_wbells" );
        params.put( "a_password", "jas3thUm" );
        params.put( "key", "ZUIDOVRG5f2d3d1cab9824342c2626b66b296b0e" );
        WS.HttpResponse response = WS.url("http://www.imageshack.us/upload_api.php")
                .setHeader("Content-Type", "multipart/form-data" )
                .params(params)
                .mimeType(picture.contentType)
                //.files(fp)
                .post();
        Document doc = response.getXml();
        Logger.info( ""+ response.success() );
        String message = doc.getElementsByTagName("error").item(0).getTextContent();
        Logger.info( "message : "  + message);
    }

    /**
     * Upload using URL from website rather than from file
     * @param picture
     */
    public static void uploadGet( Picture picture ){
        Map<String,Object> routeParams = new HashMap<String, Object>();
        routeParams.put("id", picture.id );
        String path = Router.getFullUrl( "Pictures.showPicture", routeParams );
        Map<String,Object> params = new HashMap<String, Object>();
        params.put( "url", path );
        params.put( "optsize", "resample" );
        params.put( "rembar", "yes" );
        params.put( "public", "no" );
        params.put( "a_username", "sn_wbells" );
        params.put( "a_password", "jas3thUm" );
        params.put( "key", "ZUIDOVRG5f2d3d1cab9824342c2626b66b296b0e" );
        Document doc = WS.url("http://www.imageshack.us/upload_api.php")
                .setHeader("Content-Type", picture.contentType)
                .params(params)
                .get()
                .getXml();
        picture.image_link = XPath.selectText(".//image_link", doc);
        picture.thumb_link = XPath.selectText( ".//thumb_link", doc );
        if( picture.image_link != null && picture.thumb_link != null )
            picture.file = null;
        else{
            String error = XPath.selectText(".//error", doc);
            throw new RuntimeException( "Error uploading image : " + picture.id + ". Imageshack response : " + error );
        }
    }
}
