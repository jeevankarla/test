/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package in.vasista.util;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author sindukur
 */
public class XMLUtil {

    private static  DocumentBuilder builder = null;

    private static  DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        if (builder == null) {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            builder = domFactory.newDocumentBuilder();
        }
        return builder;
    }

    public static Document getDocument(String xml) throws ParserConfigurationException, SAXException, IOException {

        return getDocumentBuilder().parse(xml);
    }
      public static Document getDocument(File xmlfile) throws ParserConfigurationException, SAXException, IOException {

        return getDocumentBuilder().parse(xmlfile);
    }
}
