package documentation;

import in.vasista.vbiz.entityanalysis.Log;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import jxl.write.*;
import in.vasista.util.XMLUtil;
import in.vasista.util.XPathUtil;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import java.io.File;
import org.w3c.dom.*;
import java.util.Locale;
import jxl.Workbook;
import jxl.WorkbookSettings;
import org.w3c.dom.NodeList;

public class ScreenstoForms2 {
public static int z=1;
public static WorkbookSettings ws;
public static Label l;
public static WritableSheet s;
public static WritableWorkbook workbook;
    public static  void main(String[] args) {

Map<String,String> mp=new HashMap<String, String>();
        try{
             ws= new WorkbookSettings();
                        ws.setLocale(new Locale("en", "EN"));
                        workbook = Workbook.createWorkbook(new File("files.xls"), ws);
                        s = workbook.createSheet("Sheeeeeeeeet1", 0);
                        l = new Label(0, 0, "Screen name");
                        s.addCell(l);
                        l = new Label(1, 0, "screen fields");
                        s.addCell(l);
                        l = new Label(2, 0, "form fields");
                        s.addCell(l);
                        l = new Label(3, 0, "included form name");
                        s.addCell(l);
                        l = new Label(4, 0, "services name");
                        s.addCell(l);
                        l = new Label(5, 0, "service location");
                        s.addCell(l);
                        l = new Label(6, 0, "entities");
                        s.addCell(l);
                        l = new Label(7, 0, "Service Description");
                        s.addCell(l);
                        l = new Label(8, 0, "Service Permission");
                        s.addCell(l);
                        l = new Label(9, 0, "default-map-name");
                        s.addCell(l);
                        l = new Label(10, 0, "names of form fields");
                        s.addCell(l);

        String path = "/home/vadmin/Desktop/VBiz0.1/applications/party/webapp/partymgr/WEB-INF/controller.xml";
        Document doc = (Document) XMLUtil.getDocument(path.toString());
        XPathExpression expr = XPathUtil.getXPathExpression("/site-conf/view-map");
        
        NodeList screens = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        for (int j = 0,p=0; j < screens.getLength(); j++,p++) {
                        XPathExpression servicesm = XPathUtil.getXPathExpression("/site-conf/view-map[@type='screen']");
                        XPathExpression expr1 = XPathUtil.getXPathExpression("@name");
                        XPathExpression expr2 = XPathUtil.getXPathExpression("@page");
        mp.put(expr1.evaluate(screens.item(j), XPathConstants.STRING).toString(),expr2.evaluate(screens.item(j), XPathConstants.STRING).toString());
        }
        parsescreen(mp,s);
        }catch(Exception px){
            px.printStackTrace();
        }
    }

    public static   void parsescreen(Map mp,WritableSheet s){
        String[] st=new String[2];
      try{
         
        Iterator it = mp.entrySet().iterator();
while(it.hasNext()){
Map.Entry entry = (Map.Entry) it.next();
String key = entry.getKey().toString();
String value = entry.getValue().toString();
            st=value.toString().replace("component:/","/home/vadmin/Desktop/VBiz0.1/applications").split("#");
             System.out.println("screen name"+key);
            l = new Label(0, z, key);
                        s.addCell(l);
       parsingscreen(st[0],key,s);
       }
        }catch(Exception px){
            px.printStackTrace();
            px.getMessage();
        }
    }

    public static  void parsingscreen(String st,String key,WritableSheet s){
        Set st1=new HashSet();
        Set st2=new HashSet();
        Set st3=new HashSet();
        Map<String,String> mp1=new HashMap<String, String>();
        try{
      Document d = (Document) XMLUtil.getDocument( new File(st));
XPathExpression e = XPathUtil.getXPathExpression("/screens/screen[@name='"+key+"']/section/actions/set");
        NodeList screens = (NodeList) e.evaluate(d, XPathConstants.NODESET);
         for (int j = 0,p=0; j < screens.getLength(); j++,p++) {
                        XPathExpression expr1 = XPathUtil.getXPathExpression("@field");
                        XPathExpression expr2 = XPathUtil.getXPathExpression("@value");
                        XPathExpression expr3 = XPathUtil.getXPathExpression("@form-field");
                       st1.add((String) expr1.evaluate(screens.item(j), XPathConstants.STRING).toString());
                        st2.add((String) expr2.evaluate(screens.item(j), XPathConstants.STRING).toString());
                         st3.add((String) expr3.evaluate(screens.item(j), XPathConstants.STRING).toString());

        }
         l = new Label(1, z, st1.toString());
                        s.addCell(l);
        System.out.println("screen fields"+st1);
        l = new Label(2, z, st2.toString());
                        s.addCell(l);
        //st1.clear();
        System.out.println("field values"+st2);
        l = new Label(3, z, st3.toString());
                        s.addCell(l);
        //st2.clear();
        System.out.println("form-field"+st3);
        XPathExpression expr1 = XPathUtil.getXPathExpression("/screens/screen[@name='"+key+"']/section/widgets/decorator-screen/decorator-section/screenlet/include-form");
        NodeList screens1 = (NodeList) expr1.evaluate(d, XPathConstants.NODESET);
        for (int j = 0,p=0; j < screens1.getLength(); j++,p++) {
                        XPathExpression expr11 = XPathUtil.getXPathExpression("@name");
                        XPathExpression expr12 = XPathUtil.getXPathExpression("@location");
                        mp1.put((String) expr11.evaluate(screens1.item(j), XPathConstants.STRING).toString(), (String) expr12.evaluate(screens1.item(j), XPathConstants.STRING).toString());
                        }
                        parseform(mp1,s);
        }catch(Exception px){
            px.printStackTrace();
            px.getMessage();}
} public static  void parseform(Map mp1,WritableSheet s){
        String[] st=new String[2];
      try{
        Iterator it = mp1.entrySet().iterator();
while(it.hasNext()){
Map.Entry entry = (Map.Entry) it.next();
String key = entry.getKey().toString();
String value = entry.getValue().toString();
            st[0]=value.toString().replace("component:/","/home/vadmin/Desktop/VBiz0.1/applications");
            System.out.println("form loc:"+st[0]);
       System.out.println("form name"+key);
       l = new Label(4, z, key.toString());
                        s.addCell(l);
       parsingform(st[0],key,s);
       }
        }catch(Exception px){
            px.printStackTrace();
            px.getMessage();
        }
    }
 public static  void parsingform(String st,String key,WritableSheet s){
        Set st11=new HashSet();
        Set st12=new HashSet();
        Set st13=new HashSet();
        Map<String,String> mp1=new HashMap<String, String>();
        try{
     Document doc = (Document) XMLUtil.getDocument(new File(st));
        XPathExpression expr = XPathUtil.getXPathExpression("/forms/form[@name='"+key+"']");
        NodeList screens = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        for (int j = 0,p=0; j < screens.getLength(); j++,p++) {
                        XPathExpression expr1 = XPathUtil.getXPathExpression("@target");
                        XPathExpression expr2 = XPathUtil.getXPathExpression("@default-map-name");
                       st11.add((String) expr1.evaluate(screens.item(j), XPathConstants.STRING).toString());
                        st12.add((String) expr2.evaluate(screens.item(j), XPathConstants.STRING).toString());
                         main1((String) expr1.evaluate(screens.item(j), XPathConstants.STRING).toString(),s);

        }
        System.out.println("target"+st11);

        l = new Label(10, z, st11.toString());
                        s.addCell(l);



        l = new Label(11, z, st12.toString());
                        s.addCell(l);


        System.out.println("default-map-name"+st12);
        XPathExpression expr1 = XPathUtil.getXPathExpression("/forms/form[@name='"+key+"']/field");
        NodeList screens1 = (NodeList) expr1.evaluate(doc, XPathConstants.NODESET);
        for (int j = 0,p=0; j < screens1.getLength(); j++,p++) {
                        XPathExpression expr11 = XPathUtil.getXPathExpression("@name");
                        st13.add((String) expr11.evaluate(screens1.item(j), XPathConstants.STRING).toString());
                        }
        l = new Label(11, z, st13.toString());
                        s.addCell(l);
                        z++;
        System.out.println("names of form fields"+st13);
        }catch(Exception px){
            px.printStackTrace();
            px.getMessage();}
 }
 public static  void main1(String arr,WritableSheet s) throws Exception {
       try{
        Document doc = (Document) XMLUtil.getDocument("/home/vadmin/Desktop/VBiz0.1/applications/party/servicedef/services.xml");
        XPathExpression expr = XPathUtil.getXPathExpression("/services/service[@name='"+arr+"']");



        NodeList services = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        System.out.println(services.getLength());
        for (int i = 0; i < services.getLength(); i++)
        {
            XPathExpression servicesm = XPathUtil.getXPathExpression("/services/service[@name='"+arr+"']");
            NodeList servi = (NodeList) servicesm.evaluate(doc, XPathConstants.NODESET);
             XPathExpression expr1 = XPathUtil.getXPathExpression("@name");
             System.out.print("Service Name:");
             l = new Label(5, z, (String) expr1.evaluate(servi.item(i), XPathConstants.STRING).toString());
                        s.addCell(l);
            System.out.println((String) expr1.evaluate(servi.item(i), XPathConstants.STRING).toString());

            XPathExpression expr2 = XPathUtil.getXPathExpression("@location");
            System.out.print("Service Location:");
            l = new Label(6, z, (String) expr2.evaluate(servi.item(i), XPathConstants.STRING).toString());
                        s.addCell(l);
            Log.LogInfoMessage((String) expr2.evaluate(servi.item(i), XPathConstants.STRING).toString());
            XPathExpression entity = XPathUtil.getXPathExpression("@default-entity-name");
            System.out.print("Service Entity:");
            l = new Label(7, z, (String) entity.evaluate(servi.item(i), XPathConstants.STRING).toString());
                        s.addCell(l);
            Log.LogInfoMessage((String) entity.evaluate(servi.item(i), XPathConstants.STRING));


            XPathExpression description = XPathUtil.getXPathExpression("/services/service/description");
            NodeList des = (NodeList) description.evaluate(doc, XPathConstants.NODESET);
            System.out.print("Service Description:");
            l = new Label(8, z, description.evaluate(des.item(i), XPathConstants.STRING).toString());
                        s.addCell(l);
             Log.LogInfoMessage(description.evaluate(des.item(i), XPathConstants.STRING).toString());
            //  System.out.println(des.item(0).toString());


             XPathExpression permission = XPathUtil.getXPathExpression("/services/service/permission-service/@service-name");
            NodeList pre = (NodeList) permission.evaluate(doc, XPathConstants.NODESET);
            System.out.print("Service Permission type:");
            l = new Label(9, z, permission.evaluate(pre.item(i), XPathConstants.STRING).toString());
                        s.addCell(l);
             Log.LogInfoMessage((String) permission.evaluate(pre.item(i), XPathConstants.STRING).toString());


           // System.out.println(i);

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