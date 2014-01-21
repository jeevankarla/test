package documentation;

import in.vasista.util.XMLUtil;
import java.io.*;
import jxl.*;
import java.util.*;
import jxl.Workbook;
import jxl.write.DateFormat;
import jxl.write.Number;

import jxl.write.*;
import java.text.SimpleDateFormat;
import in.vasista.util.XPathUtil;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class ScreenstoForms1 {

public int zr=1;

    
    public  void submain() {
Map<String,String> mp=new HashMap<String, String>();
        try{
            String filename = "input.xls";
      WorkbookSettings ws = new WorkbookSettings();
      ws.setLocale(new Locale("en", "EN"));
      WritableWorkbook workbook =
        Workbook.createWorkbook(new File(filename), ws);
      WritableSheet s = workbook.createSheet("Sheet1", 0);
       WritableFont wf = new WritableFont(WritableFont.ARIAL,
      10, WritableFont.BOLD);
    WritableCellFormat cf = new WritableCellFormat(wf);
    cf.setWrap(true);

    /* Creates Label and writes date to one cell of sheet*/
    Label l = new Label(0,0,"screen name",cf);
    s.addCell(l);
    l = new Label(1,0,"screen fields",cf);
    s.addCell(l);
    l = new Label(2, 0, "form fields",cf);
                        s.addCell(l);
                        l = new Label(3, 0, "included form name",cf);
                        s.addCell(l);
                        l = new Label(4, 0, "services name",cf);
                        s.addCell(l);
                        l = new Label(5, 0, "service location",cf);
                        s.addCell(l);
                        l = new Label(6, 0, "entities",cf);
                        s.addCell(l);
                        l = new Label(7, 0, "Service Description",cf);
                        s.addCell(l);
                        l = new Label(8, 0, "target",cf);
                        s.addCell(l);
                        l = new Label(9, 0, "default-map-name",cf);
                        s.addCell(l);
                        l = new Label(10, 0, "names of form fields",cf);
                        s.addCell(l);
        String path = "/home/vadmin/Desktop/VBiz0.1/applications/party/webapp/partymgr/WEB-INF/controller.xml";
        Document doc = (Document) XMLUtil.getDocument(path.toString());
        XPathExpression expr = XPathUtil.getXPathExpression("/site-conf/view-map");
        NodeList screens = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        System.out.println(screens.getLength());
        for (int j = 0,p=0; j < screens.getLength(); j++,p++) {
                        XPathExpression servicesm = XPathUtil.getXPathExpression("/site-conf/view-map");
                        XPathExpression expr1 = XPathUtil.getXPathExpression("@name");
                        XPathExpression expr2 = XPathUtil.getXPathExpression("@page");
        mp.put(expr1.evaluate(screens.item(j), XPathConstants.STRING).toString(),expr2.evaluate(screens.item(j), XPathConstants.STRING).toString());
        }
        parsescreen(mp,s);
        workbook.write();
        System.out.println(zr);
      workbook.close();
        }catch(Exception px){
            px.printStackTrace();
        
    }
    }

    public void parsescreen(Map mp,WritableSheet s)throws WriteException{
        
        String[] st=new String[2];
      try{
        Iterator it = mp.entrySet().iterator();
while(it.hasNext()){
Map.Entry entry = (Map.Entry) it.next();
String key = entry.getKey().toString();
String value = entry.getValue().toString();
            st=value.toString().replace("component:/","/home/vadmin/Desktop/VBiz0.1/applications").split("#");
       parsingscreen(mp,st[0],key,s);
       }
        }catch(Exception px){
            px.printStackTrace();
            px.getMessage();
        }
    }

    public  void parsingscreen(Map mp,String st,String key,WritableSheet s)throws WriteException{
        Label l=new Label(0, zr, key);
        s.addCell(l);
         System.out.println("screen name::"+key);
        Set st1=new HashSet();
        Set st2=new HashSet();
        Set st3=new HashSet();
        Map<String,String> mp1=new HashMap<String, String>();

        //


        try{
      Document d = (Document) XMLUtil.getDocument( st );
XPathExpression ett = XPathUtil.getXPathExpression("/screens/screen[@name='"+key+"']/section/widgets/decorator-screen/decorator-section/include-screen");
        NodeList screenstt = (NodeList) ett.evaluate(d, XPathConstants.NODESET);
         for (int j = 0; j < screenstt.getLength(); j++) {
                        XPathExpression exprtt1 = XPathUtil.getXPathExpression("@name");
                        //XPathExpression expr2 = XPathUtil.getXPathExpression("@value");
                        //XPathExpression expr3 = XPathUtil.getXPathExpression("@form-field");
                       System.out.println((String) exprtt1.evaluate(screenstt.item(j), XPathConstants.STRING).toString());
                       if(mp.containsKey(exprtt1.evaluate(screenstt.item(j), XPathConstants.STRING))){System.out.println("repeated");}
                       else{
                           XPathExpression e = XPathUtil.getXPathExpression("/screens/screen[@name='"+key+"']/section/actions/set");
        NodeList screens = (NodeList) e.evaluate(d, XPathConstants.NODESET);
         for (int jt = 0; jt < screens.getLength(); jt++) {
                        XPathExpression expr1 = XPathUtil.getXPathExpression("@field");
                       // XPathExpression expr2 = XPathUtil.getXPathExpression("@value");
                        XPathExpression expr3 = XPathUtil.getXPathExpression("@form-field");
                       st1.add((String) expr1.evaluate(screens.item(jt), XPathConstants.STRING).toString());
                      //  st2.add((String) expr2.evaluate(screens.item(jt), XPathConstants.STRING).toString());
                         st3.add((String) expr3.evaluate(screens.item(jt), XPathConstants.STRING).toString());

        }
        XPathExpression et = XPathUtil.getXPathExpression("/screens/screen[@name='"+key+"']/decorator-screen/decorator-section/section/condition/if-service-permission");
        NodeList screenst = (NodeList) et.evaluate(d, XPathConstants.NODESET);
         for (int jt = 0; jt < screenst.getLength(); jt++) {
                        XPathExpression expr1 = XPathUtil.getXPathExpression("@service-name");
                       // XPathExpression expr2 = XPathUtil.getXPathExpression("@value");
                        XPathExpression expr3 = XPathUtil.getXPathExpression("@form-field");
                       st1.add((String) expr1.evaluate(screenst.item(jt), XPathConstants.STRING).toString());
                       // st2.add((String) expr2.evaluate(screenst.item(jt), XPathConstants.STRING).toString());
                         st3.add((String) expr3.evaluate(screenst.item(jt), XPathConstants.STRING).toString());

        }
        l=new Label(1, zr, st1.toString());
        s.addCell(l);

        System.out.println("screen fields"+st1);
       // l=new Label(2, zr, st2.toString());
        //s.addCell(l);
        //st1.clear();
        System.out.println("field values"+st2);
        l=new Label(2, zr, st3.toString());
        s.addCell(l);
        //st2.clear();
        System.out.println("form-field"+st3);
                       // st2.add((String) expr2.evaluate(screens.item(j), XPathConstants.STRING).toString());
                         //st3.add((String) expr3.evaluate(screens.item(j), XPathConstants.STRING).toString());

        }

        
        XPathExpression expr1 = XPathUtil.getXPathExpression("/screens/screen[@name='"+key+"']/section/widgets/decorator-screen/decorator-section/screenlet/include-form");
        NodeList screens1 = (NodeList) expr1.evaluate(d, XPathConstants.NODESET);
        for (int p=0; p < screens1.getLength();p++) {
                        XPathExpression expr11 = XPathUtil.getXPathExpression("@name");
                        XPathExpression expr12 = XPathUtil.getXPathExpression("@location");
                        mp1.put((String) expr11.evaluate(screens1.item(j), XPathConstants.STRING).toString(), (String) expr12.evaluate(screens1.item(p), XPathConstants.STRING).toString());
                        }
         XPathExpression exprt1 = XPathUtil.getXPathExpression("/screens/screen[@name='"+key+"']/section/widgets/decorator-screen/decorator-section/section/widgets/include-form");
        NodeList screenst1 = (NodeList) exprt1.evaluate(d, XPathConstants.NODESET);
        for (int p=0; j < screenst1.getLength(); p++) {
                        XPathExpression expr11 = XPathUtil.getXPathExpression("@name");
                        XPathExpression expr12 = XPathUtil.getXPathExpression("@location");
                        mp1.put((String) expr11.evaluate(screenst1.item(j), XPathConstants.STRING).toString(), (String) expr12.evaluate(screenst1.item(j), XPathConstants.STRING).toString());
                        }
                        parseform(mp1,s);
        }}catch(Exception px){
            px.printStackTrace();
            px.getMessage();}
} public  void parseform(Map mp1,WritableSheet s)throws WriteException{
        String[] st=new String[2];
      try{
        Iterator it = mp1.entrySet().iterator();
while(it.hasNext()){
Map.Entry entry = (Map.Entry) it.next();
String key = entry.getKey().toString();
String value = entry.getValue().toString();
            st[0]=value.toString().replace("component:/","/home/vadmin/Desktop/VBiz0.1/applications");
            System.out.println("form loc:"+st[0]);
             Label l=new Label(3, zr, key);
        s.addCell(l);
       System.out.println("form name"+key);
       parsingform(st[0],key,s);
       }
        }catch(Exception px){
            px.printStackTrace();
            px.getMessage();
        }
    }
 public void parsingform(String st,String key,WritableSheet s)throws WriteException{
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
         Label l=new Label(8, zr, st11.toString());
             s.addCell(l);
        System.out.println("default-map-name"+st12);
        l=new Label(9, zr, st12.toString());
             s.addCell(l);
           XPathExpression expr1 = XPathUtil.getXPathExpression("/forms/form[@name='"+key+"']/field");
        NodeList screens1 = (NodeList) expr1.evaluate(doc, XPathConstants.NODESET);
        for (int j = 0,p=0; j < screens1.getLength(); j++,p++) {
                        XPathExpression expr11 = XPathUtil.getXPathExpression("@name");
                        st13.add((String) expr11.evaluate(screens1.item(j), XPathConstants.STRING).toString());
                        }
         l=new Label(10, zr, st13.toString());
             s.addCell(l);
             zr++;
             System.out.println("names of form fields"+st13);
        }catch(Exception px){
            px.printStackTrace();
            px.getMessage();}
 }
 public void main1(String arr,WritableSheet s)throws WriteException,Exception {
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
             Label l=new Label(4, zr, (String) expr1.evaluate(servi.item(i), XPathConstants.STRING).toString());
             s.addCell(l);
             System.out.println((String) expr1.evaluate(servi.item(i), XPathConstants.STRING).toString());

            XPathExpression expr2 = XPathUtil.getXPathExpression("@location");
            System.out.print("Service Location:");
            l=new Label(5, zr, (String) expr2.evaluate(servi.item(i), XPathConstants.STRING).toString());
             s.addCell(l);
            System.out.println((String) expr2.evaluate(servi.item(i), XPathConstants.STRING).toString());
            XPathExpression entity = XPathUtil.getXPathExpression("@default-entity-name");
            System.out.print("Service Entity:");
            l=new Label(6, zr, (String) entity.evaluate(servi.item(i), XPathConstants.STRING).toString());
             s.addCell(l);
            System.out.println((String) entity.evaluate(servi.item(i), XPathConstants.STRING));


            XPathExpression description = XPathUtil.getXPathExpression("/services/service/description");
            NodeList des = (NodeList) description.evaluate(doc, XPathConstants.NODESET);
            System.out.print("Service Description:");
            l=new Label(7, zr, (String) description.evaluate(servi.item(i), XPathConstants.STRING).toString());
             s.addCell(l);
            System.out.println(description.evaluate(des.item(i), XPathConstants.STRING).toString());
            //  System.out.println(des.item(0).toString());


             XPathExpression permission = XPathUtil.getXPathExpression("/services/service/permission-service/@service-name");
            NodeList pre = (NodeList) permission.evaluate(doc, XPathConstants.NODESET);
            System.out.print("Service Permission type:");
            System.out.println((String) permission.evaluate(pre.item(i), XPathConstants.STRING).toString());


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
 public static void main(String args[]){
ScreenstoForms1 lis=new ScreenstoForms1();
lis.submain();}
}