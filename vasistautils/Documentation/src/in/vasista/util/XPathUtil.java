/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package in.vasista.util;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 *
 * @author sindukur
 */
public class XPathUtil {
private static XPath xpath = null;
private static XPath getXPath(){
    if(xpath==null){
        xpath = XPathFactory.newInstance().newXPath();
    }
    return xpath;
}
public static XPathExpression getXPathExpression(String expression) throws XPathExpressionException{
    return getXPath().compile(expression);
}
}
