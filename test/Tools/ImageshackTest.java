package Tools;

import models.Picture;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import play.Logger;
import play.libs.WS;
import play.libs.XPath;
import play.test.UnitTest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: James
 * Date: 03/12/11
 * Time: 15:38
 * To change this template use File | Settings | File Templates.
 */
public class ImageshackTest extends UnitTest {

    //@Test
    public void oldGetImage() throws Exception {
        Picture picture = Picture.all().fetch().get( 0 );
        String data = URLEncoder.encode("url", "UTF-8") + "=" + URLEncoder.encode("http://2.bp.blogspot.com/_-HbIhXnhtrU/Rw9YbvL2lDI/AAAAAAAAAM8/Z2TbEoMW7qI/s400/BathingACat.jpg", "UTF-8");
        data += "&" + URLEncoder.encode("optsize", "UTF-8") + "=" + URLEncoder.encode("resample", "UTF-8");
        data += "&" + URLEncoder.encode("rembar", "UTF-8") + "=" + URLEncoder.encode("yes", "UTF-8");
        data += "&" + URLEncoder.encode("public", "UTF-8") + "=" + URLEncoder.encode("no", "UTF-8");
        data += "&" + URLEncoder.encode("a_username", "UTF-8") + "=" + URLEncoder.encode("sn_wbells", "UTF-8");
        data += "&" + URLEncoder.encode("a_password", "UTF-8") + "=" + URLEncoder.encode("jas3thUm", "UTF-8");
        data += "&" + URLEncoder.encode("key", "UTF-8") + "=" + URLEncoder.encode("ZUIDOVRG5f2d3d1cab9824342c2626b66b296b0e", "UTF-8");

        // Send data
        java.net.URL url = new java.net.URL("http://www.imageshack.us/upload_api.php");
        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(data);
        wr.flush();

        // Get the response
        InputStream is;
        if (((HttpURLConnection) conn).getResponseCode() == 400)
            is = ((HttpURLConnection) conn).getErrorStream();
        else
            is = conn.getInputStream();

        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = rd.readLine()) != null) {
            Logger.info(line);
        }

        wr.close();
        rd.close();
    }

    @Test
    public void getImage() throws Exception {
        Picture picture = Picture.all().fetch().get( 0 );
        Map<String,Object> params = new HashMap<String, Object>();
        params.put( "url", "http://2.bp.blogspot.com/_-HbIhXnhtrU/Rw9YbvL2lDI/AAAAAAAAAM8/Z2TbEoMW7qI/s400/BathingACat.jpg" );
        params.put( "optsize", "resample" );
        params.put( "rembar", "yes" );
        params.put( "public", "no" );
        params.put( "a_username", "sn_wbells" );
        params.put( "a_password", "jas3thUm" );
        params.put( "key", "ZUIDOVRG5f2d3d1cab9824342c2626b66b296b0e" );
        Document doc = WS.url("http://www.imageshack.us/upload_api.php")
                //.setHeader("Content-Type", picture.contentType )
                .params(params)
                .get()
                .getXml();
        picture.image_link = XPath.selectText( ".//image_link", doc );
        picture.thumb_link = XPath.selectText( ".//thumb_link", doc );
        if( picture.image_link != null && picture.thumb_link != null ){
            picture.file = null;
        }
        else{
            String message = doc.getElementsByTagName("error").item(0).getTextContent();
            Logger.info( "message : "  + message);
        }
    }

    @Test
    public void postImage() throws Exception {

        File image = new File("C:\\James\\Photos\\2012-01-21_22-08-20.jpg");

        WS.FileParam fp = new WS.FileParam( image, "fileupload");

        Map<String,Object> params = new HashMap<String, Object>();
        params.put( "optsize", "resample" );
        params.put( "rembar", "yes" );
        params.put( "public", "no" );
        params.put( "a_username", "sn_wbells" );
        params.put( "a_password", "jas3thUm" );
        params.put( "key", "ZUIDOVRG5f2d3d1cab9824342c2626b66b296b0e" );

        //POST request
        Document doc = WS.url( "http://www.imageshack.us/upload_api.php" )
            .params(params)
            .files(fp)
            .post()
            .getXml();

        String message = doc.getElementsByTagName("error").item(0).getTextContent();
        Logger.info("message : " + message);

    }

}
