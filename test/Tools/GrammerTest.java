package Tools;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import play.libs.Codec;
import play.libs.XPath;
import play.test.UnitTest;
import tools.Grammer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by IntelliJ IDEA.
 * User: James
 * Date: 23/11/11
 * Time: 21:42
 * To change this template use File | Settings | File Templates.
 */
public class GrammerTest extends UnitTest {

    @Test
    public void yesAddS(){
        String word = "Tom";
        assertTrue(Grammer.shouldPluralize( word ));
    }

    @Test
    public void noAddS(){
        String word = "James";
        assertFalse(Grammer.shouldPluralize( word ));
    }

    @Test
    public void caseInsensitiveMatching() throws ParserConfigurationException{
        // root elements
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element root = document.createElement("Root");
        root.setAttribute( "ID", Codec.UUID() );
        document.appendChild(root);

        Node lowercase = XPath.selectNode( ".//root", document );
        assertNull( lowercase );
        Node uppercase = XPath.selectNode( ".//Root", document );
        assertNotNull( uppercase );
        Node ignoreCase = XPath.selectNode( ".//*[lower-case(local-name())='root']", document );
        assertNotNull( ignoreCase );

        lowercase = XPath.selectNode( ".//Root/@id", document );
        assertNull( lowercase );
        uppercase = XPath.selectNode( ".//Root/@ID", document );
        assertNotNull( uppercase );
        ignoreCase = XPath.selectNode( ".//@*[lower-case(local-name())='id']", document );
        assertNotNull( ignoreCase );
    }

}
