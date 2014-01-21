package documentation;


import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import jxl.write.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import in.vasista.util.XMLUtil;
import in.vasista.util.XPathUtil;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import java.io.*;
import java.io.File;
import java.util.HashSet;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import java.util.Locale;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JPanel;
import jxl.Workbook;
import jxl.WorkbookSettings;
public class RequestMap extends JPanel
   implements ActionListener {
   JButton go;
   JFileChooser chooser;
   String choosertitle;
  public RequestMap() {
    go = new JButton("Browse");
    go.addActionListener(this);
    add(go);
   }
  public void actionPerformed(ActionEvent e) {
    chooser = new JFileChooser();
   // chooser.setCurrentDirectory(new java.io.File("/home/vadmin/Desktop/VBiz0.1/applications/order/servicedef"));
    chooser.setDialogTitle(choosertitle);
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    chooser.setAcceptAllFileFilterUsed(false);
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
     if(chooser.getSelectedFile().toString().endsWith(".xml")){
         try
    {

      String filename = chooser.getSelectedFile().toString().replace(".xml", ".xls").replace("/", "-");
      WorkbookSettings ws = new WorkbookSettings();
      ws.setLocale(new Locale("en", "EN"));
      WritableWorkbook workbook =Workbook.createWorkbook(new File(filename), ws);
      WritableSheet s = workbook.createSheet("Sheet1", 0);
     // WritableSheet s1 = workbook.createSheet("Sheet1", 0);
      //writeDataSheet(s);
      //writeImageSheet(s1);
      //requestparser(s);
      screenparser2(s);
      //serviceparse(s);
//      main1(s);
      //entityparser(s);
      workbook.write();
      workbook.close();
      chooser.setVisible(true);
      
    }
    catch (IOException x)
    {
      x.printStackTrace();
    }
    catch (WriteException y)
    {
      y.printStackTrace();
    }
      }
     else{System.out.println("select xml file only");}
      }
    else {
      System.out.println("No Selection ");
      }
     }
   public void list(WritableSheet s)throws WriteException {
WritableFont wf = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
    WritableCellFormat cf = new WritableCellFormat(wf);
    cf.setWrap(true);
        // Directory path here
        String path = "/home/vadmin/Desktop/VBiz0.1/applications/party/widget/partymgr";

        String files;
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {

            if (listOfFiles[i].isFile()) {
                files = listOfFiles[i].getName();
                System.out.println();
                System.out.println();
                System.out.println(i + files);

                try {
                    System.out.println();
                    //String filepath=path+"/"+files.toString();
                    Document doc = (Document) XMLUtil.getDocument(path + "/" + files.toString());
                    XPathExpression expr = XPathUtil.getXPathExpression("/screens/screen");
                    NodeList screens = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                    for (int j = 0; j < screens.getLength(); j++) {
                        System.out.println("");
                        XPathExpression servicesm = XPathUtil.getXPathExpression("/screens/screen");
                        NodeList servi = (NodeList) servicesm.evaluate(doc, XPathConstants.NODESET);
                        XPathExpression expr1 = XPathUtil.getXPathExpression("@name");
                        System.out.print("Screen Name:");

                        String servicename = expr1.evaluate(screens.item(j), XPathConstants.STRING).toString();
                        System.out.println((String) expr1.evaluate(screens.item(j), XPathConstants.STRING).toString());


                        XPathExpression includescreenname = XPathUtil.getXPathExpression("section/widgets/include-screen");
                        NodeList incscreen = (NodeList) includescreenname.evaluate(screens.item(j), XPathConstants.NODESET);
                        for (int f = 0; f < incscreen.getLength(); f++) {
                            XPathExpression expfield = XPathUtil.getXPathExpression("@name");
                        String incscreenname = (String) expfield.evaluate(incscreen.item(f), XPathConstants.STRING);
                        System.out.println("inclued page:" + incscreenname);
                        }
                        XPathExpression screenservice = XPathUtil.getXPathExpression("section/actions/service/@service-name");
                        String screenser = (String) screenservice.evaluate(screens.item(j), XPathConstants.STRING);

                        System.out.println("Service:" + screenser);


                        XPathExpression screenentity = XPathUtil.getXPathExpression("section/actions/entity-one");
                        NodeList entities = (NodeList) screenentity.evaluate(screens.item(j), XPathConstants.NODESET);
                        for (int f = 0; f < entities.getLength(); f++) {
                            XPathExpression expfield = XPathUtil.getXPathExpression("@entity-name");

                            String entityname = expfield.evaluate(entities.item(f), XPathConstants.STRING).toString();
                            //System.out.println((String) expr1.evaluate(fields.item(f), XPathConstants.STRING).toString());
                            System.out.println("Entity:" +entityname );
                        }

                        XPathExpression efields = XPathUtil.getXPathExpression("section/actions/set");
                        NodeList fields = (NodeList) efields.evaluate(screens.item(j), XPathConstants.NODESET);
                        for (int f = 0; f < fields.getLength(); f++) {
                            XPathExpression expfield = XPathUtil.getXPathExpression("@value");

                            String fieldname = expfield.evaluate(fields.item(f), XPathConstants.STRING).toString();
                            //System.out.println((String) expr1.evaluate(fields.item(f), XPathConstants.STRING).toString());
                            System.out.println("Fields:" + fieldname);
                        }





                        XPathExpression includescreenlocation = XPathUtil.getXPathExpression("section/widgets/include-screen/@location");
                        String screenlocation = (String) includescreenlocation.evaluate(screens.item(j), XPathConstants.STRING);
                        System.out.println("inclued page:" + screenlocation);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

   public void screenparser2(WritableSheet s)throws WriteException{
        WritableFont wf = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
    WritableCellFormat cf = new WritableCellFormat(wf);
    cf.setWrap(true);
Set set = new HashSet();
      try{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = factory.newDocumentBuilder();
         Document doc = builder.parse(chooser.getSelectedFile().toString());

         NodeList list = doc.getElementsByTagName("*");
         System.out.println("XML Elements: ");
         for (int i=0; i<list.getLength(); i++) {
          Element element = (Element)list.item(i);
           String sl = element.getNodeName();
           set.add(sl);
         Label l = new Label(0,i,sl,cf);
    s.addCell(l);         }
         System.out.println(set.toString());
       }

       catch(Exception e)
       {
           System.out.println(e.toString());
           e.printStackTrace();
       }
  }
 
  public void requestparser(WritableSheet s)throws WriteException{
      WritableFont wf = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
    WritableCellFormat cf = new WritableCellFormat(wf);
    cf.setWrap(true);
    Label l = new Label(0,0,"Request Name",cf);
    s.addCell(l);
    WritableCellFormat cf1 =
      new WritableCellFormat(DateFormats.FORMAT9);
    l = new Label(1,0,"Event Type", cf);
    s.addCell(l);
        l = new Label(2,0,"Event Action",cf);
    s.addCell(l);
     l = new Label(3,0,"Responce Type",cf);
    s.addCell(l);
     l = new Label(4,0,"Responce value",cf);
    s.addCell(l);
      try{
        Document doc = (Document) XMLUtil.getDocument(chooser.getSelectedFile());

        XPathExpression expr = XPathUtil.getXPathExpression("/site-conf/request-map");
        NodeList services = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        //int leng=services.getLength();
        for (int i = 0,k=0; i < services.getLength(); i++,k++)
        {
             XPathExpression expr1 = XPathUtil.getXPathExpression("@uri");
             System.out.print(":");
            l=new Label(0,k+1,(String) expr1.evaluate(services.item(i), XPathConstants.STRING).toString());
            s.addCell(l);

            XPathExpression expr4 = XPathUtil.getXPathExpression("event/@type");
             System.out.print(":");
             l=new Label(1,k+1,(String) expr4.evaluate(services.item(i), XPathConstants.STRING).toString());
            s.addCell(l);

             System.out.println((String) expr4.evaluate(services.item(i), XPathConstants.STRING).toString());


            XPathExpression expr5 = XPathUtil.getXPathExpression("event/@invoke");
             System.out.print(":");
              l=new Label(2,k+1,(String) expr5.evaluate(services.item(i), XPathConstants.STRING).toString());
            s.addCell(l);

             System.out.println((String) expr5.evaluate(services.item(i), XPathConstants.STRING).toString());

             XPathExpression event = XPathUtil.getXPathExpression("response");
             NodeList events = (NodeList) event.evaluate(services.item(i), XPathConstants.NODESET);

             for(int j=0;j<events.getLength();j++){

                 XPathExpression expr2 = XPathUtil.getXPathExpression("@name");
             System.out.print(":");
              l=new Label(3,k+j+1,(String) expr2.evaluate(events.item(j), XPathConstants.STRING).toString());
            s.addCell(l);

            System.out.println((String) expr2.evaluate(events.item(j), XPathConstants.STRING).toString());

                 XPathExpression expr6 = XPathUtil.getXPathExpression("@value");
             System.out.print(":");
              l=new Label(4,k+j+1,(String) expr6.evaluate(events.item(j), XPathConstants.STRING).toString());
            s.addCell(l);

            System.out.println((String) expr6.evaluate(events.item(j), XPathConstants.STRING).toString());


             }

             k=k+events.getLength();
            System.out.println(i);
        }
       }
       catch(Exception e)
       {
           System.out.println("");
           System.out.println(e.toString());
           e.printStackTrace();
       }}
   public void main1(WritableSheet s)throws WriteException {
       
       try {

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File("book.xml"));

            // normalize text representation
            doc.getDocumentElement ().normalize ();
            System.out.println ("Root element of the doc is " +
                 doc.getDocumentElement().getNodeName());


            NodeList listOfPersons = doc.getElementsByTagName("person");
            int totalPersons = listOfPersons.getLength();
            System.out.println("Total no of people : " + totalPersons);

            for(int sl=0; sl<listOfPersons.getLength() ; sl++){


                Node firstPersonNode = listOfPersons.item(sl);
                if(firstPersonNode.getNodeType() == Node.ELEMENT_NODE){


                    Element firstPersonElement = (Element)firstPersonNode;

                    //-------
                    NodeList firstNameList = firstPersonElement.getElementsByTagName("first");
                    Element firstNameElement = (Element)firstNameList.item(0);

                    NodeList textFNList = firstNameElement.getChildNodes();
                    System.out.println("First Name : " +
                           ((Node)textFNList.item(0)).getNodeValue().trim());

                    //-------
                    NodeList lastNameList = firstPersonElement.getElementsByTagName("last");
                    Element lastNameElement = (Element)lastNameList.item(0);

                    NodeList textLNList = lastNameElement.getChildNodes();
                    System.out.println("Last Name : " +
                           ((Node)textLNList.item(0)).getNodeValue().trim());

                    //----
                    NodeList ageList = firstPersonElement.getElementsByTagName("age");
                    Element ageElement = (Element)ageList.item(0);

                    NodeList textAgeList = ageElement.getChildNodes();
                    System.out.println("Age : " +
                           ((Node)textAgeList.item(0)).getNodeValue().trim());

                    //------


                }//end of if clause


            }//end of for loop with s var


        }catch (SAXParseException err) {
        System.out.println ("** Parsing error" + ", line "
             + err.getLineNumber () + ", uri " + err.getSystemId ());
        System.out.println(" " + err.getMessage ());

        }catch (SAXException e) {
        Exception x = e.getException ();
        ((x == null) ? e : x).printStackTrace ();

        }catch (Throwable t) {
        t.printStackTrace ();
        }
        //System.exit (0);
        }
 public void entityparser(WritableSheet s)throws WriteException{
     int sum=0;
     WritableFont wf = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
    WritableCellFormat cf = new WritableCellFormat(wf);
    cf.setWrap(true);
    Label l = new Label(0,0,"entity name",cf);
    s.addCell(l);
    WritableCellFormat cf1 =
      new WritableCellFormat(DateFormats.FORMAT9);
    l = new Label(1,0,"package name", cf);
    s.addCell(l);
        l = new Label(2,0,"title",cf);
    s.addCell(l);
    l = new Label(3,0,"fields",cf);
    s.addCell(l);
    l = new Label(4,0,"pri fields",cf);
    s.addCell(l);
    try{
        Document doc = (Document) XMLUtil.getDocument(chooser.getSelectedFile());

        XPathExpression expr = XPathUtil.getXPathExpression("entitymodel/entity");
        NodeList services = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        //int leng=services.getLength();
        for (int i = 0,k=0; i < services.getLength(); i++,k++)
        {
             XPathExpression expr1 = XPathUtil.getXPathExpression("@entity-name");
              l = new Label(0,k+1,expr1.evaluate(services.item(i), XPathConstants.STRING).toString(),cf);
    s.addCell(l);
             System.out.print("entity Name:");
             //String servicename=expr1.evaluate(services.item(i), XPathConstants.STRING).toString();
            System.out.println((String) expr1.evaluate(services.item(i), XPathConstants.STRING).toString());
             XPathExpression expr2 = XPathUtil.getXPathExpression("@package-name");
            System.out.print("package name:");
              l = new Label(1,k+1,expr2.evaluate(services.item(i), XPathConstants.STRING).toString(),cf);
    s.addCell(l);
             //String servicename=expr2.evaluate(services.item(i), XPathConstants.STRING).toString();
            System.out.println((String) expr2.evaluate(services.item(i), XPathConstants.STRING).toString());
             XPathExpression expr3 = XPathUtil.getXPathExpression("@title");
             System.out.print("title:");
            // String servicename=expr1.evaluate(services.item(i), XPathConstants.STRING).toString();
             l = new Label(2,k+1,expr3.evaluate(services.item(i), XPathConstants.STRING).toString(),cf);
    s.addCell(l);
             System.out.println((String) expr3.evaluate(services.item(i), XPathConstants.STRING).toString());
            
             XPathExpression att = XPathUtil.getXPathExpression("field");
    NodeList subservi = (NodeList) att.evaluate(services.item(i), XPathConstants.NODESET);
    XPathExpression pri = XPathUtil.getXPathExpression("prim-key");
    NodeList subservipri = (NodeList) pri.evaluate(services.item(i), XPathConstants.NODESET);
    for(int z=0;z<subservipri.getLength();z++)
    {
      XPathExpression primfield = XPathUtil.getXPathExpression("@field");
      String pr =(String) primfield.evaluate(subservipri.item(z), XPathConstants.STRING);
      l=new Label(4, z+1+k, pr.toString(), cf);
      s.addCell(l);
    }

      
    XPathExpression fiy = XPathUtil.getXPathExpression("relation");
    NodeList subserviprir = (NodeList) fiy.evaluate(services.item(i), XPathConstants.NODESET);
    for(int z=0;z<subserviprir.getLength();z++)
    {
      XPathExpression fkeyrel = XPathUtil.getXPathExpression("@rel-entity-name");
      String fkeyrelty =(String) fkeyrel.evaluate(subserviprir.item(z), XPathConstants.STRING);
      l=new Label(5, z+1+k, fkeyrelty.toString(), cf);
      s.addCell(l);
//      XPathExpression fkeyfield = XPathUtil.getXPathExpression("@rel-field-name");
//      String fyfield =(String) fkeyfield.evaluate(subserviprir.item(z), XPathConstants.STRING);
//      l=new Label(6, z+1+k,fyfield.toString(), cf);
//      s.addCell(l);
//      XPathExpression fkeyfiel = XPathUtil.getXPathExpression("@field-name");
//    //  String fyfield =(String) fkeyfield.evaluate(subserviprir.item(z), XPathConstants.STRING);
//      l=new Label(7, z+1+k, fkeyfiel.evaluate(subserviprir.item(z), XPathConstants.STRING).toString(), cf);
//      s.addCell(l);
    }

        for (int j = 0; j < subservi.getLength(); j++)
        {
XPathExpression att1 = XPathUtil.getXPathExpression("@name");
            String at =(String) att1.evaluate(subservi.item(j), XPathConstants.STRING);
          //   ats=ats+at+",";
        l = new Label(3,k+j+1,at.toString(),cf);
    s.addCell(l); 
        }
         k=k+subservi.getLength();





             sum=sum+subservi.getLength();
             System.out.println(sum);
             System.out.println(i);
        }
       }
       catch(Exception e)
       {
           System.out.println("");
           System.out.println(e.toString());
           e.printStackTrace();
       }
 }
  public void screenparser(WritableSheet s)throws WriteException{
       WritableFont wf = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
    WritableCellFormat cf = new WritableCellFormat(wf);
    cf.setWrap(true);
    Label l = new Label(0,0,"Screen name",cf);
    s.addCell(l);
    WritableCellFormat cf1 =
      new WritableCellFormat(DateFormats.FORMAT9);
    l = new Label(1,0,"Screen type", cf);
    s.addCell(l);
        l = new Label(2,0,"Screen location",cf);
    s.addCell(l);
    try{
        Document doc = (Document) XMLUtil.getDocument(chooser.getSelectedFile());

        XPathExpression expr = XPathUtil.getXPathExpression("/site-conf/view-map");
        NodeList services = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        //int leng=services.getLength();
        for (int i = 0; i < services.getLength(); i++)
        {
             XPathExpression expr1 = XPathUtil.getXPathExpression("@name");
              l = new Label(0,i+1,expr1.evaluate(services.item(i), XPathConstants.STRING).toString(),cf);
    s.addCell(l);
             System.out.print("Screen Name:");
             //String servicename=expr1.evaluate(services.item(i), XPathConstants.STRING).toString();
            System.out.println((String) expr1.evaluate(services.item(i), XPathConstants.STRING).toString());
             XPathExpression expr2 = XPathUtil.getXPathExpression("@type");
            System.out.print("Screen type:");
              l = new Label(1,i+1,expr2.evaluate(services.item(i), XPathConstants.STRING).toString(),cf);
    s.addCell(l);
             //String servicename=expr2.evaluate(services.item(i), XPathConstants.STRING).toString();
            System.out.println((String) expr2.evaluate(services.item(i), XPathConstants.STRING).toString());
             XPathExpression expr3 = XPathUtil.getXPathExpression("@page");
             System.out.print("Screen location:");
            // String servicename=expr1.evaluate(services.item(i), XPathConstants.STRING).toString();
             l = new Label(2,i+1,expr3.evaluate(services.item(i), XPathConstants.STRING).toString(),cf);
    s.addCell(l);
             System.out.println((String) expr3.evaluate(services.item(i), XPathConstants.STRING).toString());
            System.out.println(i);
        }
       }
       catch(Exception e)
       {
           System.out.println("");
           System.out.println(e.toString());
           e.printStackTrace();
       }
  }
  public void serviceparse(WritableSheet s)throws WriteException{

       WritableFont wf = new WritableFont(WritableFont.ARIAL,
      10, WritableFont.BOLD);
    WritableCellFormat cf = new WritableCellFormat(wf);
    cf.setWrap(true);
    Label l = new Label(0,0,"Service name",cf);
    s.addCell(l);
    WritableCellFormat cf1 =
      new WritableCellFormat(DateFormats.FORMAT9);
    l = new Label(1,0,"Service location", cf);
    s.addCell(l);
        l = new Label(2,0,"Service entity",cf);
    s.addCell(l);
      l = new Label(3, 0, "invokes",cf);
    s.addCell(l);
     l = new Label(4,0, "description",cf);
    s.addCell(l);
      l = new Label(5,0, "Service permision",cf);
    s.addCell(l);
     try{
        Document doc = (Document) XMLUtil.getDocument(chooser.getSelectedFile().toString());
        XPathExpression expr = XPathUtil.getXPathExpression("/services/service");
        NodeList servi = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
     
        for (int i = 0,k=0; i < servi.getLength(); i++,k++)
        {
           XPathExpression expr1 = XPathUtil.getXPathExpression("@name");
            l = new Label(0,k+1,expr1.evaluate(servi.item(i), XPathConstants.STRING).toString(),cf);
    s.addCell(l);
            XPathExpression expr2 = XPathUtil.getXPathExpression("@location");
           l = new Label(1,k+1,expr2.evaluate(servi.item(i), XPathConstants.STRING).toString(),cf);
    s.addCell(l);

            XPathExpression entity = XPathUtil.getXPathExpression("@default-entity-name");
           l = new Label(2,k+1,entity.evaluate(servi.item(i), XPathConstants.STRING).toString(),cf);
    s.addCell(l);
   XPathExpression att = XPathUtil.getXPathExpression("attribute");
    NodeList subservi = (NodeList) att.evaluate(servi.item(i), XPathConstants.NODESET);
 String ats="";
        
            XPathExpression inv = XPathUtil.getXPathExpression("@invoke");
       l = new Label(3,k+1,inv.evaluate(servi.item(i), XPathConstants.STRING).toString(),cf);
    s.addCell(l);
            XPathExpression description = XPathUtil.getXPathExpression("description");
            String des = (String) description.evaluate(servi.item(i), XPathConstants.STRING);
           l = new Label(4,k+1,des.toString(),cf);
    s.addCell(l);
           XPathExpression permission = XPathUtil.getXPathExpression("permission-service/@service-name");
            String pre = (String) permission.evaluate(servi.item(i), XPathConstants.STRING);
           l = new Label(5,k+1,pre.toString(),cf);
    s.addCell(l);
    for (int j = 0; j < subservi.getLength(); j++)
        {


XPathExpression att1 = XPathUtil.getXPathExpression("@name");


            String at =(String) att1.evaluate(subservi.item(j), XPathConstants.STRING);
             //ats=ats+at+",";
             l = new Label(7,k+j+1,at.toString(),cf);
    s.addCell(l);
         }
k=k+subservi.getLength()+1;
                 }
       }catch(Exception e)
       {
           e.printStackTrace();
       }
  }
  public Dimension getPreferredSize(){
    return new Dimension(200, 200);
    }
  public static void main(String s[]) {
    JFrame frame = new JFrame("");
    RequestMap panel = new RequestMap();
    frame.addWindowListener(
      new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(0);
          }
        }
      );
    frame.getContentPane().add(panel,"Center");
    frame.setSize(panel.getPreferredSize());
    frame.setVisible(true);
    }
  
}