package documentation;


import in.vasista.util.XMLUtil;
import in.vasista.util.XPathUtil;
import in.vasista.vbiz.entityanalysis.Log;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class Services {

    public  void main(String arr) throws Exception {
       try{
        Document doc = (Document) XMLUtil.getDocument("/home/vadmin/Desktop/VBiz0.1/applications/party/servicedef/services.xml");
        XPathExpression expr = XPathUtil.getXPathExpression("/services/service");



        NodeList services = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        System.out.println(services.getLength());
        for (int i = 0; i < services.getLength(); i++)
        {
            XPathExpression servicesm = XPathUtil.getXPathExpression("/services/service[@name='"+arr+"']");
            NodeList servi = (NodeList) servicesm.evaluate(doc, XPathConstants.NODESET);
             XPathExpression expr1 = XPathUtil.getXPathExpression("@name");
             System.out.print("Service Name:");
            Log.LogInfoMessage((String) expr1.evaluate(servi.item(i), XPathConstants.STRING).toString());
            
            XPathExpression expr2 = XPathUtil.getXPathExpression("@location");
            System.out.print("Service Location:");
            Log.LogInfoMessage((String) expr2.evaluate(servi.item(i), XPathConstants.STRING).toString());
            XPathExpression entity = XPathUtil.getXPathExpression("@default-entity-name");
            System.out.print("Service Entity:");
            Log.LogInfoMessage((String) entity.evaluate(servi.item(i), XPathConstants.STRING));
           

            XPathExpression description = XPathUtil.getXPathExpression("/services/service/description");
            NodeList des = (NodeList) description.evaluate(doc, XPathConstants.NODESET);
            System.out.print("Service Description:");
             Log.LogInfoMessage(description.evaluate(des.item(i), XPathConstants.STRING).toString());
            //  System.out.println(des.item(0).toString());


             XPathExpression permission = XPathUtil.getXPathExpression("/services/service/permission-service/@service-name");
            NodeList pre = (NodeList) permission.evaluate(doc, XPathConstants.NODESET);
            System.out.print("Service Permission type:");
             Log.LogInfoMessage((String) permission.evaluate(pre.item(i), XPathConstants.STRING).toString());

            
            System.out.println(i);

//              XPathExpression des=XPathUtil.getXPathExpression("description");
//              System.out.println(des.toString());
//              XPathExpression serv=XPathUtil.getXPathExpression("permission-service/@service-name");
//              System.out.println(serv.toString());
//              Node firstPersonNode = listOfPersons.item(s);
//                if(firstPersonNode.getNodeType() == Node.ELEMENT_NODE){
//
//
//                    Element firstPersonElement = (Element)firstPersonNode;
//
//                    //-------
//                    NodeList firstNameList = firstPersonElement.getElementsByTagName("first");
//                    Element firstNameElement = (Element)firstNameList.item(0);



//              NodeList desList=doc.getElementsByTagName("/services/service/description");
            //XPathExpression des = XPathUtil.getXPathExpression("/services/service/description");
        }
       }catch(Exception e)
       {
           System.out.println(e.toString());
       }

    }
}
